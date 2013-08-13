(ns embellir.illustrator.components
  (:gen-class)
  (:require 
    [embellir.illustrator :as illustrator] 
            
            )
  )

    
(defn create-component
  "create a function with the given name. The compmap must consist only of keywords.
  The created function will return a map with keywords matching the input.
  e.g. (create-component \"foo\" :bar :baz :boo) creates a function such as this:
  (foo 1 2 3) => {:name \"foo\" :bar 1 :baz 2 :boo 3)"
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

;standard components
(create-component "seesaw-canvas" :canvas )
(create-component "moveto" :endx :endy :starttime :endtime :rate-fn)
;(create-component "spin" :endrad :starttime :endtime :rate-fn)
(create-component "resize" :endwidth :endheight :starttime :endtime :rate-fn)
(create-component "icon-threshold" :minsize :icon)
(create-component "minimized" :status)
;(create-component "main-focus" :entity-name)
(create-component "layout" :priority :needs-attention)
(create-component "runme" :fn)


