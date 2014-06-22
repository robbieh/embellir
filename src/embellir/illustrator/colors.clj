(ns embellir.illustrator.colors
  (:use 
        seesaw.color)
  )





(def phosphor-green-palette
  {:primary   {:main (color 0 220 20)  :highlight (color 0 90 0) :shadow (color 0 0 0) :fill (color 0 0 0 0)}
   :secondary {:main (color 15 90 55) :highlight (color 150 110 250) :shadow (color 5 25 0) :fill (color 10 10 10 )}
;   :tertiary {:main :highlight :shadow :fill}
;   :ok {:main :highlight :shadow :fill}
;   :warning {:main :highlight :shadow :fill}
;   :alert {:main :highlight :shadow :fill}

   }
  )

(def white-palette
  {:primary   {:main (color 255 255 255)  :highlight (color 242 243 233) :shadow (color 128 128 128) :fill (color 0 0 0 0 )}
   :secondary {:main (color 205 208 179) :highlight (color 225 228 199) :shadow (color 128 128 128) :fill (color 10 10 10 )}
;   :tertiary {:main :highlight :shadow :fill}
;   :ok {:main :highlight :shadow :fill}
;   :warning {:main :highlight :shadow :fill}
;   :alert {:main :highlight :shadow :fill}

   }
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
(def default-palette phosphor-green-palette)
;(def default-palette white-palette)
