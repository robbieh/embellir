(ns embellir.doodles.wordcloud
  (:use 
    seesaw.graphics
    seesaw.color
    [embellir.illustrator.entitylist :only [entities]]
    [embellir.curator :only [get-curio]]
    )
  (:require [clj-time.core :as clj-time]
            [clj-time.local])
  )

(def entityhints {:sleepms 30000,  :placement [0 0 250 250] :central-feature false})
(def mylastchange (atom {}))
(def mysize (atom {}))
(def myimg (atom {}))
(def generator-available (atom true))

(defn try-load-img [filename] 
  (try (javax.imageio.ImageIO/read  (new java.net.URL (str "file://" filename))))
  )

(defn generate-image [worddata w h lc myname]
  (reset! generator-available false)
  (swap! mylastchange assoc myname lc)
  (swap! mysize assoc myname [w h])
  (let [tmpfile (java.io.File/createTempFile "embellir-wordcloud" ".txt" nil)
        tmpimgfile (clojure.java.io/file (System/getProperty "java.io.tmpdir") (str myname ".png"))
        wrtr (clojure.java.io/writer tmpfile)
        tagcloudscript (or (:scriptfile worddata)  
                        (str (clojure.java.io/file (System/getProperty "user.home") "bin" "tagcloud.sh")))
        ]
    (try
      (doall (map #(.write wrtr (str (key %) "," (val %) "\n") ) worddata)) 
      (catch Exception e (println (str "Exception writing tmpfile in wordcloud/generate-image:" (.getMessage e))) 
                         (str "Exception writing tmpfile in wordcloud/generate-image:" (.getMessage e)))
      (finally (.close wrtr)))


    (try
      (let [ws (str (int w))
            hs (str (int h))
            r (clojure.java.shell/sh tagcloudscript (str tmpfile) (str tmpimgfile) ws hs)]
;        (println "result:" r)
        )
      (catch Exception e (println (str "Exception running shell command: "  (.getMessage e)))
                         (str "Exception running shell command: "  (.getMessage e))))

    (let [img (try-load-img tmpimgfile)]
      (if img 
        (swap! myimg assoc myname img)
        (println "couldn't loag image file" (str tmpimgfile)))
      (.delete tmpimgfile)
      )
    (.delete tmpfile))

  (reset! generator-available true)
  )

(defn draw-image [graphics myname] 
  (when (= (type (get @myimg myname)) java.awt.image.BufferedImage)
    (.drawImage ^java.awt.Graphics2D graphics 
         ^java.awt.Image (get @myimg myname)
         0 0  
         nil)))


(defn is-img-valid [w h lastchange myname]
  (let [sz (get @mysize myname)
        lc (get @mylastchange myname)
        ]
;  (println myname "checking: " [w h] sz (= [w h] sz) (= lc lastchange ) )
  (and (= lc lastchange)
      (= [w h] sz))))

(defn draw-doodle [entname ^javax.swing.JPanel panel ^java.awt.Graphics2D graphics]
  (let [sizex   (double (.getWidth panel))
        sizey   (double (.getHeight panel))
        entdata ((keyword entname) (get-curio "wordcloud"))
        lc (:lastchange entdata)
        worddata  (:data entdata)
        ] 
    ;if I do not have an image, fire off a future to draw it
    ;otherwise...
    ; if the lastchange time of the data set 
;    (println "invoked and img is" @myimg " and generator is " @generator-available)
;    (println entname lc)
    (if (nil? (get @myimg entname)) 
        (if @generator-available (do (future (generate-image worddata sizex sizey lc entname))
                                     ))
        (if (is-img-valid sizex sizey lc entname) 
          (draw-image graphics entname)
          (if @generator-available (do (future (generate-image worddata sizex sizey lc entname))
                                       (draw-image graphics entname)))))
    ) )
  




