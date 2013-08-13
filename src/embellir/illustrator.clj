(ns embellir.illustrator
  (:gen-class)
  (:import [java.awt RenderingHints]
           [javax.swing JFrame JLabel JComponent] 
           [java.awt BorderLayout]) 
  ;           [java.awt Graphics]
  ;           [java.awt.image BufferedImage])
  (:require 
    [clojure.java.io :as io]
    [clojure.math.numeric-tower :as math]
    [seesaw.core :as seesaw]
    [clj-time.core]
    [clj-time.coerce]
    [clj-time.local])
  (:use seesaw.core
        seesaw.graphics
        seesaw.color)

  )

(def scrwidth 512)
(def scrheight 384)

(defonce entities (atom []))

(def current-layout)

(defn now-long 
  "returns the current local time as a long"
  [] (clj-time.coerce/to-long (clj-time.local/local-now)))

(defn create-entity
  "creates an entity with the specified components
  e.g. (create-entity \"foo\" (position 0 0) (drawing foo-draw-fn))"
  [entname & entcomps]
  (let [entmap (apply merge entcomps)]
    (swap! entities #(conj % (conj {:name entname} entmap)))))

(defn remove-entity
  "removes entity with the given name"
  [entname]
  (swap! entities #(remove (fn match-name [entity] (= (:name entity) entname)) %)))

(defn get-entity-by-name
  [nm]
  (filter (fn match-name [entity] (= (:name entity) nm)) @entities))

(defn get-entities 
  "returns all entities with the requested component
  e.g. (get-entites :position)"
  [component]
  (filter #(contains? % component) @entities))

;(defn add-g2d-to-entity
;  "Adds an empty java.awt.Graphics2D object to an entity with a draw component"
;  [entity]
;  
;  )


(defn pctpoint
  [p1 p2 pct]
  (if (< p1 p2) (+ p1 (* (math/abs (- p1 p2)) pct))
    (- p1 (* (math/abs (- p1 p2)) pct))))

(defn move-linear 
  [startpt endpt pct]
  (let [x (pctpoint (:x startpt) (:x endpt) pct)
        y (pctpoint (:y startpt) (:y endpt) pct) ]
    {:x x :y y}))








;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn run-on-ui-thread
  "keeps a function which will be run once on the UI drawing thread, and then discarded.
  This allows the function to run with proper UI bindings. Works like this:
  (run-on-ui-thread #(println (width) (height)))"
  [func]
  (create-entity "runme function" (runme #(func))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; the layout functions: see @layout ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn compare-entity-priority [a b]
  (let [apri (:priority (:layout a))
        bpri (:priority (:layout b))
        both (and (not (nil? apri)) (not (nil? bpri)))
        ax (not (nil? apri))
        bx (not (nil? bpri))
        ]
    (println (:name a) apri (:name b) bpri )
    (println both ax bx)
    (cond 
      both (compare apri bpri)
      ax   -1
      bx   1
      :else  (compare (:name a) (:name b))
      )))

(defn prioritize-entities [ents]
  (sort compare-entity-priority ents))

(defn half [n] (* 0.5 n))





;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn load-entity [entname]
  (let [fqi (str "embellir.doodles." entname)]
    ;    (when-not (find-ns (symbol fqi))
    (load-file (str "src/embellir/doodles/" entname ".clj"));)
    (if (find-ns (symbol fqi))
      (if-let [func (resolve (symbol fqi "illustrate"))] (func))
      (println "could not find " entname)))
  )


(defn load-scheme
  "loads an initial set of entities"
  [filename]
  (with-open [rd (io/reader filename)]
    (doseq [line (line-seq rd)]
      (println line))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn drawloop [^javax.swing.JPanel canvas ^java.awt.Graphics2D graphics2D]
  (sys-runme)
  (sys-move)
  (sys-resize)
  (sys-draw canvas graphics2D)
  )

(comment  (defn mkcomponent []
  (let [ component (proxy [JComponent ] []
                    (paint [g2d] (.drawLine g2d 0 0 100 100)  )
                    )
        ])
  )) 

(defn create-an-entity [drawfn freq name]
  (let [c (seesaw/canvas :paint drawfn :bounds [10 100 50 50])
        t (Thread. (fn [] 
                     (while true
                       (repaint! c)
                       (Thread/sleep (long (/ 1000 freq)) ) 
                       ) 

                     ))
        ]
    (.start t)
    c
    )
  
  )

(def xyz (seesaw/xyz-panel))
(defn start-illustrator []
  (illustrator.components/load-components)
  (let [
;        bp (border-panel :center (embellir.doodles.circle/mkcircle 12 "circle1") )
        f (seesaw/frame :title "foo" :width 500 :height 500 :content xyz :visible? true)]
    )

;if I just use Swing...
;  (let [ frame (JFrame. "embellir")]
;
;    (.setLayout frame (BorderLayout. )) 
;    (.setSize frame 500 500) 
;    (.setVisible frame true)
;    (.add frame (mkcomponent) BorderLayout/CENTER) 
;    ) 
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; throw-away functions ... please remove! ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

