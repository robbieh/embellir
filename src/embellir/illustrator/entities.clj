(ns embellir.illustrator.entities
;  (:gen-class)
  (:import  [java.awt.image BufferedImage]
     
     )
  (:require 
     [embellir.illustrator.screen :as screen]
     [embellir.illustrator.window :as window]
     
     )
  (:use seesaw.core
     seesaw.graphics
     seesaw.color
     )
  )

; entities is a map of maps
; name is the main map key
; map should contain a minimum of :function and :peroid, or a :threadfn
; :function - fn to update g2d object, or nil if entity handles its own thread
; :sleepms  - how many ms to sleep between updates?
; :next-time - time at which it should next run
; :threadfn - fn that will be run in a thread
; :frame    - the image object
;
; { "circle" {:function embellir.doodles.circle/draw-circle
;             :period 1000
;             }
;   "clock"  {...}}
;   

(defonce entities (atom {}))
(def base-entity-map {:frame nil
                      :buffer nil
                      :x 0
                      :y 0
                      :sleepms 1000
                      :next-time 0
                      })

(defn uniquename [n] n) ;TODO: check entity list



(defn load-entity [doodlename {:keys [placement sleepms entname] :as params} ]
  ;determine size, position
  ;resolve entity function
  ;create canvas with those attributes and overriden :paint
 (let [fqi (str "embellir.doodles." doodlename)
       bounds (screen/placement placement)
       entname' (if entname entname doodlename)
       ]
   (load-file (str "src/embellir/doodles/" doodlename ".clj"))
   (if (find-ns (symbol fqi))
     (if-let [func (resolve (symbol fqi "draw-doodle"))]
       (let [
             itemname (uniquename entname')
             paintfn (partial @func itemname)
             canvas (canvas :background (color 0 0 0 0) :bounds bounds
                            :paint paintfn)
             sleepms' (if sleepms sleepms 1000)
             entmap (conj params {:canvas canvas :sleepms sleepms'})
             ]
         (.setOpaque canvas false)
         (swap! entities #(-> % (assoc itemname entmap)))
         (config! window/xyz :items (conj (config window/xyz :items) canvas))
         )
       )
     )
   ) 
  )


(defn remove-entity [entname]
  (let [canvas (get-in @entities [entname :canvas])]
    (println (config window/xyz :items))
    (println canvas)
    (println (remove #(= % canvas) (config window/xyz :items)))
    (config! window/xyz :items (remove #(= % canvas) (config window/xyz :items)))
    (swap! entities dissoc entname)
    )
  )

(comment 
(assoc {:a 1 } )
(load-entity "circle" {:placement [ :fullscreen] :sleepms 2000 :color "aqua"} )
(remove-entity "circle")
(load-entity "circle" {:placement [10 10 150 150 ] :sleepms 2000 :entname "c2"} )
(load-entity "circle" {:placement [10 10 150 150 ] :sleepms 1000 :entname "c3"} )
(load-file "src/embellir/doodles/polarclock.clj")
(symbol "embellir.doodles.circle")
(symbol "embellir.doodles.circle" "draw-doodle")
(symbol  "draw-doodle")
(type (symbol "embellir.doodles.circle" "draw-doodle"))
(find-ns (symbol "embellir.doodles.circle" ))
(resolve (symbol "embellir.doodles.circle" "draw-doodle"))
(resolve (symbol "embellir.doodles.polarlclcok" "draw-doodle"))
(fn?  @(resolve (symbol "embellir.doodles.circle" "draw-doodle")))
(type embellir.doodles.circle/draw-doodle)
(config! window/xyz :items (dissoc (config window/xyz :items) canvas))
(config window/xyz :items )
(remove-entity "polarclock")
(remove-entity "c2")
(pprint  @entities)
(load-entity "polarclock" {:placement [ :fullscreen] :sleepms 1000} )
(config! window/xyz :items nil)
(reset! entities {})
(get entities "circle")
  )











; (def img1 (buffered-image 100 100))
;
; (-> @entities (assoc "test" {:this "that"}))     
; (swap! e #(-> % (assoc "test" {:frame img1})))
;
; (assoc-in @e ["test" :x] 1)
; (swap! e assoc-in ["test" :x] 1)
;

; (find @e "test")            ; ["test" {:frame #<BufferedImage BufferedImage@3fec3fed: ... >}]
; (get-in @e ["test" :frame]) ; returns the actual BufferedImage

; (map :frame (vals @e))      ; returns a list with all the images



