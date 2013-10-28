(ns embellir.doodles.ipviz
  (:gen-class)
  (:import 
           java.awt.AlphaComposite
     )
  (:use 
    [ipviz.core :only [draw-ip-polygon]]
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
        d (min sizex sizey) 
        ent (get @entities entname)
    ;    color (or (:color ent) java.awt.Color/GREEN)
        ip6 (or (:ip6 ent) "0123:4567:89ab:cdef:0000:0000:0000:0000:")
        img (draw-ip-polygon ip6 (conj ent [:size d]))
        ] 
;    (do (println "draw-doodle" sizex sizey d s))
;    (do (println "draw-doodle sizex,y" sizex sizey "diam" d "seconds%" pct "seconds size" s))
;    (blank-image panel graphics)
;    (.setComposite graphics (AlphaComposite/getInstance AlphaComposite/SRC_OVER, 0.5))
    (.drawImage graphics ^java.awt.Image img 0  0 nil)
    )
  )
