(ns embellir.curios.cjdnspeers
  (:require  [cjdnsadmin-clj.core :as cjd :only [request]])
  )

;{:name "weather" 
; :atom (get-weather)
; :time-to-live (* 1000 60 30) ; half an hour
; :function update-weather }
; 
; using the National Digital Forecast Database
; http://graphical.weather.gov/xml/
;

(defn get-cjdns-peers [] 
  (get-in (first (cjd/request "InterfaceController_peerStats")) ["peers"])) 

(defn setup-cjdns-peers
  []
  (get-cjdns-peers))

(defn update-cjdns-peers
  [wdata] 
 ; (merge wdata (get-cjdns-peers))
  (get-cjdns-peers)
  )

(defn curation-map [] {:atom (atom (embellir.curios.cjdnspeers/setup-cjdns-peers))
                       :function embellir.curios.cjdnspeers/update-cjdns-peers
                       :time-to-live (* 1000 2) ;every few seconds
                       :receiver-function nil})


(comment
(get (json/read-str (slurp cjdns_conf)) "addr")

)
