(ns embellir.doodles.bg
  (:import 
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage))
  (:require 
     [embellir.illustrator :as illustrator]
     [embellir.curator :as curator]
     )
  (:use
     seesaw.graphics
     seesaw.color
     seesaw.font
     seesaw.util
     )
  )

(def entityhints {:sleepms 5000 :central-feature false :placement :fullscreen :background true})

;(def imgurl "http://colourlovers.com.s3.amazonaws.com/images/patterns/3919/3919184.png?1381355487")
;(def imgurl "http://colourlovers.com.s3.amazonaws.com/images/patterns/3794/3794504.png?1375935659")
(def imgurl "file:///home/robbie/tmp/lbt/IMG_2542.JPG")
(def img (javax.imageio.ImageIO/read  (seesaw.util/to-url imgurl)))


(defn draw-background [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
      (.drawImage ^java.awt.Graphics2D graphics ^java.awt.Image img
         0 0 (.getWidth ^javax.swing.JPanel panel) (.getHeight ^javax.swing.JPanel panel)
         nil
         )
      )




(defn draw-doodle [ent ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (draw-background panel graphics)
 ; (try (draw-monthclock panel graphics) (catch Exception e (println (str "polarclock month: " (.getMessage e)))))
  )

