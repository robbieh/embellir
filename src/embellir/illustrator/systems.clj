(ns embellir.illustrator.systems
  (:require 
    [clojure.java.io :as io]
    [clojure.math.numeric-tower :as math]
    [seesaw.core :as seesaw]
    [clj-time.core]
    [clj-time.coerce]
    [clj-time.local])
  )


(defn half [n] (* 0.5 n))

(defn now-long
  "returns the current local time as a long"
  [] (clj-time.coerce/to-long (clj-time.local/local-now)))

(defn pctpoint
  [p1 p2 pct]
  (if (< p1 p2) (+ p1 (* (math/abs (- p1 p2)) pct))
    (- p1 (* (math/abs (- p1 p2)) pct))))

