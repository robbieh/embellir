(ns embellir.doodles.christmas.boxes
  (:use 
    seesaw.graphics
    seesaw.color
    embellir.illustrator.util
    [embellir.illustrator.entitylist :only [entities]]
    [embellir.curator :only [get-curio]]
    )
  (:require [clojure.walk :as walk])
  )

(def thickness 10)
;(def private-data (atom {}))

(defn vcontainer "split into left and right components"
  [graphics x y w h & [percent fna fnb]]
  (let [;x (+ x thickness)
        ;y (+ y thickness)
        ;w (- w (* 2 thickness))
        ;h (- h (* 2 thickness))
        xa x
        ya y
        wa (* percent w)
        ha h
        xb (+ x wa)
        yb y
        wb (* (- 1 percent) w)
        hb h
        ]
    ((first fna) graphics xa ya wa ha (rest fna))
    ((first fnb) graphics xb yb wb hb (rest fnb))
    [container :v percent fna fnb]
    )
  )

(defn hcontainer "split into top and bottom components"
  [graphics x y w h & [percent fna fnb]]
  (let [;x (+ x thickness)
        ;y (+ y thickness)
        ;w (- w (* 2 thickness))
        ;h (- h (* 2 thickness))
        xa x
        ya y
        wa w
        ha (* percent h)
        xb x
        yb (+ y ha)
        wb w
        hb (* (- 1 percent) h)
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

(def random-box-list [whitebox redbox greenbox])
(defn pick-random-box [] (rand-nth random-box-list))
(defn pick-random-orientation []  (rand-nth [:h :v]))

;(def boxtree (atom [(pick-random-box)]))
(comment def boxtree (atom [container :h 0.5 
                      [container :v 0.5 [redbox] [whitebox]] 
                      [container :v 0.3 [greenbox] 
                            [container :v 0.4 [container :h 0.2 [whitebox] [redbox]] [greenbox]]]]
                    ))
(def boxtree (atom [(pick-random-box)]))

(defn split-noncontainers [item]
  (if (and (vector? item) (not (= container (first item))))
    ;[container (pick-random-orientation) (rand) [(pick-random-box)] item]
    [container (pick-random-orientation) (+ 0.1 (rand 0.8)) [(pick-random-box)] item]
    item))

(defn merge-containers [item]
  (if (and (vector? item) (= container (first item))
           (not (= container (nth item 3) (nth item 4))))
        (if (> 0.5 (rand))
          (nth item 3)
          (nth item 4))
    item ))

(defn chance-split [item]
  (if (> 0.8 (rand))
    (split-noncontainers item)
    item))

(defn chance-merge [item]
  (if (> 0.2 (rand))
    (merge-containers item)
    item))

(defn draw-doodle [entname ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [sizex     (.getWidth panel)
        sizey     (.getHeight panel)
        ;ent       (get @entities entname)
        ] 
    (do ((first @boxtree) graphics 0 0 sizex sizey (rest @boxtree)))
  ))

(comment
  (def boxtree (atom [container :h 0.5 [greenbox] [redbox] ]))
  (def boxtree (atom [redbox]))
  (reset! boxtree (walk/postwalk split-noncontainers @boxtree))
  (reset! boxtree (walk/postwalk chance-split @boxtree))
  (reset! boxtree (walk/prewalk merge-containers @boxtree))
  (reset! boxtree (walk/prewalk chance-merge @boxtree))
  (dotimes [x 10]
    (reset! boxtree (walk/postwalk chance-split @boxtree))
    (reset! boxtree (walk/prewalk chance-merge @boxtree))
    (Thread/sleep 1000)
    )
  (pprint (nth @boxtree 2))
  (pprint @boxtree)
  (println (first @boxtree))
  (println (rest @boxtree))
  (walk/postwalk-demo @boxtree)
  )
