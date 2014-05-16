(ns embellir.illustrator.layout
  (:require 
     [embellir.illustrator.screen :as screen]
     [embellir.illustrator.window :as window]
     )
  (:use seesaw.core
     seesaw.graphics
     seesaw.color
     [seesaw.util :only [to-dimension]]
     [embellir.illustrator.entitylist :only [entities]]
     )
  )


;; hard cases:
;; * doodles which "hold" and manage other doodles ...
;;

(defn move-entity [entname x y]
  (let [canvas (get-in @entities [entname :canvas]) ]
    (move! canvas :to [x y])))

(defn resize-entity [entname w h]
 (let [canvas (get-in @entities [entname :canvas])
       bounds (config canvas :bounds)
       x      (.getX bounds)
       y      (.getY bounds)
        ]
       (config! canvas :bounds [x y w h]))
  )

;;;;;;;;;;;;; grid layout ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn make-optimal-size-table [n x y] 
  (apply conj {}   (for [cols (range 1 (inc n))]
    (let [rows (int  (clojure.math.numeric-tower/ceil (/ n cols)))
          colsize (double (/ x cols))
          rowsize (double (/ y rows))
          smallest (min colsize rowsize)
          xmargin (- x (* cols smallest))
          ymargin (- y (* rows smallest))
          ]
      {smallest  {:cols cols :rows rows :boxsize smallest 
                  :xmargin xmargin :ymargin ymargin} }))))

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
        xseq              (map     *
                                   (flatten (repeat (range 0 cols))) 
                                   (repeat boxsize))
        yseq              (flatten (map #(map * 
                                              (repeat cols %) 
                                              (repeat boxsize)) 
                                        (flatten (repeat (range 0 cols)))))
        xmseq            (repeat xstart)
        ymseq            (repeat ystart)
        ]
    (
     (println candidates)
     (println cols rows boxsize xmargin ymargin)
     (doall (map move-entity candidates (map + xseq xmseq) (map + yseq ymseq)))
     (doseq [c candidates] 
       (resize-entity c boxsize boxsize)
       )))
  )
;;;;;;;;;;;;; central feature layout ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def central-feature (atom nil))

(defn set-central-feature []
  (let [filterfn   #(get-in % [1 :central-feature])
        candidates (keys (filter filterfn @entities))]
  (reset! central-feature (first candidates)) ;;TODO - this ain't pretty.
  candidates)
  )

(defn layout-central-feature []
  (let [w           (.getWidth window/xyz) 
        h           (.getHeight window/xyz) 
        orientation (if (> h w) :v :h )
        margin      (if (= orientation :v) (* 0.1 h) (* 0.1 w))
        marginseq   (map * (repeat margin) (range))
        bottom      (- h margin)
        candidates  (remove #(= % @central-feature) (keys @entities))
        w           (if (= orientation :v) w (- w margin))
        h           (if (= orientation :h) h (- h margin))
        ]
    (println candidates)
    (if (= orientation :h)
      (do  ;horizontal orientation
        (move-entity @central-feature margin 0)
        (resize-entity @central-feature w h)
        (doall (map move-entity candidates (repeat 0) marginseq))
        (doall (map resize-entity candidates (repeat margin) (repeat margin)))
        
        )  
      (do  ;vertical orientation
        (move-entity @central-feature 0 0)
        (resize-entity @central-feature w h)
        (doall (map move-entity candidates marginseq (repeat bottom)))
        (doall (map resize-entity candidates (repeat margin) (repeat margin)))
        )))
  )

(comment
  (get-optimal-size 12 1367 770)
  (count @entities)
  (move-entity "c2" 10 50)
  (layout-grid)
  (resize-entity "polarclock" 150 150)
  (to-dimension [1 :by 1])
  (.getWidth (config window/xyz :bounds))
  (set-central-feature)
  (layout-central-feature)
  (println @central-feature)
  (reset! central-feature "ip4map")
  (reset! central-feature "christmas/boxes")
  (filter #(get-in %1 [1 :central-feature]) @entities)

  )

