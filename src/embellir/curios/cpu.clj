(ns embellir.curios.cpu
  (:gen-class)
  (:import [java.net URL URLClassLoader] [java.io FileReader])
  (:require  [clojure.java.io :as io]
            )
  )

;cpu data is kept in vector of maps; head is most recent; last is oldest

(def pfile "/proc/loadavg")


;note the funny call to FileReader - files on /proc don't work with slurp without it
(defn get-cpu
  []
  (let [loadavg
        (map read-string (seq (clojure.string/split (clojure.string/trim (slurp  (java.io.FileReader. pfile))) #" ")))]
        (zipmap [:one :five :fifteen :counts :lastpid] loadavg)))

(defn setup-cpu
  []
  [(get-cpu)])

;cpu data is kept in a vector which is limited to the last six observations
(defn update-cpu
  [cpudata] 
  (vec (take 30 (cons (get-cpu) cpudata)))) ;man, this is ugly.
  

(defn curation-map [] {:atom (atom (embellir.curios.cpu/setup-cpu))
                       :function embellir.curios.cpu/update-cpu
                       :time-to-live (* 1000 5)
                       :receiver-function nil})

(comment (embellir.curator/curate "cpu"))
(comment (embellir.curator/get-curio "cpu"))
(comment (map :one (embellir.curator/get-curio "cpu")))
(comment (embellir.curator/trash-curio "cpu"))
(comment (embellir.curator/curate "cpu"))
