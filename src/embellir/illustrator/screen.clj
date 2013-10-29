(ns embellir.illustrator.screen
 (:require [embellir.illustrator.window :as window]) 
  )

(defn placement-given [bounds] bounds)

(defn placement-random [bounds]
  ;return a random place on the screen where a window of size x,y will fit
  [10 10 50 50] ;hah! I know that isn't random. 
  )

(defn placement-fullscreen [bounds] 
  (let [sizex (.getWidth window/xyz)
        sizey (.getHeight window/xyz)]
    [0 0 sizex sizey]))


(defn placement [bounds]
  ;depending on data in bounds, decide where the new canvas should go
  (cond
    (some #{:fullscreen} bounds) (placement-fullscreen bounds)
    (some #{:random} bounds) (placement-random bounds)
    (= 4(.length bounds) ) (placement-given bounds)
    :else (do (println "no sane bounds supplied, defaulting to 0,0 100x100") [0 0 100 100])))

(comment
  (placement [:fullscreen])
  (placement [:random])
  (placement [50 50 50 50])
  (placement [])
  )

(defonce screeninfo (atom {:default-size [100 100]
                           :placement placement-random
                          }))
