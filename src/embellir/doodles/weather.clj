(ns embellir.doodles.weather
  (:gen-class)
  (:use [embellir.illustrator :as illus]
        [embellir.curator :as curator]
        [quil.core])
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; draw the weather conditions
; this is stupid simple at the moment ... TODO: nice graphics
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn draw-weather 
  [entity]
  (let [width (:width (:bound entity))
        height (:height (:bound entity))
        hawidth (* 0.5 height)
        haheight (* 0.5 width)
        w (get-curio "weather")
        ]
    ;(text-mode :model)
    ;(text-size 30)
    ;(text-align :center)
      (text (:temp_f w) 0 0)))  



