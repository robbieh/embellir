(ns embellir.illustrator
  (:gen-class)
  (:import [java.awt RenderingHints]
           [javax.swing JFrame JLabel JComponent] 
           [java.awt BorderLayout]) 
  ;           [java.awt Graphics]
  ;           [java.awt.image BufferedImage])
  (:require [seesaw.core :as seesaw])
  (:use embellir.illustrator.components
        embellir.illustrator.entities
        embellir.illustrator.systems
        seesaw.core
        seesaw.graphics
        seesaw.color)
  )


(defn start-illustrator []
  (let [
;        bp (border-panel :center (embellir.doodles.circle/mkcircle 12 "circle1") )
        xyz (seesaw/xyz-panel :background "#FFF")
        f (seesaw/frame :title "embellir" :width 500 :height 500 :content xyz :visible? true ) 
        ]
        (create-entity "xyz-panel" (seesaw-xyz-panel xyz))
         ;;   Assign the xyz to a new entity with component seesaw-panel
         ;;   so that everything can find it in the @entities
    )

  ;;TODO need a thread that manages the SYSTEMS, or calls drawlooop over and over
  (.start (Thread. sysloop))

;if I just use Swing...
;  (let [ frame (JFrame. "embellir")]
;
;    (.setLayout frame (BorderLayout. )) 
;    (.setSize frame 500 500) 
;    (.setVisible frame true)
;    (.add frame (mkcomponent) BorderLayout/CENTER) 
;    ) 
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; throw-away functions ... please remove! ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

