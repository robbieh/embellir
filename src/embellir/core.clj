(ns embellir.core
  (:gen-class)
  (:use [embellir.illustrator :as illus]
        [embellir.curator :as curator]
        [quil.core])
  )

; start the curator
(.start (Thread. curator/manage-queue))

(defn start-illustration []
  (embellir.illustrator/start-sketch))

(defn -main
  [& args]
  (start-illustration)
  )
