(ns embellir.curios.iplist

  )

;{:name "weather" 
; :atom (get-weather)
; :time-to-live (* 1000 60 30) ; half an hour
; :function update-weather }
; 
; using the National Digital Forecast Database
; http://graphical.weather.gov/xml/
;

 


(defn update-iplist
  [wdata] 
  wdata
  )
(defn receive-iplist
  [kdata rmap] 
  (merge kdata rmap)
  )

(defn curation-map [] {:atom (atom   {"0.0.0.0" {}
                                     "8.8.8.8" {}
                                     "255.255.255.255" {}
                                     "0.0.255.255" {}
                                     "255.255.0.0" {}
                                     "207.69.188.185" {}
                                      }
                                   )
                       :function embellir.curios.iplist/update-iplist
                       :time-to-live (* 1000 20) ;every few seconds
                       :receiver-function embellir.curios.iplist/receive-iplist})


