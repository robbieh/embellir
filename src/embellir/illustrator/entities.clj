(ns embellir.illustrator.entities
;  (:gen-class)
  (:import  [java.awt.image BufferedImage]
     
     )
  (:require 
     [embellir.illustrator.screen :as screen]
     [embellir.illustrator.window :as window]
     [embellir.illustrator.renderer :as renderer]
     [seesaw.timer :as timer]
;     [clojure.edn :as edn]
     [alembic.still] 
     ) 
  (:use seesaw.core
     seesaw.graphics
     seesaw.color
     [embellir.illustrator.entitylist :only [entities]]
     [embellir.illustrator.layout :only [relayout]]
     )
  )

(defn uniquename [n] (str n)) ;TODO: check entity list

(defn get-entity-config [entname]
  (try
    ((keyword entname) (:curios (read-string (slurp (clojure.java.io/file (System/getProperty "user.home") ".embellir.rc")))))))

(defn find-entity [ent]
  (let [home (System/getProperty "user.home")
        exts [".emb" ".clj"]
        path ["src/embellir/doodles" 
              "embellir/doodles"
              (clojure.java.io/file home ".embellir/embellir/doodles")]
        candidates (for [ e exts p path ] 
                     (let [f (clojure.java.io/file p (str ent e))
                           r (clojure.java.io/resource (str p "/" ent e))
                           ]
                       (or (if (.exists f) [f e] )
                           (if-not (nil? r) [r e])) 
                       ))
        ]
      (first (drop-while nil? candidates)) ))

