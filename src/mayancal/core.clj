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
(defonce tzolkin-c (cycle tzolkin))


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
  (concat (flatten (map #(repeat 20 %) (butlast haab)))
          (repeat 5 (last haab))))
(defonce haab-c (cycle haab-seq))


(defonce trecena (range 1 14))
(defonce trecena-c (cycle trecena))


(defonce veintena (range 20))
(defonce veintena-c (cycle (range 20)))
