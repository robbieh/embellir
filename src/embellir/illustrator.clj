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


;        (def xyz (seesaw/xyz-panel :background "#000"))
;        (def f (seesaw/frame :title "embellir" :width 500 :height 500 :content xyz :visible? true ) )

(defn start-illustrator []

;  (.start (Thread. sysloop))
;  (-> render-loop Thread. .start)

  )
