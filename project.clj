(defproject mayancal "1.0.0"
  :description "Mayan calendar generator"
  :dependencies [ [org.clojure/clojure "1.4.0"]
                  [org.clojure/tools.cli "0.2.2"]
                  [com.itextpdf/itextpdf "5.3.4"]
                  [com.itextpdf.tool/xmlworker "1.2.1"] ]
  :main mayancal.core
  :uberjar-name "mayancal.jar"
)
