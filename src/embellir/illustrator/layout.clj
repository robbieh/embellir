(ns embellir.illustrator.layout
  (:require 
     [embellir.illustrator.screen :as screen]
     [embellir.illustrator.window :as window]
     [clojure.math.numeric-tower]
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

(defn list-layout-candidates [] (keys (remove #(true? (:background (val %))) @entities)))
(defn list-background-candidates [] (keys (filter #(true? (:background (val %))) @entities)))

(defn move-entity [entname x y]
  (let [canvas (get-in @entities [entname :canvas]) ]
    (when canvas (move! canvas :to [x y])
      ))
  
  )

(defn resize-entity [entname w h]
 (let [canvas (get-in @entities [entname :canvas])
       bounds (when canvas (config canvas :bounds))
       x      (when bounds (.getX bounds))
       y      (when bounds (.getY bounds))
        ]
       (when canvas (config! canvas :bounds [x y w h])))
  )

;;;;;;;;;;;;; background handler ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn layout-background []
  (let [candidates (list-background-candidates)
        rcand      (rand-nth candidates)
        canvas     (get-in @entities [rcand :canvas])
        zindex     (dec (.getComponentCount window/xyz))
        ]
    (when rcand
      (move-entity rcand 0 0)
      (resize-entity rcand (.getWidth window/xyz) (.getHeight window/xyz))
      (.setComponentZOrder window/xyz canvas zindex))
    )
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
  (let [candidates        (list-layout-candidates)
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
     (println "Candidates:" candidates)
     (println cols rows boxsize xmargin ymargin)
     (doall (map move-entity candidates (map + xseq xmseq) (map + yseq ymseq)))
     (doseq [c candidates] 
       (println "resizing: " c boxsize)
       (resize-entity c boxsize boxsize)
       (println "done")
       )))
  )
;;;;;;;;;;;;; central feature layout ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def central-feature (atom nil))


(defn get-central-feature-list []
  (let [filterfn  #(get-in % [1 :central-feature])
        candidates (keys (filter filterfn @entities))]
  candidates)
  )


(defn set-central-feature []
  (let [filterfn   #(get-in % [1 :central-feature])
        candidates (keys (filter filterfn @entities))]
  (reset! central-feature (rand-nth candidates)) ;;TODO - this ain't pretty.
  candidates)
  )

(defn layout-central-feature []
  (layout-background)
  (if (nil? @central-feature) (set-central-feature))
  (let [w           (.getWidth window/xyz) 
        h           (.getHeight window/xyz) 
        orientation (if (> h w) :v :h )
        margin      (if (= orientation :v) (* 0.1 h) (* 0.1 w))
        marginseq   (map * (repeat margin) (range))
        bottom      (- h margin)
        candidates  (remove #(= % @central-feature) (list-layout-candidates))
        w           (if (= orientation :v) w (- w margin))
        h           (if (= orientation :h) h (- h margin))
        ]
    (println "CF:" @central-feature ", Candidates:" candidates)
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

;;;;;;;;;;;;; features-grid layout ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn layout-feature-grid []
  (layout-background)
  (if (nil? @central-feature) (set-central-feature))
  (let [w           (.getWidth window/xyz) 
        h           (.getHeight window/xyz) 
        orientation (if (> h w) :v :h )
        margin      (if (= orientation :v) (* 0.1 h) (* 0.1 w))
        marginseq   (map * (repeat margin) (range))
        bottom      (- h margin)
        candidates  (remove #(= % @central-feature) (list-layout-candidates))
        w           (if (= orientation :v) w (- w margin))
        h           (if (= orientation :h) h (- h margin))
        ]
    (println "CF:" @central-feature ", Candidates:" candidates)
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
;;;;;;;;;;;;; embossed corner layout ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn layout-embossed-corner []
  (layout-background)
  (if (nil? @central-feature) (set-central-feature))
  (let [w           (.getWidth window/xyz) 
        h           (.getHeight window/xyz) 
        wmargin     (* 0.1 w)
        hmargin     (* 0.1 h)
        candidates  (remove #(= % @central-feature) (list-layout-candidates))
        embw        (* 0.25 w)
        embh        (* 0.25 h)
        x           (- w embw wmargin) 
        y           (- h embh hmargin)
        ]
    (println "CF:" @central-feature)
    (move-entity @central-feature x y)
    (resize-entity @central-feature embw embh)
    )
  )





;;;;;;;;;;;;; main layout funcitons ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def current-layout layout-central-feature)
(defn relayout []  (current-layout))
(defn do-layout [data]
  (println data)
  (println (symbol "embellir.illustrator.layout" data))
  (println (resolve (symbol "embellir.illustrator.layout" data)))
  ((resolve (symbol "embellir.illustrator.layout" data))))

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

