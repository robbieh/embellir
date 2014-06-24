(ns embellir.curios.calendar
  (:require 
     [embellir.utility.caldav :as caldav]
     [clj-time.core :as clj-time]
     [clj-time.coerce]
     [clj-time.format]
     )
  )


;events index has to be strings. the date objects don't work well using
;:SUMMARY because it is from iCal standard

;{"calendar name" {:protocol :caldav
;                  :calrc keyname-in-file
;                  :span [-7 14]
;                  :events {"20140528" {:SUMMARY "Foo" ... and other ical fields} } 
;                  }}
; "meta"          {:selected-date "xxx"


(defn get-calrc [keyname] ((keyword keyname) (read-string (slurp (clojure.java.io/file (System/getenv "HOME") ".calendar-seq.rc")))))

(defn str-to-localdate [time-string]
  (clj-time.format/parse (clj-time.format/formatters :basic-date) time-string ))

;(localdate-to-str (str-to-localdate "20140528"))

(defn localdate-to-str [local-date]
  (clj-time.format/unparse-local (clj-time.format/formatters :basic-date) (clj-time.coerce/to-local-date  local-date) ))

;(clj-time.format/unparse-local  (clj-time.format/formatters :basic-date)(clj-time/today))
;(clj-time.format/unparse (clj-time.format/formatters :basic-date-time)(clj-time/now))

(defn get-caldav-data [calmap]
  (let [calrc (get-calrc (:calrc calmap))
        span  (or (:span calmap) [-7 14]) ;default is back a week, forward two
        now   (clj-time.core/now)
        start (clj-time.core/plus now (clj-time.core/days (first span))) 
        end   (clj-time.core/plus now (clj-time.core/days (second span))) 
        keepers [:DTEND:VALUE=DATE :DTSTART:VALUE=DATE :SUMMARY]
        ]
    (map #(select-keys % keepers) (caldav/query-cal-data start end calrc) )
    )
  )

(def protocol-map {:caldav get-caldav-data})
(defn handle-calmap [[calname calmap]]
  (let [func (get protocol-map (:protocol calmap)) ]
    {calname (merge {:data (func calmap)} calmap)}
    ;creates a map that looks like {:calname {:data ... :protocol ... }
    )
  )

;(handle-calmap [:oc {:protocol :caldav :calrc :owncloud-default}])

(defn proper-time-fmt? [time-string]
  (try (when (str-to-localdate time-string) true)
    (catch Exception e )
    (finally false)))

(defn setup-calendar
  []
  {}
  )

(defn receive-calendar
    [cdata rmap] ;rmap is ...
  (let [actions-map (into {} (filter #(complement (proper-time-fmt? (str (key %)))) rmap ))
        dates-map  (into {} (filter #(proper-time-fmt? (str (key %))) rmap ))
        ]
    (println actions-map)
    (println dates-map)
    (merge cdata (into {}  (map handle-calmap actions-map)) dates-map)
    ))

;(receive-calendar {} {"20140601" {:SUMMARY "something somewhere"}})
;(receive-calendar {} {"asdf9999" {:SUMMARY "something somewhere"}})

;(receive-calendar {} {:owncloud-caldav  {:protocol :caldav :calrc :owncloud-default }})

(defn curation-map [] {:atom (atom (embellir.curios.calendar/setup-calendar))
                       :function nil
                       :time-to-live 0
                       :receiver-function embellir.curios.calendar/receive-calendar })


(comment (embellir.curator/curate "calendar"))
(comment (embellir.curator/list-curios))
(comment (embellir.curator/get-curio "calendar"))
(comment (embellir.curator/trash-curio "calendar"))

(comment {:owncloud-caldav {:calrc :owncloud-default, :protocol :caldav, :data ({:SUMMARY "Elkmont", :DTSTART:VALUE=DATE "20140608", :DTEND:VALUE=DATE "20140612"} {:SUMMARY "Dentist 1pm", :DTSTART:VALUE=DATE "20140529", :DTEND:VALUE=DATE "20140530"} {:SUMMARY "Slaughter party", :DTSTART:VALUE=DATE "20140606", :DTEND:VALUE=DATE "20140609"} {:SUMMARY "Father's day", :DTSTART:VALUE=DATE "20140615", :DTEND:VALUE=DATE "20140616"} {:SUMMARY "NT Mario Kart", :DTSTART:VALUE=DATE "20140605", :DTEND:VALUE=DATE "20140606"} {:SUMMARY "10am dentist", :DTSTART:VALUE=DATE "20140618", :DTEND:VALUE=DATE "20140619"} {:SUMMARY "Maleficent", :DTSTART:VALUE=DATE "20140529", :DTEND:VALUE=DATE "20140530"} {:SUMMARY "not-quite game night", :DTSTART:VALUE=DATE "20140531", :DTEND:VALUE=DATE "20140601"} {:SUMMARY "Sewing with Brian", :DTSTART:VALUE=DATE "20140601", :DTEND:VALUE=DATE "20140602"} {:SUMMARY "GoT with Jon", :DTSTART:VALUE=DATE "20140601", :DTEND:VALUE=DATE "20140602"} {:SUMMARY "Jon's birthday", :DTSTART:VALUE=DATE "20140604", :DTEND:VALUE=DATE "20140605"} {:SUMMARY "Jon visit", :DTSTART:VALUE=DATE "20140603", :DTEND:VALUE=DATE "20140604"})}})

