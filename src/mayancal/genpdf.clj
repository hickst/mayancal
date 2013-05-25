;; Copyright (C) 2012 by Tohono Consulting, LLC.

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0, which can be found in the LICENSE file in the
;; resources area of this distribution. By using this software in any fashion,
;; you are agreeing to be bound by the terms of this license. You
;; must not remove this notice, or any other, from this software.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
;; Eclipse Public License for more details.

;; You should have received a copy of the Eclipse Public License
;; along with this program. If not, see http://opensource.org/licenses/eclipse-1.0.php.

(ns mayancal.genpdf
  (:require [clojure.java.io :as cjio]
            [mayancal.content :as content]
            [mayancal.mcal :as mcal :only (haab tzolkin)])
  (:import [com.itextpdf.text BaseColor Chunk Document DocumentException Element
                              Font Font$FontFamily Image PageSize Paragraph Version])
  (:import [com.itextpdf.text.pdf PdfAction PdfContentByte PdfPCell PdfDestination
                                  PdfOutline PdfPTable PdfWriter]) )


;; size definitions:
(defonce day-cell-margin-right (.floatValue 5.0))
(defonce default-shrink-factor (.floatValue 0.85))
(defonce doc-margin-left (.floatValue 72.0))
(defonce doc-margin-right (.floatValue 72.0))
(defonce doc-margin-top (.floatValue 36.0))
(defonce doc-margin-bottom (.floatValue 36.0))
(defonce icon-cell-bottom-padding (.floatValue 4.0))
(defonce icon-cell-side-padding (.floatValue 7.0))
(defonce month-cell-margin-right (.floatValue 48.0))
(defonce normal-spacing (.floatValue 5.0))
(defonce number-of-calendar-columns 5)
(defonce number-of-preface-columns 5)
(defonce table-width (.floatValue 684.0))
(defonce title-x-padding (.floatValue 7.0))
(defonce title-y-padding (.floatValue 4.0))
(defonce write-row-x-offset (.floatValue 45.0))
(defonce write-row-y-offset (.floatValue 50.0))

;; color definitions:
(defonce blank-cell-color (BaseColor. 0xE0 0xE0 0xE0))
(defonce cell-color BaseColor/WHITE)
(defonce gregor-color (BaseColor. 0x00 0x7F 0x00))
(defonce header-color (BaseColor. 0xE9 0xFF 0xDF))
(defonce link-color (BaseColor. 0x00 0x00 0x7F))
(defonce link-bg-color (BaseColor. 0xE9 0xFF 0xDF))
(defonce title-color (BaseColor. 0xE9 0xFF 0xDF))
(defonce tzday-color (BaseColor. 0x00 0x00 0x7F))

;; font definitions
(defonce gregor-font (Font. Font$FontFamily/HELVETICA (.floatValue 10.0) Font/NORMAL gregor-color))
(defonce icon-label-font (Font. Font$FontFamily/HELVETICA (.floatValue 12.0) Font/BOLD))
(defonce link-font (Font. Font$FontFamily/HELVETICA (.floatValue 10.0) Font/NORMAL link-color))
(defonce label-font (Font. Font$FontFamily/HELVETICA (.floatValue 16.0) Font/BOLD))
(defonce month-font (Font. Font$FontFamily/HELVETICA (.floatValue 18.0) Font/BOLD))
(defonce normal-font (Font. Font$FontFamily/HELVETICA (.floatValue 10.0) Font/NORMAL))
(defonce title-font (Font. Font$FontFamily/HELVETICA (.floatValue 16.0) Font/BOLD))
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


(defn- gen-background-image
  "Generate the background image of textured paper onto the current page"
  [pdf-writer]
  (let [ page-width (.getHeight PageSize/LETTER)  ;; note reversal of W-H for landscape mode
         page-height (.getWidth PageSize/LETTER)  ;; note reversal of W-H for landscape mode
         under (.getDirectContentUnder pdf-writer)
         img (Image/getInstance (cjio/resource "codexpaper.jpg")) ]
    (.scaleToFit img page-width page-height)
    (.setAbsolutePosition img 0 0)
    (.saveState under)
    (.addImage under img)
    (.restoreState under) ))


