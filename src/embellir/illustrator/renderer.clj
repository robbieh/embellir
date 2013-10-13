(ns embellir.illustrator.renderer
;  (:gen-class)
  (:import [java.awt RenderingHints]
           [javax.swing JFrame JLabel JComponent] 
           [java.awt BorderLayout]
;           [java.awt Graphics]
;           [java.awt.image BufferedImage]
           ) 
  (:require [seesaw.core :as seesaw])
  (:use 
        seesaw.core
        seesaw.graphics
        seesaw.color
        embellir.illustrator.util 
        [embellir.illustrator.entities :only [entities]] 
        )
  )


(defn swing-paint-call [^javax.swing.JPanel canvas ^java.awt.Graphics2D graphics]
  ;; loop through entity list, draw all items onto the canvas
 (let [draw-image-graphics (partial draw-image graphics)] 
   (push graphics 
       ;draw-image g2d image x y
       (comment dorun (map copy-image-to-buffer (vals @entities)))
       (dorun (map draw-image-graphics (vals @entities))))))

(def xyz (seesaw/xyz-panel :background "#000" :paint swing-paint-call))
(def f (seesaw/frame :title "embellir" :width 500 :height 500 :content xyz :visible? true ) )

(comment
(repaint! xyz) 
(pprint @entities)
(map pprint (vals @entities)) 
(render-entity "test1" (get @entities "test1"))
(render-entity "test2" (get @entities "test2"))
(println continue-repainting?) 
(println continue-rendering?) 
(println (.getState render-thread))
(println (.getState repaint-thread))
)

(def repaint-sleepms 100)
(def continue-repainting? true)
(defn repaint-loop [] (while continue-repainting?  (do (repaint! xyz)  (Thread/sleep repaint-sleepms))))
(def repaint-thread (Thread. repaint-loop))
(.start repaint-thread)

(defn render-entity
  [entname]
  ; call the :function of the entity with the :frame and graphics for the frame, let it draw in (future)
  ; figure out next run time and update :next-time according to :periodms
  ;
  (let [{:keys [function sleepms frame]} (get @entities entname)
       g (.createGraphics frame)
       next-time (+ (now-long) sleepms)
       ]
;   (println "rendering" entname next-time)
       (locking frame (function frame g)) 
       (swap! entities assoc-in [entname :next-time] next-time)
   ) 
  )

(def continue-rendering? true)
(defn render-loop
  []
  ;; run through @entities and check the :next-time
  ;; if current time is past it, call render-entity on it
  ;; sleep until the next expected time
  (while continue-rendering?
    (dorun  (map render-entity (keys @entities) )) 
    (Thread/sleep 100)
    )
  )
(def render-thread (Thread. render-loop))
(.start render-thread)




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

