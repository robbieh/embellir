(ns embellir.doodles.weather
  (:gen-class)
  (:use [embellir.illustrator :as illustrator]
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
    (text (:weather w) 0 0)
    (translate 0 (+ (text-ascent) (text-descent)))
    (text (:temp_f w) 0 0)
    (translate 0 (+ (text-ascent) (text-descent)))
    (text (:wind_dir w) 0 0)
    (translate 0 (+ (text-ascent) (text-descent)))
    (text (:wind_mph w) 0 0)
    ))  

(defn illustrate []
  (println "foo")
  (illustrator/create-entity "weather" 
                             (position 300 300) 
                             (bound 100 100) 
                             (drawing embellir.doodles.weather/draw-weather)))



