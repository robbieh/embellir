(ns embellir.curios.ufw
;     [clj-time.core :as clj-time]
     (:require [clj-time.local]) 
  )

;Nov
;10
;07:16:39
;lime
;kernel:
;[3316869.760524]
;[UFW
;BLOCK]
;IN=eth0
;OUT=
;MAC=04:01:08:12:f6:01:4c:96:14:a4:ab:f0:08:00
;SRC=94.23.40.219
;DST=162.243.28.153
;LEN=44
;TOS=0x00
;PREC=0x00
;TTL=54
;ID=21083
;PROTO=TCP
;SPT=27035
;DPT=30176
;;WINDOW=16384
;RES=0x00
;ACK
;SYN
;URGP=0
;
;

 
(defn update-ufw
  [ipdata] 
  ipdata
  )

(defn receive-ufw
  [newlines rmap] 
  
  )

(defn curation-map [] {:atom (atom   {})
                       :function embellir.curios.ufw/update-ufw
                       :time-to-live (* 1000 60) ;minute
                       :receiver-function embellir.curios.ufw/receive-ufw})

(comment
  (embellir.curator/trash-curio "ufw")
  (embellir.curator/get-curio "ufw")
  (embellir.curator/curate "ufw")
  )
