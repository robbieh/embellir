(ns embellir.illustrator.window
  (:use
        seesaw.core
     )  
  )
(def xyz (xyz-panel :background "#000" ))
(def f (frame :title "embellir" :width 500 :height 500 :content xyz :visible? true ) )
