(ns embellir.core
  (:gen-class)
  (:import [java.io File])
  (:require [embellir.illustrator :as illustrator]
            [embellir.curator :as curator]
            [embellir.bitdock :as bitdock]
            [clojure.java.io :as io]
            )
  )


(defn read-config-file [filename]
  (with-open [rd (io/reader filename)]
    (doseq [line (line-seq rd)]
      (bitdock/handle-command line)))
;  (embellir.illustrator.systems/relayout)
         
         )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


; start the curator
(curator/start-curator)

; start the bitdock
(bitdock/start-bitdock)


(defn main [& args]
  (when 
          
          (.exists (File. (str (System/getenv "HOME") "/.embellirrc")) )
          ;(.exists (File. ^String (first args)) )
          
    (read-config-file (str (System/getenv "HOME") "/.embellirrc"))
    
    )

  ; start the illustrator
;  (illustrator/start-illustrator)
  nil
  )


