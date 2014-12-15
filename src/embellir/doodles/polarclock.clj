(ns embellir.doodles.polarclock
  (:gen-class)
  (:import (java.util Calendar Date)
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage))
  (:require 
     [embellir.illustrator :as illustrator]
     [embellir.curator :as curator]
     [clj-time.core :as clj-time]
     [clj-time.local]
     [clj-time.predicates]
     [clj-time.periodic]
     [clj-time.format]
     )
  (:use     seesaw.graphics
     seesaw.color
     seesaw.font
     embellir.iutils
     embellir.illustrator.colors
     [embellir.illustrator.entitylist :only [entities]]
     )
  )

(def entityhints {:sleepms 30000 :central-feature true :placement :fullscreen })

(def droidSansMono (font :name "DroidSansMono" :size 40))

;(def monthctl {:back 7 :forward 14 :daymarker :number :calendars [:owncloud-caldav]})

;(def PI java.lang.Math/PI)
(def TWO-PI (* 2 PI))
(def HALF-PI (/ PI 2))

;(defn radians [degrees] (* degrees (/ PI 180)))
;(defn degrees [radians] (* radians (/ 180 PI)))

(defn interval-seq 
  "Returns sequence of period over interval. For example, the individual days in an interval using (clj-time.core/days 1) as the period."
  [interval period]
  (let [end (clj-time/end interval)]
  (take-while #(clj-time.core/before? % end) (clj-time.periodic/periodic-seq  (clj-time.core/start interval) period )) ) )


(defn hour-to-radians [hr]
  ; there are 15 degrees "per hour" on a 24-hr clock
  (radians (* 15 hr)))
(defn hour-to-degrees [hr]
  ; there are 15 degrees "per hour" on a 24-hr clock
  (* 15 hr))

(def month-degrees (/ 360 12))

(defn hour-to-radians-12 [hr]
  (* 2 (hour-to-radians (if (> 12 hr) hr (- hr 12)))))
(defn hour-to-degrees-12 [hr]
  (* 2 (hour-to-degrees (if (> 12 hr) hr (- hr 12)))))

(defn minutes-to-radians [minutes]
  (radians (* 6 minutes)))

(defn minutes-to-degrees [minutes]
  (* 6 minutes))

(defn radians-for-30d []
  (/ TWO-PI 30))

(defn calNow []
  (Calendar/getInstance))

(defn radians-for-this-month []
  (/ TWO-PI (.getActualMaximum ^java.util.Calendar (calNow) Calendar/DAY_OF_MONTH)))

(defn invert-cal 
  "Takes the list of ical entries such as used by the calendar curio,
  and turns them into a map with keys by date"
  [cal-curio calname]
  (let [data (get-in cal-curio [calname :data])
        reduce-ical (fn [m] {(:DTSTART:VALUE=DATE m) [(:SUMMARY m)]} )
        ]
    (apply merge-with (comp vec concat) (map reduce-ical data))))

(defn safe-divide [n d]
  (if (= 0 d) 0 (/ n d)))

(defn get-cal-dates 
  "Takes the list of calendars in monthctl config map, along with the calendar curio,
  and returns a map of dates such that {\"datestr\" [\"item1\" \"item2\"]}"
  [cal-curio monthctl]
  (let [cal-list (:calendars monthctl)]
    (first (map (partial invert-cal cal-curio) cal-list))))

;(invert-cal (embellir.curator/get-curio "calendar") :owncloud-caldav)
;(get-cal-dates (embellir.curator/get-curio "calendar") )

(defn draw-today 
  "draws the wedge for the current day"
  [^java.awt.Graphics2D graphics today y fsize]
  (let [dstr    (str (clj-time/day today))
        p embellir.illustrator.colors/default-palette
        secondary (:secondary p)
        mstroke (stroke :width 3)
        mstyle (style :foreground (:highlight secondary) :background (:fill secondary) :stroke mstroke )
        droidSansMono (font :name "DroidSansMono" :size (half fsize))
        metrics (.getFontMetrics graphics droidSansMono)
        fheight (.getHeight metrics)
        fwidth  (half (.stringWidth metrics dstr))
        ]
    (.setColor graphics (:highlight secondary))
    (.setFont graphics droidSansMono)
;    (rotate graphics (half degs))

    (push graphics
;      (translate graphics 0 y)
;      (rotate graphics 180)
      (translate graphics (- fwidth) 0)
      (.drawString graphics dstr 0 (int (+ fheight (half fheight)))) )

;    (rotate graphics (- (half degs))) 
    
    )
  )

(defn cal-day-shape [w h]
  (let [w  (double w)
        h  (double h)
        qw (* 0.125 w)]
    (doto (new java.awt.geom.GeneralPath)
      (.moveTo 0.0 0.0)
      (.lineTo w 0.0)
      (.lineTo w h)
      (.lineTo (- w qw) h)
  ;    (.curveTo w 0.0, 0.0 0.0, qw h)
      (.quadTo (* 0.5 w) 0.0, qw h)
      (.lineTo 0.0 h)
      (.lineTo 0.0 0.0)
      (.closePath)
      )))


(defn draw-cal-item
  "draws a calendar item marker onto day wedge"
  [^java.awt.Graphics2D graphics today item itemdiam mode]
  (let [dstr    (str (clj-time/day today))
        metrics (.getFontMetrics graphics droidSansMono)
        fheight (.getHeight metrics)
        fwidth  (half (.stringWidth metrics dstr))
        p embellir.illustrator.colors/default-palette
        secondary (:secondary p)
        strokewidth 4
        mstroke (stroke :width strokewidth)
        mstyle (style :foreground (:main secondary) :background (:fill secondary) :stroke mstroke )
        item-count (count item)
        mode (or mode :circles)
    ;    mstyle (style :foreground (:main secondary) :background (:main secondary) )
        ]
   
    ;this draws a bar per calendar item for this day
    (when (= mode :bars) 
      (push graphics
          (rotate graphics 180)
          (dotimes [i item-count] 
            (draw graphics 
                  (line (0 0 5 5) mstyle)
;                  (iarc 0 0 (+ (* 20 i) diam) (+ (* 20 i) diam) 0 degs) mstyle
                  ;(iarc 0 0 50 50 0 30) mstyle
                  ))))

    ;this draws circles to nest in the cups 
    (when (= mode :circles) 
      (push graphics
;          (rotate graphics (half degs))
          (translate graphics 0 strokewidth)
          (rotate graphics 90)
          (let [slicesize (/ 360 item-count)
                rotation  (map #(* slicesize %) (range item-count))
                dia       (/ itemdiam item-count)
                offset    (case item-count
                            1 0
                            2 (half itemdiam)
                            3 (/ itemdiam 2.15470)
                            4 (/ itemdiam 2.41421)
                            5 (/ itemdiam 2.70130)
                            :else (/ itemdiam 3)
                            ) ;thank you, http://mathworld.wolfram.com/CirclePacking.html
                              ;even though this is TOTALLY a cheat
                ]
            (doseq [r rotation]
              (push graphics
                    (rotate graphics r)
                    (draw graphics (circle 0 offset dia) mstyle
                          ))))))

    
    
    )
  )


(defn draw-callout [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
 (let [width (.getWidth panel)
       height (.getHeight panel)
       size (min height width)
       centerx (half width)
       centery (half height)
       diam  (half (* 0.95 size))
       cstroke (stroke :width 3)
       cstyle (style :foreground (color 65 100 25) :background (color 10 10 10 0) :stroke cstroke )
       diam (half size)
       ]
   (push graphics
         (translate graphics centerx centery)
         (draw graphics
           (iarc 0 0 diam diam 0 360) cstyle
               )
         )
   
   ) )

(defn draw-monthclock 
  [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics config diamlow diamhigh]
  (let [monthctl (:monthctl config)
        width (.getWidth panel)
        height (.getHeight panel)
        size (min height width)
        centerx (half width)
        centery (half height)
        diamhigh  (* size diamhigh) ;diam
        diamlow (* size diamlow)    ;tmdiam
        diamdiff (- diamhigh diamlow)
        hdiff (half diamdiff)
        mdiam diamlow ;(+ diamlow (* 0.01 diamdiff))
        p embellir.illustrator.colors/default-palette
        secondary (:secondary p)
        mstroke (stroke :width 3)
        mstyle (style :foreground (:main secondary)  :stroke mstroke )
        xstyle (style :foreground (:main secondary)  :background (:main secondary ) :stroke (stroke :width 1) )
        today (clj-time/today) 
        day-today (clj-time/day today)
        month-today (clj-time/month today)
        days-this-month (clj-time/in-days (clj-time/interval (clj-time/first-day-of-the-month (clj-time.local/local-now))  (clj-time/last-day-of-the-month (clj-time.local/local-now))))
        span (+ (:back monthctl) (:forward monthctl) 2)
        start-date (clj-time/ago (clj-time/days (:back monthctl)))
        end-date (clj-time/from-now (clj-time/days (inc (:forward monthctl))))
        spanival (clj-time/interval start-date end-date)
        deg-per-day (/ 360 days-this-month)
        deg-per-day-span (/ 270 span ) ;270 to get 3/4ths of circle
        day-width (circle-arc-length (half diamhigh) deg-per-day)
        caldates (get-cal-dates (curator/get-curio "calendar") monthctl)
        daymode (or (:daymode monthctl) :ticks)
        ]
;    (-> (curator/get-curio "calendar") first key type)
    ;this block draws the "month" mark
;    (when (draw-callout panel graphics))
    (push graphics
         (translate graphics centerx centery)
;         (rotate graphics -180)
         (rotate graphics (* month-degrees (dec month-today)))
         (draw graphics (iarc 0 0 mdiam mdiam 0 30) mstyle) 
      )

    ;this draws each day
    (push graphics
          (translate graphics centerx centery)
          (rotate graphics -180)
          (rotate graphics (* deg-per-day day-today)) ;rotate to today
          (rotate graphics (- (* deg-per-day-span (inc (:back monthctl))))) ;rotate back to show past days
          ;if the 'take' isn't added, the actual number of days returned fluctuates! hrm.
          (doseq [d (take span (map clj-time.coerce/to-local-date (interval-seq spanival (clj-time/days 1))))]
            ;(draw graphics (line 0 (+ hdiff tmdiam) 0 diam ) mstyle)
            ;the block below finds and draws event symbols

            (push graphics
                  (when (= daymode :ticks)
                    (push graphics
                    (rotate graphics (* -0.5 deg-per-day))
                    (draw graphics (line 0 (half diamlow) 0 (half diamhigh )) mstyle))
                    ) 
                  ;this draws the curved-wedge pieces for each day
                    (translate graphics 0 (half diamhigh)) ;jump to outside ring
                    (rotate graphics 180) ;turn around to face center of circle again
                    (when (= daymode :curves)
                    (push graphics
                          (translate graphics  (- (half day-width)) 0) ;slide over to middle of wedge
                          (draw graphics 
                                (cal-day-shape day-width (half hdiff)) xstyle )))
                  ;this draws the marks denoting calendar items
                  ;
                  (let [dstr (clj-time.format/unparse-local (clj-time.format/formatters :basic-date) d)
                        item (get caldates dstr) ]
                    (when item 
                      (translate graphics 0 (half hdiff))
                      (draw-cal-item graphics d item (half (half hdiff)) (:calmode monthctl)) ))

                 (if (= d today) (draw-today graphics d (half diamhigh) hdiff )))

            (rotate graphics deg-per-day-span)
            )
          (when (= daymode :ticks)
                  (push graphics
                    (rotate graphics (* -0.5 deg-per-day))
                    (draw graphics (line 0 (half diamlow) 0 (half diamhigh )) mstyle))

            )
          ) 

    )

)

(defn draw-timeclock 
  [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics config diamlow diamhigh]
  (let [
        width (.getWidth panel)
        height (.getHeight panel)
        size (min height width)
        centerx (half width)
        centery (half height)
        diam  (* size diamhigh)
        sdiam (* size diamlow)
        halfdiam (half (- diam sdiam))
        hrstrk (* 0.50 halfdiam)
        tmdiam (+ sdiam halfdiam)
    ;    x     (:x (:position entity))
    ;    y     (:y (:position entity))
        ;stoprad  (minutes-to-radians (clj-time/minute (clj-time/now)))
        stopdeg  (minutes-to-degrees (clj-time/minute (clj-time/now)))
        gmtdeg  (+ (hour-to-degrees (clj-time/hour (clj-time/now)))(safe-divide 360 (minutes-to-degrees (clj-time/minute (clj-time/now)))))
        hrofdaydeg  (+ (hour-to-degrees (clj-time/hour (clj-time.local/local-now)))(safe-divide 360 (minutes-to-degrees (clj-time/minute (clj-time.local/local-now)))))
        ;hourrad  (hour-to-radians-12 (clj-time/hour (clj-time.local/local-now)))
        hourdeg  (hour-to-degrees-12 (clj-time/hour (clj-time.local/local-now)))
        ;secrad  (minutes-to-radians (clj-time/sec (clj-time.local/local-now)))
        ;secdeg (minutes-to-degrees (clj-time/second (clj-time.local/local-now)))
       onerad (radians 1)
        ;minofhourrad (/ stoprad 14)
        minofhourdeg (/ stopdeg 15) 
        p embellir.illustrator.colors/default-palette
        primary (:primary p)
        secondary (:secondary p)
        hrstroke (stroke :width 2)
        hrstroke2 (stroke :width 3)
        minstroke1 (stroke :width hrstrk)
        minstroke2 (stroke :width (- hrstrk (* 0.25  hrstrk)))
        hrstyle (style :foreground (:main primary) :background (color 0 0 0 0) :stroke hrstroke )
        hrstyle2 (style :foreground (:main primary) :background (color 0 0 0 0) :stroke hrstroke )
        minstyle (style :foreground (:highlight primary) :background (color 0 0 0 0) :stroke hrstroke2 )
        minstyle1 (style :foreground (:highlight primary) :background (color 0 0 0 0) :stroke minstroke1 )
        minstyle2 (style :foreground (:main primary) :background (color 0 0 0 0) :stroke minstroke2 )
        hrofdaystyle (style :foreground (:highlight secondary) :background (color 0 0 0 0) :stroke minstroke2 )
        gmtstyle (style :foreground (:main secondary) :background (color 0 0 0 0) :stroke minstroke2 )
        ]
    (push graphics 
          (translate graphics centerx centery)
          (draw graphics
      
        ;        (iarc 0 0 tmdiam tmdiam 0 -90) tmpstyle2
;                (arc 0 0 tmdiam tmdiam 0 270) tmpstyle

      ; an hour-size mark for the hour
      (iarc 0 0 tmdiam tmdiam hourdeg 30 java.awt.geom.Arc2D/OPEN) hrstyle; 30 because 12h clock!

      ; and a line around to the current minute
;      (iarc 0 0 sdiam sdiam 0 (+ hourdeg minofhourdeg)) hrstyle

      ;   ;this shows 'hour markers' for a 24h clock
      ;    (doseq [x (range 0 23)]
      ;      (let [h (hour-to-radians x)
      ;            rdiff (radians 1)]
      ;            (arc 0 0 tmdiam tmdiam (- h rdiff) (+ h rdiff))))

      ; and an extra minute-dot on the hour...
      ; with a line out to the minutes ring
     (iarc 0 0 tmdiam tmdiam (+ hourdeg minofhourdeg -0.1) 0.1 java.awt.geom.Arc2D/OPEN) gmtstyle
    ; the minutes ; drawn in two parts
;    (stroke 0 220 20)
;    (stroke-weight 3)
   (iarc 0 0 diam diam 0 (+ minofhourdeg hourdeg) java.awt.geom.Arc2D/OPEN) minstyle
;    (let []
;      (push-matrix)
;      (rotate (+ minofhourrad hourrad ))
;      (line (* tmdiam 0.5)  0 (* diam 0.5) 0)
;      (pop-matrix)
;      )
;
;    (stroke 0 90 0)
;    (stroke-weight 20)
    (iarc 0 0 diam diam 0 stopdeg java.awt.geom.Arc2D/OPEN) minstyle1
;    (stroke 0 220 20)
;    (stroke-weight 16)
    (iarc 0 0 diam diam 0 stopdeg java.awt.geom.Arc2D/OPEN) minstyle2
;
    ; let's see... 'dot' on the GMT hour

    (iarc 0 0 tmdiam tmdiam (- gmtdeg 1) 2) gmtstyle
    (iarc 0 0 tmdiam tmdiam (- hrofdaydeg 1) 2) hrofdaystyle
    ;(push graphics (rotate graphics (* gmtrad (/ 180 PI)))
    ;      (draw graphics (circle 0 tmdiam 10) gmtstyle)
    ;      )
;
;    (stroke 0 13 5)
;    (stroke-weight 50)
;    (arc 0 0 sdiam sdiam (- secrad PI) (+ secrad PI))
;    (stroke-weight 60)
;    (stroke 0 0 0)
;;   (arc 0 0 sdiam sdiam (- secdeg 10 ) 20) minstyle
                )
;
  )
 ))

(defn draw-forecast
  [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics config diamlow diamhigh]
  (try (let [
        width (.getWidth panel)
        height (.getHeight panel)
        size (min height width)
        centerx (half width)
        centery (half height)
        dlow (* size diamlow 0.5)
        dhigh (* size diamhigh 0.5)
        ddiff (- dhigh dlow)
        dmid (* 0.5 (+ dlow dhigh))

        fcast (embellir.curator/get-curio "forecast")
            ;peek at temperatures with this:
            ;      x (sort (let [ fcast (embellir.curator/get-curio "forecast") ] (map #(conj []  % (get-in fcast [% :TemperatureF] )) (keys fcast))))
        p embellir.illustrator.colors/default-palette
        primary (:primary p)
        secondary (:secondary p)

        stroke (stroke :width 1)
        style-main (style :foreground (:main primary) :stroke stroke :background (:fill primary) )
        style-hi (style :foreground (:highlight primary) :stroke stroke )
        style-shd (style :foreground (:shadow primary) :stroke stroke )
        style-secondary-highlight (style :foreground (:highlight secondary) :stroke stroke )
        style-secondary-main (style :foreground (:main secondary) :stroke stroke )
        
        datelist (let [
                       starttime (clj-time.local/local-now)
                       hourlist (take 10 (clj-time.periodic/periodic-seq starttime (clj-time/hours 1) )) 
                       formatter (clj-time.format/formatter-local "MM/dd-HH")
                       ]
                   (map #(clj-time.format/unparse formatter %) hourlist)
                   )
        
        ]
    
    (push graphics
          (translate graphics centerx centery)
          (rotate graphics -180)
          (comment draw graphics
            (circle 0 0 dlow) style
            (circle 0 0 dhigh) style
                )
          (rotate graphics (hour-to-degrees-12 (clj-time/hour (clj-time.local/local-now)))) ;need to rotate to current hr
          (doseq [d datelist]
            (let [data (get fcast d ) ]
              (when data
                (let [temp (Integer. (str (:TemperatureF data)))
                      precip (Integer. (str (:PrecipitationPotential% data)))
                      arcsize (* 360 (Math/sin (/ temp 100)))]  
                  ;emtpy circle
                  (draw graphics (circle 0 dmid (half ddiff) ) style-shd)
                  ;fill in temp
                  (draw graphics (iarc 0 dmid ddiff ddiff (- (half arcsize)) arcsize java.awt.geom.Arc2D/CHORD) style-main)
                  (draw graphics (iarc 0 dmid ddiff ddiff (- (half arcsize)) arcsize java.awt.geom.Arc2D/CHORD) style-main)
                  ;overlay with precipitation%
                  (draw graphics (circle 0 dmid (/ precip (half ddiff))) style-secondary-highlight)
                  ;this was ugly...
                  ;(draw graphics (line 0 dlow 0 (+ dlow temp)) style-main)
                  (rotate graphics 30))

                )

              )
            )
          
      )
    ))
 
  )

(defn draw-financeclock
  [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics diamlow diamhigh]
  
  )

(defn draw-tenths [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [
        width (.getWidth panel)
        height (.getHeight panel)
        size (min height width)
        centerx (half width)
        centery (half height)

        p embellir.illustrator.colors/default-palette
        primary (:primary p)

        stroke (stroke :width 1)
        style (style :foreground (:main primary) :stroke stroke )
        
        ]
    (push graphics
          (translate graphics centerx centery)
          
          (doseq [d (range 1 6)]
            (draw graphics
                  (circle 0 0 (* (/ size 10) d)) style
                  )
            )
          )
    )
  )

(defn draw-prep [ent ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [config (:config (get @entities ent))
        width (.getWidth panel)
        height (.getHeight panel)
        size (min height width)
        centerx (half width)
        centery (half height)


        graphics-potato {
                         :width width
                         :height height
                         :size size
                         :centerx centerx
                         :centery centery
                         }

        ;funcs [draw-timeclock draw-monthclock draw-forecastclock draw-financeclock] 
        funcs [draw-timeclock draw-monthclock draw-forecast]
        func-seq (map partial funcs (repeat panel) (repeat graphics) (repeat config))
        diameters [[0.6 0.7] [0.8 0.95] [0.2 0.3] [0.4 0.6]]
        
        ]

(doall (map apply func-seq  diameters))
         
;  (draw-timeclock panel graphics 0.6 0.7)
;  (draw-monthclock panel graphics 0.8 0.9)
;  (draw-tenths panel graphics)
    
    )
  nil



  )


(defn draw-doodle [ent ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (draw-prep ent panel graphics)
;  (push-matrix)
 ; (try (draw-monthclock panel graphics) (catch Exception e (println (str "polarclock month: " (.getMessage e)))))
;  (pop-matrix)
 ; (draw-monthclock entity)
  )

;this forces a redraw when the file is reloaded
(embellir.illustrator.renderer/repaint-entity "polarclock")

;(clojure.repl/dir embellir.doodles.polarclock)