(defn- gen-image-page
  "Generate the picture page for the current month"
  [document pdf-writer image-name & extrargs]
  (let [ page-width (.getHeight PageSize/LETTER)  ;; note reversal of W-H for landscape mode
         page-height (.getWidth PageSize/LETTER)  ;; note reversal of W-H for landscape mode
         img (Image/getInstance (cjio/resource image-name))
         canvas (.getDirectContent pdf-writer)
         image-shrink-factor (or (first extrargs) default-shrink-factor)
        ]
    (.scaleToFit img (* page-width image-shrink-factor) (* page-height image-shrink-factor))
    (.setAbsolutePosition img (/ (- page-width (.getScaledWidth img)) 2)
                              (/ (- page-height (.getScaledHeight img)) 2) )
    (.addImage canvas img)
    (.newPage document) ))


(defn- gen-cover
  "Generate the cover page"
  [document pdf-writer]
  (PdfOutline. (.getRootOutline pdf-writer) (PdfDestination. PdfDestination/FITH) "Cover" true)
  (gen-background-image pdf-writer)
  (gen-image-page document pdf-writer "cover.png" 1.0))


(defn make-label-cell
  "Create and return the icon table title cell for the given time unit type"
  [document pdf-writer unit-type]
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


(defn- make-icon-cell
  "Create and return the icon label/image cell using info from the given time-unit"
  [time-unit unit-type]
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


(defn gen-icon-table
  "Generate a table of day or month icons"
  [document pdf-writer icon-data unit-type]
  (PdfOutline. (.getRootOutline pdf-writer)
               (PdfDestination. PdfDestination/FITH)
               (:title (unit-type icon-props)) true)
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


(defn- gen-introduction
  "Generate a calendar introduction page"
  [document pdf-writer content-ref]
  (PdfOutline. (.getRootOutline pdf-writer)
               (PdfDestination. PdfDestination/FITH)
               (:title content-ref) true)
  (gen-background-image pdf-writer)

  (.add document                            ; content title
     (doto (Paragraph.)
       (.setAlignment Element/ALIGN_CENTER)
       (.setSpacingAfter normal-spacing)
       (.add (doto (Chunk. (:title content-ref) month-font)
               (.setBackground title-color
                               title-x-padding title-y-padding title-x-padding title-y-padding))) ))

  (doseq [clause (:clauses content-ref)]
    (.add document                          ; content clauses
      (doto (Paragraph.)
        (.setSpacingBefore normal-spacing)
        (.setAlignment Element/ALIGN_LEFT)
        (.add (Chunk. clause normal-font)) )))

  (.add document (Paragraph. "  " normal-font)) ; spacer

  (doseq [ndx-link (map vector (iterate inc 1) (:links content-ref))]
    (.add document                          ; content links
      (doto (Paragraph.)
        (.setSpacingBefore normal-spacing)
        (.setAlignment Element/ALIGN_LEFT)
        (.add (Chunk. (str "[" (first ndx-link) "] " (second ndx-link)) link-font)) )))
)


(defn- gen-intro-table
  "Generate a calendar introduction page"
  [document pdf-writer content-ref]
  (.add document (doto (Paragraph. "  " normal-font) ; spacer
                   (.setSpacingBefore (* 2 normal-spacing)) ))

  (let [ title (:title content-ref)
         rows (:rows content-ref)
         numcols (apply max (map count rows))
         table (doto (PdfPTable. numcols)
                 (.setHorizontalAlignment Element/ALIGN_TOP)
                 (.setKeepTogether true)
                 (.setTotalWidth table-width)
                 (.setLockedWidth true)) ]

    (.setBackgroundColor (.getDefaultCell table) cell-color)
    (.addCell table (doto (PdfPCell.)       ; add title cell
                      (.setColspan numcols)
                      (.setBackgroundColor header-color)
                      (.setHorizontalAlignment Element/ALIGN_MIDDLE)
                      (.setPaddingBottom 15)
                      (.setPaddingTop 15)
                      (.setUseDescender true)
                      (.addElement (doto (Paragraph. title title-font)
                                     (.setAlignment Element/ALIGN_CENTER))) ))

    (doseq [row rows]
      (doseq [cell row]
        (.addCell table (doto (PdfPCell.)   ; add data cells
                          (.setBackgroundColor cell-color)
                          (.setHorizontalAlignment Element/ALIGN_LEFT)
                          (.setVerticalAlignment Element/ALIGN_TOP)
                          (.setPaddingLeft icon-cell-side-padding)
                          (.setPaddingRight icon-cell-side-padding)
                          (.setPaddingBottom icon-cell-bottom-padding)
                          (.setUseDescender true)
                          (.addElement (Paragraph. cell icon-label-font)) )))
      (.completeRow table) )

    (.add document table)
))


(defn- gen-preface
  "Generate the preface pages"
  [document pdf-writer]
  (gen-introduction document pdf-writer content/calintro)
  (.newPage document)
  (gen-introduction document pdf-writer content/longcnt)
  (gen-intro-table document pdf-writer (:table content/longcnt))
  (.newPage document)
  (gen-icon-table document pdf-writer mcal/haab :haab)
  (gen-icon-table document pdf-writer mcal/tzolkin :tzolkin)
)


(defn- make-month-cell
  "Create and return the month title/image cell using month info from the given day"
  [day]
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


(defn- make-blank-cell
  "Create and return a new blank cell for the given numbered day"
  [day-index]
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


(defn- gen-months
  "Generate the pages for all the months"
  [document pdf-writer roundcal]
  (doseq [month roundcal]
    (let [ day1 (first month)
           month-name (:name (:haab day1))
           month-title (:title (:haab day1))
           skip-blanks (or (= month-name "wayeb") (= month (last roundcal))) ]

      (PdfOutline. (.getRootOutline pdf-writer)
                   (PdfDestination. PdfDestination/FITH)
                   (str month-title ": Image") true)
      (gen-background-image pdf-writer)
      (gen-image-page document pdf-writer (str month-image-path (:image (:haab day1))))

      (PdfOutline. (.getRootOutline pdf-writer)
                   (PdfDestination. PdfDestination/FITH)
                   (str month-title ": Calendar") true)
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


(defn- gen-postface
  "Generate the back page"
  [document pdf-writer]
  (PdfOutline. (.getRootOutline pdf-writer)
               (PdfDestination. PdfDestination/FITH)
               (:title content/background) true)
  (gen-background-image pdf-writer)

  (.add document                            ; background title
     (doto (Paragraph.)
       (.setAlignment Element/ALIGN_CENTER)
       (.setSpacingAfter normal-spacing)
       (.add (doto (Chunk. (:title content/background) title-font)
               (.setBackground title-color
                               title-x-padding title-y-padding title-x-padding title-y-padding))) ))

  (doseq [clause (:clauses content/background)]
    (.add document                          ; background clauses
      (doto (Paragraph.)
        (.setSpacingBefore normal-spacing)
        (.setAlignment Element/ALIGN_LEFT)
        (.add (Chunk. clause normal-font)) )))

  (.add document                            ; licenses title
     (doto (Paragraph.)
       (.setAlignment Element/ALIGN_CENTER)
       (.setSpacingBefore (* 3.0 normal-spacing))
       (.setSpacingAfter normal-spacing)
       (.add (doto (Chunk. (:title content/license-text) title-font)
               (.setBackground title-color
                               title-x-padding title-y-padding title-x-padding title-y-padding))) ))

  (doseq [clause (:clauses content/license-text)]
    (.add document                          ; license clauses
      (doto (Paragraph.)
        (.setSpacingBefore normal-spacing)
        (.setAlignment Element/ALIGN_LEFT)
        (.add (Chunk. clause normal-font)) )))

  (.add document                          ; version clauses
    (doto (Paragraph.)
      (.setSpacingBefore normal-spacing)
      (.setAlignment Element/ALIGN_LEFT)
      (.add (Chunk. (str "This calendar generated using "
                         (.getVersion (Version/getInstance))) normal-font)) ))

  (.add document                            ; acknowledgements title
     (doto (Paragraph.)
       (.setAlignment Element/ALIGN_CENTER)
       (.setSpacingBefore (* 3.0 normal-spacing))
       (.setSpacingAfter normal-spacing)
       (.add (doto (Chunk. (:title content/acknowledgements) title-font)
               (.setBackground title-color
                               title-x-padding title-y-padding title-x-padding title-y-padding))) ))

  (doseq [ack (:entries content/acknowledgements)]
    (.add document                          ; image acknowledgements
      (doto (Paragraph.)
        (.setSpacingBefore normal-spacing)
        (.setAlignment Element/ALIGN_LEFT)
        (.add (Chunk. (:name ack) normal-font))
        (.add (Chunk. ":    " normal-font))
        (.add (doto (Chunk. (:url ack) link-font)
                (.setAction (PdfAction. (:url ack) true)))) )))
)


(defn gen-cal
  "Top-level call to generate PDF version of Mayan Calendar"
  [roundcal outfile]
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
