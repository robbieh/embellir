(ns embellir.iutils
  (:gen-class)
  (:import [java.util Calendar Date]
          [java.awt Graphics2D RenderingHints]
          [java.awt.image BufferedImage]
          [java.awt.geom Area]
     )
    (:require [embellir.illustrator :as illustrator]
            [clj-time.core :as clj-time]
            [clj-time.local])
    (:use     seesaw.graphics
            seesaw.color)
    )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;(defn abs [x] (if (>= 0 x) x (- x)))

(def PI java.lang.Math/PI)
(defn radians [degrees] (* degrees (/ PI 180)))
(defn degrees [radians] (* radians (/ 180 PI)))

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

(defn ringarc
  "returns a GeneralPath like a slice of a ring, defined by outer and inner
  radius and size of angle in degrees
  [x y w1 h1 w2 h2 start extent]"
  [x y w1 h1 w2 h2 start extent]
  (let [hw1 (* 0.5 w1)
        hh1 (* 0.5 h1)
        hw2 (* 0.5 w2)
        hh2 (* 0.5 h2)
        tstart (radians start)
        textent (radians (+ start extent))
        tmid (* 0.5 (- textent tstart))
;        nstart (- start)
;        nextent (- extent)
        
        ]
;    (println w1 h1 start ":" (* hw1  (Math/cos tstart))  (* hh1  (Math/sin tstart)))
;    (println w2 h2 extent ":" (* hw2  (Math/cos textent))  (* hh2  (Math/sin textent)))
;    (println tstart tmid textent (* hw2  (Math/cos tmid))  (* hh2  (Math/sin tmid)))
    (doto (new java.awt.geom.GeneralPath)
      (.moveTo (* hw1 (Math/cos tstart)) (* hh1 (Math/sin tstart)))
      (.lineTo (* hw2 (Math/cos tstart)) (* hh2 (Math/sin tstart)) )
      (.quadTo (* hw2 (Math/cos tmid)) (* hh2 (Math/sin tmid))  (* hw2 (Math/cos textent)) (* hh2 (Math/sin textent)) )
      (.lineTo (* hw1 (Math/cos textent)) (* hh1 (Math/sin textent)) )
      (.quadTo (* hw1  (Math/cos tmid))  (* hh1  (Math/sin tmid))  (* hw1 (Math/cos tstart)) (* hh1 (Math/sin tstart)) )
      )
    ))

(defn correct-pie  [x y w h s e]
    (pie  (- x  (* 0.5 w) )  (- y  (* 0.5 h) ) w h  (- s)  (- e)))

(defn oarc
  [gc, iradius, oradius, start, extent]
  (let [odiam   (* 2 oradius)
        pie     (correct-pie 0 0, odiam odiam, start extent)
        piearea (new Area pie)
        c       (circle 0 0 iradius)
        carea   (new Area c)
        ]

    (.subtract piearea carea)
    piearea )
  )


;(ringarc 0 0, 10 10, 15 15, 0 90)
;
(defn hex
  "draw a hexagon"
  []
  )


(defn point-on-ellipse [x y w h deg]
  (let [t (radians (- 90 deg))]
  [ (+ x (* w (Math/cos t))) 
    (+ y (* h (Math/sin t)))
   ]
    )
  )

(defn circle-arc-length [radius degrees]
  (/ (* degrees PI radius) 180)
  )

(defn half [x] (* 0.5 x))

;thank you, http://www.had2know.com/academics/inner-circular-ring-radii-formulas-calculator.html
(defn calculate-circle-ring-radius 
  "R: radius of outer circle
  n: number of inner circles
  returns: radius of smaller circles"
  [R n]
  (if (= 1 n) R
    (let [s (Math/sin (/ PI n))
          r  (/
               (* R s)
               (+ 1 s))
          ]
      r )))

