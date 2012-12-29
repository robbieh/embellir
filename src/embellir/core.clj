(ns embellir.core
  (:gen-class)
  (:require [embellir.baubles.polarclock])
  (:use [embellir.illustrator :as illus]
        [embellir.curator :as curator]
        [quil.core])
  )


; start the curator
(.start (Thread. manage-queue))


(defn draw-alpha [e]
    (stroke 255)
    (fill 255)
    (text (curator/get-curio "stringflipper1") 0 0))

(defn draw-circle [e]
  (stroke 255)
  (fill 255)
  (let [width (get-in e [:bound :width])
        height (get-in e [:bound :height])]
    (ellipse 0 0 width height)))

(curate "stringflipper1" "abcdefghijklmnopqrstuvwxyz" clojure.string/reverse 1000)


(defn start-illustration []
  (embellir.illustrator/start-sketch))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  )
