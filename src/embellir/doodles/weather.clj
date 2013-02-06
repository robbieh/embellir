(ns embellir.doodles.weather
  (:gen-class)
  (:import (java.util Calendar Date)
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage))
  (:require [embellir.illustrator :as illustrator]
     [clj-time.core :as clj-time]
     [clj-time.local])
  (:use     seesaw.graphics
     seesaw.color)
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
    (.setColor graphics2D (to-color "#C8FFC8"))

    (.drawString graphics2D  (:weather w) 0 0)
    ;    (translate 0 (+ (text-ascent) (text-descent)))
    (.drawString graphics2D  (:temp_f w) 0 20)
    ;    (translate 0 (+ (text-ascent) (text-descent)))
    (.drawString graphics2D  (:wind_dir w) 0 30)
    ;    (translate 0 (+ (text-ascent) (text-descent)))
    (.drawString graphics2D  (:wind_mph w) 0 40)
    ))  

(defn illustrate []
  (println "foo")
  (let [bi (java.awt.image.BufferedImage. (illustrator/scrwidth) (illustrator/scrheight) java.awt.image.BufferedImage/TYPE_INT_ARGB)
        gr (.createGraphics bi)]
    (doto gr (.setRenderingHint RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON))


    (illustrator/create-entity "weather" 
                 (illustrator/position 0 0)
                 (illustrator/bound (illustrator/scrwidth) (illustrator/scrheight) :round)

                 (drawing embellir.doodles.weather/draw-weather bi gr)))
  )


