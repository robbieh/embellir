(ns embellir.bitdock
  (:gen-class)
  (:import (java.net
             ServerSocket
             SocketException)
           (java.io
             PrintWriter
             BufferedReader
             InputStreamReader))
  (:require 
    [clj-time.local]
    [clj-time.core]
    [clj-time.coerce]
    [clojure.string :as str]
    [embellir.curator :as curator]
    [embellir.illustrator :as illustrator]
    [server.socket]
    )
  )

; samples of the protocol:
; supply mail :last-date <some date here> :unread-count :total-count :last-subject :last-from
; direct start mail
; direct focus mail
; supply text :data "a big long line of text......."
; direct layout 
; request


;(def response-codes {:0 "OK"
;                     :1 "Failed"})


(defn read-map 
  "this reads a single Clojure map as a string; if the input is not a map, nil is returned"
  [data] 
  (let [result (read-string data)]
    (when (map? result) result)))

(defn supply [data] 
  (let [[curioname remainder] (str/split data #" " 2)
        dmap (read-map remainder)]
        (curator/receive-data-for-curio curioname dmap)))

(defn layout [data] 
  (if data 
    (illustrator/relayout data)
    (illustrator/relayout)))

(defn curate [data]
  (doseq [item (str/split data #" ")]
    (println "curating: " item)
    (curator/curate item))
  )

(defn illustrate [data]
  (doseq [item (str/split data #" ")]
    (println "illustrating: " item)
    (illustrator/load-entity item))
  )

(def cmd-map { "supply" supply
              "layout" layout 
              "curate" curate
              "illustrate" illustrate})
(defn handle-command [line]
  (when-not (= \ (first (str/trim line)))
    (let [[cmd remainder] (str/split line #" " 2)]
      (if-let [func (get cmd-map (str/lower-case cmd))] 
        (func remainder)
        (str "Did not understand how to handle '" cmd "'")
        ))))

(defn handle-stream [in out]
  (binding [*in*  (BufferedReader. (InputStreamReader. in))
            *out*   (PrintWriter. out)]
    (loop [line (read-line)]
      (when-not (empty? line)
        (println (handle-command line)) (recur (read-line))))))

(defn start-bitdock []
  (server.socket/create-server 9999 handle-stream))


