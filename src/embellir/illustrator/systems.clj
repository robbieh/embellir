(ns embellir.illustrator.systems
  (:gen-class)
  (:require 
    [clojure.java.io :as io]
    [clojure.math.numeric-tower :as math]
    [seesaw.core :as seesaw]
    [clj-time.core]
    [clj-time.coerce]
    [clj-time.local])

    (:use embellir.illustrator.entities 
          embellir.illustrator.components)
  )

(def scrwidth 512)
(def scrheight 384)

(def current-layout)

(defn half [n] (* 0.5 n))

(defn now-long
  "returns the current local time as a long"
  [] (clj-time.coerce/to-long (clj-time.local/local-now)))

(defn pctpoint
  [p1 p2 pct]
  (if (< p1 p2) (+ p1 (* (math/abs (- p1 p2)) pct))
    (- p1 (* (math/abs (- p1 p2)) pct))))

(defn move-linear 
  [startpt endpt pct]
  (let [x (pctpoint (:x startpt) (:x endpt) pct)
        y (pctpoint (:y startpt) (:y endpt) pct) ]
    {:x x :y y}))


(defn pctpoint
  [p1 p2 pct]
  (if (< p1 p2) (+ p1 (* (math/abs (- p1 p2)) pct))
    (- p1 (* (math/abs (- p1 p2)) pct))))

(defn move-linear 
  [startpt endpt pct]
  (let [x (pctpoint (:x startpt) (:x endpt) pct)
        y (pctpoint (:y startpt) (:y endpt) pct) ]
    {:x x :y y}))

(defn sys-draw
  "draws everything with a draw component, using the :fn from it"
  [^javax.swing.JPanel canvas ^java.awt.Graphics2D graphics2D]
  ;  (println (get-entities :draw))
  (doseq [entity (get-entities :drawing)]
    (let [drawcomp (:drawing entity)
          poscomp (:position entity)
          x (:x poscomp)
          y (:y poscomp)
          drawfn (:fn drawcomp)
          g2d (:g2d drawcomp)
          img (:image drawcomp)
          ]
      ;      (push-style) (push-matrix) 
      ;      (translate (:x poscomp) (:y poscomp))
      (try
        (drawfn entity g2d)
        (.drawImage graphics2D, ^java.awt.Image img, ^Integer x, ^Integer y, nil)
        (catch Exception e (do (println "Removing this entity due to error: \n" entity
                                        "\n" (.printStackTrace e))
                             (remove-entity (:name entity)))
          ))
      ;      (pop-matrix) (pop-style)
      )))

(defn sys-move
  "updates the :position of every entity which has a :moveto component"
  []
  ;  (if (< 0 (count (get-entities :moveto))) (frame-rate 120) (frame-rate 10))
  (doseq [entity (get-entities :moveto)]
    (let [entname (:name entity)
          mcomp (:moveto entity)
          poscomp (:position entity)
          now (now-long)
          starttime (:starttime mcomp)
          endtime (:endtime mcomp)
          rate-fn (:rate-fn mcomp)
          nnow (- now starttime) ;'normalized' now
          timediff (- endtime starttime)
          timepct (min 1 (/ nnow timediff)) ;what percentage of time has passed
          startpt (if (:x mcomp) (select-keys mcomp [:x :y]) ;use x,y if there, if not copy from position
                    (let [newmcomp (assoc mcomp :x (:x poscomp) :y (:y poscomp))]
                      (add-component-to-entity entname {:moveto newmcomp})
                      (select-keys newmcomp [:x :y])))
          endpt {:x (:endx mcomp)  :y (:endy mcomp)}
          nowpt (move-linear startpt endpt timepct)
          newpos (merge poscomp nowpt)]
      (if (= 1 timepct) (remove-component-from-entity entname :moveto))
      (add-component-to-entity entname {:position newpos})
      )))

(defn sys-resize
  "resizes the :bound component of every entity which has a :resize component"
  []
  (doseq [entity (get-entities :resize)]
    ;  (if (< 0 (count (get-entities :moveto))) (frame-rate 120) (frame-rate 10))
    (try
      (let [entname (:name entity)
            reszcomp (:resize entity)
            bcomp (:bound entity)
            starttime (:starttime reszcomp)
            endtime (:endtime reszcomp)
            now (now-long)
            nnow (- now starttime) ;'normalized' now
            timediff (- endtime starttime)
            timepct (min 1 (/ nnow timediff)) ;what percentage of time has passed
            startdmsn (if (:startwidth reszcomp) (select-keys reszcomp [:startheight :startwidth])
                        (let [newreszcomp (assoc reszcomp :startwidth (:width bcomp) :startheight (:height bcomp))]
                          (add-component-to-entity entname {:resize newreszcomp})
                          (select-keys newreszcomp [:startwidth :startheight])))
            enddmsn (select-keys reszcomp [:endwidth :endheight])
            diffdmsn  {:width (- (:endwidth enddmsn) (:startwidth startdmsn))
                       :height (- (:endheight enddmsn) (:startheight startdmsn))}
            pctdmsn {:width (* timepct (:width diffdmsn)) :height (* timepct (:height diffdmsn))}
            newbound (merge bcomp {:width (+ (:width pctdmsn) (:startwidth startdmsn)) 
                                   :height (+ (:height pctdmsn) (:startheight startdmsn))})
            ]
        (if (= 1 timepct) (remove-component-from-entity entname :resize))
        (add-component-to-entity entname {:bound newbound})
        )
      (catch Exception e (do (println "Removing this resize from this entity due to error: \n" entity
                                      "\n" (.printStackTrace e))
                           (remove-component-from-entity (:name entity) :resize)))
      )))


