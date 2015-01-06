(ns embellir.doodles.netusage
  (:import 
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage))
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

(def entityhints {:sleepms 5000 :central-feature false :background false})

(defn draw-iface [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics graphics-potato ]
  
  )

(defn draw-netstatus [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics graphics-potato ]
  (let [{:keys [width height size hawidth haheight hasize curio now]} graphics-potato
        maxdiam (* 0.9 (min width height))
        maxrad (half (* 0.9 (min width height)))
;        segdegree (/ 360 12) 
;        nowdegree (* 6 (clj-time.core/second (clj-time.core/now))) ;convert current minute to degrees
        ifaces (keys (:diff curio))
       
;        colorstep (/ 255 howmany)
        
        p embellir.illustrator.colors/default-palette
        primary (:primary p)
        secondary (:secondary p)
        ]
    
    ) )

(defn draw-prep [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [width (.getWidth panel)
        height (.getHeight panel)
        size  (min width height)
        hawidth (half width)
        haheight (half height)
        hasize  (half size)
        curio (embellir.curator/get-curio "netstatus")
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
    (draw-netstatus panel graphics graphics-potato)
    )
  )


(defn draw-doodle [ent ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (draw-prep panel graphics))

