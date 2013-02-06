(ns embellir.doodles.text
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


(defn draw-text [entity]
  (let [width (:width (:bound entity))
        height (:height (:bound entity))
        hawidth (* 0.5 height)
        haheight (* 0.5 width)
        ]
    (.setColor graphics2D (to-color "#C8FFC8"))
      (.drawString graphics2D "Test" 5 10)
)) 

