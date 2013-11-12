(ns embellir.illustrator.renderer
;  (:gen-class)
  (:import 
            java.awt.AlphaComposite
           ) 
  (:require [seesaw.core :as seesaw]
            [seesaw.timer :as timer]
     [embellir.illustrator.entities]
     )
  (:use 
        seesaw.core
        seesaw.graphics
        seesaw.color
        embellir.illustrator.util 
        [embellir.illustrator.entities :only [entities]] 
        )
  )





(defn repaint-entity
  [entname & more]
  ; call the :function of the entity with the :frame and graphics for the
  ; frame, let it draw in (future) figure out next run time and update
  ; :next-time according to :sleepms
  ;
  (let [{:keys [canvas sleepms timer]} (get @entities entname) 
        d (.getDelay timer)
        ]
;    (println "painting: " entname)
      (repaint! canvas) 
    (if (not (= d sleepms)) (.setDelay timer sleepms))
       
   ) 
  )










