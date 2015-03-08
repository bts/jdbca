(defproject jdbca "0.1.0-SNAPSHOT"
  :description "An asynchrous interface to java.jdbc."
  :url "http://github.com/bts/jdbca"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[manifold "0.1.0-SNAPSHOT"]
                 [org.clojure/java.jdbc "0.3.6"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.6.0"]
                                  [org.postgresql/postgresql "9.4-1201-jdbc41"]]}}
  :global-vars {*warn-on-reflection* true}
  :test-selectors {:default (complement :integration)
                   :integration :integration
                   :all (constantly true)})
