(ns mayancal.genpdf
  (:import [com.itextpdf.text Document DocumentException Element PageSize Paragraph])
  (:import [com.itextpdf.text.pdf PdfWriter]) )

(defn genCal []
  "Top-level call to generate PDF version of Mayan Calendar"
  (let [ document (Document. (.rotate PageSize/LETTER))
         fos (java.io.FileOutputStream. "/tmp/firstPdf.pdf") ]
    (PdfWriter/getInstance document fos)
    (.setMargins document 72 72 108 72)
    (.setMarginMirroringTopBottom document true) ;for top binding
    (.open document)
    (.add document (Paragraph. "Hello 2012!"))
    (.close document)))
