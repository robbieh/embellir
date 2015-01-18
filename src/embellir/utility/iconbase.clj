(ns embellir.utility.iconbase
  (:require [clojure.java.io :refer [file]]
            [clojure.core.memoize :as core-memoize]
            [embellir.utility.svgutils :as svgutils]
            )
  )


(def icondirs [(file (System/getProperty "user.home") ".embellir" "iconbase") 
               (file (System/getenv "EMBELLIR_ICONBASE"))
               ])

(defn get-icon-location [iname]
  (first (filter #(.exists ^java.io.File %) 
          (map file icondirs (repeat (str iname ".svg")))))
  )

(defn get-icon' [iname]
  (try
    ;(javax.imageio.ImageIO/read (seesaw.util/to-url (str "file://" (get-icon-location iname))))
    (svgutils/get-img-from-svg (get-icon-location iname))
    (catch Exception e (.printStackTrace e))
    )
;  (seesaw.util/to-url (str "file://" (get-icon-location iname)))
;  (str "file://" (get-icon-location iname))
  )

(def memoized-get-icon (core-memoize/ttl get-icon' :ttl/threshold (* 1000 60 1)))  ;ttl 1 min

(defn get-icon [iname]
  (memoized-get-icon iname))

