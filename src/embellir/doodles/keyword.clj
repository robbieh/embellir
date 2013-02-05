(ns embellir.doodles.keyword
  (:gen-class)
  (:import (java.util Calendar Date)
          (java.awt Graphics2D RenderingHints)
          (java.awt.image BufferedImage))
    (:require [embellir.illustrator :as illustrator]
            [clj-time.core :as clj-time]
            [clj-time.local])
    (:use     seesaw.graphics
            seesaw.color)
    )


;(defn binary-size-search [size sizemin text fitwidth]
;  (if (>= sizemin size) 
;    size
;    (do 
;      (text-size size)
;      (let [twidth (text-width text)] ;caluclate text's actual width for this size
;        (if (>= twidth fitwidth)
;          (recur (/ size 2) sizemin text fitwidth)
;          size)))))


(defn calc-header-size
  "Calcuate a comfortable header text size:
  It needs to fit in the bounding box,
  be sized proportional to the screen,
  but not be gigantic if the word is short
  and the box is long."
  [width text]
  ;for now, I'm just going to fit it in the box! this is a major TODO
  )


(defn draw-keyword [entity ^java.awt.Graphics2D graphics2D]
  (let [width (:width (:bound entity))
        height (:height (:bound entity))
        hawidth (* 0.5 height)
        haheight (* 0.5 width)
        curio (embellir.curator/get-curio "keyword")
        headertext (str (:keyword curio))
        bodytext (str (:text curio))
        ;headertextsize (binary-size-search 100 8 headertext width)
        headertextsize 10
        textsize 10
        ]
    (.setColor graphics2D (to-color "#C8FFC8"))
    (.drawString graphics2D headertext 5 10)
    (.drawString graphics2D bodytext 5 30)
    ))

(defn illustrate []
  (let [bi (java.awt.image.BufferedImage. (illustrator/scrwidth) (illustrator/scrheight) java.awt.image.BufferedImage/TYPE_INT_ARGB)
        gr (.createGraphics bi)]
    (doto gr (.setRenderingHint RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON))

    (illustrator/create-entity "keyword" 
                 (illustrator/position 0 0)
                 (illustrator/bound (illustrator/scrwidth) (illustrator/scrheight) :round)
                 (illustrator/drawing embellir.doodles.keyword/draw-keyword bi gr ))))



