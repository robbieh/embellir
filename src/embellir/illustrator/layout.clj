(ns embellir.illustrator.layout
  (:require 
     [embellir.illustrator.screen :as screen]
     [embellir.illustrator.window :as window]
     [embellir.illustrator.entities :only entities]
     )
  (:use seesaw.core
     seesaw.graphics
     seesaw.color
     )
  )

;; can I use swing? probably not easily...
;;
;; What do I need?
;; Different schemes for laying out doodles
;; and differente classes of doodles
;;
;; hard cases:
;; * doodles which "hold" and manage other doodles ...
;;
;; grid layout
;;
;; assume each entity is a square
;; n entities
;; screen x by y
;; from 1 to n:
;; size of squares when all one column? (min x/1, y/n-0)
;;                    when two columns? (min x/2, y/n-1)
;;                               three? (min x/3, y/n-2)
;;

(defn get-optimal-size [n x y]
  (for [cols (range 1 (inc n))]
    (let [rows (int  (clojure.math.numeric-tower/ceil (/ n cols)))
          colsize (double (/ x cols))
          rowsize (double (/ y rows))
          difference (clojure.math.numeric-tower/abs (- colsize rowsize ))
          biggest (max colsize rowsize)
          smallest (min colsize rowsize)
          ]
      [cols rows colsize rowsize smallest]    
      )
    )
  )
(comment
  (get-optimal-size 2 1367 770)
  (for [x (range 1 11)]
    (conj [] x (apply max (map last (get-optimal-size x 1367 770)))))
  )

