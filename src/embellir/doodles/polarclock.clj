(ns embellir.doodles.polarclock
  (:gen-class)
  (:import (java.util Calendar Date)
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage))
  (:require [embellir.illustrator :as illustrator]
     [clj-time.core :as clj-time]
     [clj-time.local])
  (:use     seesaw.graphics
     seesaw.color
     embellir.iutils)
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; draw the clock
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;(illus/create-entity "polar clock" 
;                     (position (* (rand) (width)) (* (rand) (height)))
;                     (bound :square 300 300)
;                     (drawing draw-polarclock))

(def PI java.lang.Math/PI)
(def TWO-PI (* 2 PI))
(def HALF-PI (/ PI 2))

(defn radians [degrees] (* degrees (/ PI 180)))

(defn hour-to-radians [hr]
  ; there are 15 degrees "per hour" on a 24-hr clock
  (radians (* 15 hr)))
(defn hour-to-degrees [hr]
  ; there are 15 degrees "per hour" on a 24-hr clock
  (* 15 hr))

(defn hour-to-radians-12 [hr]
  (* 2 (hour-to-radians (if (> 12 hr) hr (- hr 12)))))
(defn hour-to-degrees-12 [hr]
  (* 2 (hour-to-degrees (if (> 12 hr) hr (- hr 12)))))

(defn minutes-to-radians [minutes]
  (radians (* 6 minutes)))

(defn minutes-to-degrees [minutes]
  (* 6 minutes))

(defn radians-for-30d []
  (/ TWO-PI 30))

(defn calNow []
  (Calendar/getInstance))

(defn radians-for-this-month []
  (/ TWO-PI (.getActualMaximum ^java.util.Calendar (calNow) Calendar/DAY_OF_MONTH)))

;(defn draw-monthclock [entity]
;  ;  (rotate (- PI))
;  (rotate (- HALF-PI))
;  (stroke 0 75 25)
;  (stroke-weight 1)
;  (let [width (:width (:bound entity))
;        x     (* width 0.5)
;        y     (* width 0.5)]
;    (doseq [day (range 0 (.getActualMaximum (calNow) java.util.Calendar/DAY_OF_MONTH))]
;      (line  (* width (* 0.90 0.5))  0 (* width (* 0.95 0.5)) 0)
;      (rotate (radians-for-this-month))
;      )
;    )
;  (stroke-weight 3)
;  (stroke 10 95 0)
;  (pop-matrix)(push-matrix)
;  (let [width (:width (:bound entity))
;        diam (* 0.95 width)]
;    (rotate (* (clj-time/day (clj-time.local/local-now)) (radians-for-this-month)))
;    (line  (* width (* 0.85 0.5))  0 (* width (* 0.95 0.5)) 0)
;    (rotate (- (radians-for-this-month)))
;    (line (* width (* 0.85 0.5))  0 (* width (* 0.95 0.5)) 0)
;    (stroke-weight 5)
;    (arc 0 0 diam diam 0 (radians-for-this-month))
;    )
;  (pop-style)
;  (pop-matrix))

