(ns embellir.illustrator
;  (:gen-class)
  (:import [java.awt RenderingHints]
           [javax.swing JFrame JLabel JComponent] 
           [java.awt BorderLayout]
;           [java.awt Graphics]
;           [java.awt.image BufferedImage]
           ) 
  (:require [seesaw.core :as seesaw])
  (:use embellir.illustrator.components
        embellir.illustrator.entities
        embellir.illustrator.systems
        seesaw.core
        seesaw.graphics
        seesaw.color)
  )


(defn start-illustrator []

;  (.start (Thread. sysloop))
;  (-> render-loop Thread. .start)

  )
