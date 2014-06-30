(ns embellir.illustrator.colors
  (:use 
        seesaw.color)
  )


(def palettes (atom {}))


(swap! palettes merge {:phosphor-green
  {:primary   {:main (color 0 220 20)  :highlight (color 0 90 0) :shadow (color 0 70 0) :fill (color 0 110 10)}
   :secondary {:main (color 15 90 55) :highlight (color 150 110 250) :shadow (color 5 25 0) :fill (color 10 10 10 )}
;   :tertiary {:main :highlight :shadow :fill}
;   :ok {:main :highlight :shadow :fill}
;   :warning {:main :highlight :shadow :fill}
;   :alert {:main :highlight :shadow :fill}

   }})

(swap! palettes merge {:white
  {:primary   {:main (color 255 255 255)  :highlight (color 242 243 233) :shadow (color 128 128 128) :fill (color 0 0 0 0 )}
   :secondary {:main (color 205 208 179) :highlight (color 225 228 199) :shadow (color 128 128 128) :fill (color 10 10 10 )}
;   :tertiary {:main :highlight :shadow :fill}
;   :ok {:main :highlight :shadow :fill}
;   :warning {:main :highlight :shadow :fill}
;   :alert {:main :highlight :shadow :fill}

   }}
  )

(swap! palettes merge {:red
  {:primary   {:main (color 200 0 0)  :highlight (color 90 00 0) :shadow (color 20 0 0) :fill (color 0 0 0 0)}
   :secondary {:main (color 90 15 15) :highlight (color 20 110 0) :shadow (color 20 0 10) :fill (color 10 10 10 )}
;   :tertiary {:main :highlight :shadow :fill}
;   :ok {:main :highlight :shadow :fill}
;   :warning {:main :highlight :shadow :fill}
;   :alert {:main :highlight :shadow :fill}

   }}
  )

(def x-palette
  {:primary   {:main (color 0 220 20)  :highlight (color 0 90 0) :shadow (color 0 0 0) :fill (color 0 0 0 0)}
   :secondary {:main (color 15 90 55) :highlight (color 00 110 90) :shadow (color 0 0 0) :fill (color 10 10 10 )}
;   :tertiary {:main :highlight :shadow :fill}
;   :ok {:main :highlight :shadow :fill}
;   :warning {:main :highlight :shadow :fill}
;   :alert {:main :highlight :shadow :fill}

   }
  )

(defn set-palette [p]
  (let [pk (keyword p)]
    (when (pk @palettes)) 
    (def default-palette (pk @palettes)))
  )
;(set-palette :red-palette)


(def default-palette (:phosphor-green @palettes))
;(def default-palette red-palette)
;(def default-palette white-palette)
