(ns embellir.doodles.circle
  (:gen-class)
  (:require [embellir.illustrator :as illustrator])
  (:use 
     seesaw.graphics
     seesaw.color
     ))


(defn draw-doodle [d panel g2D]
  (let [sizex (.getWidth panel)
        sizey (.getHeight panel)
        s (long (* d (mod 1000 (System/currentTimeMillis)) 0.01))
        ] 
        (draw g2D
          (ellipse (/ 2 sizex) (/ 2 sizey) s s) (style :foreground java.awt.Color/RED )
          (ellipse (/ 2 sizex) (/ 2 sizey) d d) (style :foreground java.awt.Color/BLUE )
              
              ) 
    
    )
  )

(defn new-doodle [identifier]
  should be returning the information to run create-entity...
  (comment embellir.illustrator.entities/create-an-entity (partial draw-doodle r) 100 name)
  create-entity wants [entname & components]
  [ identifier 
   (embellir.illustrator.components/seesaw-canvas (illustrator/create-doodle-canvas (partial draw-doodle)))
    ]
  )


