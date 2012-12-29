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

(defn now-long 
  "returns the current local time as a long"
  [] (clj-time.coerce/to-long (clj-time.local/local-now)))

(defn create-component
  "create a function with the given name. The compmap must consist only of keywords.
  The created function will return a map with keywords matching the input."
  [compname & compmap]
  ;TODO: check that compmap is all keywords
  (intern *ns* (symbol compname) 
          (fn [& more] {(keyword compname) (zipmap compmap more)})))

(defn add-component-to-entity
  "adds or replaces the component for the matching entity"
  [entityname, component]
  (swap! entities 
         #(map (fn merge-if [e] (if (= (:name e) entityname) (merge e component) e)) % )))

(defn remove-component-from-entity
  "removes the compnent (which must be a keyword) from the matching entity"
  [entityname, componentkey]
  (swap! entities 
         #(map (fn dissoc-if [e] (if (= (:name e) entityname) (dissoc e componentkey) e)) % )))

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

(defn pctpoint
  [p1 p2 pct]
  (if (< p1 p2) (+ p1 (* (abs (- p1 p2)) pct))
                (- p1 (* (abs (- p1 p2)) pct))))

(defn move-linear 
  [startpt endpt pct]
  (let [x (pctpoint (:x startpt) (:x endpt) pct)
        y (pctpoint (:y startpt) (:y endpt) pct) ]
    {:x x :y y}))

(defn sys-move
  "updates the :position of every entity which has a :moveto component"
  []
  (doseq [entity (get-entities :moveto)]
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
                    (let [newmcomp (assoc mcomp :x (:x poscomp) :y (:y poscomp))]
                      (add-component-to-entity entname {:moveto newmcomp})
                      (select-keys newmcomp [:x :y])))
          endpt {:x (:endx mcomp)  :y (:endy mcomp)}
          nowpt (move-linear startpt endpt timepct)
          newpos (merge poscomp nowpt)]
      (if (= 1 timepct) (remove-component-from-entity entname :moveto))
      (add-component-to-entity entname {:position newpos})
      )))

(defn sys-resize
  "resizes the :bound component of every entity which has a :resize component"
  []
  (doseq [entity (get-entities :resize)]
    (let [entname (:name entity)
          reszcomp (:resize entity)
          bcomp (:bound entity)
          starttime (:starttime reszcomp)
          endtime (:endtime reszcomp)
          now (now-long)
          nnow (- now starttime) ;'normalized' now
          timediff (- endtime starttime)
          timepct (min 1 (/ nnow timediff)) ;what percentage of time has passed
          startdmsn (if (:startwidth reszcomp) (select-keys reszcomp [:startheight :startwidth])
                      (let [newreszcomp (assoc reszcomp :startwidth (:width bcomp) :startheight (:height bcomp))]
                        (add-component-to-entity entname {:resize newreszcomp})
                        (select-keys newreszcomp [:startwidth :startheight])))
          enddmsn (select-keys reszcomp [:endwidth :endheight])
          diffdmsn  {:width (- (:endwidth enddmsn) (:startwidth startdmsn))
                       :height (- (:endheight enddmsn) (:startheight startdmsn))}
          pctdmsn {:width (* timepct (:width diffdmsn)) :height (* timepct (:height diffdmsn))}
          newbound (merge bcomp {:width (+ (:width pctdmsn) (:startwidth startdmsn)) 
                                :height (+ (:height pctdmsn) (:startheight startdmsn))})
          ]
      (if (= 1 timepct) (remove-component-from-entity entname :resize))
      (add-component-to-entity entname {:bound newbound})
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
(create-component "icon-threshold" :minsize :icon)
(create-component "main-focus" :entity-name)
(create-component "minimized" :status)

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
  (sys-move)
  (sys-resize)
  (sys-draw)
  )

(defn start-sketch []
  (defsketch illustrator
             :title "embellir"
             :size [600 500]
             :setup setup
             :draw draw))

