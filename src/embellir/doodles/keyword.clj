(ns embellir.doodles.keyword
  (:gen-class)
  (:require [embellir.illustrator :as illustrator])
  (:use      [quil.core]))

(defn binary-size-search [size sizemin text fitwidth]
  (if (>= sizemin size) 
    size
    (do 
      (text-size size)
      (let [twidth (text-width text)] ;caluclate text's actual width for this size
        (if (>= twidth fitwidth)
          (recur (/ size 2) sizemin text fitwidth)
          size)))))


(defn calc-header-size
  "Calcuate a comfortable header text size:
  It needs to fit in the bounding box,
  be sized proportional to the screen,
  but not be gigantic if the word is short
  and the box is long."
  [width text]
  ;for now, I'm just going to fit it in the box! this is a major TODO
  )


(defn draw-keyword [entity]
  (let [width (:width (:bound entity))
        height (:height (:bound entity))
        hawidth (* 0.5 height)
        haheight (* 0.5 width)
        curio (embellir.curator/get-curio "keyword")
        headertext (str (:keyword curio))
        bodytext (str (:text curio))
        headertextsize (binary-size-search 100 8 headertext width)
        textsize 10
        ]
    (stroke 255 0 255)
    (stroke-weight 10)
    (text-align :center)
    (text-mode :model)
    (translate 0 (+ (- haheight) (text-ascent) ))
    (text-size headertextsize)
    (text headertext 0 0)
    (translate 0 (text-descent))
    (text-size textsize)
    (translate 0 (text-ascent))
    (translate (- hawidth) 0)
    (text-align :left)
    (text bodytext 0 0)
    ))

(defn illustrate []
  (illustrator/create-entity "keyword" (illustrator/position 300 300) (illustrator/bound 100 100) (illustrator/drawing embellir.doodles.keyword/draw-keyword)))

  

