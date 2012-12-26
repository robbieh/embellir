(ns embellir.baubles.weather
  (:gen-class)
  (:use [embellir.illustrator :as illus]
        [embellir.curator :as curator]
        [quil.core])
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; provide curation of the weather conditions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn get-weather [mWeather]
  )

(curate "weather" {} embellir.baubles.weather/get-weather (* 60 60 100)) ;update hourly

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; draw the weather conditions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn draw-weather [e]
  )

(illus/create-entity "weather" 
                              (position (* (rand) (width)) (* (rand) (height)))
                              (bound :square 50 50)
                              (drawing draw-weather))

