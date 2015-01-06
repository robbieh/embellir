(ns embellir.curios.netusage
  (:gen-class)
  (:import [java.net URL URLClassLoader] [java.io FileReader])
  (:require  [clojure.java.io :as io]
            )
  )

(def netfile "/proc/net/dev")

;note the funny call to FileReader - files on /proc don't work with slurp without it
(def fields [:rbytes :rpackets :rerrs :rdrop :rfifo :rframe :rcompressed :rmulticast
 :tbytes :tpackets :terrs :tdrop :tfifo :tframe :tcompressed :tmulticast])
(defn get-netusage
  []
  (let [netdata (map (comp vec (partial remove empty?))
             (map #(clojure.string/split % #"\t") (map #(clojure.string/replace % #" +" "\t")
              ((comp rest rest) (clojure.string/split-lines (slurp  (java.io.FileReader. netfile)) ))))
             
             )]
        (apply merge (map #(hash-map (keyword (clojure.string/replace (first %) ":" "")) (zipmap fields (map read-string (rest %)) ))  netdata))
    ))

(defn setup-netusage
  []
  {:counts (get-netusage)})




;cpu data is kept in a vector which is limited to the last six observations
(defn update-netusage
  [olddata] 
  (let [newdata (get-netusage)
        counts (:counts olddata)
        ifaces (keys newdata)
        diff (apply merge (for [i ifaces] {i (merge-with - (i newdata) (i counts))})) 
        ]
    {:counts newdata
     :diff diff }
    )
)

(defn curation-map [] {:atom (atom (embellir.curios.netusage/setup-netusage))
                       :function embellir.curios.netusage/update-netusage
                       :time-to-live (* 1000 5)
                       :receiver-function nil})

(comment (embellir.curator/curate "netusage"))
(comment (embellir.curator/get-curio "netusage"))
(comment (:diff (embellir.curator/get-curio "netusage")))
(comment (embellir.curator/trash-curio "netusage"))
(comment (embellir.curator/curate "netusage"))
