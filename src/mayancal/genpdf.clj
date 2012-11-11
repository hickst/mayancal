(ns mayancal.genpdf
  (:require [clojure.java.io :as cjio]
            [mayancal.mcal :as mcal :only (haab tzolkin)])
  (:import [com.itextpdf.text BaseColor Chunk Document DocumentException Element
                              Font Font$FontFamily Image PageSize Paragraph])
  (:import [com.itextpdf.text.pdf PdfContentByte PdfPCell PdfPTable PdfWriter]) )


;; size definitions:
(defonce day-cell-margin-right (.floatValue 5.0))
(defonce doc-margin-left (.floatValue 72.0))
(defonce doc-margin-right (.floatValue 72.0))
(defonce doc-margin-top (.floatValue 36.0))
(defonce doc-margin-bottom (.floatValue 36.0))
(defonce icon-cell-bottom-padding (.floatValue 4.0))
(defonce icon-cell-side-padding (.floatValue 7.0))
(defonce image-shrink-factor (.floatValue 0.85))
(defonce month-cell-margin-right (.floatValue 48.0))
(defonce number-of-calendar-columns 5)
(defonce number-of-preface-columns 5)
(defonce table-width (.floatValue 684.0))
(defonce write-row-x-offset (.floatValue 45.0))
(defonce write-row-y-offset (.floatValue 50.0))

;; color definitions:
(defonce blank-cell-color (BaseColor. 0xE0 0xE0 0xE0))
(defonce cell-color BaseColor/WHITE)
(defonce gregor-color (BaseColor. 0x00 0x7F 0x00))
(defonce header-color (BaseColor. 0xE9 0xFF 0xDF))
(defonce tzday-color (BaseColor. 0x00 0x00 0x7F))

;; font definitions
(defonce gregor-font (Font. Font$FontFamily/HELVETICA (.floatValue 10.0) Font/NORMAL gregor-color))
(defonce icon-label-font (Font. Font$FontFamily/HELVETICA (.floatValue 12.0) Font/BOLD))
(defonce label-font (Font. Font$FontFamily/HELVETICA (.floatValue 16.0) Font/BOLD))
(defonce month-font (Font. Font$FontFamily/HELVETICA (.floatValue 18.0) Font/BOLD))
(defonce tzday-font (Font. Font$FontFamily/HELVETICA (.floatValue 10.0) Font/NORMAL tzday-color))
(defonce tzindex-font (Font. Font$FontFamily/HELVETICA (.floatValue 12.0) Font/BOLD))

;; misc definitions
(defonce icon-props {
  :haab { :title "Haab: Month Names and Translations" :scalePct 45.0
          :path "MonthIcons/" }
  :tzolkin { :title "Tzolk'in: Day Names and Translations" :scalePct 35.0
             :path "DayIcons/" } })
(defonce month-image-path "MonthPics/")


(defn- add-metadata [document]
  (doto document
    (.addTitle "Mayan Calendar for a Gregorian Year")
    (.addAuthor "Dianne Patterson")
    (.addCreator "Tom Hicks") ))


(defn- gen-background-image [pdf-writer]
  "Generate the background image of textured paper onto the current page"
  (let [ page-width (.getHeight PageSize/LETTER)  ;; note reversal of W-H for landscape mode
         page-height (.getWidth PageSize/LETTER)  ;; note reversal of W-H for landscape mode
         under (.getDirectContentUnder pdf-writer)
         img (Image/getInstance (cjio/resource "codexpaper.jpg")) ]
    (.scaleToFit img page-width page-height)
    (.setAbsolutePosition img 0 0)
    (.saveState under)
    (.addImage under img)
    (.restoreState under) ))


(defn- gen-image-page [document pdf-writer image-name]
  "Generate the picture page for the current month"
  (let [ page-width (.getHeight PageSize/LETTER)  ;; note reversal of W-H for landscape mode
         page-height (.getWidth PageSize/LETTER)  ;; note reversal of W-H for landscape mode
         img (Image/getInstance (cjio/resource image-name))
         canvas (.getDirectContent pdf-writer) ]
    (.scaleToFit img (* page-width image-shrink-factor) (* page-height image-shrink-factor))
    (.setAbsolutePosition img (/ (- page-width (.getScaledWidth img)) 2)
                              (/ (- page-height (.getScaledHeight img)) 2) )
    (.addImage canvas img)
    (.newPage document) ))


