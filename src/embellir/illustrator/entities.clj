(ns embellir.illustrator.entities
;  (:gen-class)
  (:import  [java.awt.image BufferedImage]
     
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



(comment 

(def img1 (buffered-image 100 100))
(def img2 (buffered-image 100 100))
(swap! entities #(-> % (assoc "test1" {:frame img1})))
(swap! entities #(-> % (assoc "test2" {:frame img2})))

(def img1buf (buffered-image 100 100) )
(def img2buf (buffered-image 100 100) )

(def canvas1 (canvas :background (color 0 0 0 255) :width 100 :height 100 :paint embellir.doodles.circle/draw-doodle))
(swap! entities #(-> % (assoc "test1" {:canvas canvas1})))

; (assoc-in @e ["test" :x] 1)
(swap! entities assoc-in ["test1" :sleepms] 2000)
(swap! entities assoc-in ["test2" :sleepms] 1000)

(swap! entities dissoc "test1")
(swap! entities dissoc "test2")

(swap! entities assoc-in ["test1" :frame] (let [g  (.getGraphics img1)] (seesaw.graphics/push g ( seesaw.graphics/draw g (rect 0 0 100 100) (style :foreground java.awt.Color/RED)))))

(def s1 (style :foreground java.awt.Color/RED :background java.awt.Color/RED))
(def s2 (style :foreground java.awt.Color/BLUE))

(let [g  (.createGraphics img1)] (draw g (rect 0 0 90 90) s1  ))
(let [g  (.getGraphics img1)] (draw g (rect 0 0 100 100) s2  ))
(let [g  (.createGraphics img1)] (draw g (line 0 0 100 100) s2  ))

(let [g  (.createGraphics img1)] g)
(.getWidth img1)
img1

(pprint @entities)

  )
