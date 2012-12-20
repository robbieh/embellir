(ns embellir.curator
  (:gen-class)
  (:require [clj-time.local]
            [clj-time.core]
            [clj-time.coerce])
  )

;a map of 'items' - key is ascending integer
;items should be a map holding :name :function :atom :time-to-live
(def collection 
  "A map of 'items' to be curated. Keys are :0 :1 :2 ... etc"
  "The items should hold :name :function :atom :time-to-live"
  (atom {}))

;remember: .put .take .peek
(def timemap-comparator (comparator (fn [a b] (clj-time.core/before? (:time a) (:time b)))))
(def timemap
  "A list of maps with :time to be executed and :collection-key of item to be executed"
  (java.util.concurrent.PriorityBlockingQueue. 5 timemap-comparator))

;should this look for the highest key?
;should the key be UUIDs instead?
(defn next-collection-key 
  "Return the next key to be used in the @collection map"
  [] (-> @collection count str keyword))

;finds the item 
(defn run-item [item-key]
  "Fetch the item from @collection"
  "Then apply the item's :function to the item's :atom"
  (assert (>= 0 item-key))
  (assert (< (count @collection) item-key))
  (let [item (get @collection (-> item-key str keyword))
        itematom (:atom item)]
    (println item)
    (swap! itematom (:function item))))

(defn manage-queue []
  (loop [item (.take updateq)]
    (println "found item: " item)
    (run-item (:colleciton-key item))
    (let [newitem (.peek updateq)
          newitemtime (:time newitem)
          now (clj-time.coerce/to-long (clj-time.local/local-now))
          timedifference (- newitemtime now)]
      (if (> 0 timedifference) (Thread/sleep timedifference)))
    (recur (.take updateq))))


