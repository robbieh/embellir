(defproject embellir "0.1.0-SNAPSHOT"
  :description "embellir: making beautiful what you need to know"
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-time "0.4.4"]
                 [quil "1.6.0"]]
  :profiles {:dev {:dependencies [[clj-ns-browser "1.3.0"]]}}
  :main embellir.core)
