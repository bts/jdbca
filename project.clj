(defproject jdbca "0.1.0-SNAPSHOT"
  :description "An asynchrous interface to java.jdbc."
  :url "http://github.com/bts/jdbca"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/java.jdbc "0.3.6"]
                 [manifold "0.1.0-SNAPSHOT"]
                 [io.aleph/dirigiste "0.1.0-alpha8"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.6.0"]
                                  [org.postgresql/postgresql "9.4-1201-jdbc41"]
                                  [org.clojure/tools.namespace "0.2.9"]
                                  [com.h2database/h2 "1.4.186"]]
                   :source-paths ["dev"]}}
  :global-vars {*warn-on-reflection* true})
