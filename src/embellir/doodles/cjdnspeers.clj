(ns embellir.doodles.cjdnspeers
  (:import 
     ;      java.awt.AlphaComposite
     )
  (:use 
    [ipviz.core :only [draw-ip-polygon]]
    seesaw.graphics
    seesaw.color
    embellir.illustrator.util
    [embellir.illustrator.entities :only [entities]]
    [embellir.curator :only [get-curio]]
    )
  (:require 
     [cjdnsadmin-clj.util :as cjd :only [public-to-ip6]]
     [clj-time.core :as clj-time]
     [clj-time.local]) 
  )

; {:imgcache {"ip" {size img, size img, ...}}
;
(def private-data (atom {}))

(defn fetch-ip-img [ip6 size]
  (let [img (draw-ip-polygon ip6 {:size size}) ]
    (swap! private-data assoc-in [:imgcache ip6 size] img)
  img))

(defn get-ip-img [ip6 size]
  (or
        (get-in @private-data [:imgcache ip6 size]) 
        (fetch-ip-img ip6 size)))

(defn linexy [xy1 xy2]
  (line (first xy1) (last xy1) (first xy2) (last xy2)))
(defn radian-to-xy  [d radius]  
  [(* radius  (Math/cos d))  (* radius  (Math/sin d))])
(defn draw-image-at-xy [graphics img xy]
   (.drawImage graphics ^java.awt.Image img (int (first xy)) (int (last xy))  nil))

(defn draw-doodle [entname ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [sizex     (.getWidth panel)
        sizey     (.getHeight panel)
        halfsize  (* 0.5 (min sizex sizey))
        maxdia    (* 0.8 (min sizex sizey)) 
        maxradius (* 0.5 maxdia)
        ent       (get @entities entname)
;       color     (or (:color ent) java.awt.Color/GREEN)
        spacing   (* 0.2 maxdia)
        ipdia     (/  (* 0.8 maxdia) 3) ;diameter for IP visualizations
        ipradius  (* 0.5 ipdia)
        ringdia   (+ spacing ipdia)    ;diameter of ring on which peers will sit
        host      (:ip6 ent)
        hostimg   (get-ip-img host ipdia)
                s1  (style :foreground "red")
                s2  (style :foreground "green" :background "lightgreen")
                s3  (style :foreground "red" :background "pink")
                s4  (style :foreground "green" :stroke  (stroke :width 10))
                s4  (style :foreground "green" :stroke  (stroke :width 5))
        peerlist  (get-curio "cjdnspeers")
        peerct    (count peerlist)
        peerrad   (/ (* Math/PI 2) peerct)
        ] 
    ;;draw ring 
    ;;("switchLabel" "bytesOut" "lostPackets" "duplicates" "bytesIn" "isIncoming" "state" "last" "publicKey" "receivedOutOfRange")
    ;;
   (push graphics 
         (translate graphics halfsize halfsize)
         (doseq [i (range peerct)]
           (let [xy     (radian-to-xy (* i peerrad) ringdia)
                 peer   (nth peerlist i)
                 peerip (cjd/public-to-ip6  (get peer   "publicKey"))
                 img  (get-ip-img peerip ipdia)
                 ]
             (draw graphics (linexy [0 0] xy )                  s4 )
             (push graphics
               (translate graphics (first xy) (last xy))
                (draw-image-at-xy graphics img [(- ipradius)  (- ipradius)] ) 
                   )
             )
           )
         (draw-image-at-xy graphics hostimg [ (- ipradius) (- ipradius) ])
         ;(draw graphics (circle 0 0 ringdia) s1) 
    )
  ))

