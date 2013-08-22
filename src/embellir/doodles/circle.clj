(ns embellir.doodles.circle
  (:gen-class)
  (:use 
    seesaw.graphics
    seesaw.color
    ))


(defn draw-doodle [d canvas]
  (let [
        sizex (:width canvas)
        sizey (:height canvas)
        s (long (* d (mod 1000 (System/currentTimeMillis)) 0.01))
        ] 
    (draw canvas
          (ellipse (/ 2 sizex) (/ 2 sizey) s s) (style :foreground java.awt.Color/RED )
          (ellipse (/ 2 sizex) (/ 2 sizey) d d) (style :foreground java.awt.Color/BLUE )

          ) 

    )
  )

(defn new-doodle [identifier]
  (let [canvas  (embellir.illustrator.systems/create-doodle-canvas (partial draw-doodle 99 canvas ))
        entmap  [(embellir.illustrator.entities/create-entitiy identifier
                           (embellir.illustrator.components/seesaw-canvas canvas)
                           (embellir.illustrator.components/fps-draw )
                           )
                 ]
        ]
    entmap
    )
  (comment embellir.illustrator.entities/create-an-entity (partial draw-doodle r) 100 name)
  (comment embellir.illustrator.components/seesaw-canvas (illustrator/create-doodle-canvas (partial draw-doodle)))
   
  )


