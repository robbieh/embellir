(ns embellir.doodles.cpu
  (:import 
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage))
  (:require 
     [embellir.illustrator :as illustrator]
     [embellir.curator :as curator]
     [embellir.utility.iconbase :refer [get-icon]]
     )
  (:use
     seesaw.graphics
     seesaw.color
     seesaw.font
     seesaw.util
     embellir.iutils
     )
  )

(def entityhints {:sleepms 5000 :central-feature true :placement :fullscreen :background false})


(defn draw-cpu [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics graphics-potato ]
  (let [{:keys [width height size hawidth haheight hasize curio]} graphics-potato
        ]

    )
      )

(defn draw-prep [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [width (.getWidth panel)
        height (.getHeight panel)
        size  (min width height)
        hawidth (half width)
        haheight (half height)
        hasize  (half size)
        curio (embellir.curator/get-curio "cpu")

        graphics-potato {:width width
                         :height height
                         :size size
                         :hawidth hawidth
                         :haheight haheight
                         :hasize hasize
                         :curio curio
                         }
        ]
    (draw-cpu panel graphics graphics-potato)
    )
  )


(defn draw-doodle [ent ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (draw-prep panel graphics)
 ; (try (draw-monthclock panel graphics) (catch Exception e (println (str "polarclock month: " (.getMessage e)))))
  )

