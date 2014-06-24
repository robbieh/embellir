(ns embellir.curios.weather
  (:gen-class)
  (:require  [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zxml]
            )
  )

;{:name "weather" 
; :atom (get-weather)
; :time-to-live (* 1000 60 30) ; half an hour
; :function update-weather }

(def nws-rss)
(def radar-url "http://radar.weather.gov/RadarImg/N0R/FFC_N0R_0.gif")

(defn get-weather
  []
  (into {} (for [ c (zxml/xml-> 
                      (->> @nws-rss io/input-stream xml/parse zip/xml-zip) 
                      clojure.zip/children)] 
             [(:tag c) (first (:content c))]))) 

(defn setup-weather
  []
  (def nws-rss (atom "http://w1.weather.gov/xml/current_obs/KPDK.xml"))
  (get-weather))

(defn update-weather
  [wdata] 
  (merge wdata (get-weather)))

(defn receive-weather
  [wdata rmap]
  (merge wdata rmap))

(defn curation-map [] {:atom (atom (embellir.curios.weather/setup-weather))
                       :function embellir.curios.weather/update-weather
                       :time-to-live (* 1000 60 30)
                       :receiver-function embellir.curios.weather/receive-weather})



; get-weather returns a map such as this:
; {:suggested_pickup "15 minutes after the hour",
;   :two_day_history_url
;   "http://www.weather.gov/data/obhistory/KPDK.html",
;   :longitude "-84.3",
;   :wind_string "Calm",
;   :relative_humidity "100",
;   :privacy_policy_url "http://weather.gov/notice.html",
;   :credit "NOAA's National Weather Service",
;   :pressure_in "29.96",
;   :icon_url_name "ra1.png",
;   :dewpoint_string "50.0 F (10.0 C)",
;   :credit_URL "http://weather.gov/",
;   :observation_time_rfc822 "Tue, 01 Jan 2013 17:53:00 -0500",
;   :location "Atlanta, DeKalb-Peachtree Airport, GA",
;   :image
;   {:tag :url,
;      :attrs nil,
;      :content ["http://weather.gov/images/xml_logo.gif"]},
;   :temperature_string "50.0 F (10.0 C)",
;   :latitude "33.88",
;   :wind_dir "North",
;   :observation_time "Last Updated on Jan 1 2013, 5:53 pm EST",
;   :wind_kt "0",
;   :visibility_mi "3.00",
;   :wind_mph "0.0",
;   :ob_url "http://www.weather.gov/data/METAR/KPDK.1.txt",
;   :station_id "KPDK",
;   :temp_f "50.0",
;   :dewpoint_c "10.0",
;   :temp_c "10.0",
;   :pressure_string "1015.1 mb",
;   :dewpoint_f "50.0",
;   :disclaimer_url "http://weather.gov/disclaimer.html",
;   :copyright_url "http://weather.gov/disclaimer.html",
;   :weather "Light Rain Fog/Mist",
;   :suggested_pickup_period "60",
;   :pressure_mb "1015.1",
;   :icon_url_base "http://forecast.weather.gov/images/wtf/small/",
;   :wind_degrees "0"}
; 
