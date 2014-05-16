
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
(def myname (atom nil))
(def specials (atom 2))

(defn list-resources []  
  (seq (.listFiles (new java.io.File (.getFile (clojure.java.io/resource "christmas"))))))

(defn try-load-img [filename] 
  (try (javax.imageio.ImageIO/read  (new java.net.URL (str "file://" filename)))))

(defn not-nil [x] (not (nil? x)))

(def images   (vec (filter not-nil (map try-load-img (list-resources))))  )


(defn anim-speed [x]
  (when @myname
    (swap! entities assoc-in  [@myname :sleepms] x))
  )

(defn time-to-pct [[tgttime duration tgtpct rverse] ]
  (let [now     (clj-time.local/local-now)]
    (if (clj-time/after? now tgttime) 
      (if rverse 0.01 1)
      (let [i (clj-time/in-millis (clj-time/interval now tgttime))]
        (if rverse
          (/ i duration)
          (- 1 (/ i duration)))
        
        ))))


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
(def speed (atom 3))
(defn colorbox [graphics x y w h & more]
  (when (and (> w thickness ) (> h thickness))  
    (let [x (+ x thickness)
        y (+ y thickness)
        w (- w (* 2 thickness))
        h (- h (* 2 thickness))
        s (style :background (first (first more)))
        ]
   (draw graphics (rect x y w h) s)))
  [colorbox (first (first more))]
  )


(defn imagebox [graphics x y w h & more] 
  (let [x (+ x thickness)
        y (+ y thickness)
        w (- w (* 2 thickness))
        h (- h (* 2 thickness))
        w2 (- w thickness)
        h2 (- h thickness)
        ]
    (when (and (> w thickness) (> h thickness))
       (.drawImage ^java.awt.Graphics2D graphics 
         ^java.awt.Image (first (first more))
         x y  (+ x w) (+ y h)
         0 0 w2 h2
     nil)))
  )

(defn reindeer [graphics x y w h & more] )



(defn random-color-box [] 
  [colorbox (rand-nth ["red" "darkred" "green" "darkgreen" "white" "black"])])

(defn pick-random-orientation []  (rand-nth [:h :v]))

;(def boxtree (atom [(pick-random-box)]))

(defn random-box [] (if (< 0.95 (rand)) [imagebox (rand-nth images) ]
                      (random-color-box)
                      ))

(def boxtree (atom (random-box)))
(defn create-container [item]
    [container (pick-random-orientation) 
                 [(clj-time/plus (clj-time.local/local-now) (clj-time/secs @speed)) 
                 (* 1000 @speed) 
                 (+ 0.1 (rand 0.8)) false]
               (random-box) item])

(defn split-noncontainers [item]
  (if (and (vector? item) (fn? (first item)) (not (= container (first item))))
    ;[container (pick-random-orientation) (rand) [(pick-random-box)] item]
    (create-container item)
    item))

(defn cleanup-containers [item]
  (if (and (vector? item) (= container (first item))
           (true? (nth (nth item 2) 3))
           (clj-time/after? (clj-time.local/local-now) (first (nth item 2) ))
           )
        (nth item 4)
        ;(if (> 0.5 (rand)) (nth item 3) (nth item 4))
    item))

(defn merge-containers [item]
  (if (and (vector? item) (= container (first item))
           (not (= container (nth item 3) (nth item 4))))
    [container (nth item 1) 
               [(clj-time/plus (clj-time.local/local-now) (clj-time/secs @speed)) 
                 (* 1000 @speed) 
                 (+ 0.1 (rand 0.8)) true]
            (nth item 3) (nth item 4) ]
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
    (reset! myname entname)
    (do ((first @boxtree) graphics 0.0 0.0 sizex sizey (rest @boxtree)))
  ))

(def keep-moving (atom true))
(def sleep1 (atom 120000))
(def sleep2 (atom 240000))
(def sleep1 (atom 10000))
(def sleep2 (atom 10000))
(def tstate (atom "none"))
(defn movement-thread []
  (let [splitfn (partial walk/postwalk chance-split)
        mergefn (partial walk/postwalk chance-merge) 
        cleanfn (partial walk/postwalk cleanup-containers)]  
   (while @keep-moving
       (dotimes [x 3] 
         (anim-speed 100)
         (Thread/sleep 1000)
         (future (Thread/sleep (* 1000 @speed) ) (anim-speed 1000))
         (swap! boxtree splitfn) 
         (reset! tstate (str "splitfn sleep:" x))
         (Thread/sleep @sleep1)) 
       (dotimes [x 2] 
         (anim-speed 100)
         (Thread/sleep 1000)
         (future (Thread/sleep (* 1000 @speed) ) (anim-speed 1000))
         (swap! boxtree mergefn)
         (reset! tstate (str "mergefn sleep:" x))
         (future (Thread/sleep (+ 100 (* 1000 @speed)) )  (swap! boxtree cleanfn))
         (Thread/sleep @sleep2)
         )
     )
     (reset! tstate "stop") 
    ))

(comment
  (println @tstate)
  (.start (Thread. movement-thread))
  (reset! keep-moving false)
  (reset! keep-moving true)
  (reset! embellir.doodles.christmas.boxes/sleep1 120000)
  (reset! embellir.doodles.christmas.boxes/sleep2 240000)
  (reset! sleep2 240000)
  (reset! sleep1 5000)
  (reset! sleep2 5000)
  (reset! tstate "stop")
  (count images)
  (println images)
  (reset! boxtree [imagebox (rand-nth images)])
  (println images)
  (def boxtree (atom [container :h [(clj-time/plus (clj-time.local/local-now) (clj-time/secs 10)) 10 0.5] [greenbox] [redbox] ]))
  (def boxtree (atom [colorbox "darkblue"]))
  (reset! keep-moving false)
    (reset! speed 3)
    (reset! speed 10)
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
  (reset! boxtree (walk/prewalk cleanup-containers @boxtree))
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
  (println @tstate)
  (walk/postwalk-demo @boxtree)
  )