(defn- gen-cover [document pdf-writer]
  "Generate the cover page"
  (gen-background-image pdf-writer)
  (gen-image-page document pdf-writer "cover.png"))


(defn make-label-cell [document pdf-writer unit-type]
  "Create and return the icon table title cell for the given time unit type"
  (let [ label (:title (unit-type icon-props))
        para (doto (Paragraph. label label-font)
                (.setAlignment Element/ALIGN_CENTER)) ]
    (doto (PdfPCell.)
      (.setColspan number-of-preface-columns)
      (.setBackgroundColor header-color)
      (.setHorizontalAlignment Element/ALIGN_MIDDLE)
      (.setPaddingBottom 15)
      (.setPaddingTop 15)
      (.setUseDescender true)
      (.addElement para) )                  ; returns the label-cell
))


(defn- make-icon-cell [time-unit unit-type]
  "Create and return the icon label/image cell using info from the given time-unit"
  (let [ para (Paragraph. (:title time-unit) icon-label-font)
         glyph-path (str (:path (unit-type icon-props)) (:glyph time-unit))
         icon-cell (PdfPCell.) ]

    (doto icon-cell
      (.setBackgroundColor cell-color)
      (.setHorizontalAlignment Element/ALIGN_LEFT)
      (.setVerticalAlignment Element/ALIGN_TOP)
      (.setPaddingLeft icon-cell-side-padding)
      (.setPaddingRight icon-cell-side-padding)
      (.setPaddingBottom icon-cell-bottom-padding)
      (.setUseDescender true) )

    (when-let [glyph-img (Image/getInstance (cjio/resource glyph-path))]
      (.scalePercent glyph-img (:scalePct (unit-type icon-props)))
      (.add para Chunk/NEWLINE)
      (.add para (Chunk. glyph-img (/ (.getScaledWidth glyph-img) 2) 0 true)) )

    (doto icon-cell
      (.addElement para))                   ; returns the icon-cell
))


(defn gen-icon-table [document pdf-writer icon-data unit-type]
  "Generate a table of day or month icons"
  (gen-background-image pdf-writer)
  (let [table (doto (PdfPTable. number-of-preface-columns)
                (.setHorizontalAlignment Element/ALIGN_TOP)
                (.setKeepTogether true)
                (.setTotalWidth table-width)
                (.setLockedWidth true)) ]
    (.setBackgroundColor (.getDefaultCell table) cell-color)
    (.addCell table (make-label-cell document pdf-writer unit-type))

    (doseq [time-unit icon-data]
      (.addCell table (make-icon-cell time-unit unit-type)) )
    (.completeRow table)
    (.add document table)
  )
  (.newPage document)
)


(defn- gen-preface [document pdf-writer]
  "Generate the preface pages"
  (gen-icon-table document pdf-writer mcal/haab :haab)
  (gen-icon-table document pdf-writer mcal/tzolkin :tzolkin)
)


(defn- make-month-cell [day]
  "Create and return the month title/image cell using month info from the given day"
  (println "Generating: " (:title (:haab day)))
  (let [ para (doto (Paragraph. (:title (:haab day)) month-font)
                (.setAlignment Element/ALIGN_RIGHT)
                (.setIndentationRight month-cell-margin-right) )
         glyph-path (str (:path (:haab icon-props)) (:glyph (:haab day)))
         month-cell (PdfPCell.) ]

    (doto month-cell
      (.setColspan number-of-calendar-columns)
      (.setBackgroundColor header-color)
      (.setHorizontalAlignment Element/ALIGN_MIDDLE)
      (.setVerticalAlignment Element/ALIGN_RIGHT)
      (.setPaddingBottom 15)
      (.setPaddingTop 15)
      (.setUseDescender true) )

    (when-let [glyph-img (Image/getInstance (cjio/resource glyph-path))]
      (.scalePercent glyph-img 50.0)
      (.setAlignment glyph-img Image/TEXTWRAP)
      (.add para (Chunk. glyph-img 20 -15 true)) )

    (doto month-cell
      (.addElement para))                   ; returns the month-cell
))


