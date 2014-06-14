(ns embellir.utility.caldav
  (:require 
     [org.httpkit.client :as http]
     [me.raynes.laser :as l]
     [clojure.java.io :as io]
     [clojure.xml :as xml]
     [clojure.zip :as czip]
     [clojure.data.zip.xml :as zip-xml]
     [clj-time.core]
     [clj-time.format]
     )
  )

(defn get-response [z]
  (let [href (first (map zip-xml/text (zip-xml/xml-> z :d:href)))
        status (first (map zip-xml/text (zip-xml/xml-> z :d:propstat :d:status)))
        restype (map :tag (mapcat czip/children (zip-xml/xml-> z :d:propstat :d:prop :d:resourcetype)))
        ]
    {:href href :status status :resourcetypes (into #{} restype)}))

(defn get-calendars [calrc]
  (let [propfind-response (:body @(http/propfind (str (:urlbase calrc) (:calendar calrc) "/calendars/" (:user calrc)) {:basic-auth [(:user calrc) (:pass calrc)]}))
        propfind-zip (-> (java.io.ByteArrayInputStream. (.getBytes propfind-response)) xml/parse) 
        propfind-root (czip/xml-zip propfind-zip) ]
    (map get-response (zip-xml/xml-> propfind-root :d:response))))

;(get-calendars calrc)

(defn calendar-query [start-date end-date] 
(let [start-date  (clj-time.format/unparse (clj-time.format/formatters :basic-date-time-no-ms) start-date)
      end-date    (clj-time.format/unparse (clj-time.format/formatters :basic-date-time-no-ms) end-date)]
  (l/document (l/parse (io/resource "calendar-query.template.xml") :xml)
     (l/element= :c:time-range) 
     (comp (l/attr "end"  end-date) (l/attr "start" start-date)))))

;(calendar-query (clj-time.core/now ) (clj-time.core/minus (clj-time.core/now ) (clj-time.core/days 1)))
(defn split-n-scrub [s]
  (let [[k v] (clojure.string/split s #":")]
    [(keyword (clojure.string/replace k #";" ":")) v])
  )

(defn ical-map [d] (into {} (map split-n-scrub (clojure.string/split-lines d))))

(defn query-cal-data [start-date end-date calrc] 
  (let [cqdoc     (calendar-query start-date end-date)
        url       (str (:urlbase calrc ) (:calbasepath calrc) (:calendar calrc))
        reportdoc (:body @(http/report  url {:basic-auth [(:user calrc) (:pass calrc)] :body cqdoc}) )
        ]
  (map (comp ical-map #(apply str %) :content) 
      (l/select (l/parse reportdoc) (l/element= :cal:calendar-data))))
  )



(comment

  (let [calrc (read-string (slurp (clojure.java.io/file (System/getenv "HOME") ".calendar-seq.rc")))
        now (clj-time.core/now)
        later (clj-time.core/plus now (clj-time.core/days 5)) 
        
        ]
    (query-cal-data now later calrc))

 (pprint (map (comp ical-map #(apply str %) :content) 
      (l/select
         (l/parse (query-cal-data (clj-time.core/now ) (clj-time.core/plus (clj-time.core/now ) (clj-time.core/days 1))))
         (l/element= :cal:calendar-data)))) 
  
  ({:href "/owncloud/remote.php/caldav/calendars/robbie/", :status "HTTP/1.1 200 OK", :resourcetypes #{:d:collection}} 
 {:href "/owncloud/remote.php/caldav/calendars/robbie/defaultcalendar/", :status "HTTP/1.1 200 OK", :resourcetypes #{:cal:calendar :d:collection}} 
 {:href "/owncloud/remote.php/caldav/calendars/robbie/contact_birthdays/", :status "HTTP/1.1 200 OK", :resourcetypes #{:cal:calendar :d:collection}} 
 {:href "/owncloud/remote.php/caldav/calendars/robbie/outbox/", :status "HTTP/1.1 200 OK", :resourcetypes #{:cal:schedule-outbox :d:collection}})

  
  [{:type :document, :content [{:type :xml-declaration, :attrs {:declaration "xml version=\"1.0\" encoding=\"UTF-8\"?"}} "\n" {:type :element, :attrs {:xmlns:c "urn:ietf:params:xml:ns:caldav", :xmlns:d "DAV:"}, :tag :c:calendar-query, :content ["\n" {:type :element, :attrs nil, :tag :d:prop, :content ["\n" {:type :element, :attrs nil, :tag :c:calendar-data, :content nil} "\n"]} "\n" {:type :element, :attrs nil, :tag :c:filter, :content ["\n" {:type :element, :attrs {:name "VCALENDAR"}, :tag :c:comp-filter, :content ["\n" {:type :element, :attrs {:name "VEVENT"}, :tag :c:comp-filter, :content ["\n" {:type :element, :attrs {:end "ENDDATE", :start "STARTDATE"}, :tag :c:time-range, :content nil} "\n"]} "\n"]} "\n"]} "\n"]} "\n"]} nil]

  (l/select (l/parse (io/resource "calendar-query.template.xml") :xml)
     (l/element= :d:prop))
  (l/select (l/parse (io/resource "calendar-query.template.xml") :xml)
     (l/and (l/element= :c:time-range )) (l/attr= :end "ENDDATE"))
  (l/document (l/parse (io/resource "calendar-query.template.xml") :xml)
     (l/element= :c:time-range) 
     (comp (l/attr "end"  "201406180000Z") (l/attr "start"  "201406110000Z"))
     )
(def cqdoc (calendar-query (clj-time.core/now ) (clj-time.core/plus (clj-time.core/now ) (clj-time.core/days 1))))
  (:body @(http/report (str (:urlbase calrc ) "/owncloud/remote.php/caldav/calendars/robbie/defaultcalendar/") {:basic-auth [(:user calrc) (:pass calrc)] :body cqdoc}))

  (clj-time.format/show-formatters)

; can't use odd CalDav request types with clj-http without writing them into Apache HttpComponents itself
;(client/get (str (:url calrc ) "/calendars/robbie") {:basic-auth [(:user calrc) (:pass calrc)]})
;(System/getProperty "javax.net.ssl.keyStore")
;(def tmpbody (client/get (str (:url calrc ) "/calendars/robbie") {:basic-auth [(:user calrc) (:pass calrc)]}))
;(client/request {:method :options :url (str (:url calrc ) "/calendars/robbie") :basic-auth [(:user calrc) (:pass calrc)] })
(def propfind-response (:body @(http/propfind (str (:url calrc ) "/calendars/robbie") {:basic-auth [(:user calrc) (:pass calrc)]})))
(type propfind-response )
(pprint propfind-response )
(l/parse propfind-response :xml)
(nth (:content (first (l/parse propfind-response :xml))) 2)
(l/select (l/parse propfind-response :xml) (l/element= "d:response"))
(map l/fragment (l/select (l/parse propfind-response :xml) (l/element= "d:response")))
(first (l/select (l/parse propfind-response :xml) (l/element= "d:response"))) 

(defn get-response [hz]

  )

(map get-response  (l/select (l/parse propfind-response :xml) (l/element= "d:response")) ) 

{:type :element, :attrs nil, :tag :d:response, :content [{:type :element, :attrs nil, :tag :d:href, :content ["/owncloud/remote.php/caldav/calendars/robbie/"]} {:type :element, :attrs nil, :tag :d:propstat, :content [{:type :element, :attrs nil, :tag :d:prop, :content [{:type :element, :attrs nil, :tag :d:resourcetype, :content [{:type :element, :attrs nil, :tag :d:collection, :content nil}]}]} {:type :element, :attrs nil, :tag :d:status, :content ["HTTP/1.1 200 OK"]}]}]} 

{:type [:collection :calendar] :href "..." :status "..." :lastmodified ""}

(l/select (l/parse propfind-response :xml) (l/element= "d:href"))
(-> (l/select (l/parse propfind-response :xml) (l/element= "d:response")) first :content first :content)



 ;how ugly, just to parse a string! 
(def propfind-response (:body @(http/propfind (str (:url calrc ) "/calendars/robbie") {:basic-auth [(:user calrc) (:pass calrc)]})))
(def propfind-zip (-> (java.io.ByteArrayInputStream. (.getBytes propfind-response)) xml/parse) )
(def propfind-root (czip/xml-zip propfind-zip))




; ({:tag :d:collection, :attrs nil, :content nil}  {:tag :cal:calendar, :attrs nil, :content nil})
 
  )
