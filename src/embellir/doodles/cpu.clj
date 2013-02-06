(ns embellir.doodles.cpu
  (:gen-class)
;  (:import (java.util Calendar Date))
  (:require [embellir.illustrator :as illustrator]
            )
  )

(defn minutes-to-radians [minutes]
  (radians (* 6 minutes)))

(defn wedge [x y iw ih ow oh start stop]
  (begin-shape)
  (...
  (end-shape)

(defn draw-cpu [entity]


  (let [width (:width (:bound entity))
        x     (* width 0.5)
        y     (* width 0.5)]

(defn illustrate []
  (illustrator/create-entity "cpu" (illustrator/position 0 0) (illustrator/bound 100 100 :round) (illustrator/drawing embellir.doodles.cpu/draw-cpu)))

