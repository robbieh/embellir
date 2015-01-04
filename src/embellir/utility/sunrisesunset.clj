(ns embellir.utility.sunrisesunset
    (:import 
            [com.luckycatlabs.sunrisesunset.dto Location]
            [com.luckycatlabs.sunrisesunset SunriseSunsetCalculator]
            java.util.Calendar))


(defn get-sscalculator 
  "Returns a SunriseSunsetCalculator for lat, lon, and timezone
  example:
  (get-sscalculator \"33.5\" \"-75.2\" \"America/New_York\")
  Leave out options to get default location and tz."
  ([lat lon tz]
    (new SunriseSunsetCalculator  (new Location (str lat) (str lon)) tz))
  ([]
   (let [location (:location (read-string (slurp (clojure.java.io/file (System/getProperty "user.home") ".embellir.rc"))))
         timezone (.getID (.getTimeZone (Calendar/getInstance)))
         ]
     (get-sscalculator (:lat location) (:lon location) timezone)
     
     )
   )
  )

(defn get-sun-rise-set-today [sscalculator]
  [(.getOfficialSunriseForDate sscalculator  (Calendar/getInstance)) (.getOfficialSunsetForDate sscalculator (Calendar/getInstance))])

;(get-sun-rise-set (get-sscalculator))
