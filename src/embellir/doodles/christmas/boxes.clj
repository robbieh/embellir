
(ns embellir.doodles.christmas.boxes
  (:use 
    seesaw.graphics
    seesaw.color
    embellir.illustrator.util
    [embellir.illustrator.entitylist :only [entities]]
    [embellir.curator :only [get-curio]]
    )
  (:require [clojure.walk :as walk]
            [clj-time.core :as clj-time]
            [clj-time.local])
  )

(def thickness 3)
;(def private-data (atom {}))

(defn time-to-pct [[tgttime duration tgtpct]]
  (let [now     (clj-time.local/local-now)]
    (if (clj-time/after? now tgttime) 
      1 ; - returning here! -
      (let [i (clj-time/in-secs (clj-time/interval now tgttime))]
        (- 1 (/ i duration))
        )
      )
    ))


(defn container [])


(defn vcontainer "split into left and right components"
  [graphics x y w h & [percent fna fnb]]
  (let [p  (if (vector? percent) (* (time-to-pct percent) (nth percent 2) ) percent)
        xa x
        ya y
        wa (* p w)
        ha h
        xb (+ x wa)
        yb y
        wb (* (- 1 p) w)
        hb h
        ]
    ((first fna) graphics xa ya wa ha (rest fna))
    ((first fnb) graphics xb yb wb hb (rest fnb))
    [container :v percent fna fnb]
    )
  )

(defn hcontainer "split into top and bottom components"
  [graphics x y w h & [percent fna fnb]]
  (let [p  (if (vector? percent) (* (time-to-pct percent) (nth percent 2)) percent)
        xa x
        ya y
        wa w
        ha (* p h)
        xb x
        yb (+ y ha)
        wb w
        hb (* (- 1 p) h)
        ]
    ((first fna) graphics xa ya wa ha (rest fna))
    ((first fnb) graphics xb yb wb hb (rest fnb))
    [container :h percent fna fnb]
    )
  )

(defn container [graphics x y w h [orientation percent fna fnb]]
    (case orientation
      :v (vcontainer graphics x y w h percent fna fnb)
      :h (hcontainer graphics x y w h percent fna fnb)
      :else (println "don't know how to handle: " orientation percent fna fnb)
      ) 
  )

(def s-white (style :background "white"))
(def s-red (style :background "red"))
(def s-green (style :background "green"))
(def s-black (style :background "black"))
(def speed (atom 5))
(defn colorbox [graphics x y w h & more]
  (let [x (+ x thickness)
        y (+ y thickness)
        w (- w (* 2 thickness))
        h (- h (* 2 thickness))
        s (style :background (first (first more)))
        ]
   (draw graphics (rect x y w h) s))
  [colorbox (first (first more))]
  )

(defn blackbox [graphics x y w h & more]
  (let [x (+ x thickness)
        y (+ y thickness)
        w (- w (* 2 thickness))
        h (- h (* 2 thickness))]
   (draw graphics (rect x y w h) s-black ))
  [blackbox]
  )

(defn whitebox [graphics x y w h & more]
  (let [x (+ x thickness)
        y (+ y thickness)
        w (- w (* 2 thickness))
        h (- h (* 2 thickness))]
   (draw graphics (rect x y w h) s-white ))
  [whitebox]
  )
(defn greenbox [graphics x y w h & more]
  (let [x (+ x thickness)
        y (+ y thickness)
        w (- w (* 2 thickness))
        h (- h (* 2 thickness))]
   (draw graphics (rect x y w h) s-green ))
  [greenbox]
  )
(defn redbox [graphics x y w h & more]
  (let [x (+ x thickness)
        y (+ y thickness)
        w (- w (* 2 thickness))
        h (- h (* 2 thickness))]
   (draw graphics (rect x y w h) s-red ))
  [redbox]
  )

(defn candystripe [graphics x y w h & more] )

(defn reindeer [graphics x y w h & more] )

(def random-box-list [blackbox blackbox whitebox redbox greenbox])
(defn pick-random-box [] (rand-nth random-box-list))
(defn random-color-box [] 
  [colorbox (rand-nth ["red" "darkred" "green" "darkgreen" "white" "black"])])

(defn pick-random-orientation []  (rand-nth [:h :v]))

