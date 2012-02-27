(ns mayancal.genpdf
  (:require [mayancal.mcal :as mcal
             :only (gregorian haab-name haab-number tzolkin-number tzolkin-name)])
  (:import [com.itextpdf.text BaseColor Chunk Document DocumentException Element
                              Font Font$FontFamily Image PageSize Paragraph])
  (:import [com.itextpdf.text.pdf PdfContentByte PdfPCell PdfPTable PdfWriter]) )


;; size definitions:
(defonce day-cell-margin-right (.floatValue 5.0))
(defonce doc-margin-left (.floatValue 72.0))
(defonce doc-margin-right (.floatValue 72.0))
(defonce doc-margin-top (.floatValue 108.0))
(defonce doc-margin-bottom (.floatValue 72.0))
(defonce month-cell-margin-right (.floatValue 10.0))
(defonce number-of-columns 5)
(defonce table-width (.floatValue 700.0))
(defonce write-row-x-offset (.floatValue 45.0))
(defonce write-row-y-offset (.floatValue 50.0))

;; color definitions:
(defonce blank-cell-color (BaseColor. 0xE0 0xE0 0xE0))
(defonce cell-color BaseColor/WHITE)
(defonce gregor-color (BaseColor. 0x00 0x7F 0x00))
(defonce header-color (BaseColor. 0xE9 0xFF 0xDF))
(defonce tzday-color (BaseColor. 0x00 0x00 0x7F))

;; font definitions
(defonce month-font (Font. Font$FontFamily/HELVETICA (.floatValue 18.0) Font/BOLD))
(defonce tzday-font (Font. Font$FontFamily/HELVETICA (.floatValue 10.0) Font/NORMAL tzday-color))
(defonce tzindex-font (Font. Font$FontFamily/HELVETICA (.floatValue 12.0) Font/BOLD))
(defonce gregor-font (Font. Font$FontFamily/HELVETICA (.floatValue 10.0) Font/NORMAL gregor-color))


(defn- add-metadata [document]
  (doto document
    (.addTitle "Mayan Calendar for a Gregorian Year")
    (.addAuthor "Dianne Patterson")
    (.addCreator "Tom Hicks") ))


(defn- gen-background-image [canvas]
  (let [ page-height (.getHeight PageSize/LETTER)
         page-width (.getWidth PageSize/LETTER)
         img (Image/getInstance "resources/codexpaper.jpg") ]
    (.scaleToFit img page-height page-width)
    (.setAlignment img Element/ALIGN_CENTER)
    (.setAbsolutePosition img 0 0)
    (.addImage canvas img) ))


(defn- gen-preface [document canvas]
  (gen-background-image canvas)
  (.add document (Paragraph. "Hello 2012!"))
  (.newPage document) )


(defn- gen-month-image [canvas month-name]
  (let [ page-height (.getHeight PageSize/LETTER)
         page-width (.getWidth PageSize/LETTER)
         ;; img (Image/getInstance (month-to-image-map month-name))
         img (Image/getInstance "resources/MonthIcons/kankin_glyph.png") ]
    (.scaleToFit img page-height page-width)
    (.setAbsolutePosition img 0 0)
    ;; (.setAbsolutePosition img (/ (- page-height (.getScaledHeight img)) 2)
    ;;                           (/ (- page-width (.getScaledWidth img)) 2) )
    (.addImage canvas img) ))


(defn- make-month-cell [month-name]
  (println "MONTH_NAME: " month-name)       ; REMOVE LATER
  (let [ para (doto (Paragraph. month-name month-font)
                (.setAlignment (bit-or Image/RIGHT Image/TEXTWRAP))
                (.setIndentationRight month-cell-margin-right))
         glyph-filename (get mcal/haab-glyph-filenames month-name) ]

    (when glyph-filename
      (when-let [glyph-img (Image/getInstance (str "resources/MonthIcons/" glyph-filename))]
        (.scalePercent glyph-img 50.0)
        (.add para (Chunk. glyph-img 20 0 true)) ))

    (doto (PdfPCell.)
      (.setColspan number-of-columns)
      (.setBackgroundColor header-color)
      (.setHorizontalAlignment Element/ALIGN_TOP)
      (.setVerticalAlignment Element/ALIGN_RIGHT)
      (.setUseDescender true)
      (.addElement para)
      (.addElement Chunk/NEWLINE) )
))


(defn- make-blank-cell [day-index]
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
  (let [ tz-lbl (str (mcal/tzolkin-number day) "-" (mcal/tzolkin-name day) "  ")
         gregor-lbl (mcal/gregorian day)
         day-index (mcal/haab-number day)
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
      (.setUseDescender true)
      (.setBackgroundColor cell-color)
      (.setHorizontalAlignment Element/ALIGN_TOP)
      (.setVerticalAlignment Element/ALIGN_RIGHT)
      (.addElement para) )))


(defn- gen-months [document canvas roundcal]
  (doseq [month roundcal]
    (let [ first-day (first month)
          month-name (mcal/haab-name first-day)
          skip-blanks (or (.startsWith month-name "Uayeb") (= month (last roundcal))) ]
      (gen-background-image canvas)
      ;; (gen-month-image canvas month-name)
      (let [table (PdfPTable. number-of-columns)]
        (.setHorizontalAlignment table Element/ALIGN_TOP)
        (.setTotalWidth table table-width)
        (.setBackgroundColor (.getDefaultCell table) cell-color)
        (.addCell table (make-month-cell month-name))
        (when-not skip-blanks
          (dotimes [day-index (- 20 (count month))]
            (.addCell table (make-blank-cell day-index))))
        (doseq [day month]
          (.addCell table (make-day-cell day)))
        (.completeRow table)
        (.writeSelectedRows table 0 -1 write-row-x-offset
                            (.floatValue (+ write-row-y-offset (.getTotalHeight table)))
                            canvas)
      )
      (.newPage document) )))


(defn- gen-postface [document canvas]
  (gen-background-image canvas)
  (.add document (Paragraph. "Goodbye 2012!")) )


(defn gen-cal [roundcal outfile]
  "Top-level call to generate PDF version of Mayan Calendar"
  (let [ document (Document. (.rotate PageSize/LETTER))
         fos (java.io.FileOutputStream. (str outfile ".pdf"))
         pdf-writer (PdfWriter/getInstance document fos) ]
    (.setMargins document doc-margin-left doc-margin-right doc-margin-top doc-margin-bottom)
    (.setMarginMirroringTopBottom document true)      ; for top binding
    (.open document)
    (let [canvas (.getDirectContent pdf-writer)]
      (add-metadata document)
      (gen-preface document canvas)
      (gen-months document canvas roundcal)
      (gen-postface document canvas)
    )
    (.close document)
))