(defn resolve-emb-file! [f]
  (let [conf (read-string (slurp f))
        deps (:dependencies conf)
        ]
    (doseq [dep deps] (alembic.still/distill dep)))
  (clojure.string/replace (str f) #".emb$" ".clj") ;TODO this is dumb. should get :code from config map
  )

(defn resolve-entity [ent]
  (let [[file ext] (find-entity ent)]
    (if (= ".emb" ext) (resolve-emb-file! file) (str file)) ))

(defn load-entity [doodlename {:keys [placement sleepms entname] :as params} ]
  ;determine size, position
  ;resolve entity function
  ;create canvas with those attributes and overriden :paint
 (let [fqi (str "embellir.doodles." (clojure.string/replace doodlename "/" ".")) ;this lets the code handle format like "category/subcategory/doodle"
       placement (or placement [:fullscreen])
       bounds (screen/placement placement)
       entname' (if entname entname doodlename)
       sleepms (or sleepms 1000)
       params (or params {})
       filetoload (resolve-entity doodlename )
       ]
   (println "loading:" filetoload)
   (load-reader (clojure.java.io/reader filetoload)) ;this pulls in a .clj namespace
   (if (find-ns (symbol fqi))
     (if-let [func (resolve (symbol fqi "draw-doodle"))]
       (let [
             itemname (uniquename entname')
             paintfn (partial @func itemname)
             canvas (canvas :background (color 0 0 0 0) :bounds bounds
                            :paint paintfn)
             t (timer (partial renderer/repaint-entity itemname ) :repeats? true :delay sleepms :start? false)
             entmap (conj params {:canvas canvas :sleepms sleepms :timer t})
             entityhints (try (deref (resolve (symbol fqi "entityhints"))) (catch Exception e))
             entityconfig {:config (get-entity-config entname')}
             ]
         ;(println "loading " entname' "as" itemname " with hints " entityhints)
         (.start ^javax.swing.Timer t)
         (.setOpaque ^javax.swing.JPanel canvas false)
         (swap! entities assoc itemname entmap) 
         (println @entities)
         (when entityhints (swap! entities update-in [itemname] merge entityhints))
         (println @entities)
         (when entityconfig (swap! entities update-in [itemname] merge entityconfig))
         (println @entities)
         (config! window/xyz :items (conj (config window/xyz :items) canvas))
         (relayout)
         (get @entities itemname) ; just so something sensible is returned
         )
       )
     (println "could not find namespace from" (symbol fqi))
     )
   ) 
  )


(defn remove-entity [entname]
  (let [canvas (get-in @entities [entname :canvas])]
    ;(println (config window/xyz :items))
    ;(println canvas)
    ;(println (remove #(= % canvas) (config window/xyz :items)))
    ;if we dont' stop the timer, it keeps on firin' ...
    (.stop ^javax.swing.Timer (:timer (get @entities entname)))
    (config! window/xyz :items (remove #(= % canvas) (config window/xyz :items)))
    (swap! entities dissoc entname)
    )
  )

(comment 
(assoc {:a 1 } )
(load-entity "circle" {:placement [ :fullscreen] :sleepms 2000 :color "aqua"} )
(load-entity "circle" nil)
(remove-entity "circle")
(load-entity "circle" {:placement [10 10 150 150 ] :sleepms 2000 :entname "c2"} )
(load-entity "circle" {:placement [10 10 150 150 ] :sleepms 1000 :entname "c3"} )
(load-entity "circle" {:placement [10 10 150 150 ] :sleepms 1000 :entname "c4"} )
(load-entity "circle" {:placement [10 10 150 150 ] :sleepms 1000 :entname "c5"} )
(load-entity "circle" {:placement [10 10 150 150 ] :sleepms 1000 :entname "c6"} )
(load-file "src/embellir/doodles/polarclock.clj")
(load-file "src/embellir/doodles/polarclock.clj")
(load-file "src/embellir/doodles/polarclock.clj")
(load-file "src/embellir/doodles/polarclock.clj")
(symbol "embellir.doodles.circle")
(symbol "embellir.doodles.circle" "draw-doodle")
(symbol  "draw-doodle")
(type (symbol "embellir.doodles.circle" "draw-doodle"))
(find-ns (symbol "embellir.doodles.circle" ))
(resolve (symbol "embellir.doodles.circle" "draw-doodle"))
(resolve (symbol "embellir.doodles.polarlclcok" "draw-doodle"))
(fn?  @(resolve (symbol "embellir.doodles.circle" "draw-doodle")))
(type embellir.doodles.circle/draw-doodle)
(config! window/xyz :items (dissoc (config window/xyz :items) canvas))
(remove-entity "c2")
(do (remove-entity "polarclock")
    (load-entity "polarclock" {:placement [ :fullscreen] :sleepms 10000
                           :central-feature true } ))
(pprint  @entities)
(embellir.curator/curate "weather")
(do (remove-entity "weather")
  (load-entity "weather" {:sleepms 5000}))

(remove-entity "ipviz")
(remove-entity "wordcloud")
(get entities "circle")
(load-entity "ipviz" {:placement [ :fullscreen] :sleepms 1000
                      :ip6 "fcd2:b843:787a:59f3:6345:7ac2:6df3:5523" } )
(do  (config! window/xyz :items nil) (reset! entities {})) ;reset it all!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
(config window/xyz :items )
(pprint  @entities)

(.getDelay  (get-in @entities  [ "christmas/boxes" :timer]))
(swap! entities assoc-in  ["christmas/boxes" :sleepms] 100)
(do (remove-entity "christmas/boxes") (load-entity "christmas/boxes" {:sleepms 1000}))

(embellir.curator/curate "iplist")
(embellir.curator/get-curio "iplist")
(embellir.curator/list-curios)
(do (remove-entity "ip4map") (load-entity "ip4map" {:sleepms 2000}))
(do (remove-entity "ip4plaid") (load-entity "ip4plaid" {:sleepms 5000}))
(remove-entity "cjdnspeers")
(embellir.curator/curate "cjdnspeers")

(load-entity "cjdnspeers" {:placement [:fullscreen] :sleepms 2000
                           :ip6 "fcd2:b843:787a:59f3:6345:7ac2:6df3:5523"
                           })
  

(let [c (get-in @entities ["christmas/boxes" :canvas])]
;  (.paintImmediately c (.getBounds c))
  (.setIgnoreRepaint c true)
  (.setOpaque ^javax.swing.JPanel c false)
  (.setBackground ^javax.swing.JPanel c (color "white"))
  (.update ^javax.swing.JPanel c (.getGraphics ^javax.swing.JPanel c) )
  ))








; (def img1 (buffered-image 100 100))
;
; (-> @entities (assoc "test" {:this "that"}))     
; (swap! e #(-> % (assoc "test" {:frame img1})))
;
; (assoc-in @e ["test" :x] 1)
; (swap! e assoc-in ["test" :x] 1)
;

; (find @e "test")            ; ["test" {:frame #<BufferedImage BufferedImage@3fec3fed: ... >}]
; (get-in @e ["test" :frame]) ; returns the actual BufferedImage

; (map :frame (vals @e))      ; returns a list with all the images