(defn- make-blank-cell [day-index]
  "Create and return a new blank cell for the given numbered day"
  (let [ para (doto (Paragraph.)
                (.setAlignment Element/ALIGN_RIGHT)
                (.setIndentationRight day-cell-margin-right)
                (.add (Chunk. (str day-index) tzindex-font))
                (.add Chunk/NEWLINE)) ]
    (dotimes [x 5] (.add para Chunk/NEWLINE))
    (doto (PdfPCell.)
      (.setBackgroundColor blank-cell-color)
      (.setHorizontalAlignment Element/ALIGN_TOP)
      (.setUseDescender true)
      (.setVerticalAlignment Element/ALIGN_RIGHT)
      (.addElement para) )))


(defn- make-day-cell [day]
  (let [ tz-lbl (str (:trecena day) "-" (:title (:tzolkin day)) "  ")
         gregor-lbl (:gregorian day)
         day-index (:haab-number day)
         para (doto (Paragraph.)
                (.setAlignment Element/ALIGN_RIGHT)
                (.setIndentationRight day-cell-margin-right)
                (.add (Chunk. tz-lbl tzday-font))
                (.add (Chunk. (str day-index) tzindex-font))
                (.add Chunk/NEWLINE)
                (.add (Chunk. gregor-lbl gregor-font))
                (.add Chunk/NEWLINE)) ]
    (dotimes [x 4] (.add para Chunk/NEWLINE))
    (doto (PdfPCell.)
      (.setBackgroundColor cell-color)
      (.setHorizontalAlignment Element/ALIGN_TOP)
      (.setVerticalAlignment Element/ALIGN_RIGHT)
      (.setUseDescender true)
      (.addElement para) )))


(defn- gen-months [document pdf-writer roundcal]
  "Generate the pages for all the months"
  (doseq [month roundcal]
    (let [ day1 (first month)
           month-name (:name (:haab day1))
           skip-blanks (or (= month-name "wayeb") (= month (last roundcal))) ]
      (gen-background-image pdf-writer)
      (gen-image-page document pdf-writer (str month-image-path (:image (:haab day1))))
      (gen-background-image pdf-writer)
      (let [table (doto (PdfPTable. number-of-calendar-columns)
                    (.setHorizontalAlignment Element/ALIGN_TOP)
                    (.setKeepTogether true)
                    (.setTotalWidth table-width)
                    (.setLockedWidth true)) ]
        (.setBackgroundColor (.getDefaultCell table) cell-color)
        (.addCell table (make-month-cell day1))
        (when-not skip-blanks
          (dotimes [day-index (- 20 (count month))]
            (.addCell table (make-blank-cell day-index))))
        (doseq [day month]
          (.addCell table (make-day-cell day)))
        (.completeRow table)
        (.add document table)
      )
      (.newPage document) )))


(defn- gen-postface [document pdf-writer]
  (gen-background-image pdf-writer)
  (.add document (Paragraph. "Goodbye 2012!")) )


(defn gen-cal [roundcal outfile]
  "Top-level call to generate PDF version of Mayan Calendar"
  (let [ document (Document. (.rotate PageSize/LETTER))
         fos (java.io.FileOutputStream. (str outfile ".pdf"))
         pdf-writer (PdfWriter/getInstance document fos) ]
    (.setMargins document doc-margin-left doc-margin-right doc-margin-top doc-margin-bottom)
    (.setMarginMirroringTopBottom document true)      ; for top binding
    (.open document)
    (add-metadata document)
    (gen-cover document pdf-writer)
    (gen-preface document pdf-writer)
    (gen-months document pdf-writer roundcal)
    (gen-postface document pdf-writer)
    (.close document)
))
