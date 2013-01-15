(ns embellir.doodles.text
  (:gen-class)
  (:use      [quil.core]))

(defn draw-text [entity]
  (let [width (:width (:bound entity))
        height (:height (:bound entity))
        hawidth (* 0.5 height)
        haheight (* 0.5 width)
        ]
    (stroke 255 0 255)
    (stroke-weight 10)
    (text-size 30)
    (text-align :center)
    (text-mode :model)
    (text "test" 0 0)))

  

