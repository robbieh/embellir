(ns embellir.doodles.polarclock
  (:gen-class)
  (:import (java.util Calendar Date)
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage))
  (:require [embellir.illustrator :as illustrator]
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
     )
  )

(def entityhints {:sleepms 30000 :central-feature true :placement :fullscreen })

(def droidSansMono (font :name "DroidSansMono" :size 40))

(def monthctl {:back 7 :forward 14 :daymarker :number :calendars [:owncloud-caldav]})

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

(defn get-cal-dates 
  "Takes the list of calendars in monthctl var, along with the calendar curio,
  and returns a map of dates such that {\"datestr\" [\"item1\" \"item2\"]}"
  [cal-curio]
  (let [cal-list (:calendars monthctl)]
    (first (map (partial invert-cal cal-curio) cal-list))))

;(invert-cal (embellir.curator/get-curio "calendar") :owncloud-caldav)
;(get-cal-dates (embellir.curator/get-curio "calendar") )

(defn draw-today 
  "draws the wedge for the current day"
  [^java.awt.Graphics2D graphics today y degs]
  (let [dstr    (str (clj-time/day today))
        metrics (.getFontMetrics graphics droidSansMono)
        fheight (.getHeight metrics)
        fwidth  (* 0.5 (.stringWidth metrics dstr))
        p embellir.illustrator.colors/default-palette
        secondary (:secondary p)
        mstroke (stroke :width 3)
        mstyle (style :foreground (:highlight secondary) :background (:fill secondary) :stroke mstroke )
        ]
    (.setColor graphics (:highlight secondary))
    (.setFont graphics droidSansMono)
    (rotate graphics (* 0.5 degs))

    (push graphics
      (translate graphics 0 y)
      (rotate graphics 180)
      (translate graphics (- fwidth) 0)
      (.drawString graphics dstr 0 fheight) )

    (rotate graphics (- (* 0.5 degs))) )
  )

(defn cal-item-shape [w h]
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
  "draws a calendar item onto day wedge"
  [^java.awt.Graphics2D graphics today y degs diam item]
  (let [dstr    (str (clj-time/day today))
        metrics (.getFontMetrics graphics droidSansMono)
        fheight (.getHeight metrics)
        fwidth  (* 0.5 (.stringWidth metrics dstr))
        p embellir.illustrator.colors/default-palette
        secondary (:secondary p)
        mstroke (stroke :width 5)
        mstyle (style :foreground (:main secondary) :background (:fill secondary) :stroke mstroke )
    ;    mstyle (style :foreground (:main secondary) :background (:main secondary) )
        ]
   
    ;this draws a bar per calendar item for this day
    (push graphics
          (rotate graphics 180)
          (dotimes [i (count item)] 
            (draw graphics 
                  (iarc 0 0 (+ (* 20 i) diam) (+ (* 20 i) diam) 0 degs) mstyle
                  ;(iarc 0 0 50 50 0 30) mstyle
                  )))
    
    (comment push graphics
          (translate graphics 0 y)
          (rotate graphics 180)
          (rotate graphics (* 0.5 degs))
          (dotimes [i (count item)] 
            (draw graphics 
                  (cal-item-shape 105 50) mstyle
                  
                  )))
    
    )
  )

(embellir.illustrator.renderer/repaint-entity "polarclock")

(defn draw-callout [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
 (let [width (.getWidth panel)
       height (.getHeight panel)
       size (min height width)
       centerx (* 0.5 width)
       centery (* 0.5 height)
       diam  (* 0.5 (* 0.95 size))
       cstroke (stroke :width 3)
       cstyle (style :foreground (color 65 100 25) :background (color 10 10 10 0) :stroke cstroke )
       diam (* 0.5 size)
       ]
   (push graphics
         (translate graphics centerx centery)
         (draw graphics
           (iarc 0 0 diam diam 0 360) cstyle
               )
         )
   
   ) )

(defn draw-monthclock 
  [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [
        width (.getWidth panel)
        height (.getHeight panel)
        size (min height width)
        centerx (* 0.5 width)
        centery (* 0.5 height)
        diam  (* 0.5 (* 0.95 size))
        tmdiam (* 0.5 (* 0.73 size))
        mdiam (* 1 (* 0.78 size))
        hdiam (* 0.5 (- diam tmdiam))
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
        caldates (get-cal-dates (curator/get-curio "calendar"))
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
          (rotate graphics (* deg-per-day day-today))
          (rotate graphics (- (* deg-per-day-span (inc (:back monthctl)))))
          ;if the 'take' isn't added, the actual number of days returned fluctuates! hrm.
          (doseq [d (take span (map clj-time.coerce/to-local-date (interval-seq spanival (clj-time/days 1))))]
            ;(draw graphics (line 0 (+ hdiam tmdiam) 0 diam ) mstyle)
            ;the block below finds and draws event symbols
            (let [dstr (clj-time.format/unparse-local (clj-time.format/formatters :basic-date) d)
                  item (get caldates dstr) ]
              (when item 
                (comment draw-cal-item graphics d diam deg-per-day-span (+ hdiam mdiam) item) )
            (push graphics
                  (translate graphics 0 diam)
                  (rotate graphics 180)
                  (rotate graphics (* 0.5 deg-per-day-span))
                    (draw graphics 
                          (cal-item-shape 105 50) xstyle

                          )))
            (if (= d today) (draw-today graphics d diam deg-per-day-span))

            (rotate graphics deg-per-day-span)
            )
;          (draw graphics (line 0 (+ hdiam tmdiam) 0 diam ) mstyle)
          ) 

    )

  ;  (rotate (- PI))
  ;(stroke 0 75 25)
  ;(stroke-weight 1)
  ;(let [width (:width (:bound entity))
  ;      x     (* width 0.5)
  ;      y     (* width 0.5)]
  ;  (doseq [day (range 0 (.getActualMaximum (calNow) java.util.Calendar/DAY_OF_MONTH))]
  ;    (line  (* width (* 0.90 0.5))  0 (* width (* 0.95 0.5)) 0)
  ;    (rotate (radians-for-this-month))
  ;    )
  ;  )
  ;(stroke-weight 3)
  ;(stroke 10 95 0)
  ;(pop-matrix)(push-matrix)
  ;(let [width (:width (:bound entity))
  ;      diam (* 0.95 width)]
  ;  (rotate (* (clj-time/day (clj-time.local/local-now)) (radians-for-this-month)))
  ;  (line  (* width (* 0.85 0.5))  0 (* width (* 0.95 0.5)) 0)
  ;  (rotate (- (radians-for-this-month)))
  ;  (line (* width (* 0.85 0.5))  0 (* width (* 0.95 0.5)) 0)
  ;  (stroke-weight 5)
  ;  (arc 0 0 diam diam 0 (radians-for-this-month))
  ;  )
  ;(pop-style)
  ;(pop-matrix))
)

(defn draw-timeclock 
  [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [width (.getWidth panel)
        height (.getHeight panel)
        size (min height width)
        centerx (* 0.5 width)
        centery (* 0.5 height)
        diam  (* 0.7 size)
        tmdiam (* 0.6 size)
        sdiam (* 0.5 size)
    ;    x     (:x (:position entity))
    ;    y     (:y (:position entity))
        ;stoprad  (minutes-to-radians (clj-time/minute (clj-time/now)))
        stopdeg  (minutes-to-degrees (clj-time/minute (clj-time/now)))
        ;gmtrad  (hour-to-radians (clj-time/hour (clj-time/now)))
        ;hourrad  (hour-to-radians-12 (clj-time/hour (clj-time.local/local-now)))
        hourdeg  (hour-to-degrees-12 (clj-time/hour (clj-time.local/local-now)))
        ;secrad  (minutes-to-radians (clj-time/sec (clj-time.local/local-now)))
        ;secdeg (minutes-to-degrees (clj-time/second (clj-time.local/local-now)))
;       onerad (radians 1)
        ;minofhourrad (/ stoprad 14)
        minofhourdeg (/ stopdeg 15) 
        p embellir.illustrator.colors/default-palette
        primary (:primary p)
        secondary (:secondary p)
        hrstroke (stroke :width 2)
        hrstroke2 (stroke :width 3)
        minstroke1 (stroke :width 20)
        minstroke2 (stroke :width 16)
        hrstyle (style :foreground (:main primary) :background (:fill primary) :stroke hrstroke )
        hrstyle2 (style :foreground (:main primary) :background (:fill primary) :stroke hrstroke )
        minstyle (style :foreground (:highlight primary) :background (:fill primary) :stroke hrstroke2 )
        minstyle1 (style :foreground (:highlight primary) :background (:fill primary) :stroke minstroke1 )
        minstyle2 (style :foreground (:main primary) :background (:fill primary) :stroke minstroke2 )
        ]
    (push graphics 
          (translate graphics centerx centery)
          (draw graphics
      
        ;        (iarc 0 0 tmdiam tmdiam 0 -90) tmpstyle2
;                (arc 0 0 tmdiam tmdiam 0 270) tmpstyle

      ; an hour-size mark for the hour
                
      (iarc 0 0 tmdiam tmdiam hourdeg 30) hrstyle; 30 because 12h clock!

      ;   ;this shows 'hour markers' for a 24h clock
      ;    (doseq [x (range 0 23)]
      ;      (let [h (hour-to-radians x)
      ;            rdiff (radians 1)]
      ;            (arc 0 0 tmdiam tmdiam (- h rdiff) (+ h rdiff))))

      ; and an extra minute-dot on the hour...
      ; with a line out to the minutes ring
     (iarc 0 0 tmdiam tmdiam (+ hourdeg minofhourdeg -0.01) 0.01 ) hrstyle2

    ; the minutes ; drawn in two parts
;    (stroke 0 220 20)
;    (stroke-weight 3)
   (iarc 0 0 diam diam 0 (+ minofhourdeg hourdeg) ) minstyle
;    (let []
;      (push-matrix)
;      (rotate (+ minofhourrad hourrad ))
;      (line (* tmdiam 0.5)  0 (* diam 0.5) 0)
;      (pop-matrix)
;      )
;
;    (stroke 0 90 0)
;    (stroke-weight 20)
    (iarc 0 0 diam diam 0 stopdeg) minstyle1
;    (stroke 0 220 20)
;    (stroke-weight 16)
    (iarc 0 0 diam diam 0 stopdeg) minstyle2
;
;    ; let's see... red 'dot' on the GMT hour
;    (stroke 250 25 25)
;    (stroke-weight 5)
;    (arc 0 0 tmdiam tmdiam (- gmtrad onerad) (+ gmtrad onerad))
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

(defn draw-doodle [ent ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
;  (push-matrix)
  (draw-timeclock panel graphics)
  (draw-monthclock panel graphics)
 ; (try (draw-monthclock panel graphics) (catch Exception e (println (str "polarclock month: " (.getMessage e)))))
;  (pop-matrix)
 ; (draw-monthclock entity)
  )

