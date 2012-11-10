(defproject mayancal "0.0.28"
  :description "Mayan calendar generator"
  :dependencies [ [org.clojure/clojure "1.4.0"]
                  [org.clojure/tools.cli "0.2.1"]
                  [com.itextpdf/itextpdf "5.1.3"]
                  [com.itextpdf.tool/xmlworker "1.1.1"] ]
  :main mayancal.core
  :uberjar-name "mayancal.jar"
)
