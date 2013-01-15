(ns embellir.curios.keyword
  "An embellir curio which holds documentation for a 'keyword'.
  The intended use is to have reference docs displayed on screen
  for a period of time. A simple shell script should be able to
  supply the keyword and related documentation."
  (:gen-class)
  )

;{:name "keyword" 
; :atom {}
; :function nil
; :time-to-live 0 ;should not ever need to update itself
; :receiver-function receive-keyword

(defn receive-keyword
  [kdata rmap]
  (merge kdata rmap))

(defn curation-map [] {:atom (atom {})
                       :function nil
                       :time-to-live nil
                       :receiver-function embellir.curios.keyword/receive-keyword})

