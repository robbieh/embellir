(ns embellir.doodles.icon
  (:import 
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage)
     (java.io File)
     )
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
     [embellir.illustrator.svgutils :only [get-img-from-svg]]
     )
  )

(def entityhints {:sleepms 5000 :background false})

(def ^java.awt.image.BufferedImage img (atom nil))

(type (get-img-from-svg "/home/robbie/company.svg"))
(defn draw-icon [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (when (nil? @img)
    (reset! img (get-img-from-svg "/home/robbie/company.svg")) )
  (let [
        ;icon (get-icon "shopping_cart")
        icon @img
        ]
      (.drawImage ^java.awt.Graphics2D graphics ^java.awt.Image icon
         0 0 (.getWidth ^javax.swing.JPanel panel) (.getHeight ^javax.swing.JPanel panel)
         nil
         ))
      )




(defn draw-doodle [ent ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (draw-icon panel graphics)
 ; (try (draw-monthclock panel graphics) (catch Exception e (println (str "polarclock month: " (.getMessage e)))))
  )

(embellir.illustrator.renderer/repaint-entity "icon")
