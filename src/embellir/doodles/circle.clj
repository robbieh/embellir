(ns embellir.doodles.circle
  (:gen-class)
  (:import 
           java.awt.AlphaComposite
     )
  (:use 
    seesaw.graphics
    seesaw.color
    embellir.illustrator.util
    [embellir.illustrator.entities :only [entities]]
    )
  (:require 
     [clj-time.core :as clj-time]
     [clj-time.local]) 
  )

(def private-data (atom {}))

(defn draw-doodle [entname ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]

  (let [sizex (.getWidth panel)
        sizey (.getHeight panel)
        ;d (:diameter @private-data)
        d (min sizex sizey) 
        pct (/ (mod (clj-time/sec (clj-time.local/local-now)) 60 ) 60 ) 
        s (* d pct)
        ent (get @entities entname)
        color (or (:color ent) java.awt.Color/RED)
        ] 
;    (do (println "draw-doodle" sizex sizey d s))
;    (do (println "draw-doodle sizex,y" sizex sizey "diam" d "seconds%" pct "seconds size" s))
;    (blank-image panel graphics)
;    (.setComposite graphics (AlphaComposite/getInstance AlphaComposite/SRC_OVER, 0.5))
    (push graphics (draw graphics
                (ellipse (/ 2 sizex) (/ 2 sizey) s s) (style :foreground color)
                (ellipse (/ 2 sizex) (/ 2 sizey) d d) (style :foreground java.awt.Color/GREEN)
                )) 

    )
  )

(comment defn new-doodle [identifier]
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


