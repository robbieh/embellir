(ns embellir.curios.memory
  (:gen-class)
  (:import [java.net URL URLClassLoader] [java.io FileReader])
  (:require  [clojure.java.io :as io]
            )
  )

;cpu data is kept in vector of maps; head is most recent; last is oldest

(def pfile "/proc/meminfo")

;note the funny call to FileReader - files on /proc don't work with slurp without it
(defn get-memory
  []
  (let [pairs (map #(clojure.string/split % #":")  (clojure.string/split (clojure.string/replace (slurp  (java.io.FileReader. pfile)) #"[ \t]|kB" "") #"\n")) 
        ]
        (into {} (map (fn [[k v]] (hash-map (keyword k) (read-string v) )) pairs))
        ))

(defn setup-memory
  []
  (get-memory))

(defn update-memory
  [cpudata] 
  (get-memory)
;  (vec (take 30 (cons (get-memory) cpudata)))
  
  ) 
  

(defn curation-map [] {:atom (atom (embellir.curios.memory/setup-memory))
                       :function embellir.curios.memory/update-memory
                       :time-to-live (* 1000 5)
                       :receiver-function nil})

(comment (embellir.curator/curate "memory"))
(comment (embellir.curator/get-curio "memory"))
(comment (:MemFree (embellir.curator/get-curio "memory")))
(comment (embellir.curator/trash-curio "memory"))
