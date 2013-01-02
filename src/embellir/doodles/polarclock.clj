(ns embellir.doodles.polarclock
  (:gen-class)
  (:import (java.util Calendar Date))
  (:require [clj-time.core :as clj-time]
        [clj-time.local])
  (:use      [quil.core])
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; draw the clock
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;(illus/create-entity "polar clock" 
;                     (position (* (rand) (width)) (* (rand) (height)))
;                     (bound :square 300 300)
;                     (drawing draw-polarclock))


(defn hour-to-radians [hr]
  ; there are 15 degrees "per hour" on a 24-hr clock
  (radians (* 15 hr)))

(defn hour-to-radians-12 [hr]
  (* 2 (hour-to-radians (if (> 12 hr) hr (- hr 12)))))

(defn minutes-to-radians [minutes]
  (radians (* 6 minutes)))

(defn radians-for-30d []
  (/ TWO-PI 30))

(defn calNow []
  (Calendar/getInstance))

(defn radians-for-this-month []
  (/ TWO-PI (.getActualMaximum (calNow) Calendar/DAY_OF_MONTH)))

(defn draw-monthclock [entity]
  ;  (rotate (- PI))
  (rotate (- HALF-PI))
  (push-matrix)
  (push-style)
  (stroke 0 75 25)
  (stroke-weight 1)
  (let [width (:width (:bound entity))
        x     (* width 0.5)
        y     (* width 0.5)]
    (doseq [day (range 0 (.getActualMaximum (calNow) java.util.Calendar/DAY_OF_MONTH))]
      (line  (* width (* 0.90 0.5))  0 (* width (* 0.95 0.5)) 0)
      (rotate (radians-for-this-month))
      )
    )
  (stroke-weight 3)
  (stroke 10 95 0)
  (pop-matrix)(push-matrix)
  (let [width (:width (:bound entity))
        diam (* 0.95 width)]
    (rotate (* (clj-time/day (clj-time.local/local-now)) (radians-for-this-month)))
    (line  (* width (* 0.85 0.5))  0 (* width (* 0.95 0.5)) 0)
    (rotate (- (radians-for-this-month)))
    (line (* width (* 0.85 0.5))  0 (* width (* 0.95 0.5)) 0)
    (stroke-weight 5)
    (arc 0 0 diam diam 0 (radians-for-this-month))
    )
  (pop-style)
  (pop-matrix))

(defn draw-timeclock [entity]
  (let [width (:width (:bound entity))
        diam  (* 0.8 width)
        tmdiam (* 0.7 width)
        sdiam (* 0.5 width)
        x     (:x (:position entity))
        y     (:y (:position entity))
        stoprad  (minutes-to-radians (clj-time/minute (clj-time/now)))
        gmtrad  (hour-to-radians (clj-time/hour (clj-time/now)))
        hourrad  (hour-to-radians-12 (clj-time/hour (clj-time.local/local-now)))
        secrad  (minutes-to-radians (clj-time/sec (clj-time.local/local-now)))
        onerad (radians 1)
        minofhourrad (/ stoprad 14)
        ]
    (rotate (- HALF-PI))

    ; an hour-size mark for the hour
    ;(stroke 50 200 95)
    (stroke 0 220 20)
    (stroke-weight 3)
    (no-fill)
    (arc 0 0 tmdiam tmdiam hourrad (+ hourrad (radians 30))) ; 30 because 12h clock!

    ;   ;this shows 'hour markers' for a 24h clock
    ;    (doseq [x (range 0 23)]
    ;      (let [h (hour-to-radians x)
    ;            rdiff (radians 1)]
    ;            (arc 0 0 tmdiam tmdiam (- h rdiff) (+ h rdiff))))

    ; and an extra minute-dot on the hour...
    ; with a line out to the minutes ring
    (stroke-weight 5)
    (arc 0 0 tmdiam tmdiam (+ hourrad minofhourrad -0.01) (+ hourrad minofhourrad 0.01))

    ; the minutes ; drawn in two parts
    (stroke 0 220 20)
    (stroke-weight 3)
    (arc 0 0 diam diam 0 (+ minofhourrad hourrad))
    (let []
      (push-matrix)
      (rotate (+ minofhourrad hourrad ))
      (line (* tmdiam 0.5)  0 (* diam 0.5) 0)
      (pop-matrix)
      )

    (stroke 0 90 0)
    (stroke-weight 20)
    (arc 0 0 diam diam 0 stoprad)
    (stroke 0 220 20)
    (stroke-weight 16)
    (arc 0 0 diam diam 0 stoprad)

    ; let's see... red 'dot' on the GMT hour
    (stroke 250 25 25)
    (stroke-weight 5)
    (arc 0 0 tmdiam tmdiam (- gmtrad onerad) (+ gmtrad onerad))

    (stroke 0 13 5)
    (stroke-weight 50)
    (arc 0 0 sdiam sdiam (- secrad PI) (+ secrad PI))
    (stroke-weight 90)
    (stroke 0 0 0)
    (arc 0 0 sdiam sdiam (- secrad onerad) (+ secrad onerad))

    ))

(defn draw-polarclock [entity]
  (push-matrix)
  (draw-timeclock entity)
  (pop-matrix)
  (draw-monthclock entity))


