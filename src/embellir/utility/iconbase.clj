(ns embellir.utility.iconbase
  (:require [clojure.java.io :refer [file]])
  )


(def icondirs [(file (System/getProperty "user.home") ".embellir" "iconbase") ])

(defn get-icon [iname]
  (first (filter #(.exists ^java.io.File %) 
          (map file icondirs (repeat (str iname ".png")))))
  )

