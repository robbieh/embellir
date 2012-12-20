(ns embellir.curator
  (:gen-class)
  (:require [clj-time.local]
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
  "Return the next key to be used in the @collection map"
  [] (-> @collection count str keyword))

(defn queue-item [item-id]
  (let [item-keyword (-> item-id str keyword )]
    (.put updateq {:collection-key item-id 
                   :time (clj-time.coerce/to-date-time
                           (+ 
                             (-> @collection item-keyword :time-to-live)
                             (clj-time.coerce/to-long (clj-time.local/local-now) )))})))

;finds the item 
(defn run-item [item-key]
  "Fetch the item from @collection 
  Then apply the item's :function to the item's :atom"
  (assert (>= item-key 0))
  (assert (> (count @collection) item-key))
;  (println "run-item: " item-key)
  (let [item (get @collection (-> item-key str keyword))
        itematom (:atom item)
        itemfunc (:function item)]
;        (println "run-item: " item itematom itemfunc)
    (swap! itematom itemfunc)))

(defn manage-queue []
  (loop [item (.take updateq)]
;    (println "found item: " item (get item :collection-key))
    (run-item (get item :collection-key))
    (queue-item (get item :collection-key))
    (when (.peek updateq)
;      (println "something is in the queue")
      (let [newitem (.peek updateq)
            newitemtime (:time newitem)
            newitemtimelong (clj-time.coerce/to-long (:time newitem))
            now (clj-time.coerce/to-long (clj-time.local/local-now))
            timedifference (- newitemtimelong now)]
;        (println newitem newitemtime newitemtimelong now timedifference)
        (if (> timedifference 0) 
;          (println "sleeping: " (min timedifference 1000))
          (Thread/sleep (min timedifference 10)))))
    (recur (.take updateq))))

(defn start-curator []
  "Starts the curator in a separate thread"
  (.start (Thread. manage-queue)))

(defn xcolset [] 
  (def collection (atom 
                    {:0 {:atom (atom 0) :function inc :time-to-live 10000 :name "counter1"}
                     :1 {:atom (atom 0) :function inc :time-to-live 5000 :name "counter2"}
                     }))
  )
