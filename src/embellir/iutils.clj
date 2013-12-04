(ns embellir.iutils
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;(defn abs [x] (if (>= 0 x) x (- x)))

(defn iarc
  "return an arc, defined from the center instead of from the corners
  also reverse drawing direction of arc (such that positive values turn clockwise)
  and correct 0 to be at the top
  see: seesaw.graphics/arc"
  ([x y w h start extent arc-type]
   (let [hw (* 0.5 w)
         hh (* 0.5 h)
         nstart (- start)
         nextent (- extent)]
     (seesaw.graphics/arc (- x hw) (- y hh), w h, (+ nstart 90) nextent arc-type)))
  ([x y w h start extent]
   (iarc x y w h start extent java.awt.geom.Arc2D/OPEN)))

(defn hex
  "draw a hexagon"
  []
  )
