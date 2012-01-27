(ns mayancal.core
  (:require [clojure.tools.cli :as cli])
  (:require [mayancal.mcal :as mcal])
  (:require [mayancal.genpdf :as pdf])
  (:gen-class)
)

(defn -main [ & args]
  (let [ usage "Usage: java -jar mayancal.jar -o out-pdf-file [year]"
         [options other-args flag-usage]
           (cli/cli args
             ["-o" "The basename (without extension) for the output PDF file"]) ]

    ;; if user asks for help, print usage messages and exit
    (if (:h options)
      (do
        (println usage)
        (println flag-usage)
        (System/exit 1)))

    ;; check for any missing arguments
    (if (not (:o options))
      (do
        (println "ERROR: Required output file basename argument is missing.")
        (println usage)
        (println flag-usage)
        (System/exit 2)))

    ;; generate a round calendar the desired year and pass it to the PDF formatter
    (let [ year (or (first other-args) "2012")
           roundcal (mcal/roundcal-for-year year) ]
        (pdf/gen-cal roundcal (:o options)))
))

(comment
  (ns mayancal.core)
  (load "core")
  (-main "-o" "out-file-basename")
)
