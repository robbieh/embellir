(ns embellir.doodles.forecast
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

(def entityhints {:sleepms 600000 })

(def droidSansMono (font :name "DroidSansMono" :size 40))
(def TWO-PI (* 2 PI))
(def HALF-PI (/ PI 2))

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

(defn draw-forecast
  [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics config diamlow diamhigh]
  (let [
        width (.getWidth panel)
        height (.getHeight panel)
        size (min height width)
        centerx (half width)
        centery (half height)
        dlow (* size diamlow 0.5)
        dhigh (* size diamhigh 0.5)
        dmid (* 0.5 (+ dlow dhigh))

        fcast (embellir.curator/get-curio "forecast")

        p embellir.illustrator.colors/default-palette
        primary (:primary p)

        stroke (stroke :width 1)
        style-main (style :foreground (:main primary) :stroke stroke )
        style-hi (style :foreground (:highlight primary) :stroke stroke )
        style-shd (style :foreground (:shadow primary) :stroke stroke )
        
        datelist (let [
                       starttime (clj-time.local/local-now)
                       hourlist (take 10 (clj-time.periodic/periodic-seq starttime (clj-time/hours 1) )) 
                       formatter (clj-time.format/formatter "MM/dd-HH")
                       ]
                   (map #(clj-time.format/unparse formatter %) hourlist)
                   )

        
        ]
    
        ;{:primary {:main #<Color java.awt.Color[r=0,g=220,b=20]>, :highlight #<Color java.awt.Color[r=0,g=90,b=0]>, :shadow #<Color java.awt.Color[r=0,g=70,b=0]>, :fill #<Color java.awt.Color[r=0,g=110,b=10]>}, :secondary {:main #<Color java.awt.Color[r=15,g=90,b=55]>, :highlight #<Color java.awt.Color[r=150,g=110,b=250]>, :shadow #<Color java.awt.Color[r=5,g=25,b=0]>, :fill #<Color java.awt.Color[r=10,g=10,b=10]>}}
    ;
    (push graphics
          (translate graphics centerx centery)
          (comment draw graphics
            (circle 0 0 dlow) style
            (circle 0 0 dhigh) style
                )
          (rotate graphics 15) ;need to rotate to current hr
          (doseq [d datelist]
            (let [data (get fcast d )
                  temp (Integer. (str (:TemperatureF data)))
                  precip (Integer. (str (:PrecipitationPotential% data)))
                  ]
              (draw graphics (circle 0 dmid (half temp)) style-main)
              (draw graphics (circle 0 dmid (half precip)) style-main)
              (rotate graphics 30)

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
        funcs [draw-forecast]
        func-seq (map partial funcs (repeat panel) (repeat graphics) (repeat config))
        diameters [[0.8 0.95] [0.6 0.7] [0.4 0.6]]
        
        ]

(doall (map apply func-seq  diameters)) ) nil )


(defn draw-doodle [ent ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (draw-prep ent panel graphics)
  )

;this forces a redraw when the file is reloaded
(embellir.illustrator.renderer/repaint-entity "forecast")


