(ns embellir.doodles.memory
  (:gen-class)
  (:import 
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage))
  (:require 
     [embellir.illustrator :as illustrator]
     [embellir.curator :as curator]
     )
  (:use     seesaw.graphics
     seesaw.color
     seesaw.font
     embellir.iutils
     embellir.illustrator.colors
     [embellir.illustrator.entitylist :only [entities]]
     )
  )

(def entityhints {:sleepms 50000 })

(def droidSansMono (font :name "DroidSansMono" :size 40))
(def TWO-PI (* 2 PI))
(def HALF-PI (/ PI 2))


(defn draw-memory
  [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics config ]
  (let [
        width (.getWidth panel)
        height (.getHeight panel)
        size (min height width)
        centerx (half width)
        centery (half height)
        diam (* 0.8 (min width height))
        rad (half (* 0.8 (min width height)))

        meminfo (embellir.curator/get-curio "memory")
        {:keys [:MemTotal :MemFree :SwapTotal :SwapFree :Buffers]} meminfo
        Combined (+ MemTotal SwapTotal)
        MemUsed (- MemTotal MemFree Buffers)
        SwapUsed (- SwapTotal SwapFree)
        MemSizeArc (int (* 360 (/ MemTotal Combined)))
        MemArc (int (* 360 (/ MemUsed Combined)))
        SwapArc (int (* 360 (/ SwapUsed Combined)))
        BufArc (int (* 360 (/ Buffers Combined)))

        p embellir.illustrator.colors/default-palette
        primary (:primary p)

        stroke (stroke :width 1)
        style-main (style :foreground (:main primary) :background (:main primary) :stroke stroke )
        style-hi (style :foreground (:highlight primary) :background (:highlight primary) :stroke stroke )
        style-shd (style :foreground (:shadow primary) :background (:shadow primary) :stroke stroke )
        style-shd-empty (style :foreground (:shadow primary) :stroke stroke )
        style-fill (style :foreground (:fill primary) :background (:fill primary) :stroke stroke )
        ]
    
   (push graphics
         (translate graphics centerx centery)
         (draw graphics (circle 0 0 rad ) style-shd-empty)
         (draw graphics (iarc 0 0 diam diam 0 MemSizeArc             java.awt.geom.Arc2D/PIE) style-main)  ;total main
         (draw graphics (iarc 0 0 diam diam 0 MemArc                 java.awt.geom.Arc2D/PIE) style-fill)  ;main
         (draw graphics (iarc 0 0 diam diam MemArc BufArc            java.awt.geom.Arc2D/PIE) style-shd)   ;buffer
         (draw graphics (iarc 0 0 diam diam (- 360 SwapArc) SwapArc  java.awt.geom.Arc2D/PIE) style-main)  ;swap
         
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
        funcs [draw-memory]
        func-seq (map partial funcs (repeat panel) (repeat graphics) (repeat config))
        
        ]
(doseq [f func-seq] (f))
;(doall (map apply func-seq xxx )) 
    
    ) nil )


(defn draw-doodle [ent ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (draw-prep ent panel graphics)
  )

;this forces a redraw when the file is reloaded
(embellir.illustrator.renderer/repaint-entity "memory")
(comment (embellir.illustrator.entities/load-entity "memory" {}))

