(ns embellir.curios.forecast
  (:gen-class)
  (:use [weathergov-hourly-forecast.core :only [get-forecast-table pivot-data]])
  )


(defn get-forecast []
  (let [location (:location (read-string (slurp (clojure.java.io/file (System/getProperty "user.home") ".embellir.rc"))))]
    (if (nil? location)
      (println "Please add {:location {:lat <latitude> :lon <longitude>} to ~/.embellir.rc to get forecasts for your location")
      (pivot-data (get-forecast-table (:lat location) (:lon location)))))) 

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
  (embellir.curator/run-item "forecast")
  
  ;visually comparing this to output from website...
  ;returns date and temp
  (let [c (embellir.curator/get-curio "forecast")
        f (fn [k] 
            [k (:TemperatureF (get c  k))]     
            )]
    (map f (keys c)))


    )
