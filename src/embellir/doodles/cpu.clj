(ns embellir.doodles.cpu
  (:gen-class)
;  (:import (java.util Calendar Date))
  (:require [embellir.illustrator :as illustrator]
            )
  (:use     [quil.core])
  )

(defn minutes-to-radians [minutes]
  (radians (* 6 minutes)))

(defn wedge [x y iw ih ow oh start stop]
  (begin-shape)
  (...
  (end-shape)

(defn draw-cpu [entity]
  (rotate (- HALF-PI))

  (stroke 0 75 25)
  (stroke-weight 1)

  (let [width (:width (:bound entity))
        x     (* width 0.5)
        y     (* width 0.5)]
    (doseq [day (range 0 (.getActualMaximum (calNow) java.util.Calendar/DAY_OF_MONTH))]
      (line  (* width (* 0.90 0.5))  0 (* width (* 0.95 0.5)) 0)
    (arc 0 0 diam diam 0 stoprad)
      (rotate (radians-for-this-month))
      )
    )

(defn illustrate []
  (illustrator/create-entity "cpu" (illustrator/position 0 0) (illustrator/bound 100 100 :round) (illustrator/drawing embellir.doodles.cpu/draw-cpu)))

