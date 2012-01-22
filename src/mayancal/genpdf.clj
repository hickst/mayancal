(ns mayancal.genpdf
  (:import [com.itextpdf.text Document DocumentException Paragraph])
  (:import [com.itextpdf.text.pdf PdfWriter]) )

(defn genCal []
  "Top-level call to generate PDF version of Mayan Calendar"
  (let [ document (Document.)
        fos (java.io.FileOutputStream. "/tmp/firstPdf.pdf") ]
    (PdfWriter/getInstance document fos)
    (.open document)
    (.add document (Paragraph. "Hello 2012!"))
    (.close document)))
