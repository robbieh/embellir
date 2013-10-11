(ns embellir.illustrator.entities
  (:gen-class)
  )

(defonce entities (atom {}))

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

