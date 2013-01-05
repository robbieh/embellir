(ns embellir.core
  (:gen-class)
  (:require [embellir.illustrator :as illustrator]
            [embellir.curator :as curator]
            [embellir.bitdock :as bitdock]
            )
  )

; start the curator
(curator/start-curator)

; start the bitdock
(bitdock/start-bitdock)


(defn -main
  [& args]
  ;check for filename in args

  ; start the illustrator
  (illustrator/start-illustrator)
  )
