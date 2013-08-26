(ns embellir.doodles.circle
  (:gen-class)
  (:use 
    seesaw.graphics
    seesaw.color
    ))

(def private-data (atom {}))

(defn draw-doodle [^javax.swing.JPanel canvas ^java.awt.Graphics2D graphics]
  (let [sizex (.getWidth canvas)
        sizey (.getHeight canvas)
        ;d (:diameter @private-data)
        d 100
        s (long (* d (mod (System/currentTimeMillis) 1000 ) 0.01))
        ] 
;    (do (println "draw-doodle" sizex sizey d s))
    (push graphics (draw graphics
                (ellipse (/ 2 sizex) (/ 2 sizey) s s) (style :foreground java.awt.Color/RED ) 
                (ellipse (/ 2 sizex) (/ 2 sizey) d d) (style :foreground java.awt.Color/BLUE)
                )) 

    )
  )

(defn new-doodle [identifier]
  (let [canvas  (embellir.illustrator.systems/create-doodle-canvas draw-doodle )
                 
        ]
      ;return an entity map
        (embellir.illustrator.entities/create-entity identifier
                           (embellir.illustrator.components/seesaw-canvas canvas)
                           (embellir.illustrator.components/fps-draw 60)
                           )
    )
  (comment swap! private-data #(conj {:diameter 100} %) ... something like this to add a diameter to the private data)
  (comment embellir.illustrator.entities/create-an-entity (partial draw-doodle r) 100 name)
  (comment embellir.illustrator.components/seesaw-canvas (illustrator/create-doodle-canvas (partial draw-doodle 10)))

  )


