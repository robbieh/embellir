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


; start the curator
(curator/start-curator)

; start the bitdock
(bitdock/start-bitdock)

;the illustrator is started when embellir.illustrator.window is required in the ns

(defn -main [& args]
    (when (.exists (File. (str (System/getenv "HOME") "/.embellir.startup")) )
      (read-config-file (str (System/getenv "HOME") "/.embellir.startup")) )
  nil)


