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

(ns mayancal.core
  (:require [clojure.tools.cli :as cli])
  (:require [mayancal.mcal :as mcal])
  (:require [mayancal.genpdf :as pdf])
  (:gen-class)
)

(defn -main [ & args]
  (let [ usage "Usage: java -jar mayancal.jar [--year year] -bn basename"
         [options other-args flag-usage]
           (cli/cli args
             ["-bn" "--basename"   "The basename (without extension) for the output PDF file"]
             ["-y"  "--year"       "Gregorian year for calendar (> 1900)"
                                   :parse-fn #(Integer. %) :default 2012]
             ["-h"  "--help"       "Show usage message for this program" :flag true]
             ["-v"  "--verbose"    "Operate in verbose mode" :flag true] )]

    ;; if user asks for help, print usage messages and exit
    (if (:h options)
      (do
        (println usage)
        (println flag-usage)
        (System/exit 1)))

    ;; check for missing required output file basename argument
    (if (not (:basename options))
      (do
        (println "ERROR: Required output file basename argument is missing.")
        (println usage)
        (println flag-usage)
        (System/exit 2)))


    ;; generate a round calendar for the desired year and pass it to the PDF formatter
    (let [ roundcal (mcal/roundcal-year options) ]
        (pdf/gen-cal roundcal (:basename options)))
))

(comment
  ;; Generate calendar for 2012 in /tmp/mcal.pdf:
  (ns mayancal.core)
  (load "core")
  (-main "-bn" "/tmp/mcal")
)
