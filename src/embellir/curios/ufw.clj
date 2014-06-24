(ns embellir.curios.ufw
  ;     [clj-time.core :as clj-time]
  (:require 
     [clj-time.local]
     [instaparse.core :as ipac] 
     ) 
  )

(def parse-ufw  (ipac/parser (clojure.java.io/resource "ufw.bnf")))       

(defn split-ufw-field [item] (let [[k v] (clojure.string/split item #"=")] {(keyword k) v}))
(defn ufwdata-to-map [data] (apply merge (map split-ufw-field (clojure.string/split data #" "))))

(defn line-to-map [line]
  (update-in
          (apply merge (map 
                         #(hash-map (first %) (apply str (rest %)))  
                         (parse-ufw line)))
          [:UFWDATA]
          ufwdata-to-map))

(line-to-map "Jun  6 17:30:47 Olive kernel: [16476984.945911] [UFW BLOCK] IN=tun0 OUT= MAC= SRC=fcd4:eeaf:b23e:08c9:ab84:d0cf:3f64:c55d DST=fce3:b909:def1:aa07:2efa:4314:e83e:c3dc LEN=72 TC=0 HOPLIMIT=64 FLOWLBL=0 PROTO=TCP SPT=35624 DPT=443 WINDOW=78 RES=0x00 ACK FIN URGP=0")
;{:DATE1 "Jun  6 17:30:47", :HOST "Olive", :UFWDATA {:IN "tun0", :DST "fce3:b909:def1:aa07:2efa:4314:e83e:c3dc", :SPT "35624", :OUT nil, :MAC nil, :FLOWLBL "0", :URGP "0", :SRC "fcd4:eeaf:b23e:08c9:ab84:d0cf:3f64:c55d", :HOPLIMIT "64", :FIN nil, :RES "0x00", :ACK nil, :TC "0", :DPT "443", :LEN "72", :PROTO "TCP", :WINDOW "78"}, :DATE2 "16476984.945911", :KERNEL "kernel:", :UFWSTAT "BLOCK"}

;{:DATE1 "Jun  6 17:30:47", :HOST "Olive", :UFWDATA "IN=tun0 OUT= MAC= SRC=fcd4:eeaf:b23e:08c9:ab84:d0cf:3f64:c55d DST=fce3:b909:def1:aa07:2efa:4314:e83e:c3dc LEN=72 TC=0 HOPLIMIT=64 FLOWLBL=0 PROTO=TCP SPT=35624 D PT=443 WINDOW=78 RES=0x00 ACK FIN URGP=0", :DATE2 "16476984.945911", :KERNEL "kernel:", :UFWSTAT "BLOCK"}
 
(defn update-ufw
  [ipdata] 
  ipdata
  )

(defn receive-ufw
  [ufwmap rmap] 
  ;(println "received:" ufwmap rmap)
  (let [processed (reverse (take 50 (reverse (concat (:list ufwmap) [(line-to-map (:data rmap))]))))]
  ;  (println "processed:" processed)
    (assoc-in ufwmap [:list] processed)
    )
  )

(defn curation-map [] {:atom (atom   {:list []})
                       :function embellir.curios.ufw/update-ufw
                       :time-to-live (* 1000 60) ;minute
                       :receiver-function embellir.curios.ufw/receive-ufw})

(comment
  (embellir.curator/trash-curio "ufw")
  (embellir.curator/get-curio "ufw")
  (map :DATE1 (:list (embellir.curator/get-curio "ufw")))
  (embellir.curator/curate "ufw")
  )
