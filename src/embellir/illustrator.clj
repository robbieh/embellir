(ns embellir.illustrator
  (:gen-class)
  (:require [clj-time.core]
            [clj-time.coerce]
            [clj-time.local])
  (:use [quil.core])
  )


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def entities "the entities in this component entity system" (atom []))

(defn now-long [] (clj-time.coerce/to-long (clj-time.local/local-now)))

(defn create-component
  [compname & compmap]
  ;TODO: check that compmap is all keywords
  (intern *ns* (symbol compname) 
          (fn [& more] {(keyword compname) (zipmap compmap more)})))

(defn add-component-to-entity 
  [entityname, component]
  (swap! entities 
         #(map (fn merge-if [e] (if (= (:name e) entityname) (merge e component) e)) % )))

(defn remove-component-from-entity
  [entityname, componentkey]
  (swap! entities 
         #(map (fn dissoc-if [e] (if (= (:name e) entityname) (dissoc e componentkey) e)) % )))

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

(defn pctpoint [p1 p2 pct]
  (if (< p1 p2) (+ p1 (* (abs (- p1 p2)) pct))
                (- p1 (* (abs (- p1 p2)) pct))))

(defn move-linear [startpt endpt pct]
  (let [x (pctpoint (:x startpt) (:x endpt) pct)
        y (pctpoint (:y startpt) (:y endpt) pct) ]
    {:x x :y y}))

(defn sys-move
  "moves everything with a moveto component"
  []
  (doseq [entity (get-entities :moveto)]
    ;if there is no :x in :moveto, copy :x and :y from :position
    (let [entname (:name entity)
          mcomp (:moveto entity)
          poscomp (:position entity)
          now (now-long)
          starttime (:starttime mcomp)
          endtime (:endtime mcomp)
          rate-fn (:rate-fn mcomp)
          nnow (- now starttime) ;'normalized' now
          timediff (- endtime starttime)
          timepct (min 1 (/ nnow timediff)) ;what percentage of time has passed
          startpt (if (:x mcomp) (select-keys mcomp [:x :y]) ;use x,y if there, if not copy from position
                    (let [poscomp (:position entity)
                          entname (:name entity)
                          mcomp (:moveto entity)
                          newmcomp (assoc mcomp :x (:x poscomp) :y (:y poscomp))]
                      (add-component-to-entity entname {:moveto newmcomp})
                      (select-keys newmcomp [:x :y])))
          endpt {:x (:endx mcomp)  :y (:endy mcomp)}
          nowpt (move-linear startpt endpt timepct)
          newpos (merge poscomp nowpt)]
      (if (= 1 timepct) (remove-component-from-entity entname :moveto))
      (add-component-to-entity entname {:position newpos})
      )))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;standard components
(create-component "position" :x :y :r)
(create-component "bound" :shape :width :height :more)
(create-component "drawing" :fn)
(create-component "moveto" :endx :endy :starttime :endtime :rate-fn)
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
  (sys-move)
  )

(defn start-sketch []
  (defsketch illustrator
             :title "embellir"
             :size [600 500]
             :setup setup
             :draw draw))

