(ns embellir.illustrator.util
;  (:gen-class)

  (:use seesaw.core
        seesaw.graphics)
  (:require  [clj-time.local]
                 [clj-time.core]
                 [clj-time.coerce])
  (:import java.net.URL
           java.awt.image.AffineTransformOp
           java.awt.geom.AffineTransform
           java.awt.Graphics2D
           java.awt.AlphaComposite
     
     )
  )

(comment defn copy-image-to-buffer [{:keys [buffer frame]} ]
  (let [g2d (.createGraphics buffer)]
         (.drawImage g2d ^java.awt.Image frame  0  0 nil)) )

(defn draw-image [^java.awt.Graphics2D g2d {:keys [frame x y]} ]
        (.drawImage g2d ^java.awt.Image frame ^Integer x ^Integer y nil))

(defn now-long [] (clj-time.coerce/to-long  (clj-time.local/local-now)))

(defn blank-image [^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [sizex (.getWidth panel)
        sizey (.getHeight panel)]
    (do 
        (.setComposite graphics (AlphaComposite/getInstance AlphaComposite/CLEAR))
        (.fillRect graphics 0 0 sizex sizey)
        (.setComposite graphics (AlphaComposite/getInstance AlphaComposite/SRC_OVER))
      )
    )
  )
