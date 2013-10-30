(ns embellir.illustrator.layout
  (:require 
     [embellir.illustrator.screen :as screen]
     [embellir.illustrator.window :as window]
     )
  (:use seesaw.core
     seesaw.graphics
     seesaw.color
     [embellir.illustrator.entities :only [entities]]
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

(defn move-entity [entname x y]
  (let [canvas (get-in @entities [entname :canvas])
        ]
    (move! canvas :to [x y])
    )
  )

(defn make-optimal-size-table [n x y] 
  (apply conj {}   (for [cols (range 1 (inc n))]
    (let [rows (int  (clojure.math.numeric-tower/ceil (/ n cols)))
          colsize (double (/ x cols))
          rowsize (double (/ y rows))
          smallest (min colsize rowsize)
          xmargin (- x (* cols smallest))
          ymargin (- y (* rows smallest))
          ]
      {smallest  {:cols cols :rows rows :boxsize smallest :xmargin xmargin :ymargin ymargin} }))))

(defn get-optimal-size [n x y]
  (let [results (make-optimal-size-table n x y) 
        maximum (apply max (keys results))]
  (get results maximum))) 

(defn layout-grid []
  (let [candidates        (keys @entities)
        ccount            (count candidates)
        optimal           (get-optimal-size ccount
                                            (.getWidth window/xyz) 
                                            (.getHeight window/xyz))
        {:keys [cols
                rows
                boxsize 
                xmargin 
                ymargin]} optimal
        xstart            (/ xmargin 2)
        ystart            (/ ymargin 2)
        xseq              (map *   (flatten (repeat (range 0 cols))) (repeat boxsize))
        yseq              (flatten (map #(map * 
                                              (repeat cols %) 
                                              (repeat boxsize)) 
                                        (flatten (repeat (range 0 cols)))))
  ;      points            (flatten (map list (take ccount xseq) (take ccount yseq)))
        ]
    (
     (println candidates)
     (println cols rows boxsize xmargin ymargin)
;     (println points)
     (doall (map move-entity candidates xseq yseq))
     ))
  )

(comment
  (get-optimal-size 12 1367 770)
  (count @entities)
  (move-entity "c2" 10 50)
  (layout-grid)
  )

