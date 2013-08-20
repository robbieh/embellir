(defproject embellir "0.1.0"
            :description "embellir: making beautiful what you need to know"
            :url ""
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [seesaw "1.4.2"]
                           [clj-time "0.4.4"]
                           [the/parsatron "0.0.3"]
                           [org.clojure/data.xml "0.0.6"]
                           [org.clojure/data.zip "0.1.1"]
                           [org.clojure/math.numeric-tower "0.0.2"]
                           [server-socket "1.0.0"]
                           ]
            ;  :profiles {:dev {:dependencies [[clj-ns-browser "1.3.0"]]}}
            :warn-on-reflection true
            :main embellir.core
           :user  {:repl-options  {:timeout 120000}} 

            )
