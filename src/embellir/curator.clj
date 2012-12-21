(ns embellir.curator
  (:gen-class)
  (:require [clojure.inspector :only atom?]
      [clj-time.local]
            [clj-time.core]
            [clj-time.coerce])
  )

(def collection 
  "A map of 'curios' to be curated."
  (atom {}))

;remember: .put .take .peek
(def updateq-comparator (comparator (fn [a b] (clj-time.core/before? (:time a) (:time b)))))
(def updateq
  "A list of maps with :time to be executed and :collection-key of item to be executed"
  (java.util.concurrent.PriorityBlockingQueue. 5 updateq-comparator))

(defn queue-item [itemname]
    (.put updateq {:collection-key itemname
                   :time (clj-time.coerce/to-date-time
                           (+ 
                             (get-in @collection [itemname :time-to-live])
                             (clj-time.coerce/to-long (clj-time.local/local-now))))}))

(defn curate 
  "Define an item that the curator will watch over:
  itemname - handy string holding a human-friendly item name
  itemdata - the data you wish to curate
  function - a function the curator will use to update the item
    note: swap! will be called on the itemdata and function
  time-to-live - the curator will update the atom after this amount of time passes
    note: milliseconds"
  [itemname itemdata function time-to-live]
  (assert (string? itemname))
  (assert (fn? function))
  (assert (> time-to-live 0))
  (swap! collection #(assoc % itemname
                            {:atom (atom itemdata)
                             :function function
                             :time-to-live time-to-live}))
  (queue-item itemname))

(defn get-curio [itemname]
  (let [curio (get @collection itemname)]
    @(:atom curio)))

(defn trash-curio [itemname]
    (swap! collection #(dissoc % itemname)))

(defn list-curios [] (keys @collection))

(defn run-item [itemname]
  "Fetch the item from @collection 
  Then apply the item's :function to the item's :atom"
  (let [item (get @collection itemname)
        itematom (:atom item)
        itemfunc (:function item)]
    (swap! itematom itemfunc)))

(defn manage-queue []
  (loop [item (.take updateq)]
    ;    (println "found item: " item (get item :collection-key))
    (try
    (let [itemtime (:time item)
          itemtimelong (clj-time.coerce/to-long (:time item))
          now (clj-time.coerce/to-long (clj-time.local/local-now))
          timedifference (- itemtimelong now)] ;negative when item time is in the past
      (if (< timedifference 0)
        (do (run-item (get item :collection-key))
          (queue-item (get item :collection-key)))
        (do (Thread/sleep (min timedifference 1000))
          (.put updateq item))))
      (catch Exception e (str "Exception in manage-queue: " (.getMessage e))))
    (recur (.take updateq))))

(defn start-curator []
  "Starts the curator in a separate thread"
  (.start (Thread. manage-queue)))

