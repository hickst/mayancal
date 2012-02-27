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
(defonce tzolkin [ "Imix (Alligator)"
                   "Ik (Wind)"
                   "Akbal (House)"
                   "Kan (Lizard)"
                   "Chikchan (Snake)"
                   "Kimi (Death)"
                   "Manik (Deer)"
                   "Lamat (Rabbit)"
                   "Muluk (Water)"
                   "Ok (Dog)"
                   "Chuen (Monkey)"
                   "Eb (Grass)"
                   "Ben (Reed)"
                   "Ix (Jaguar)"
                   "Men (Eagle)"
                   "Kib (Vulture)"
                   "Kaban (Earthquake)"
                   "Etznab (Knife)"
                   "KauaK (Rain)"
                   "Ahau (Flower)" ])

;; infinite cycle of 20 named days, aligned to our arbitrary start date of 1/1/2012.
(defonce tzolkin-cyc (drop 4 (cycle tzolkin)))


;; repeating 13 day-number cycle: combined with Tzolkin to form 260 unique days.
(defonce trecena (range 1 14))

;; infinite sequence of 13 day-numbers, aligned to our arbitrary start date of 1/1/2012.
(defonce trecena-cyc (drop 12 (cycle trecena)))


;; basic cycle of Mayan solar calendar: 18 named months of 20 days each + 1 month of 5 days.
(defonce haab [ "Pop (Mat)"
                "Uo (Night Jaguar)"
                "Zip (Cloud Serpent)"
                "Zotz' (Leaf Nosed Bat)"
                "Tsek (Sky and Earth)"
                "Xul (Dog)"
                "Yaxk'in (New Sun)"
                "Mol (Water)"
                "Ch'en (Cave of the Moon)"
                "Yax (Green, New)"
                "Zak (White, Frog)"
                "Keh (Red, Red Deer)"
                "Mak (Enclosure)"
                "K'ank'in (Underworld Dog)"
                "Muan (Screech Owl)"
                "Pax (Great Puma)"
                "K'ayab (Turtle)"
                "Kumh'u (Underworld Dragon)"
                "Uayeb (Poisonous)" ])

;; map the Haab month strings to filenames containing their glyphs
(defonce haab-glyph-filenames
  (zipmap haab
          [ "pop_glyph.png"
            "uo_glyph.png"
            "zip_glyph.png"
            "zotz_glyph.png"
            "tsek_glyph.png"
            "xul_glyph.png"
            "yaxkin_glyph.png"
            "mol_glyph.png"
            "chen_glyph.png"
            "yax_glyph.png"
            "zak_glyph.png"
            "keh_glyph.png"
            "mak_glyph.png"
            "kankin_glyph.png"
            "muan_glyph.png"
            "pax_glyph.png"
            "kayab_glyph.png"
            "kumhu_glyph.png"
            "uayeb_glyph.png" ]))

;; map the Haab month strings to filenames containing their month images
(defonce haab-image-filenames
  (zipmap haab
          [ "pop.png"
            "uo.png"
            "zip.png"
            "zotz.png"
            "tsek.png"
            "xul.png"
            "yaxkin.png"
            "mol.png"
            "chen.png"
            "yax.png"
            "zak.png"
            "keh.gif"
            "mak.png"
            "kankin.png"
            "muan.png"
            "pax.png"
            "kayab.png"
            "kumhu.gif"
            "uayeb.png" ]))


;; solar calendar of 365 days: Haab months crossed with Veintena cycle.
(defonce haab-seq
  (concat
    (for [ h (butlast haab) veintena (range 20) ] (seq [h veintena]))
    (for [ veintena (range 5) ] (seq [(last haab) veintena]))))

;; infinite sequence of Haab month/day pairs.
(defonce haab-cyc (drop (+ 13 (* 13 20)) (cycle haab-seq)))


;; aligned sequences of Gregorian, Haab, Trecena, and Tzolkin cycles
(defonce calround-seq
  (map (fn [& args] args)
       gregorian-date-seq
       haab-cyc
       (map (fn [& args] args) trecena-cyc tzolkin-cyc)))

(defn roundcal-year [year]
  ;; TODO: really implement this LATER
  (partition-by (fn [val] (first (second val))) (take 366 calround-seq)))

;; accessor functions for the roundcal months (produced by the roundcal-year function)
(defn gregorian [roundcal-month] (first roundcal-month))
(defn haab-name [roundcal-month] (first (second roundcal-month)))
(defn haab-number [roundcal-month] (second (second roundcal-month)))
(defn tzolkin-number [roundcal-month] (first (nth roundcal-month 2)))
(defn tzolkin-name [roundcal-month] (second (nth roundcal-month 2)))
