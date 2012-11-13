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

(ns mayancal.content)

(defonce background
  { :title "Calendar Background"
    :clauses [
      "A printed Mayan calendar, very similar to this one, was manually assembled by Dr. Dianne Patterson in 2004. The Mayan Calendar Generator program, which produced this calendar, is the creation of Tom Hicks, of Tohono Consulting LLC, and Dr. Patterson. The program was implemented using the Clojure programming language (http://clojure.org)."
]})


(defonce calintro
  { :title "The Mayan Calendar "
    :links [ "http://www.michielb.nl/maya/calendar.html"
             "http://edj.net/mc2012/fap4.html"
             "http://en.wikipedia.org/wiki/Maya_calendar"
             "http://www.tondering.dk/claus/cal/maya.php"
           ]
    :clauses [
      "The Maya developed a sophisticated calendar based on the intersection of various cycles, especially the 260 day Tzolkin (or ritual calendar) and the 365 day Haab (a rough solar calendar also known as the \"cycle of rains\" calendar)."

      "The Tzolkin named each day; like our days of the week. There were 20 day names, each represented by a unique symbol. The days were also numbered from 1 to 13. Since there were 20 day names, after the count of thirteen was reached the next day was numbered 1 again. Since 13 and 20 have no common divisors, this system uniquely represents all 260 (13*20) days of the sacred year with a unique number and day-name combination. So why was the 260 day cycle so important? First, it approximates the 9-month gestation period of human beings, an obviously important period of growth and development. It also corresponds to the interval between Venus emerging as an evening star and its emergence as a morning star (about 258 days), as well as the interval between the planting and harvesting of certain types of crops. [1]"

      "The Haab was a rough solar year of 365 days. The Haab year contained named months called Uinals. These were 18 regular months of 20 days each and one special five-day month called Uayeb. The 5 days of Uayeb were considered unnamed and unlucky and this period was thought to be a dangerous time. Days of the Haab months were numbered 0 to 19. Each day had a number and day name from the 260-day Tzolkin as well as a number for each day of the Haab month. Using the intersections of these cycles, each day can be identified by a four item list: [Tzolkin number, Tzolkin day, Haab number, Haab month]. Day counting cycles through the items, as in the following example: \"One Imix, Zero Pop\", \"Two Ik, One Pop\", \"Three Ak'b'al Two Pop\". The thirteenth day was \"Thirteen B'en, Twelve Pop\" and the next day was \"One Ix, Thirteen Pop\", followed by \"Two Men, Fourteen Pop\". After \"Seven Ajaw, Nineteen Pop\", the next day was \"Eight Imix, Zero Uo\"."

      "Year Bearers: The quality of a year is determined by the day-sign which falls on New Years Day - which is the first day of the Haab. This special day is called the Year Bearer. Since the twenty day-signs divide into the 365-day Haab 18 times with 5 left over, the Year Bearer advances by 5 day-signs every year. Furthermore, five goes into 20 four times; thus there are four possible Year Bearers. They correspond to the four directions and, for the Quiche Maya, the four sacred mountains. In this way, the Calendar's \"windows to the New Year\" are anchored in the directional pillars of the cosmos. [2]  In addition, the four years headed by the Year Bearers are named after them and share their characteristics; therefore, they also have their own prognostications and patron deities. [3]"
]})


