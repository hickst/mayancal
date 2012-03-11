(ns mayancal.genpdf
  (:import [com.itextpdf.text BaseColor Chunk Document DocumentException Element
                              Font Font$FontFamily Image PageSize Paragraph])
  (:import [com.itextpdf.text.pdf PdfContentByte PdfPCell PdfPTable PdfWriter]) )


;; size definitions:
(defonce day-cell-margin-right (.floatValue 5.0))
(defonce doc-margin-left (.floatValue 72.0))
(defonce doc-margin-right (.floatValue 72.0))
(defonce doc-margin-top (.floatValue 36.0))
(defonce doc-margin-bottom (.floatValue 36.0))
(defonce month-cell-margin-right (.floatValue 48.0))
(defonce number-of-columns 5)
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
(defonce month-font (Font. Font$FontFamily/HELVETICA (.floatValue 18.0) Font/BOLD))
(defonce tzday-font (Font. Font$FontFamily/HELVETICA (.floatValue 10.0) Font/NORMAL tzday-color))
(defonce tzindex-font (Font. Font$FontFamily/HELVETICA (.floatValue 12.0) Font/BOLD))
(defonce gregor-font (Font. Font$FontFamily/HELVETICA (.floatValue 10.0) Font/NORMAL gregor-color))


(defn- add-metadata [document]
  (doto document
    (.addTitle "Mayan Calendar for a Gregorian Year")
    (.addAuthor "Dianne Patterson")
    (.addCreator "Tom Hicks") ))


(defn- gen-background-image [pdf-writer]
  "Generate the background image of textured paper onto the current page"
  (let [ page-height (.getHeight PageSize/LETTER)
         page-width (.getWidth PageSize/LETTER)
         under (.getDirectContentUnder pdf-writer)
         img (Image/getInstance "resources/codexpaper.jpg") ]
    (doto img
      (.scaleToFit page-height page-width)
      (.setAlignment Element/ALIGN_CENTER)
      (.setAbsolutePosition 0 0) )
    (.saveState under)
    (.addImage under img)
    (.restoreState under) ))


(defn- gen-cover [document pdf-writer]
  "Generate the cover page"
  (gen-background-image pdf-writer)
  (let [ page-height (.getHeight PageSize/LETTER)
         page-width (.getWidth PageSize/LETTER)
         img (Image/getInstance "resources/MonthPics/cover.png")
         canvas (.getDirectContent pdf-writer) ]
    (.scaleToFit img page-height page-width)
    (.setAbsolutePosition img 0 0)
    ;; (.setAbsolutePosition img (/ (- page-height (.getScaledHeight img)) 2)
    ;;                           (/ (- page-width (.getScaledWidth img)) 2) )
    (.addImage canvas img)
    (.newPage document) ))


(defn- gen-preface [document pdf-writer]
  "Generate the preface pages"
  ; TODO - IMPLEMENT LATER
)

(defn- gen-month-image [document pdf-writer day]
  "Generate the picture page for the current month"
  (let [ page-height (.getHeight PageSize/LETTER)
         page-width (.getWidth PageSize/LETTER)
         img-filename (:image (:haab day))
         img (Image/getInstance (str "resources/MonthPics/" img-filename))
         canvas (.getDirectContent pdf-writer) ]
    (.scaleToFit img page-height page-width)
    (.setAbsolutePosition img 0 0)
    (.addImage canvas img)
    (.newPage document) ))


(defn- make-month-cell [day]
  "Create and return the month title/image cell using month info from the given day"
  (println "Generating: " (:title (:haab day)))
  (let [ para (doto (Paragraph. (:title (:haab day)) month-font)
                (.setAlignment Element/ALIGN_RIGHT)
                (.setIndentationRight month-cell-margin-right) )
         glyph-filename (:glyph (:haab day))
         month-cell (PdfPCell.) ]

    (doto month-cell
      (.setColspan number-of-columns)
      (.setBackgroundColor header-color)
      (.setHorizontalAlignment Element/ALIGN_MIDDLE)
      (.setVerticalAlignment Element/ALIGN_RIGHT)
      (.setPaddingBottom 15)
      (.setPaddingTop 15)
      (.setUseDescender true) )

    (when glyph-filename
      (when-let [glyph-img (Image/getInstance (str "resources/MonthIcons/" glyph-filename))]
        (.scalePercent glyph-img 50.0)
        (.setAlignment glyph-img Image/TEXTWRAP)
        (.add para (Chunk. glyph-img 20 -15 true)) ))

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
      (.setUseDescender true)
      (.setBackgroundColor cell-color)
      (.setHorizontalAlignment Element/ALIGN_TOP)
      (.setVerticalAlignment Element/ALIGN_RIGHT)
      (.addElement para) )))


(defn- gen-months [document pdf-writer roundcal]
  "Generate the pages for all the months"
  (doseq [month roundcal]
    (let [ day1 (first month)
           month-name (:name (:haab day1))
           skip-blanks (or (= month-name "wayeb") (= month (last roundcal))) ]
      (gen-background-image pdf-writer)
      (gen-month-image document pdf-writer day1)
      (gen-background-image pdf-writer)
      (let [table (doto (PdfPTable. number-of-columns)
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
