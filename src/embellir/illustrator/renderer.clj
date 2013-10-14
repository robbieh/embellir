(ns embellir.illustrator.renderer
;  (:gen-class)
  (:import [java.awt RenderingHints]
           [javax.swing JFrame JLabel JComponent] 
           [java.awt BorderLayout]
;           [java.awt Graphics]
;           [java.awt.image BufferedImage]
           ) 
  (:require [seesaw.core :as seesaw]
     [embellir.illustrator.entities]
     )
  (:use 
        seesaw.core
        seesaw.graphics
        seesaw.color
        embellir.illustrator.util 
        [embellir.illustrator.entities :only [entities]] 
        )
  )


(def xyz (seesaw/xyz-panel :background "#000" ))
(def f (seesaw/frame :title "embellir" :width 500 :height 500 :content xyz :visible? true ) )

(comment
(repaint! xyz) 
(pprint @entities)
(map pprint (vals @entities)) 
(render-entity "test1" (get @entities "test1"))
(render-entity "test2" (get @entities "test2"))
(println continue-rendering?) 
(println (.getState render-thread))

(def canvas1 (canvas :background (color 0 0 0 255) :bounds [50 50 100 100] :id :test1 
    :paint embellir.doodles.circle/draw-doodle))    
(def canvas2 (canvas :background (color 0 0 0 255) :bounds [75 75 100 100] :id :test2 
    :paint embellir.doodles.circle/draw-doodle))    
(swap! entities #(-> % (assoc "test1" {:canvas canvas1})))  
(swap! entities #(-> % (assoc "test2" {:canvas canvas2})))  
(swap! entities assoc-in ["test1" :sleepms] 2000)
(swap! entities assoc-in ["test2" :sleepms] 2000)
(config! xyz :items (conj (config xyz :items) canvas1))
(config! xyz :items (conj (config xyz :items) canvas2))
(config! xyz :items nil)
(swap! entities dissoc "test1")
(seesaw.dev/show-options xyz)
)


(defn repaint-entity
  [entname]
  ; call the :function of the entity with the :frame and graphics for the frame, let it draw in (future)
  ; figure out next run time and update :next-time according to :periodms
  ;
  (let [{:keys [function sleepms frame]} (get @entities entname)
       g (.createGraphics frame)
       next-time (+ (now-long) sleepms)
       ]
;   (println "rendering" entname next-time)
       (function frame g) 
       (swap! entities assoc-in [entname :next-time] next-time)
   ) 
  )

(def continue-repainting? true)
(defn repaint-loop
  []
  ;; run through @entities and check the :next-time
  ;; if current time is past it, call render-entity on it
  ;; sleep until the next expected time
  (while continue-rendering?
    (dorun  (map render-entity (keys @entities) )) 
    (Thread/sleep 100)
    )
  )
(comment def repaint-thread (Thread. repaint-loop))
(comment .start repaint-thread)




;remember: .put .take .peek
(def updateq-comparator (comparator (fn [a b] (clj-time.core/before? (:time a) (:time b)))))
(def updateq
  "A list of maps with :time to be executed and :collection-key of item to be executed"
  (java.util.concurrent.PriorityBlockingQueue. 5 updateq-comparator))


(comment defn queue-entity [entityname itemname]
    (.put updateq {:entity itemname
                   :time (clj-time.coerce/to-date-time
                           (+ 
                             (get-in @collection [itemname :time-to-live])
                             (clj-time.coerce/to-long (clj-time.local/local-now))))}))

(comment defn render-item [itemname]
  "Fetch the item from @collection 
  Then apply the item's :function to the item's :atom"
  (let [item (get @collection itemname)
        itematom (:atom item)
        itemfunc (:function item)]
    (swap! itematom itemfunc)))

(comment defn render-loop []
  (loop [item (.take ^java.util.concurrent.PriorityBlockingQueue updateq)]
    ;    (println "found item: " item (get item :collection-key))
    (try
      (let [itemtime (:time item)
            itemtimelong (clj-time.coerce/to-long (:time item))
            now (clj-time.coerce/to-long (clj-time.local/local-now))
            timedifference (- itemtimelong now)] ;negative when item time is in the past
        (if (< timedifference 0)
          (do (run-item (get item :collection-key))
            (queue-item (get item :collection-key)))
          (do (Thread/sleep (min timedifference 1000))
            (.put updateq item))))
      (catch Exception e (str "Exception in manage-queue: " (.getMessage e))))
    (recur (.take ^java.util.concurrent.PriorityBlockingQueue updateq))))

