(ns embellir.illustrator.components
  (:gen-class)
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


;standard components
(create-component "seesaw-xyz-panel" :xyz-panel )
(create-component "seesaw-canvas" :canvas )
(create-component "moveto" :endx :endy :starttime :endtime :rate-fn)
;(create-component "spin" :endrad :starttime :endtime :rate-fn)
(create-component "resize" :endwidth :endheight :starttime :endtime :rate-fn)
(create-component "icon-threshold" :minsize :icon)
(create-component "minimized" :status)
;(create-component "main-focus" :entity-name)
(create-component "layout" :priority :needs-attention)
(create-component "runme" :fn)


