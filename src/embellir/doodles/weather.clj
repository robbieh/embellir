(ns embellir.doodles.weather
  (:gen-class)
  (:import 
     (java.util Calendar Date)
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage)
     )
  (:require 
     [embellir.illustrator :as illustrator]
     [embellir.curator :as curator]
     [clj-time.core :as clj-time]
     [clj-time.local])
  (:use     
     seesaw.graphics
     seesaw.color
     seesaw.font
     )
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; draw the weather conditions
; this is stupid simple at the moment ... TODO: nice graphics
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def entityhints {:sleepms 60000})
(def droidSansMono (font :name "DroidSansMono" :size 30))

(defn draw-weather 
  [entity panel graphics2D]
  (let [width (.getWidth panel)
        height (.getHeight panel)
        hawidth (* 0.5 height)
        haheight (* 0.5 width)
        w (curator/get-curio "weather")
        ]
    ;(text-mode :model)
    ;(text-size 30)
    ;(text-align :center)
    (.setColor graphics2D (to-color "#C8FFC8"))
    (.setFont graphics2D droidSansMono)

    (.drawString graphics2D  (:weather w) 0 40)
    ;    (translate 0 (+ (text-ascent) (text-descent)))
    (.drawString graphics2D  (:temp_f w) 0 80)
    ;    (translate 0 (+ (text-ascent) (text-descent)))
    (.drawString graphics2D  (:wind_dir w) 0 120)
    ;    (translate 0 (+ (text-ascent) (text-descent)))
    (.drawString graphics2D  (:wind_mph w) 0 160)
    ))  

(defn draw-doodle [ent ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (draw-weather (curator/get-curio "weather") panel graphics)
  )

(comment defn illustrate []
  (println "foo")
  (let [bi (java.awt.image.BufferedImage. (illustrator/scrwidth) (illustrator/scrheight) java.awt.image.BufferedImage/TYPE_INT_ARGB)
        gr (.createGraphics bi)]
    (doto gr (.setRenderingHint RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON))


    (illustrator/create-entity "weather" 
                 (illustrator/position 0 0)
                 (illustrator/bound (illustrator/scrwidth) (illustrator/scrheight) :round)

                 (drawing embellir.doodles.weather/draw-weather bi gr)))
  )


