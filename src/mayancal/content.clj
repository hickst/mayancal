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
      {:name "Front Cover"
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
