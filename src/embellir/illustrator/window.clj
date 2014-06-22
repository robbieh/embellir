(ns embellir.illustrator.window
  (:use
        seesaw.core
     )  
  )

(defonce xyz (xyz-panel :background "#000" ))
(defonce f (frame :title "embellir" :width 500 :height 500 :content xyz :visible? true ) )

