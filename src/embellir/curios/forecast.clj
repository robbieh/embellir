(ns embellir.curios.forecast
  (:gen-class)
  (:use [weathergov-hourly-forecast.core :only [get-forecast-table pivot-data]])
  )


(defn get-forecast
  []
  (pivot-data (get-forecast-table 33.82 -84.36))) 

(defn setup-forecast
  []
  (get-forecast))

(defn update-forecast
  [wdata] 
  (merge wdata (get-forecast)))

(defn curation-map [] {:atom (atom (embellir.curios.forecast/setup-forecast))
                       :function embellir.curios.forecast/update-forecast
                       :time-to-live (* 1000 60 60)
                       :receiver-function nil})


(comment
  (embellir.curator/curate "forecast")
  (embellir.curator/get-curio "forecast")
  )
