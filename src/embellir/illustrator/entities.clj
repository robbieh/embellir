(ns embellir.illustrator.entities
;  (:gen-class)
  (:import  [java.awt.image BufferedImage]
     
     )
  (:require 
     [embellir.illustrator.screen :as screen]
     [embellir.illustrator.window :as window]
     [embellir.illustrator.renderer :as renderer]
            [seesaw.timer :as timer]
     )
  (:use seesaw.core
     seesaw.graphics
     seesaw.color
     [embellir.illustrator.entitylist :only [entities]]
     )
  )

(defn uniquename [n] n) ;TODO: check entity list

(defn load-entity [doodlename {:keys [placement sleepms entname] :as params} ]
  ;determine size, position
  ;resolve entity function
  ;create canvas with those attributes and overriden :paint
 (let [fqi (str "embellir.doodles." doodlename)
       placement (or placement [:fullscreen])
       bounds (screen/placement placement)
       entname' (if entname entname doodlename)
       sleepms (or sleepms 1000)
       params (or params {})
       ]
   (load-file (str "src/embellir/doodles/" doodlename ".clj"))
   (if (find-ns (symbol fqi))
     (if-let [func (resolve (symbol fqi "draw-doodle"))]
       (let [
             itemname (uniquename entname')
             paintfn (partial @func itemname)
             canvas (canvas :background (color 0 0 0 0) :bounds bounds
                            :paint paintfn)
             t (timer (partial renderer/repaint-entity itemname ) :repeats? true :delay sleepms :start? false)
             entmap (conj params {:canvas canvas :sleepms sleepms :timer t})
             ]
         (.start t)
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
    ;(println (config window/xyz :items))
    ;(println canvas)
    ;(println (remove #(= % canvas) (config window/xyz :items)))
    (config! window/xyz :items (remove #(= % canvas) (config window/xyz :items)))
    (swap! entities dissoc entname)
    )
  )

(comment 
(assoc {:a 1 } )
(load-entity "circle" {:placement [ :fullscreen] :sleepms 2000 :color "aqua"} )
(load-entity "circle" nil)
(remove-entity "circle")
(load-entity "circle" {:placement [10 10 150 150 ] :sleepms 2000 :entname "c2"} )
(load-entity "circle" {:placement [10 10 150 150 ] :sleepms 1000 :entname "c3"} )
(load-entity "circle" {:placement [10 10 150 150 ] :sleepms 1000 :entname "c4"} )
(load-entity "circle" {:placement [10 10 150 150 ] :sleepms 1000 :entname "c5"} )
(load-entity "circle" {:placement [10 10 150 150 ] :sleepms 1000 :entname "c6"} )
(load-file "src/embellir/doodles/polarclock.clj")
(load-file "src/embellir/doodles/polarclock.clj")
(load-file "src/embellir/doodles/polarclock.clj")
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
(remove-entity "c2")
(load-entity "polarclock" {:placement [ :fullscreen] :sleepms 1000
                           :central-feature true } )
(remove-entity "polarclock")
(remove-entity "ipviz")
(get entities "circle")
(load-entity "ipviz" {:placement [ :fullscreen] :sleepms 1000
                      :ip6 "fcd2:b843:787a:59f3:6345:7ac2:6df3:5523" } )
(do  (config! window/xyz :items nil)
     (reset! entities {}))

(config window/xyz :items )
(pprint  @entities)
(do 
(remove-entity "cjdnspeers")
(load-entity "cjdnspeers" {:placement [:fullscreen] :sleepms 500
                           :ip6 "fcd2:b843:787a:59f3:6345:7ac2:6df3:5523"
                           }))
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



