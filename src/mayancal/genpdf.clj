(ns mayancal.genpdf
  (:import [com.itextpdf.text BaseColor Document DocumentException Element PageSize Paragraph])
  (:import [com.itextpdf.text.pdf PdfContentByte PdfPCell PdfPTable PdfWriter]) )

(defonce cell-color (BaseColor. 252 255 224))
(defonce header-color (BaseColor. 233 255 223))

(defn- gen-preface [document]
  (.add document (Paragraph. "Hello 2012!"))
  (.newPage document)
)

(defn- gen-month-image [canvas month-name]
)

(defn- make-month-cell [month-name]
  (println "MONTH_NAME: " month-name)       ; REMOVE LATER
  (let [para (doto (Paragraph. month-name) (.setAlignment Element/ALIGN_RIGHT))]
    (doto (PdfPCell.)
      (.setColspan 5)
      (.setBackgroundColor header-color)
      (.setUseDescender true)
      (.addElement para))))


(defn- gen-months [document pdf-writer roundcal]
  (let [canvas (.getDirectContent pdf-writer)]
    (doseq [month roundcal]
      (let [month-name (first (second (first month)))] ; LATER: month-name fn
        (gen-month-image canvas month-name)
        (let [table (PdfPTable. 5)]         ; table with 5 columns
          ; (.setWidthPercentage table (.floatValue 70.0))
          (.setTotalWidth table (.floatValue 600.0))
          (.setBackgroundColor (.getDefaultCell table) cell-color)
          (.addCell table (make-month-cell month-name))
          (dotimes [n 20] (.addCell table (str "cell" n))) ; REPLACE LATER
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
    (gen-preface document)
    (gen-months document pdf-writer roundcal)
    (gen-postface document)
    (.close document)
))