(defn draw-timeclock 
  [entity ^java.awt.Graphics2D graphics2D]
  (let [width (:width (:bound entity)) ;yes, this assumes the boundary is squared :(
        center (* 0.5 width)
        diam  (* 0.8 width)
        tmdiam (* 0.7 width)
        sdiam (* 0.5 width)
        x     (:x (:position entity))
        y     (:y (:position entity))
        ;stoprad  (minutes-to-radians (clj-time/minute (clj-time/now)))
        stopdeg  (clj-time/minute (clj-time/now))
        ;gmtrad  (hour-to-radians (clj-time/hour (clj-time/now)))
        ;hourrad  (hour-to-radians-12 (clj-time/hour (clj-time.local/local-now)))
        hourdeg  (hour-to-degrees-12 (clj-time/hour (clj-time.local/local-now)))
        ;secrad  (minutes-to-radians (clj-time/sec (clj-time.local/local-now)))
        secdeg (minutes-to-degrees (clj-time/sec (clj-time.local/local-now)))
;       onerad (radians 1)
        ;minofhourrad (/ stoprad 14)
        minofhourdeg (/ (* stopdeg 6) 15) 
        hrstroke (stroke :width 2)
        hrstroke2 (stroke :width 3)
        minstroke1 (stroke :width 20)
        minstroke2 (stroke :width 16)
        hrstyle (style :foreground (color 0 220 20) :background (color 10 10 10 0) :stroke hrstroke )
        hrstyle2 (style :foreground (color 0 220 20) :background (color 10 10 10 0) :stroke hrstroke )
        minstyle (style :foreground (color 0 90 0) :background (color 10 10 10 0) :stroke hrstroke2 )
        minstyle1 (style :foreground (color 0 90 0) :background (color 10 10 10 0) :stroke minstroke1 )
        minstyle2 (style :foreground (color 0 220 20) :background (color 10 10 10 0) :stroke minstroke2 )
        ]
    (push graphics2D 
          (translate graphics2D center center)
          (draw graphics2D
        ;        (iarc 0 0 tmdiam tmdiam 0 -90) tmpstyle2
;                (arc 0 0 tmdiam tmdiam 0 270) tmpstyle

      ; an hour-size mark for the hour
                
      (iarc 0 0 tmdiam tmdiam hourdeg 30) hrstyle; 30 because 12h clock!

      ;   ;this shows 'hour markers' for a 24h clock
      ;    (doseq [x (range 0 23)]
      ;      (let [h (hour-to-radians x)
      ;            rdiff (radians 1)]
      ;            (arc 0 0 tmdiam tmdiam (- h rdiff) (+ h rdiff))))

      ; and an extra minute-dot on the hour...
      ; with a line out to the minutes ring
     (iarc 0 0 tmdiam tmdiam (+ hourdeg minofhourdeg -0.01) 0.01 ) hrstyle2

    ; the minutes ; drawn in two parts
;    (stroke 0 220 20)
;    (stroke-weight 3)
   (iarc 0 0 diam diam 0 (+ minofhourdeg hourdeg) ) minstyle
;    (let []
;      (push-matrix)
;      (rotate (+ minofhourrad hourrad ))
;      (line (* tmdiam 0.5)  0 (* diam 0.5) 0)
;      (pop-matrix)
;      )
;
;    (stroke 0 90 0)
;    (stroke-weight 20)
    (iarc 0 0 diam diam 0 stopdeg) minstyle1
;    (stroke 0 220 20)
;    (stroke-weight 16)
    (iarc 0 0 diam diam 0 stopdeg) minstyle2
;
;    ; let's see... red 'dot' on the GMT hour
;    (stroke 250 25 25)
;    (stroke-weight 5)
;    (arc 0 0 tmdiam tmdiam (- gmtrad onerad) (+ gmtrad onerad))
;
;    (stroke 0 13 5)
;    (stroke-weight 50)
;    (arc 0 0 sdiam sdiam (- secrad PI) (+ secrad PI))
;    (stroke-weight 60)
;    (stroke 0 0 0)
;;   (arc 0 0 sdiam sdiam (- secdeg 10 ) 20) minstyle
                )
;
  )
 ))

(defn draw-polarclock [entity ^java.awt.Graphics2D graphics2D]
;  (push-matrix)
  (draw-timeclock entity graphics2D)
;  (pop-matrix)
 ; (draw-monthclock entity)
  )

(defn illustrate []
  (let [bi (java.awt.image.BufferedImage. (illustrator/scrwidth) (illustrator/scrheight) java.awt.image.BufferedImage/TYPE_INT_ARGB)
        gr (.createGraphics bi)]
    (doto gr (.setRenderingHint RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON))
    (illustrator/create-entity "polarclock" 
                 (illustrator/position 0 0) 
                 (illustrator/bound (illustrator/scrwidth) (illustrator/scrheight) :round) 
                 (illustrator/drawing embellir.doodles.polarclock/draw-polarclock bi gr)
                 )))

