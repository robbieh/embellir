(ns embellir.curios.iplist
;     [clj-time.core :as clj-time]
     (:require [clj-time.local]) 
  )

;{:name "weather" 
; :atom (get-weather)
; :time-to-live (* 1000 60 30) ; half an hour
; :function update-weather }
; 
; using the National Digital Forecast Database
; http://graphical.weather.gov/xml/
;

(defn update-values  [m f & args]
   (reduce  (fn  [r  [k v]]  (assoc r k  (apply f v args)))  {} m)) 

(defn get-count [ip m]
  (get ip m)
  )

(defn update-iplist
  [ipdata] 
  ipdata
  )

(defn receive-iplist
  [ipdata rmap] 
  (let [now (clj-time.local/local-now)
        dmap (update-values rmap conj {:last-seen now})
        dmap (into {} (for [[k v] dmap] 
                        (let [x (get-in ipdata [k :count] 0 )]
                           [k (assoc v :count (inc x))])
                        ))
        ]
    
    
    (merge ipdata dmap))
  )

(defn curation-map [] {:atom (atom   {})
                       :function embellir.curios.iplist/update-iplist
                       :time-to-live (* 1000 20) ;every few seconds
                       :receiver-function embellir.curios.iplist/receive-iplist})

(comment
  (embellir.curator/trash-curio "iplist")
  (embellir.curator/get-curio "iplist")
  (embellir.curator/curate "iplist")
  )
