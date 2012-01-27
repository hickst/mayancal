(ns mayancal.genpdf
  (:import [com.itextpdf.text Document DocumentException Element PageSize Paragraph])
  (:import [com.itextpdf.text.pdf PdfWriter]) )

(defn- gen-preface [document]
  (.add document (Paragraph. "Hello 2012!"))
)

(defn gen-months [document roundcal]
)

(defn- gen-postface [document]
  (.add document (Paragraph. "Goodbye 2012!"))
)


(defn gen-cal [roundcal outfile]
  "Top-level call to generate PDF version of Mayan Calendar"
  (let [ document (Document. (.rotate PageSize/LETTER))
         fos (java.io.FileOutputStream. (str outfile ".pdf")) ]
    (PdfWriter/getInstance document fos)
    (.setMargins document 72 72 108 72)
    (.setMarginMirroringTopBottom document true) ;for top binding
    (.open document)
    (gen-preface document)
    (gen-months document roundcal)
    (gen-postface document)
    (.close document)
))
