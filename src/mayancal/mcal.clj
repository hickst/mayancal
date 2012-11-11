;; Copyright (C) 2012 by Tohono Consulting, LLC.

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0, which can be found in the LICENSE file
;; at the root of this distribution. By using this software in any fashion,
;; you are agreeing to be bound by the terms of this license. You
;; must not remove this notice, or any other, from this software.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
;; Eclipse Public License for more details.

;; You should have received a copy of the Eclipse Public License
;; along with this program. If not, see http://opensource.org/licenses/eclipse-1.0.php.

(ns mayancal.mcal
  (:require [clojure.stacktrace :as st]))

(defn gregorian-date-seq []
  "Return an infinite sequence of Gregorian date strings starting on January 1st of 1900"
  (let [ start-date (java.util.GregorianCalendar. 1900 0 0 0 0)
         gd-format (java.text.SimpleDateFormat. "EEE M/d/yyyy") ]
    (repeatedly
      (fn []
        (.add start-date java.util.Calendar/DAY_OF_YEAR 1)
        (.format gd-format (.getTime start-date))))))


;; basic cycle of the Mayan calendar: 20 named days combine with Trecena to form 260 unique days.
(defonce tzolkin [
   { :name "imix"     :title "Imix' (Alligator)"   :glyph "imix.gif" }
   { :name "ik"       :title "Ik' (Wind)"          :glyph "ik.gif" }
   { :name "akbal"    :title "Ak'b'al (House)"     :glyph "akbal.gif" }
   { :name "kan"      :title "K'an (Lizard)"       :glyph "kan.gif" }
   { :name "chikchan" :title "Chikchan (Snake)"    :glyph "chikchan.gif" }
   { :name "kimi"     :title "Kimi (Death)"        :glyph "kimi.gif" }
   { :name "manik"    :title "Manik' (Deer)"       :glyph "manik.gif" }
   { :name "lamat"    :title "Lamat (Rabbit)"      :glyph "lamat.gif" }
   { :name "muluk"    :title "Muluk (Water)"       :glyph "muluk.gif" }
   { :name "ok"       :title "Ok (Dog)"            :glyph "ok.gif" }
   { :name "chuwen"   :title "Chuwen (Monkey)"     :glyph "chuwen.gif" }
   { :name "eb"       :title "Eb' (Grass)"         :glyph "eb.gif" }
   { :name "ben"      :title "B'en (Reed)"         :glyph "ben.gif" }
   { :name "ix"       :title "Ix (Jaguar)"         :glyph "ix.gif" }
   { :name "men"      :title "Men (Eagle)"         :glyph "men.gif" }
   { :name "kib"      :title "Kib' (Vulture)"      :glyph "kib.gif" }
   { :name "kaban"    :title "Kab'an (Earthquake)" :glyph "kaban.gif" }
   { :name "etznab"   :title "Etz'nab' (Knife)"    :glyph "etznab.gif" }
   { :name "kawak"    :title "Kawak (Rain)"        :glyph "kawak.gif" }
   { :name "ajaw"     :title "Ajaw (Flower)"       :glyph "ajaw.gif" }
  ])


