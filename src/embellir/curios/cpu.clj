(ns embellir.curios.cpu
  (:gen-class)
  (:import [java.net URL URLClassLoader])
  (:require  [clojure.java.io :as io]
            )
  )

;cpu data is kept in vector of maps; head is most recent; last is oldest

(def pfile)

(defn get-cpu
  []
  (let [loadavg
        (map read-string (seq (clojure.string/split (clojure.string/trim (slurp (java.io.FileReader. pfile))) #" ")))]
        (zipmap [:one :five :fifteen :counts :lastpid] loadavg)))

(defn setup-cpu
  []
  (def pfile "/proc/loadavg")
  [(get-cpu)])

;cpu data is kept in a vector which is limited to the last six observations
(defn update-cpu
  [cpudata] 
  (vec (take 6 (cons (get-cpu) cpudata)))) ;man, this is ugly.
  

(defn curation-map [] {:atom (atom (embellir.curios.cpu/setup-cpu))
                       :function embellir.curios.cpu/update-cpu
                       :time-to-live (* 1000 5)
                       :receiver-function nil})

