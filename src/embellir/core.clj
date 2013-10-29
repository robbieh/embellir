(ns embellir.core
  (:gen-class)
  (:import [java.io File])
  (:require [embellir.illustrator :as illustrator]
            [embellir.curator :as curator]
            [embellir.bitdock :as bitdock]
            [clojure.java.io :as io]
            )
  )


(comment defn read-config-file [filename]
  (with-open [rd (io/reader filename)]
    (doseq [line (line-seq rd)]
      (bitdock/handle-command line)))
  (embellir.illustrator.systems/relayout))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


; start the curator
(curator/start-curator)

; start the bitdock
(bitdock/start-bitdock)


(defn main [& args]
  (when (.exists (File. ^String (first args)))
    (comment read-config-file (first args)))

  ; start the illustrator
  (illustrator/start-illustrator)
  nil
  )


