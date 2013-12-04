(ns embellir.illustrator.renderer
;  (:gen-class)
  (:import 
            java.awt.AlphaComposite
           ) 
  (:require [seesaw.core :as seesaw]
            [seesaw.timer :as timer]
     )
  (:use 
        seesaw.core
        seesaw.graphics
        seesaw.color
        embellir.illustrator.util 
        [embellir.illustrator.entitylist :only [entities]] 
        )
  )





(defn repaint-entity
  [entname & more]
  ; call the :function of the entity with the :frame and graphics for the
  ; frame, let it draw in (future) figure out next run time and update
  ; :next-time according to :sleepms
  ;
  (let [{:keys [canvas sleepms timer]} (get @entities entname) 
        d (.getDelay ^javax.swing.Timer timer)
        ]
    (repaint! ^javax.swing.JPanel canvas) 
    
;    (.setBackground ^javax.swing.JPanel canvas (color "black"))
;    (.update ^javax.swing.JPanel canvas (.getGraphics ^javax.swing.JPanel canvas))
    ;(.paintImmediately ^javax.swing.JPanel canvas (.getBounds  ^javax.swing.JPanel canvas))
   
    (if (not (= d sleepms)) (.setDelay ^javax.swing.Timer timer sleepms))
       
   ) 
  )