(defn sys-runme
  "runs the functions specified by :runme components"
  []
  (doseq [entity (get-entities :runme)]
    (let [entname (:name entity)
          runfn (:fn (:runme entity))]
      (remove-entity entname)
      (runfn))))

(defn run-on-ui-thread
  "keeps a function which will be run once on the UI drawing thread, and then discarded.
  This allows the function to run with proper UI bindings. Works like this:
  (run-on-ui-thread #(println (width) (height)))"
  [func]
  (create-entity "runme function" (runme #(func))))

(defn layout-tiled
  "Create a grid and assign each entity to a cell."
  []
  (when (not-empty @entities)
    (let [size ((comp math/ceil math/sqrt) (count @entities)) ;size of the grid - it's always square
          pxsize (min (scrwidth) (scrheight)) ;size of the grid in pixels
          xpadding (* 0.5 (- (scrwidth) pxsize))
          ypadding (* 0.5 (- (scrheight) pxsize))
          blocksize (/ pxsize size)
          gridy (flatten (repeat size (range size)))
          gridx (sort gridy)
          gridpairs (for [x gridx] (vec (for [y gridy] [x y])))
          adjustx (fn adjust [i] (+ (* 0.5 blocksize) (* blocksize i) xpadding))
          adjusty (fn adjust [i] (+ (* 0.5 blocksize) (* blocksize i) ypadding))
          ; gridpxcenters (map #(+ (* 0.5 blocksize) (* blocksize %)) (range size))
          entnames (map #(:name %) (get-entities :drawing))
          biglist (interleave entnames (map adjustx gridx) (map adjusty gridy))
          ]
      ;        (println size gridy gridx entnames)
      ;        (println (map adjust gridx))
      ;        (println biglist)
      (loop [items biglist]
        (let [head (take 3 items)
              tail (drop 3 items)
              [entname x y] head
              ent (get-entity-by-name entname)
              poscomp (:position ent)
              bcomp (:bound ent)]
          (add-component-to-entity entname (moveto x y (now-long) (+ 250 (now-long))))
          ;        (add-component-to-entity entname {:position (assoc poscomp :x x :y y)})
          (add-component-to-entity entname (resize blocksize blocksize (now-long) (+ 250 (now-long))))
          ;        (add-component-to-entity entname {:bound (assoc bcomp :width blocksize :height blocksize)})
          (when (not-empty tail) (recur tail)))))))

(defn layout-spring
  "treat entities as if connected by springs (not yet implemented)"
  []
  )

(defonce current-layout  (atom layout-tiled))

(defn relayout "possibly changes, then reapplies the current layout" 
  ([]
   (run-on-ui-thread #(@current-layout)))
  ([layout-name]
   (let [func (resolve (symbol "embellir.illustrator" layout-name))]
     (when func (swap! current-layout (fn [_] func))))
   (relayout))
  )

(defn layout-major-central
  "Item with highest :layout :priority is maximized and centered.
  The rest are arranged in the available margin space. (not yet implemented)"
  []
  (when (not-empty @entities)
    (let [prioritized-list (prioritize-entities @entities)
          featured (first prioritized-list)
          remainder (rest prioritized-list)
          psize (min (scrwidth) (scrheight))
          minmargin (* 0.1 psize)
          margin (max minmargin (math/abs (- (scrwidth) (scrheight))))
          halfmargin (half margin)
          quartermargin (half halfmargin)
          ] 
      (println "width: " (scrwidth))
      (println "height: " (scrheight))
      (println "psize: " psize)
      (println "minmargin: " minmargin)
      (println "margin: " margin)

      (add-component-to-entity (:name featured) 
                               (moveto (+ halfmargin (half psize))
                                       (+ 0 (half psize))
                                       (now-long) 
                                       (+ 250 (now-long))))
      (add-component-to-entity (:name featured) 
                               (resize psize psize (now-long) (+ 250 (now-long))))

      (let [combined (interleave remainder (range))]
        (doseq [[ent mult] (partition 2 combined)]
          (add-component-to-entity (:name ent) 
                                   (moveto (+ halfmargin (half halfmargin) psize) (+ (* mult halfmargin) quartermargin) (now-long) (+ 250 (now-long))))
          (add-component-to-entity (:name ent) 
                                   (resize halfmargin halfmargin (now-long) (+ 250 (now-long))))
          ))

      )

    ;separate main item from the list, prioritize the rest
    ;figure out how to fit them

    ))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; the layout functions: see @layout ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;






;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn load-entity [entname]
  (let [fqi (str "embellir.doodles." entname)]
    ;    (when-not (find-ns (symbol fqi))
    (load-file (str "src/embellir/doodles/" entname ".clj"));)
    (if (find-ns (symbol fqi))
    ;; TODO  and I should get the return value and call create-entity
      (if-let [func (resolve (symbol fqi "new-doodle"))] (func))
      (println "could not find " entname)))
  )


(defn load-scheme
  "loads an initial set of entities"
  [filename]
  (with-open [rd (io/reader filename)]
    (doseq [line (line-seq rd)]
      (println line))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn drawloop [^javax.swing.JPanel canvas ^java.awt.Graphics2D graphics2D]
  ;; would be great if core.async only ran these when the corresponding components are available.
  (sys-runme)
  (sys-move)
  (sys-resize)

  ;TODO: this isn't right. shouldn't it call (repaint!) on entities which need it?
  ;;maybe it should be sys-repaint
  (sys-draw canvas graphics2D) 
  ;;or maybe there should be a component to draw at a rate separate from one to draw from a queue...
  (do (Thread/sleep (1000)))
  (drawloop)
  )

(defn create-doodle-canvas [drawfn ]
  ;; TODO: this should be SMART and figure out where to place something new 
  (let [c (seesaw/canvas :paint drawfn :bounds [10 100 50 50]) ]
    c))