(defonce longcnt
  { :title "The Long Count"
    :links [ "http://www.michielb.nl/maya/calendar.html"
             "http://en.wikipedia.org/wiki/Maya_calendar"
    ]
    :clauses [
      "The Maya used special glyphs to indicate various periods of time. The Kin represented one day. A period of 20 days made up a Uinal, a single \"month\" in the Haab cycle. The Tun was a year of 360 days and the K'atun was 20 years of 360 days each. The end of the K'atun was a special time period celebrated by the Maya. The Maya also counted 400 year periods called B'ak'tuns. [1]"

      "All of the aforementioned time periods were used by the Maya in a special day numbering system which is now called the Long Count. Typically, a Long Count date is written as a conjunction of these named periods. For example, the Long Count 9.14.12.2.17 represents 9 B'ak'tuns, 14 K'atuns, 12 Tuns, 2 Uinals and 17 K'ins. [1]  The Long Count calendar identifies a date by counting the number of days from the Mayan creation date 4 Ajaw, 8 Kumk'u. The number of days since the Mayan creation date is found by multiplying each position by its base (see the table below). Thus, the Long Count date 0.0.0.1.5 is day 25, the Long Count date 0.0.0.2.0 is day 40, and the Long Count date 0.0.1.0.0 is day 360 (as there are only 18 Uinal in a Tun)."

      "One Great Cycle, which began in mid 3114 B.C., ends (or ended, if you're still alive to read this in the future) on December 21st, 2012 A.D. Despite some apocalyptic hype, this is merely the end of the current 13-B'ak'tun cycle and the beginning of the next one."
    ]
    :table {
      :title "Time Periods of the Long Count"
      :rows [
             [ "Kin" "" "1 day" "" ]
             [ "Uinal" "20 Kin" "20 days" "" ]
             [ "Tun" "18 Uinal" "360 days" "~1 year" ]
             [ "K'atun" "20 Tuns" "7200 days" "~20 (19.7) years" ]
             [ "B'ak'tun" "20 Katuns" "144,000 days" "~400 (394.3) years" ]
             [ "Great Cycle" "13 B'ak'tuns" "1,872,000 days" "~5100 (5125.3) years" ]
      ]}
})


(defonce license-text
  { :title "Software Licenses"
    :clauses [
      "The Mayan Calendar Generator program (hereafter 'MCGp') is copyright (C) 2012 by Tohono Consulting, LLC. The use and distribution terms for the MCGp are covered by the Eclipse Public License 1.0 (see http://opensource.org/licenses/eclipse-1.0.php or the 'resources/LICENSE' file in the MCGp source code distribution). By using the MCGp in any fashion, you are agreeing to be bound by the terms of this license."

      "The MCGp, and all its generated outputs, are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Eclipse Public License for more details."

      "The MCGp software uses libraries copyrighted and licensed by the iText Software Corp (http://itextpdf.com/contact.php). iText software is licensed under the GNU Affero General Public License (AGPL). A copy of the AGPL can be found in the 'resources/AGP-LICENSE' file of the MCGp source code distribution."
]})

(defonce acknowledgements
  { :title "Image Acknowledgements"
    :entries [
      {:name "FRONT COVER"
       :url "http://commons.wikimedia.org/wiki/File:Mayan_Zodiac_Circle.jpg" }
      {:name "PAX (GREAT PUMA)"
       :url "http://commons.wikimedia.org/wiki/File:Puma_Sleeping.jpg"}
      {:name "POP (CHIEF)"
       :url "http://commons.wikimedia.org/wiki/File:Mayan_people_and_chocolate.jpg"}
      {:name "ZIP (CLOUD SERPENT)"
       :url "http://commons.wikimedia.org/wiki/File:Mixcoatl_telleriano-remensis.jpg"}
      {:name "TSEK (SKY & EARTH)"
       :url "http://commons.wikimedia.org/wiki/File:Tulum_-_Mayan_Pyramid.jpg"}
      {:name "XUL (DOG)"
       :url "http://commons.wikimedia.org/wiki/File:Xolotl_1.jpg"}
      {:name "YAXKIN (FIRST SUN)"
       :url "http://commons.wikimedia.org/wiki/File:MayanSunGodEffigyClip.jpg"}
      {:name "MOL (WATER)"
       :url "http://commons.wikimedia.org/wiki/File:Mexico_Cenotes.jpg"}
      {:name "ZAC (FROG)"
       :url "http://commons.wikimedia.org/wiki/File:Dendrobates_pumilio.jpg"}
      {:name "MAC (ENCLOSURE)"
       :url "http://commons.wikimedia.org/wiki/File:Jaguar_vase.jpg"}
]})
