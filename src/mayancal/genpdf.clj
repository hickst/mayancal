(ns mayancal.genpdf
  (:require [mayancal.mcal :as mcal
             :only (gregorian haab-name haab-number tzolkin-number tzolkin-name)])
  (:import [com.itextpdf.text BaseColor Chunk Document DocumentException Element
                              Font Font$FontFamily PageSize Paragraph])
  (:import [com.itextpdf.text.pdf PdfContentByte PdfPCell PdfPTable PdfWriter]) )

;; various color definitions:
(defonce cell-color (BaseColor. 252 255 224))
(defonce gregor-color (BaseColor. 0 127 0))
(defonce header-color (BaseColor. 233 255 223))
(defonce tzday-color (BaseColor. 0 0 127))

;; various font definitions
(defonce month-font (Font. Font$FontFamily/HELVETICA (.floatValue 18.0) Font/BOLD))
(defonce tzday-font (Font. Font$FontFamily/HELVETICA (.floatValue 10.0) Font/NORMAL tzday-color))
(defonce tzindex-font (Font. Font$FontFamily/HELVETICA (.floatValue 12.0) Font/BOLD))
(defonce gregor-font (Font. Font$FontFamily/HELVETICA (.floatValue 10.0) Font/NORMAL gregor-color))


(defn- add-metadata [document]
  (doto document
    (.addTitle "Grego-Mayan Calendar")
    (.addAuthor "Tom Hicks")
    (.addCreator "Dianne Patterson")))

(defn- gen-preface [document]
  (.add document (Paragraph. "Hello 2012!"))
  (.newPage document)
)

(defn- gen-month-image [canvas month-name]
)

(defn- make-month-cell [month-name]
  (println "MONTH_NAME: " month-name)       ; REMOVE LATER
  (let [ para (doto (Paragraph. month-name month-font)
                (.setAlignment Element/ALIGN_RIGHT)
                (.setIndentationRight (.floatValue 50.0))) ]
    (doto (PdfPCell.)
      (.setColspan 5)
      (.setBackgroundColor header-color)
      (.setUseDescender true)
      (.addElement para))))

(defn- make-blank-cell []
  (doto (PdfPCell.)
    (.setUseDescender true)
    (.setBackgroundColor BaseColor/WHITE)
    (.addElement Chunk/NEWLINE)
    (.addElement Chunk/NEWLINE)))

(defn- make-day-cell [day]
  (let [ tz-lbl (str (mcal/tzolkin-number day) "-" (mcal/tzolkin-name day) "  ")
         gregor-lbl (mcal/gregorian day)
         phrase (doto (Paragraph.)
                  (.setAlignment Element/ALIGN_RIGHT)
                  (.add (Chunk. tz-lbl tzday-font))
                  (.add (Chunk. "18" tzindex-font))
                  (.add Chunk/NEWLINE)
                  (.add (Chunk. gregor-lbl gregor-font))) ]
    (doto (PdfPCell.)
      (.setUseDescender true)
      (.setBackgroundColor cell-color)
      (.setHorizontalAlignment Element/ALIGN_TOP)
      (.setVerticalAlignment Element/ALIGN_RIGHT)
      (.addElement phrase))))



(defn- gen-months [document pdf-writer roundcal]
  (let [canvas (.getDirectContent pdf-writer)]
    (doseq [month roundcal]
      (let [ first-day (first month)
             month-name (mcal/haab-name first-day) ]
        (gen-month-image canvas month-name)
        (let [table (PdfPTable. 5)]         ; table with 5 columns
          ; (.setWidthPercentage table (.floatValue 70.0))
          (.setTotalWidth table (.floatValue 600.0))
          (.setBackgroundColor (.getDefaultCell table) cell-color)
          (.addCell table (make-month-cell month-name))
          (dotimes [blank-cells (- 20 (count month))]
            (.addCell table (make-blank-cell)))
          (doseq [day month]
            (.addCell table (make-day-cell day)))
          (.completeRow table)
          (.writeSelectedRows table 0 -1
                              (.floatValue 100.0) (.floatValue (+ 400.0 (.getTotalHeight table)))
                              canvas)
        )
        (.newPage document)
   ))
))

(defn- gen-postface [document]
  (.add document (Paragraph. "Goodbye 2012!"))
)


(defn gen-cal [roundcal outfile]
  "Top-level call to generate PDF version of Mayan Calendar"
  (let [ document (Document. (.rotate PageSize/LETTER))
         fos (java.io.FileOutputStream. (str outfile ".pdf"))
         pdf-writer (PdfWriter/getInstance document fos) ]
    (.setMargins document 72 72 108 72)
    (.setMarginMirroringTopBottom document true) ;for top binding
    (.open document)
    (add-metadata document)
    (gen-preface document)
    (gen-months document pdf-writer roundcal)
    (gen-postface document)
    (.close document)
))
