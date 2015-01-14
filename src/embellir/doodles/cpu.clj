(ns embellir.doodles.cpu
  (:import 
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage)
     [java.awt Color]
     )
  (:require 
     [embellir.illustrator :as illustrator]
     [embellir.curator :as curator]
     [embellir.utility.iconbase :refer [get-icon]]
     [clj-time.core]
     [clj-time.local]
     )
  (:use
     seesaw.graphics
     seesaw.color
     seesaw.font
     seesaw.util
     embellir.iutils
     )
  )

(def entityhints {:sleepms 5000 :central-feature false :placement :fullscreen :background false})

(defn add-alpha-to-color [c a]
  (color (.getRed ^java.awt.Color c) (.getGreen ^java.awt.Color c) (.getBlue ^java.awt.Color c) a))
;(defn add-alpha-to-color [c a ] (memoize add-alpha-to-color'))


(defn draw-cpu [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics graphics-potato ]
  (let [{:keys [width height size hawidth haheight hasize curio now]} graphics-potato
        maxdiam (* 0.9 (min width height))
        maxrad (half (* 0.9 (min width height)))
        segdegree (/ 360 12) 
        nowdegree (* 6 (clj-time.core/second (clj-time.core/now))) ;convert current minute to degrees
        ones (take 24 (map :one curio))
        howmany (count ones)
        colorstep (/ 255 howmany)
        
        p embellir.illustrator.colors/default-palette
        primary (:primary p)
        secondary (:secondary p)


        ]
    (push graphics
          (translate graphics hawidth haheight)
          (rotate graphics 180)
          (rotate graphics nowdegree)
          (draw graphics
                (line 0 0 0 hasize) 
                    (style :foreground (:highlight secondary) :stroke (stroke :with 3)) ;ugh, style need fix
                )
          (rotate graphics 180)
          (rotate graphics (- segdegree))
          (doseq [i (range howmany)]
            (let [one (nth ones i)
                  diam (min maxdiam (* 0.5 one maxdiam))] 
              (draw graphics
                    (iarc 0 0 diam diam 0 segdegree java.awt.geom.Arc2D/PIE)  
                    (style :foreground (:shadow primary) 
                           :background (add-alpha-to-color (:main primary) (- 255  (int  (* i colorstep)))) 
                           :stroke (stroke :with 3)) ;ugh, style need fix
                    )
                    (rotate graphics (- segdegree)) 
              )
      ) )
    ) )

(defn draw-prep [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [width (.getWidth panel)
        height (.getHeight panel)
        size  (min width height)
        hawidth (half width)
        haheight (half height)
        hasize  (half size)
        curio (embellir.curator/get-curio "cpu")
        now (clj-time.local/local-now) 

        graphics-potato {:width width
                         :height height
                         :size size
                         :hawidth hawidth
                         :haheight haheight
                         :hasize hasize
                         :curio curio
                         :now now
                         }
        ]
    (draw-cpu panel graphics graphics-potato)
    )
  )


(defn draw-doodle [ent ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (draw-prep panel graphics)
 ; (try (draw-monthclock panel graphics) (catch Exception e (println (str "polarclock month: " (.getMessage e)))))
  )

