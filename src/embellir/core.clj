(ns embellir.core
  (:gen-class)
  (:use [embellir.illustrator :as illus]
        [embellir.curator :as curator]
        [quil.core])
  )



(defn draw-alpha [e]
    (stroke 255)
    (fill 255)
    (text (curator/get-curio "stringflipper1") 0 0))

(curate "stringflipper1" "abcdefghijklmnopqrstuvwxyz" clojure.string/reverse 1000)
(.start (Thread. manage-queue))

(illus/create-entity "alphaloop" 
                              (position 20 20)
                              (bound :square 20 20)
                              (drawing  draw-alpha)
               )

(defn start-illustration []
  (embellir.illustrator/start-sketch))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  )
