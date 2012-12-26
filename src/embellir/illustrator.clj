(ns embellir.illustrator
  (:gen-class)
  (:require [clj-time]
            [clj-time.coerce]
            [clj-time.local])
  (:use [quil.core])
  )


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def entities "the entities in this component entity system" (atom []))

(defn create-component
  [compname & compmap]
  ;TODO: check that compmap is all keywords
  (intern *ns* (symbol compname) 
          (fn [& more] {(keyword compname) (zipmap compmap more)})))

(defn create-entity
  "Creates an entity with the specified components"
  [entname & entcomps]
  (let [entmap (apply merge entcomps)]
    (swap! entities #(conj % (conj {:name entname} entmap)))))

(defn remove-entity
  "Removes entity with the given name"
  [entname]
  (swap! entities #(remove (fn match-name [entity] (= (:name entity) entname)) %)))

(defn get-entities 
  "returns all entities with the requested component
  e.g. (get-entites :position)"
  [component]
  (filter #(contains? % component) @entities))

(defn sys-draw
  "draws everything with a draw component, using the :fn from it"
  []
  ;  (println (get-entities :draw))
  (doseq [entity (get-entities :drawing)]
    (let [drawcomp (:drawing entity)
          poscomp (:position entity)
          drawfn (:fn drawcomp)
          ]
      (push-style) (push-matrix) 
      (translate (:x poscomp) (:y poscomp))
      (drawfn entity)
      (pop-matrix) (pop-style)
      )))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;standard components
(create-component "position" :x :y :r)
(create-component "bound" :shape :width :height :more)
(create-component "drawing" :fn)
(create-component "moveto" :x :y :starttime :endtime :rate-fn)
(create-component "spin" :endrad :starttime :endtime :rate-fn)
(create-component "resize" :endwidth :endheight :starttime :endtime :rate-fn)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn setup []
  (create-entity "polar clock"
                 (position (* 0.5 (width)) (* 0.5 (height)))
                 (bound :round (min (width) (height)) (min (width) (height)))
                 (drawing embellir.baubles.polarclock/draw-polarclock))

  (frame-rate 10)
  (smooth))

(defn draw []
  (background 0)
  (sys-draw)
  )

(defn start-sketch []
  (defsketch illustrator
             :title "embellir"
             :size [600 500]
             :setup setup
             :draw draw))

