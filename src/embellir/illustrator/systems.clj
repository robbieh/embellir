(ns embellir.illustrator.components
  (:gen-class)
  (:require 
    [embellir.illustrator :as illustrator] 
            
            )
  )

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
