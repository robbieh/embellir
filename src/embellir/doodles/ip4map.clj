(ns embellir.doodles.ip4map
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
        size      (min sizex sizey)
        ipsizex   (/ 65025 sizex)
        ipsizey   (/ 65025 sizey)
        halfsize  (* 0.5 sizey)
        ips       (get-curio "iplist") 
        s1        (style :foreground (color :green 128) :background (color :green 192)) 
        ] 
   (push graphics 
;         (translate graphics halfsize halfsize)
         (doseq [[ip flags] ips]
           (let [ ip           (java.net.Inet4Address/getByName ip)
                 ipnum        (new BigInteger 1 (.getAddress ip))
                 ipx          (Math/round (double (if (= 0 ipnum) 0.0 (mod ipnum 65025))))
                 ipy          (Math/round (double (/ ipnum 65025)))
                 x            (/ ipx ipsizex)
                 y            (/ ipy ipsizey)
                 ]
;            (println ip ipnum x y)
            (draw graphics (circle x y 3) s1 )
             
             )
           )
         
        
       
    )

  ))

(comment
  )
