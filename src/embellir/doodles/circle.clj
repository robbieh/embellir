(ns embellir.doodles.circle
  (:gen-class)
  (:require [embellir.illustrator :as illustrator])
  (:use 
     seesaw.graphics
     seesaw.color
     ))

(defn draw-circle [e ^javax.swing.JPanel canvas ^java.awt.Graphics2D graphics2D]
  (let [width (get-in e [:bound :width])
        height (get-in e [:bound :height])]
    (draw graphics2D
      (ellipse 0 0 100 100) (style :foreground java.awt.Color/RED))
  ))

(defn illustrate []
    (illustrator/create-entity "circle" (illustrator/position 0 0) (illustrator/bound 100 100 :round) (illustrator/drawing embellir.doodles.circle/draw-circle)))

