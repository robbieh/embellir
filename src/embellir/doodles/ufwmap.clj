(ns embellir.doodles.ufwmap
  (:import 
     (java.util Calendar Date)
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage)
     ;      java.awt.AlphaComposite
     )
  (:require 
     [embellir.illustrator :as illustrator]
     [embellir.curator :as curator]
     [clj-time.core :as clj-time]
     [clj-time.local]) 
  (:use 
    [ipviz.core :only [draw-ip-polygon]]
    seesaw.graphics
    seesaw.color
    seesaw.font
    embellir.illustrator.util
    )
  )

(def private-data (atom {}))
(def qmax 10)
(defn blend-colors [c1 c2 pct]
  (let [
        r (int (* pct (Math/abs (- (nth c1 0) (nth c2 0)) )))
        g (int (* pct (Math/abs (- (nth c1 1) (nth c2 1)) )))
        b (int (* pct (Math/abs (- (nth c1 2) (nth c2 2)) )))
        
        ]
    [r g b]
    )
  )
(def c1 [0 255 0]) ;green
(def c2 [255 0 0]) ;red
(defn age-to-color [then now]
  ;now - red
  ;60 s - yellow
  ;60m - green
   (let [age-seconds (clj-time/in-secs (clj-time/interval then now)) ]
     (cond (< age-seconds 120) (blend-colors [255 0 0] [255 255 0] ( / (max  age-seconds 1) 120))
           (< age-seconds (* 60 10)) (blend-colors [255 255 0] [0 255 0] (/ age-seconds 600))
           :else [0 255 0]
)))
(defn age-to-alpha [then now]
     ;minute old? alpha 0%
     ;hour old? 50%
     ;day old? alpha 95%
     (let [age (clj-time/in-minutes (clj-time/interval then now))]
       (int (- 255 (* 255 (cond (< age 1) 0
                          (< age 60) (- 0.8 (/ age 60 2))
                          (< age 1440) (- 0.8  (/ age 1440 2))
                          :else 0.95))))))

(defn count-to-alpha [c]
  (max 120 (min c 255))
  )





(defn resolve-ip [ip6]
  (.getCanonicalHostName (java.net.Inet6Address/getByName ip6)))













(defn get-ip-img [ip6 size]
  (or
        (get-in @private-data [:imgcache ip6 size]) 
        (fetch-ip-img ip6 size)))


(defn draw-image-at-xy [graphics img xy]
   (.drawImage graphics ^java.awt.Image img (int (first xy)) (int (last xy))  nil))


(def droidSansMono (font :name "DroidSansMono" :size 30))
(defn draw-doodle [entname ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [now       (clj-time.local/local-now)
        sizex     (.getWidth panel)
        sizey     (.getHeight panel)
        size      (min sizex sizey)
        halfsize  (* 0.5 sizey)
        ufw       (get-curio "ufw") 
        s1        (style :foreground (color :green 128) :background (color :green 192)) 
        ] 

     
;    (.setFont graphics2D droidSansMono)

  ))

(comment
  (get-in  (get-curio "iplist") ["127.0.0.1" :last-seen])
  
  )
