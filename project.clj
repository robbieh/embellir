(defproject embellir "0.1.3"
            :description "embellir: making beautiful what you need to know"
            :url ""
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :java-cmd "/usr/lib/jvm/java-7-oracle/bin/java"
;            :jvm-opts ["-Djavax.net.debug=SSL,handshake"]
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [seesaw "1.4.2" :exclusions [org.clojure/clojure]]
                           [clj-time "0.7.0"]
;                           [org.clojure/data.xml "0.0.6"]
                           [org.clojure/data.zip "0.1.1"]
                           [server-socket "1.0.0"]
                           [http-kit "2.1.18"]
                           [me.raynes/laser "1.1.1"]
                           [me.raynes/hickory "0.4.2"]
;                           [instaparse "1.3.2"]
                           [org.clojure/core.memoize "0.5.6"]
                           [info.hoetzel/clj-nio2 "0.1.1"]
                           [org.apache.xmlgraphics/batik-transcoder "1.7"] 
                           [weathergov-hourly-forecast "0.0.1"]
                           [alembic "0.2.1"]
                           [com.luckycatlabs/SunriseSunsetCalculator "1.2"]
                           ]
;            :profiles {:dev {:dependencies [[alembic "0.2.1"]]}}
            :source-paths ["src", "/home/robbie/.embellir/"]
            :resouce-paths ["resources"]
;            :repositories [["caldav4j-repo" "https://caldav4j.googlecode.com/svn/maven/"]]
            :repl-options {:timeout 600000}
            :warn-on-reflection true
            :main embellir.core
            :aot [embellir.core]
            :user  {:repl-options  {:timeout 120000}} 

            )