;(def boxtree (atom [(pick-random-box)]))
(comment def boxtree (atom [container :h 0.5 
                      [container :v 0.5 [redbox] [whitebox]] 
                      [container :v 0.3 [greenbox] 
                            [container :v 0.4 [container :h 0.2 [whitebox] [redbox]] [greenbox]]]]
                    ))
(def boxtree (atom [(pick-random-box)]))
(defn create-container [item]
  (let [duration @speed]
    [container (pick-random-orientation) 
                 [(clj-time/plus (clj-time.local/local-now) (clj-time/secs duration)) 
                 duration 
                 (+ 0.1 (rand 0.8)) ]
               (random-color-box) item])
  )
(defn split-noncontainers [item]
  (if (and (vector? item) (fn? (first item)) (not (= container (first item))))
    ;[container (pick-random-orientation) (rand) [(pick-random-box)] item]
    (create-container item)
    item))

(defn merge-containers [item]
  (if (and (vector? item) (= container (first item))
           (not (= container (nth item 3) (nth item 4))))
        (if (> 0.5 (rand))
          (nth item 3)
          (nth item 4))
    item ))

(defn chance-split [item]
  (if (> 0.80 (rand))
    (split-noncontainers item)
    item))

(defn chance-merge [item]
  (if (> 0.20 (rand))
    (merge-containers item)
    item))

(defn draw-doodle [entname ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [sizex     (double (.getWidth panel))
        sizey     (double (.getHeight panel))
        ;ent       (get @entities entname)
        ] 
    (do ((first @boxtree) graphics 0.0 0.0 sizex sizey (rest @boxtree)))
  ))

(def keep-moving (atom true))
(def sleep1 (atom 120000))
(def sleep2 (atom 240000))
(def tstate (atom "none"))
(defn movement-thread []
  (let [splitfn (partial walk/postwalk chance-split)
        mergefn (partial walk/postwalk chance-merge)] 
   (while @keep-moving
       ;(when ())
       (dotimes [x 5] (swap! boxtree splitfn) 
         (reset! tstate (str "splitfn sleep:" x))
         (Thread/sleep @sleep1)) 
       (reset! tstate "cycle sleep:")
       (Thread/sleep @sleep2)
       (dotimes [x 5] (swap! boxtree mergefn)))
     (reset! tstate "stop") 
    ))

(comment
  (.start (Thread. movement-thread))
  (println @tstate)
  (reset! keep-moving false)
  (reset! keep-moving true)
  (reset! embellir.doodles.christmas.boxes/sleep1 120000)
  (reset! embellir.doodles.christmas.boxes/sleep2 240000)
  (reset! sleep2 240000)
  (reset! sleep1 10)
  (reset! sleep2 20)
  (reset! tstate "stop")
  (def boxtree (atom [container :h [(clj-time/plus (clj-time.local/local-now) (clj-time/secs 10)) 10 0.5] [greenbox] [redbox] ]))
  (def boxtree (atom [colorbox "darkblue"]))
  (reset! keep-moving false)
    (reset! speed 1)
    (reset! speed 5)
    (reset! speed 120)
    (reset! speed 180)
    (reset! speed 360)
    (reset! speed 720)
  (time (reset! boxtree (walk/postwalk split-noncontainers @boxtree)))
  (time (walk/postwalk split-noncontainers @boxtree))
  (let [walkfn (partial walk/postwalk split-noncontainers)]
    (time (swap! boxtree walkfn ))
    )
  (time (swap! boxtree (partial walk/postwalk split-noncontainers) ))
  (reset! boxtree (walk/postwalk split-noncontainers @boxtree))
  (reset! boxtree (walk/postwalk chance-split @boxtree))
  (reset! boxtree (walk/prewalk merge-containers @boxtree))
  (reset! boxtree (walk/prewalk chance-merge @boxtree))
(println @speed)
  (do
    (reset! speed 10)
;    (reset! boxtree (walk/postwalk chance-split @boxtree))
;    (reset! boxtree (walk/postwalk chance-split @boxtree))
;    (Thread/sleep 10000)
    (reset! speed 180)
    (dotimes [x 10]
    (reset! boxtree (walk/postwalk chance-split @boxtree))
    (reset! boxtree (walk/prewalk chance-merge @boxtree))
    (Thread/sleep 90000)
    ))
  (pprint (nth @boxtree 2))
  (pprint @boxtree)
  (println (first @boxtree))
  (println (rest @boxtree))
  (walk/postwalk-demo @boxtree)
  )
