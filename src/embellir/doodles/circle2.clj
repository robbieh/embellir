(ns embellir.doodles.circle2
  (:gen-class)
  (:use 
    seesaw.graphics
    seesaw.color
    )
  (:require 
     [clj-time.core :as clj-time]
     [clj-time.local]) 
  )

(def private-data (atom {}))

(defn draw-doodle [^javax.swing.JPanel canvas ^java.awt.Graphics2D graphics]
  (let [sizex (.getWidth canvas)
        sizey (.getHeight canvas)
        ;d (:diameter @private-data)
        d (min sizex sizey) 
        pct (/ (- 60 (mod (clj-time/sec (clj-time.local/local-now)) 60 )) 60 ) 
        s (* d pct)
        ] 
;    (do (println "draw-doodle" sizex sizey d s))
    (do (println "draw-doodle2 sizex,y" sizex sizey "diam" d "seconds%" pct "seconds size" s))
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


