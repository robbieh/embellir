(ns embellir.illustrator.entitylist)

; entities is a map of maps
; name is the main map key
; map should contain a minimum of :function and :peroid, or a :threadfn
; :function - fn to update g2d object, or nil if entity handles its own thread
; :sleepms  - how many ms to sleep between updates?
; :next-time - time at which it should next run
; :threadfn - fn that will be run in a thread
; :frame    - the image object
;
; { "circle" {:function embellir.doodles.circle/draw-circle
;             :period 1000
;             }
;   "clock"  {...}}
;   

(defonce entities (atom {}))
