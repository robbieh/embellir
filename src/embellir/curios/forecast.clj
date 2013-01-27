(ns embellir.curios.forecast
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
; 
; using the National Digital Forecast Database
; http://graphical.weather.gov/xml/
;

(def nws-rss)

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



