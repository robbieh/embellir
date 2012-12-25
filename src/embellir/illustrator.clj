(ns embellir.illustrator
  (:gen-class)
  (:require [embellir.curator :as curator])
  (:use [quil.core])
  )
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn setup []
  (frame-rate 10)
  (smooth))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def entities
  "the entities in this component entity system"
  (atom []))

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
;      (println "drawing: " (:name entity))
;      (println "with: " drawfn)
;      (println "at: " (:x poscomp) (:y poscomp))
      (push-style) (push-matrix) 
      (translate (:x poscomp) (:y poscomp))
      (drawfn entity)
      (pop-matrix) (pop-style)
      )))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn draw-alpha [e]
  (stroke 255)
  (fill 255)
  (text (curator/get-curio "stringflipper1") 0 0))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(create-component "position" :x :y :r)
(create-component "bound" :shape :width :height :more)
(create-component "drawing" :fn)
(create-component "moveto" :x :y :starttime :endtime :rate-fn)
(create-component "spin" :endrad :starttime :endtime :rate-fn)
(create-component "resize" :endwidth :endheight :starttime :endtime :rate-fn)


(create-entity "alphaloop" 
               (position 20 20)
               (bound :square 20 20)
               (drawing  draw-alpha)
)


(defn xillus
  "load up the illustrations list with some simple tests
  start the curator, too"
  []

  (def illustrations (atom {} ) )

  )
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn draw []
  (background 0)
  (sys-draw)
  )

(defn mksk []
  (defsketch illustrator
             :title "embellir"
             :size [600 500]
             :setup setup
             :draw draw
             ))
