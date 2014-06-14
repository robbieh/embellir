(ns embellir.doodles.cjdnspeers
  (:import 
     ;      java.awt.AlphaComposite
     )
  (:use 
    [ipviz.core :only [draw-ip-polygon]]
    seesaw.graphics
    seesaw.color
    embellir.illustrator.util
    [embellir.illustrator.entitylist :only [entities]]
    [embellir.curator :only [get-curio]]
    )
  (:require 
     [cjdnsadmin-clj.util :as cjd :only [public-to-ip6]]
     [clj-time.core :as clj-time]
     [clj-time.local]) 
  )

; {:imgcache {"ip" {size img, size img, ...}
;  :iolast   {"ip" {:in x, :out y}}
; }
;  
;
(def private-data (atom {}))
(def qmax 10)

(def entityhints {:sleepms 5000,  :placement [0 0 250 250] :central-feature true})

; {"ip" persistenQueue({:in x :out y},...), ...}
(def iostats (atom {}))

(defn resolve-ip [ip6]
  (.getCanonicalHostName (java.net.Inet6Address/getByName ip6)))

(defn fetch-ip-img [ip6 size]
  (let [img (draw-ip-polygon ip6 {:size size}) ]
    (swap! private-data assoc-in [:imgcache ip6 size] img)
  img))

(defn new-ioq []
  (let [q (ref clojure.lang.PersistentQueue/EMPTY)
        qlist (repeat qmax {:in 0 :out 0} )
        ]
    (dosync (alter q (partial apply conj) qlist) )
  q))

(defn queue-io-stats [ip6 bin bout]
  (let [peerioq (or (get @iostats ip6) (new-ioq))
        iolast  (or (get-in @private-data [ip6 :iolast]) {:in 0 :out 0})
        peerio  (peek @peerioq )
        previn  (:in iolast)
        prevout (:out iolast)
        diffmap  {:in (- bin previn) :out (- bout prevout)}
        lastmap {:in bin :out bout}
        ]
    (if (not (= (:in diffmap) 0)) 
      (do
;      (println ip6 diffmap lastmap)
      (dosync (alter peerioq conj diffmap) )
      (dosync (alter peerioq pop) )
      (swap! iostats assoc-in [ip6] peerioq )
      (swap! private-data assoc-in [ip6 :iolast] lastmap )))
    lastmap
    )
  )


(defn draw-io-part [graphics {:keys [in out]} i seg start]
  (push graphics 
    (draw graphics
        (rect (+ start (* i seg)) 0, seg (- (Math/pow (Math/log10 in) 2))) (style :foreground "lightgreen" :background "lightgreen")
        (rect (+ start (* i seg)) 0, seg (Math/pow (Math/log10 out) 2)) (style :foreground "lightgreen" :background "lightgreen")
          
          )
        )
      )

(defn draw-io-line [graphics ip6 length start]
  (let [peerioq @(get @iostats ip6)
        lineseg (/ (- length start) qmax)
        ]
    (doall (map #(draw-io-part graphics %1 %2 lineseg start) peerioq (range qmax )))
    )
  )


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
           (let [xy       (radian-to-xy (* i peerrad) ringdia)
                 peer     (nth peerlist i)
                 peerip   (cjd/public-to-ip6  (get peer   "publicKey"))
                 bytesin  (get peer "bytesIn")
                 bytesout (get peer "bytesOut")
                 peername (resolve-ip peerip)
                 img      (get-ip-img peerip ipdia)
                 ]
             (queue-io-stats peerip bytesin bytesout)
             (draw graphics (linexy [0 0] xy )                  s4 )
             (push graphics
               (translate graphics (first xy) (last xy))
                (draw-image-at-xy graphics img [(- ipradius)  (- ipradius)] ) 
                (.drawString graphics (String. peername) (int (- ipradius)) (int ipradius)))
             (push graphics
               (.rotate graphics (* i peerrad))
               (draw-io-line graphics peerip (- ringdia ipradius ) ipradius)
                   
             )
               ;(.drawString graphics (str (:in iodelta)) (int (- ipradius)) (int (+ 20 ipradius)))
                   
             )
           )
         (draw-image-at-xy graphics hostimg [ (- ipradius) (- ipradius) ])
         ;(draw graphics (circle 0 0 ringdia) s1) 
    )
  ))

(comment
  (java.net.Inet6Address/getByName "fcd2:b843:787a:59f3:6345:7ac2:6df3:5523")
  (println (:iostats @private-data))
  (resolve-ip "fcd4:eeaf:b23e:8c9:ab84:d0cf:3f64:c55d")
  (seq @(get @iostats "fcdb:158c:ef6c:dfea:000f:bd01:a738:895f" ))
  (seq @(new-ioq))
  (get-io-delta "test" 2 2)
  (println @iostats)
  (magnitude 70)
  (Math/log10 8000)
  )
