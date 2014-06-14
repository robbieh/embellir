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
     [clj-time.local]
     [clojure.core.memoize]
     ) 
  (:use 
    [ipviz.core :only [draw-ip-polygon]]
    seesaw.graphics
    seesaw.color
    seesaw.font
    embellir.illustrator.util
    embellir.iutils
    )
  )

(def entityhints {:sleepms 5000 :central-feature true :placement :fullscreen })

(defn blend-colors [c1 c2 pct]
  (let [
        r (int (* pct (Math/abs (- (nth c1 0) (nth c2 0)) )))
        g (int (* pct (Math/abs (- (nth c1 1) (nth c2 1)) )))
        b (int (* pct (Math/abs (- (nth c1 2) (nth c2 2)) )))
        
        ]
    [r g b]
    )
  )

(defn iptype [ipstring]
  (if (re-find #":" ipstring) 6 4 ))

(defn resolve-ip6 [ip6]
  (.getCanonicalHostName (java.net.Inet6Address/getByName ip6)))

;(comment defn get-ip-img [ip6 size]
;  (or
;        (get-in @private-data [:imgcache ip6 size]) 
;        (fetch-ip-img ip6 size)))


(defn draw-image-at-xy [graphics img xy]
   (.drawImage graphics ^java.awt.Image img (int (first xy)) (int (last xy))  nil))


(def droidSansMono (font :name "DroidSansMono" :size 10))

(defn ip4-to-int [ipstr]
  (new BigInteger 1 (.getAddress (java.net.InetAddress/getByName ipstr))))
;(ip6-to-int "fcd4:eeaf:b23e:08c9:ab84:d0cf:3f64:c55d")
;(ip6-to-int "ffff:eeaf:b23e:08c9:ab84:d0cf:3f64:c55d")

(defn ip6-to-int 
  "ha ha! as if this is posisble.
  So just use first octet"
  [ip6str]
  (let [oct1 (first (clojure.string/split ip6str #":" ))]
    (Integer/parseInt oct1 16) ))

(defn ip-to-int [ipstr]
  (case (iptype ipstr) 4 (ip4-to-int ipstr) 6 (ip6-to-int ipstr)))

(defn draw-ufw-base [graphics-potato]
  (let [{:keys [graphics now sizex sizey size halfx halfy sbaseport sbaseip margin]} graphics-potato
        ]
;    (println "base")
    (push graphics 
          (translate graphics halfx halfy)
          (draw graphics
                (line 0 (- halfy margin) 0 (+ margin (- halfy))) sbaseport
                (iarc 0 0 (- size margin ) (- size margin) 15 150) sbaseip
                (iarc 0 0 (- size margin ) (- size margin) 195 150) sbaseip
                ))
    ))

(defn draw-ufw-item [ufwitem graphics-potato]
  (let [{:keys [graphics now sizex sizey size halfx halfy sallow saudit sblock margin]} graphics-potato
        ufwdata (:UFWDATA ufwitem)
        dpt     (Integer. (:DPT ufwdata)) 
        src     (:SRC ufwdata)
        iptype  (iptype src)
        stat    (:UFWSTAT ufwitem)
        style   (case stat "ALLOW" sallow "AUDIT" saudit "BLOCK" sblock)
        [px py] (port-to-xy halfy margin dpt iptype)
        [sx sy] (memoized-ip-to-xy size margin src iptype) 
        
        ]

    (push graphics 
          (translate graphics halfx halfy)
          (draw graphics (line px py sx sy) style)
          )
    )
  )


(defn port-to-xy [halfy margin dpt iptype]
(let [
        porttop (- halfy margin)
        portbtm (+ margin (- halfy))
        length  (- porttop portbtm)
        portmultiplier (/ length 65535)
        ipadjust (case iptype 4 -10, 6 10 )
      
      ]
  [ipadjust (+ (* portmultiplier dpt) margin (- halfy))]
  )  
  )

(defn draw-ufw-dpt [dptitem graphics-potato]
  ;(println dptitem)
  (let [{:keys [graphics now sizex sizey size halfx halfy sallow saudit sblock margin]} graphics-potato
        [l dptcount] dptitem
        [stat dpt iptype] l
        dpt (Integer. dpt)
        [x y] (port-to-xy halfy margin dpt iptype)
        ;porttop (- halfy margin)
        ;portbtm (+ margin (- halfy))
        ;length  (- porttop portbtm)
        ;portmultiplier (/ length 65535)
        style (case stat "ALLOW" sallow "AUDIT" saudit "BLOCK" sblock)
        ;ipadjust (case iptype 4 -10, 6 10 )
        metrics (.getFontMetrics graphics droidSansMono)
        sw      (.stringWidth metrics (str dpt))
        fontadjust (case iptype 4 (Integer. (- (* 2 x) sw)) 
                                6 (Integer. (* 2 x)))

        ]
;    (println dpt src portmultiplier (* portmultiplier dpt))
    (push graphics 
          (translate graphics halfx halfy)
          (draw graphics (circle x y dptcount ) style))

          ;fonts don't translate? urgh. had to manually add halfx, halfy
          (.setColor graphics (to-color "#11FF11"))
          (.setFont graphics droidSansMono)
          (.drawString graphics (str dpt) (int (+ halfx fontadjust)) (int (+ halfy y)))
;          (.drawString graphics (str dpt) (* 2 ipadjust) (+ (* portmultiplier dpt) margin))
    )
  )

(defn ip-to-xy [size margin src iptype ]
  (let [ipint (ip-to-int src)
        length  (circle-arc-length (- size margin ) 135)
        maxip   (case iptype 4 3372220415 6 65535)
        ipmultiplier (/ 135 maxip)
        iploc   (case iptype 4 (* ipmultiplier ipint) 6 (- length (* ipmultiplier ipint))) ;invert ip6 addresses
        ipdegree (case iptype 4 (+ 195 iploc) 6 (+ 195 iploc)) 
        xyvec   (point-on-ellipse 0 0 (* 0.5 (- size margin )) (* 0.5 (- size margin )) ipdegree)
        ]
    xyvec))
(def memoized-ip-to-xy (clojure.core.memoize/lru ip-to-xy :lru/threshold 10))
;(ip-to-xy {:size 500 :margin 15} "192.168.0.1" 4)
(comment (pprint (clojure.core.memoize/snapshot memoized-ip-to-xy)))

(defn draw-ufw-src [srcitem graphics-potato]
  (let [{:keys [graphics now sizex sizey size halfx halfy s2 sallow saudit sblock margin]} graphics-potato
        [l srccount] srcitem
        [stat src iptype] l
;        ipint (ip-to-int src)
        style (case stat "ALLOW" sallow "AUDIT" saudit "BLOCK" sblock)
;        metrics (.getFontMetrics graphics droidSansMono)
;        sw      (.stringWidth metrics src)
;        length  (circle-arc-length (- size margin ) 135)
;        maxip   (case iptype 4 3372220415 6 65535)
;        ipmultiplier (/ 135 maxip)
;        iploc   (* ipmultiplier ipint)
;        ipdegree (case iptype 4 (+ 195 iploc) 6 (+ 15 iploc)) 
;        xyvec   (point-on-ellipse 0 0 (* 0.5 (- size margin )) (* 0.5 (- size margin )) ipdegree)
;        xyvec   (point-on-ellipse 0 0 100 100 ipdegree)
        xyvec (memoized-ip-to-xy size margin src iptype)
        [x y]   xyvec
;        fontadjust (case iptype 4 (Integer. (- (* 2 ipadjust) sw)) 6 (Integer. (* 2 ipadjust)))
        ]

;    (println dpt src portmultiplier (* portmultiplier dpt))

    ;(println src     ipint      ipmultiplier         length            iploc                 ipdegree )
    ; 162.243.28.153 2733841561 6.942354168314688E-7 2341.114845455114 1897.9296356520283    1912.9296356520283
    ; 162.243.28.153 2733841561 27/674444083         2341.114845455114 109.44                124.443              
    ;
;    (println src ipint (long ipdegree) xyvec)

    (push graphics 
          (translate graphics halfx halfy)
          (draw graphics (circle x y srccount) style)

          ;fonts don't translate? urgh. have to had halfx to x
         ; (.setColor graphics (to-color "#11FF11"))
         ; (.setFont graphics droidSansMono)
         ; (.drawString graphics (str dpt) 
         ;    (int (+ halfx fontadjust ) ) 
            ; (int (+ (* portmultiplier dpt) margin))
             
             )
;          (.drawString graphics (str dpt) (* 2 ipadjust) (+ (* portmultiplier dpt) margin))
    )
  )



(defn do-doodle  [entname ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [
        sizex     (.getWidth panel)
        sizey     (.getHeight panel)
        graphics-potato 
          {:graphics graphics
          :now       (clj-time.local/local-now)
          :sizex     sizex
          :sizey     sizey
          :size      (min sizex sizey)
          :halfx     (* 0.5 sizex)
          :halfy     (* 0.5 sizey)
          :sbaseport (style :foreground (color :green 192) :background (color :green 192)) 
          :sbaseip   (style :foreground (color :green 64) :stroke (stroke :width 20)) 
          :sallow    (style :foreground (color :green 192) :background (color :green 192)) 
          :saudit    (style :foreground (color :yellow 192) :background (color :yellow 192)) 
          :sblock    (style :foreground (color :red 192) :background (color :red 192)) 
          :s2        (style :foreground (color :green 192) ) 
          :margin    (* 0.05 sizex)
          }
        ufwlist   (:list (curator/get-curio "ufw"))  
        dptfreq   (frequencies (map #(list (get-in % [:UFWSTAT]) (get-in % [:UFWDATA :DPT]) (iptype (get-in % [:UFWDATA :SRC]))) ufwlist))
        srcfreq   (frequencies (map #(list (get-in % [:UFWSTAT]) (get-in % [:UFWDATA :SRC]) (iptype (get-in % [:UFWDATA :SRC]))) ufwlist))
        ] 
    (doall ())
;    (println "tick" (pprint srcfreq))
    (draw-ufw-base graphics-potato)

    (doall (map draw-ufw-dpt dptfreq (repeat graphics-potato)))
    (doall (map draw-ufw-src srcfreq (repeat graphics-potato)))
    (doall (map draw-ufw-item ufwlist (repeat graphics-potato)))

  
     
;    (.setFont graphics2D droidSansMono)

  ))

(defn draw-doodle [entname ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (do-doodle entname panel graphics))

