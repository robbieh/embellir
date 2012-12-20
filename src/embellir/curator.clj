(ns embellir.curator
  (:gen-class)
  (:require [clojure.inspector :only atom?]
      [clj-time.local]
            [clj-time.core]
            [clj-time.coerce])
  )

;a map of 'items' - key is ascending integer
;items should be a map holding :name :function :atom :time-to-live
(def collection 
  "A map of 'items' to be curated. Keys are :0 :1 :2 ... etc 
  The items should hold :name :function :atom :time-to-live"
  (atom {}))

;remember: .put .take .peek
(def updateq-comparator (comparator (fn [a b] (clj-time.core/before? (:time a) (:time b)))))
(def updateq
  "A list of maps with :time to be executed and :collection-key of item to be executed"
  (java.util.concurrent.PriorityBlockingQueue. 5 updateq-comparator))

;should this look for the highest key?
;should the key be UUIDs instead?
(defn next-collection-key 
  "Return the next key to be used in the @collection map as an int"
;  [] (-> @collection count str keyword))
    [] (count @collection))

(defn queue-item [item-id]
  (let [item-keyword (-> item-id str keyword )]
    (.put updateq {:collection-key item-id 
                   :time (clj-time.coerce/to-date-time
                           (+ 
                             (-> @collection item-keyword :time-to-live)
                             (clj-time.coerce/to-long (clj-time.local/local-now) )))})))

(defn curate 
  "Define an item that the curator will watch over:
  itemname - handy string holding a human-friendly item name
  atomx - a Clojure atom holding the initial state of the item to be curated
  function - a function the curator will use to update the item
    note: swap! will be called on the atom and function
  time-to-live - the curator will update the atom after this amount of time passes
    note: milliseconds"
  [itemname atomx function time-to-live]
  (assert (string? itemname))
  (assert (clojure.inspector/atom? atomx))
  (assert (fn? function))
  (assert (> time-to-live 0))
  (let [itemkey (next-collection-key)]
    (swap! collection #(assoc % (-> itemkey str keyword)
                            {:name itemname
                             :atom atomx
                             :function function
                             :time-to-live time-to-live}))
       (queue-item itemkey)))

(defn get-itemid-by-name
  "Searches for an item ID by :name"
  []
  ())


;finds the item 
(defn run-item [item-id]
  "Fetch the item from @collection 
  Then apply the item's :function to the item's :atom"
  (assert (>= item-id 0))
  (assert (> (count @collection) item-id))
;  (println "run-item: " item-id)
  (let [item (get @collection (-> item-id str keyword))
        itematom (:atom item)
        itemfunc (:function item)]
;        (println "run-item: " item itematom itemfunc)
    (swap! itematom itemfunc)))

(defn manage-queue []
  (loop [item (.take updateq)]
;    (println "found item: " item (get item :collection-key))
    (let [itemtime (:time item)
          itemtimelong (clj-time.coerce/to-long (:time item))
          now (clj-time.coerce/to-long (clj-time.local/local-now))
          timedifference (- itemtimelong now)] ;negative when item time is in the past
      (if (< timedifference 0)
        (do (run-item (get item :collection-key))
            (queue-item (get item :collection-key)))
        (do (Thread/sleep (min timedifference 1000))
            (.put updateq item))))
    (recur (.take updateq))))



;    (when (.peek updateq)
;      (println "something is in the queue")
;      (let [newitem (.peek updateq)
;            newitemtime (:time newitem)
;            newitemtimelong (clj-time.coerce/to-long (:time newitem))
;            now (clj-time.coerce/to-long (clj-time.local/local-now))
;            timedifference (- newitemtimelong now)]
;        (println newitem newitemtime newitemtimelong now timedifference)
;        (if (> timedifference 0) 
;          (println "sleeping: " (min timedifference 1000))
;          (Thread/sleep (min timedifference 10)))))
;    (recur (.take updateq))))

(defn start-curator []
  "Starts the curator in a separate thread"
  (.start (Thread. manage-queue)))

(defn xcolset [] 
  (def collection (atom 
                    {:0 {:atom (atom 0) :function inc :time-to-live 10000 :name "counter1"}
                     :1 {:atom (atom 0) :function inc :time-to-live 5000 :name "counter2"}
                     }))
  )
