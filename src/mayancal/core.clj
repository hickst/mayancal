(ns mayancal.core)

(defonce tzolkin [ "Imix (Alligator)",
                   "Ik (Wind)",
                   "Akbal (House)",
                   "Kan (Lizard)",
                   "Chikchan (Snake)",
                   "Kimi (Death)",
                   "Manik (Deer)",
                   "Lamat (Rabbit)",
                   "Muluk (Water)",
                   "Ok (Dog)",
                   "Chuen (Monkey)",
                   "Eb (Grass)",
                   "Ben (Reed)",
                   "Ix (Jaguar)",
                   "Men (Eagle)",
                   "Kib (Vulture)",
                   "Kaban (Earthquake)",
                   "Etznab (Knife)",
                   "KauaK (Rain)",
                   "Ahau (Flower)" ])
(defonce tzolkin-c (drop 4 (cycle tzolkin)))


(defonce haab [ "Pop (Mat)",
                "Uo (Night Jaguar)",
                "Zip (Cloud Serpent)",
                "Zotz' (Leaf Nosed Bat)",
                "Tsek (Sky and Earth)",
                "Xul (Dog)",
                "Yaxk'in (New Sun)",
                "Mol (Water)",
                "Ch'en (Cave of the Moon)",
                "Yax (Green, New)",
                "Zak (White, Frog)",
                "Keh (Red, Red Deer)",
                "Mak (Enclosure)",
                "K'ank'in (Underworld Dog)",
                "Muan (Screech Owl)",
                "Pax (Great Puma)",
                "K'ayab (Turtle)",
                "Kumh'u (Underworld Dragon)",
                "Uayeb (Poisonous)" ])

(defonce haab-seq
  (concat
    (for [ h (butlast haab) veintena (range 20) ] [h veintena])
    (for [ veintena (range 5) ] [(last haab) veintena])))

(defonce haab-c (drop (+ 13 (* 13 20)) (cycle haab-seq)))


(defonce trecena (range 1 14))
(defonce trecena-c (drop 12 (cycle trecena)))

(defonce calround-c (map (fn [& args] args) haab-c trecena-c tzolkin-c))