;; basic cycle of Mayan solar calendar: 18 named months of 20 days each + 1 month of 5 days.
(defonce haab [
  { :name "pop"    :title "Pop (Mat)"                  :glyph "pop_glyph.png"    :image "pop.png" }
  { :name "wo"     :title "Wo (Night Jaguar)"          :glyph "uo_glyph.png"     :image "uo.png" }
  { :name "sip"    :title "Sip (Cloud Serpent)"        :glyph "zip_glyph.png"    :image "zip.png" }
  { :name "sotz"   :title "Sotz' (Leaf Nosed Bat)"     :glyph "zotz_glyph.png"   :image "zotz.png" }
  { :name "sek"    :title "Sek (Sky and Earth)"        :glyph "tsek_glyph.png"   :image "tsek.png" }
  { :name "xul"    :title "Xul (Dog)"                  :glyph "xul_glyph.png"    :image "xul.png" }
  { :name "yaxkin" :title "Yaxk'in (New Sun)"          :glyph "yaxkin_glyph.png" :image "yaxkin.png" }
  { :name "mol"    :title "Mol (Water)"                :glyph "mol_glyph.png"    :image "mol.png" }
  { :name "chen"   :title "Ch'en (Cave of the Moon)"   :glyph "chen_glyph.png"   :image "chen.png" }
  { :name "yax"    :title "Yax (Green, New)"           :glyph "yax_glyph.png"    :image "yax.png" }
  { :name "sak"    :title "Sak (White, Frog)"          :glyph "zak_glyph.png"    :image "zak.png" }
  { :name "keh"    :title "Keh (Red, Red Deer)"        :glyph "keh_glyph.png"    :image "keh.gif" }
  { :name "mak"    :title "Mak (Enclosure)"            :glyph "mak_glyph.png"    :image "mak.png" }
  { :name "kankin" :title "K'ank'in (Underworld Dog)"  :glyph "kankin_glyph.png" :image "kankin.png" }
  { :name "muwan"  :title "Muwan (Screech Owl)"        :glyph "muan_glyph.png"   :image "muan.png" }
  { :name "pax"    :title "Pax (Great Puma)"           :glyph "pax_glyph.png"    :image "pax.png" }
  { :name "kayab"  :title "K'ayab (Turtle)"            :glyph "kayab_glyph.png"  :image "kayab.png" }
  { :name "kumku"  :title "Kumk'u (Underworld Dragon)" :glyph "kumhu_glyph.png"  :image "kumhu.gif" }
  { :name "wayeb"  :title "Wayeb (Poisonous)"          :glyph "uayeb_glyph.png"  :image "uayeb.png" } ])


;; solar calendar of 365 days: Haab months crossed with Veintena cycle of 20 days.
(defonce haab-seq
  (concat
    (mapcat (partial repeat 20) (butlast haab))
    (repeat 5 (last haab))))

(defonce haab-number-seq
  (concat (reduce concat [] (repeat 18 (range 20))) (range 5)))


(defn days-between-1900-and-year [year]
  "Return the number of days between 1/1/1900 and 1/1/year"
  (let [ start-millis (.getTimeInMillis (java.util.GregorianCalendar. 1900 0 0 0 0))
         end-millis (.getTimeInMillis (java.util.GregorianCalendar. year 0 0 0 0)) ]
    (/ (- end-millis start-millis) 86400000) ; 24 hours * 60 min * 60 sec * 1000 ms
))

(defn find-days-in-year [year]
  "Return the number of days in the given year"
  (if (.isLeapYear (java.util.GregorianCalendar.) year) 366 365))

(def transpose
  "Transpose a finite sequence of infinite sequences into an infinite sequence of finite sequences"
  (partial apply map vector))


;; create aligned sequences of Gregorian, Haab, Trecena, and Tzolkin cycles
(defn make-calround-cycle [options]
  "Return an infinite lazy sequence of tuples of Gregorian, Haab, Trecena, and Tzolkin day info"
  (let [ days-since-1900 (days-between-1900-and-year (:year options))
         greg-seq (gregorian-date-seq)                      ; starts at 1/1/1900
         haab-cyc (drop 246 (cycle haab-seq))               ; sync cycle to 1/1/1900
         haab-number-cyc (drop 246 (cycle haab-number-seq)) ; sync cycle to 1/1/1900
         tzolkin-cyc (drop 17 (cycle tzolkin))              ; sync cycle to 1/1/1900
         trecena-cyc (drop 3 (cycle (range 1 14))) ]        ; sync cycle to 1/1/1900
    (transpose (map #(drop days-since-1900 %)
                    [greg-seq haab-cyc haab-number-cyc trecena-cyc tzolkin-cyc])) ))


;; main method to create and return the round calendar
(defn roundcal-year [options]
  "Create and return a round calendar sequence for the specified year"
  (let [ days-in-year (find-days-in-year (:year options)) ]
    (partition-by #(:haab %)
                  (map #(apply hash-map
                               (interleave [:gregorian :haab :haab-number :trecena :tzolkin] %))
                       (take days-in-year (make-calround-cycle options))))
))
