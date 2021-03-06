(ns embellir.bitdock
;  (:gen-class)
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
    [embellir.illustrator.layout]
    [embellir.illustrator.entities]
    [embellir.illustrator.colors :as colors]
    [server.socket]
    )
  )




(defn read-map 
  "this reads a single Clojure map as a string; if the input is not a map, nil is returned"
  [data] 
  (let [result (read-string data)]
    (when (map? result) result)))

(defn supply [data] 
  (let [[curioname remainder] (str/split data #" " 2)
        dmap (read-map remainder)]
;    (println "supplied with " curioname dmap)
        (curator/receive-data-for-curio curioname dmap)))

;layout layout-central-feature
;layout layout-grid

(defn layout [data] 
  (embellir.illustrator.layout/do-layout data) )

(defn curate [data]
  (doseq [item (str/split data #" ")]
    (println "curating: " item)
    (curator/curate item))
  )

(defn illustrate [data]
  (let [[item remainder] (str/split data #" " 2)
        remainder (if (empty? remainder) nil (read-map remainder))
        ]
    (println "illustrating:" item "with" remainder)
    (embellir.illustrator.entities/load-entity item remainder))
  )

(defn deillustrate [data]
  (let [[item remainder] (str/split data #" " 2) ]
    (println "de-illustrating:" item )
    (embellir.illustrator.entities/remove-entity item)))

(defn palette [data]
  (println "setting palette to:" data)
  (colors/set-palette data)
  )

(def cmd-map {"supply" supply
              "layout" layout 
              "curate" curate
              "illustrate" illustrate
              "deillustrate" deillustrate
              "palette" palette
              })

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
  (or  (try (server.socket/create-server 9999 handle-stream)
         (catch java.net.BindException e (println "could not bind 9999, trying 9998") nil))
      (try (server.socket/create-server 9998 handle-stream)
        (catch java.net.BindException e (println "could not bind 9999, trying 9998") nil)))

  )


