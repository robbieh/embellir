(ns embellir.utility.iconbase
  (:require [clojure.java.io :refer [file]]
            [clojure.core.memoize :as core-memoize]
            )
  )


(def icondirs [(file (System/getProperty "user.home") ".embellir" "iconbase") ])

(defn get-icon-location [iname]
  (first (filter #(.exists ^java.io.File %) 
          (map file icondirs (repeat (str iname ".png")))))
  )

(defn get-icon' [iname]
  (try
    (javax.imageio.ImageIO/read (seesaw.util/to-url (str "file://" (get-icon-location iname))))
    (catch Exception e (.printStackTrace e))
    )
;  (seesaw.util/to-url (str "file://" (get-icon-location iname)))
;  (str "file://" (get-icon-location iname))
  )

(def memoized-get-icon (core-memoize/ttl get-icon' :ttl/threshold (* 1000 60 1)))  ;ttl 1 min

(defn get-icon [iname]
  (memoized-get-icon iname)
  )

;(seesaw.util/to-url (str "file://" (get-icon-location "shopping_cart")))
;(str "file://" (get-icon-location "shopping_cart"))
;(get-icon-location "shopping_cart")
;(.exists (get-icon-location "shopping_cart"))
;(get-icon "shopping_cart")
;(core-memoize/snapshot memoized-get-icon)

