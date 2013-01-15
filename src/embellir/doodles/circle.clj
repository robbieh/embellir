(ns embellir.doodles.circle
  (:gen-class)
  (:use [quil.core]))

(defn draw-circle [e]
  (stroke 255)
  (fill 255)
  (let [width (get-in e [:bound :width])
        height (get-in e [:bound :height])]
    (ellipse 0 0 width height)))

