(ns embellir.doodles.weather
  (:gen-class)
  (:import 
     (java.util Calendar Date)
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage)
     )
  (:require 
     [embellir.illustrator :as illustrator]
     [embellir.curator :as curator]
     [clj-time.core :as clj-time]
     [clj-time.local])
  (:use     
     embellir.iutils
     seesaw.graphics
     seesaw.color
     seesaw.font
     )
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; draw the weather conditions
; this is stupid simple at the moment ... TODO: nice graphics
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;sets of degrees and y-coordinates (on unit circle) to define points for drawing temperature
;o - outer, 10s place; i - inner, 1s place
;mv indicates direction of movement
(def odegs (atom (into '() (repeatedly 13 #(rand 360) ))))
(def oys   (atom (into '() (repeatedly 13 #(+ 0.5 (rand 0.5 ))))))
(def odegmv (atom (into '() (repeatedly 13 #(rand-nth [:cw :ccw :cw2 :ccw2])))))
(def idegs (atom (into '() (repeatedly 10 #(rand 360) ))))  
(def iys   (atom (into '() (repeatedly 10 #(+ 0.33 (rand 0.5 ))))))
(def idegmv (atom (into '() (repeatedly 10 #(rand-nth [:cw2 :ccw2])))))

(defn theta-mv [dir theta]
  (case dir
    :cw (mod (inc theta) 360)
    :ccw (mod (dec theta) 360)  
    :cw2 (mod (inc (inc theta)) 360)
    :ccw2 (mod (dec (dec theta)) 360)  
    theta
    )
  )



(defn perturb-points! []
  ;(swap! odegs #(map theta-mv @odegmv %) )
  ;(swap! idegs #(map theta-mv @idegmv %) )
  (reset! odegs (doall (map theta-mv @odegmv @odegs)) )
  (reset! idegs (doall (map theta-mv @idegmv @idegs)) )
  nil
  )

(def entityhints {:sleepms 5000})
(def droidSansMono (font :name "DroidSansMono" :size 22))

(def directions {"North" 0
                 "Northeast" 45
                 "East" 90
                 "Southeast" 135
                 "South" 180
                 "Southwest" 225
                 "West" 270
                 "Northwest" 315
                 })

(defn draw-weather-meter
  [entity ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [width (.getWidth panel)
        height (.getHeight panel)
        hawidth (* 0.5 height)
        haheight (* 0.5 width)
        diam (* 2 (* 0.9 (min hawidth haheight)))
        wcdiam (* 2 (* 0.95 (min hawidth haheight)))
        ltop (* 0.85 (min hawidth haheight))
        lbtm (* 0.80 (min hawidth haheight))
        lbtml (* 0.75 (min hawidth haheight))
        w (curator/get-curio "weather")

        p embellir.illustrator.colors/default-palette
        primary (:primary p)
        secondary (:secondary p)

        stroke-s (stroke :width 2)
        stroke-l (stroke :width 6)
        style-main-s (style :foreground (:main primary) :stroke stroke-s )
        style-hi-s (style :foreground (:highlight primary) :stroke stroke-s )
        style-shd-s (style :foreground (:shadow primary) :stroke stroke-s )
        style-main-l (style :foreground (:main primary) :stroke stroke-l )
        style-hi-l (style :foreground (:highlight primary) :stroke stroke-l )
        style-shd-l (style :foreground (:shadow primary) :stroke stroke-l )

        temp (Float. (str (:temp_f w))) 
        wc (Float. (str (:windchill_f w))) 

        ]
    ;(text-mode :model)
    ;(text-size 30)
    ;(text-align :center)


    ;text
    (push graphics

          (.setColor graphics (:highlight secondary))
          (.setFont graphics droidSansMono)

          (let [metrics (.getFontMetrics graphics droidSansMono)
                height  (.getHeight metrics)
                weather (:weather w)
                temp (:temp_f w)
                weatherofst (half (.stringWidth metrics weather))
                tempofst (half (.stringWidth metrics temp))
                ]

          (.drawString graphics  ^java.lang.String (:weather w) (int (- hawidth weatherofst)) (int (- haheight (half height)  )))
          ;    (translate 0 (+ (text-ascent) (text-descent)))
          (.drawString graphics  ^java.lang.String (:temp_f w) (int (- hawidth tempofst)) (int (+ haheight height))))
          ;    (translate 0 (+ (text-ascent) (text-descent)))
;          (.drawString graphics  ^java.lang.String (:wind_dir w) 0 120)
          ;    (translate 0 (+ (text-ascent) (text-descent)))
;          (.drawString graphics  ^java.lang.String (:wind_mph w) 0 160)
        )

    (push graphics
          (translate graphics hawidth haheight)
          ;draw temperature and temperature scale marks
          (draw graphics
                (iarc 0 0 diam diam 0 (* 360 (/ temp 100)) ) style-shd-l
                (iarc 0 0 diam diam 0 (* 360 (/ temp 100)) ) style-main-s
                (iarc 0 0 wcdiam wcdiam 0 (* 360 (/ wc 100)) ) style-main-s
                )
          
          (push graphics (doseq [x (range 100)]
            (if (= 0 (mod x 10))  
              (draw graphics (line 0 lbtml 0 ltop) style-main-s)
              (draw graphics (line 0 lbtm 0 ltop) style-shd-s) 
              )
            (rotate graphics 3.6)
            ))

          ;freezing mark
          (push graphics
                (rotate graphics -180)
                (rotate graphics (* 32 3.6))
                (draw graphics (line 0 lbtm 0 ltop) style-main-l)
                )
          ;comfort mark
          (push graphics
                (rotate graphics -180)
                (rotate graphics (* 66 3.6))
                (draw graphics (line 0 lbtm 0 ltop) style-main-l)
                )
          
          )

    ;draw wind speed & direction
    (push graphics
          (translate graphics hawidth haheight)
          (rotate graphics -180)
          ;(rotate graphics (get directions (:wind_dir w)))
          (rotate graphics (Integer. (str (:wind_degrees w))))
          (let [sz (Float. (str (:wind_mph w)))
                triangle (doto (new java.awt.geom.GeneralPath)
                           (.moveTo 5.0 0.0) (.lineTo 0.0 sz) (.lineTo -3.0 0.0) (.lineTo 3.0 0.0))
                ]
          (draw graphics triangle style-main-l)
          (draw graphics triangle style-shd-s)
          ;(draw graphics (line 0 0 0 (Float. (str (:wind_mph w)))) style-main-l)
          ;(draw graphics (line 0 0 0 (Float. (str (:wind_mph w)))) style-shd-s)
            
            )
         
          )
   
    
    ))

(defn draw-alt-weather 
  [entity ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [width (.getWidth panel)
        height (.getHeight panel)
        hawidth (* 0.5 height)
        haheight (* 0.5 width)
        diam (* 2 (* 0.9 (min hawidth haheight)))
        rad (* 0.9 (min hawidth haheight))
        wcdiam (* 2 (* 0.95 (min hawidth haheight)))
        ltop (* 0.85 (min hawidth haheight))
        lbtm (* 0.80 (min hawidth haheight))
        lbtml (* 0.75 (min hawidth haheight))

        ; mid marks boundary between drawing 10s and 1s
        diaminner (* 0.25 diam)
        diammid (* 0.5 diam)

        w (curator/get-curio "weather")

        p embellir.illustrator.colors/default-palette

        primary (:primary p)
        secondary (:secondary p)

        stroke-s (stroke :width 2)
        stroke-l (stroke :width 6)
        style-main-s (style :foreground (:highlight primary) :background (:main primary) :stroke stroke-s )
        style-hi-s (style :foreground (:highlight primary) :background (:fill primary ) :stroke stroke-s )
        style-shd-s (style :foreground (:shadow primary) :stroke stroke-s )
        style-main-l (style :foreground (:main primary) :stroke stroke-l )
        style-hi-l (style :foreground (:highlight primary) :stroke stroke-l )
        style-shd-l (style :foreground (:shadow primary) :stroke stroke-l )

        temp (Float. (str (:temp_f w))) 
        ;wc (Float. (str (:windchill_f w))) 

        ;o - outer, i -inner (o for 10s, i for 1s)
        ocount (quot temp 10)
        icount (mod temp 10)


       ;   (map vector odegs oys)

        owt (* 0.075 (min height width)) ;half the wedge thickness 
        iwt (* 0.5 owt) ;half the wedge thickness 
        ]

    ;text
    ;(println ocount (count @odegs) @odegs )
    ;(println icount (count @idegs) @idegs )
    (try (perturb-points!)
      (catch Exception e (spit "/home/robbie/err.txt" (.getStackTrace e)))
      )
    

    
    (comment push graphics

          (.setColor graphics (:highlight secondary))
          (.setFont graphics droidSansMono)

          (let [metrics (.getFontMetrics graphics droidSansMono)
                height  (.getHeight metrics)
                weather (:weather w)
                temp (:temp_f w)
                weatherofst (half (.stringWidth metrics weather))
                tempofst (half (.stringWidth metrics temp))
                ]

          (.drawString graphics  ^java.lang.String (:weather w) (int (- hawidth weatherofst)) (int (- haheight (half height)  )))
          (.drawString graphics  ^java.lang.String (:temp_f w) (int (- hawidth tempofst)) (int (+ haheight height))))
        )

    ;ring-arcs for temperature display
    (try (push graphics
          (translate graphics hawidth haheight)

          (doseq [i (range ocount)]
            (let [theta (nth @odegs i)
                  y     (* diam (nth @oys i))]
              (push graphics
                    (rotate graphics theta)
                    ;(draw graphics (rect -1 (- y 1) 2 2 ) style-main-l)
                    (draw graphics (ringarc 0 0 (- y owt) (- y owt) (+ y owt) (+ y owt) 0 20 ) style-main-s)
                    )))
          (doseq [i (range icount)]
            (let [theta (nth @idegs i) 
                  y     (* rad (nth @iys i))]
              (push graphics
                    (rotate graphics theta)
                    ;(draw graphics (rect -1 (- y 1) 2 2 ) style-shd-l)
                    (draw graphics (ringarc 0 0 (- y iwt) (- y iwt) (+ y iwt) (+ y iwt) 0 20 ) style-hi-s)
                    ))))
      (catch java.lang.IndexOutOfBoundsException e (println "java.lang.IndexOutOfBoundsException; ocount" ocount "icount" icount "temp" temp "@odegs" @odegs "@oys" @oys)
        
        )
      )


    ;draw wind speed & direction
    (push graphics
          (translate graphics hawidth haheight)
          (rotate graphics -180)
          ;(rotate graphics (get directions (:wind_dir w)))
          (rotate graphics (Integer. (str (:wind_degrees w))))
          (let [sz (Float. (str (:wind_mph w)))
                triangle (doto (new java.awt.geom.GeneralPath)
                           (.moveTo 5.0 0.0) (.lineTo 0.0 sz) (.lineTo -3.0 0.0) (.lineTo 3.0 0.0))
                ]
          (draw graphics triangle style-main-l)
          (draw graphics triangle style-shd-s)
          ;(draw graphics (line 0 0 0 (Float. (str (:wind_mph w)))) style-main-l)
          ;(draw graphics (line 0 0 0 (Float. (str (:wind_mph w)))) style-shd-s)
            
            )
         
          )
   
    
    ))  

(defn draw-doodle [ent ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [w (curator/get-curio "weather")]
  (when w (draw-alt-weather ent panel graphics))))

(comment defn illustrate []
  (println "foo")
  (let [bi (java.awt.image.BufferedImage. (illustrator/scrwidth) (illustrator/scrheight) java.awt.image.BufferedImage/TYPE_INT_ARGB)
        gr (.createGraphics bi)]
    (doto gr (.setRenderingHint RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON))


    (illustrator/create-entity "weather" 
                 (illustrator/position 0 0)
                 (illustrator/bound (illustrator/scrwidth) (illustrator/scrheight) :round)

                 (drawing embellir.doodles.weather/draw-doodle bi gr)))
  )


(embellir.illustrator.renderer/repaint-entity "weather")
