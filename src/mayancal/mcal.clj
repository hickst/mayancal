(ns mayancal.mcal)

;; infinite sequence of Gregorian Dates starting on Jan 1 2012
(defonce gregorian-date-seq
  (let [ start-date (java.util.GregorianCalendar. 2012 0 0 0 0)
         gd-format (java.text.SimpleDateFormat. "EEE M/d/yyyy") ]
    (repeatedly
      (fn []
        (.add start-date java.util.Calendar/DAY_OF_YEAR 1)
        (.format gd-format (.getTime start-date))))))


;; basic cycle of the Mayan calendar: 20 named days combine with Trecena to form 260 unique days.
(defonce tzolkin
  [
   { :name "imix"     :title "Imix' (Alligator)"   :glyph "YYY" }
   { :name "ik"       :title "Ik' (Wind)"          :glyph "YYY" }
   { :name "akbal"    :title "Ak'b'al (House)"     :glyph "YYY" }
   { :name "kan"      :title "K'an (Lizard)"       :glyph "YYY" }
   { :name "chikchan" :title "Chikchan (Snake)"    :glyph "YYY" }
   { :name "kimi"     :title "Kimi (Death)"        :glyph "YYY" }
   { :name "manik"    :title "Manik' (Deer)"       :glyph "YYY" }
   { :name "lamat"    :title "Lamat (Rabbit)"      :glyph "YYY" }
   { :name "muluk"    :title "Muluk (Water)"       :glyph "YYY" }
   { :name "ok"       :title "Ok (Dog)"            :glyph "YYY" }
   { :name "chuwen"   :title "Chuwen (Monkey)"     :glyph "YYY" }
   { :name "eb"       :title "Eb' (Grass)"         :glyph "YYY" }
   { :name "ben"      :title "B'en (Reed)"         :glyph "YYY" }
   { :name "ix"       :title "Ix (Jaguar)"         :glyph "YYY" }
   { :name "men"      :title "Men (Eagle)"         :glyph "YYY" }
   { :name "kib"      :title "Kib' (Vulture)"      :glyph "YYY" }
   { :name "kaban"    :title "Kab'an (Earthquake)" :glyph "YYY" }
   { :name "etznab"   :title "Etz'nab' (Knife)"    :glyph "YYY" }
   { :name "kawak"    :title "Kawak (Rain)"        :glyph "YYY" }
   { :name "ajaw"     :title "Ajaw (Flower)"       :glyph "YYY" }
  ]
)

;; given an annual cycle of days or day numbers, offset by the given number of days.
(defn offset-into-cycle [units cycle] (drop units cycle))

;; Tzolkin: infinite cycle of 20 named days.
;; aligned to our arbitrary start date of 1/1/2012.
(defonce tzolkin-cyc (offset-into-cycle 4 (cycle tzolkin)))

;; Trecena: infinite cycle of 13 day-numbers: combined with Tzolkin to form 260 unique days.
;; aligned to our arbitrary start date of 1/1/2012.
(defonce trecena-cyc (offset-into-cycle 12 (cycle (range 1 14))))


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
  { :name "wayeb"  :title "Wayeb (Poisonous)"          :glyph "uayeb_glyph.png"  :image "uayeb.png" }
  ]
)

(defn find-haab [name]
  (keep-indexed #(if (= name (:name %2)) [%1 %2]) haab))


;; solar calendar of 365 days: Haab months crossed with Veintena cycle of 20 days.
(defonce haab-seq
  (concat
    (mapcat (partial repeat 20) (butlast haab))
    (repeat 5 (last haab))))

(defonce haab-number-seq
  (concat (reduce concat [] (repeat 18 (range 20))) (range 5)))


;; begin the given annual cycle of days, offset by the given number of months and days
(defn offset-into-haab-cycle [months days cycle]
  (drop (+ days (* months 20)) cycle))


;; infinite sequence of Haab month names
(defonce haab-cyc (offset-into-haab-cycle 13 13 (cycle haab-seq)))

;; infinite sequence of Haab day numbers
(defonce haab-number-cyc (offset-into-haab-cycle 13 13 (cycle haab-number-seq)))


;; aligned sequences of Gregorian, Haab, Trecena, and Tzolkin cycles
(defonce calround-cyc
  (map (fn [& args] args)
       gregorian-date-seq haab-cyc haab-number-cyc trecena-cyc tzolkin-cyc))


(defn roundcal-year [year]
  ;; TODO: really implement this for each year LATER
  (partition-by #(:haab %)
                (map #(apply hash-map
                             (interleave [:gregorian :haab :haab-number :trecena :tzolkin] %))
                     (take 366 calround-cyc))))
