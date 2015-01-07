(ns embellir.core
  (:gen-class :main true)
  (:import [java.io File])
  (:require 
     embellir.illustrator.window
     [embellir.curator :as curator]
     [embellir.bitdock :as bitdock]
     [clojure.java.io :as io]
     )
  )


(defn read-config-file [filename]
  (println "file:" filename)
  (with-open [rd (io/reader filename)]
    (doseq [line (line-seq rd)]
      (println "line:" line)
      (bitdock/handle-command line)))
  (embellir.illustrator.layout/relayout))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn startup []
  ; start the curator
  (curator/start-curator)

  ; start the bitdock
  (bitdock/start-bitdock)

  ;open the window. this also starts the illustrator
  (embellir.illustrator.window/make-window)
 )


(defn find-config-file []
  (let [home-env-file (str (System/getenv "EMBELLIR_STARTUP"))
        home-file (str (System/getProperty "user.home") "/.embellir.startup")   
        home-appdata (str (System/getenv "APPDATA") "\\embellir\\embellir.startup")
        internal-resource (.getFile (clojure.java.io/resource "embellir.startup"))
        filecheck (fn [s] (if (.exists (File. (str s))) s nil))
        ]
  (or (filecheck home-env-file)
      (filecheck home-file)
      (filecheck home-appdata)
      internal-resource
      ))
  )

(defn -main [& args]
    (startup)
    (read-config-file (find-config-file)) 
  nil)









