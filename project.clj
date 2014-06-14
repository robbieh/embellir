(defproject embellir "0.1.0"
            :description "embellir: making beautiful what you need to know"
            :url ""
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [seesaw "1.4.2"]
                           [clj-time "0.7.0"]
                           [org.clojure/data.xml "0.0.6"]
                           [org.clojure/data.zip "0.1.1"]
                           [org.clojure/math.numeric-tower "0.0.4"]
                           [server-socket "1.0.0"]
                           [ipviz "0.1.0-SNAPSHOT"]
                           [org.clojure/data.json "0.2.3"]
                           [cjdnsadmin-clj "0.1.0-SNAPSHOT"]
                           [org.edn-format/data.edn "0.1.0"]
                           [org.osaf/caldav4j "0.7"]
                           [http-kit "2.1.18"]
                           [me.raynes/laser "1.1.1"]
                           [me.raynes/hickory "0.4.2"]
                           [org.clojure/data.zip "0.1.1"]
                           [instaparse "1.3.2"]
                           [org.clojure/core.memoize "0.5.6"]
                           ]
            :profiles {:dev {:dependencies [[alembic "0.2.1"]]}}
            :resouce-paths ["resources"]
            :repositories [["caldav4j-repo" "https://caldav4j.googlecode.com/svn/maven/"]]
            :repl-options {:timeout 600000}
            :warn-on-reflection true
            :main embellir.core
           :user  {:repl-options  {:timeout 120000}} 

            )
