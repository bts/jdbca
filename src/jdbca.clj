(ns jdbca
  "Functions for interacting with databases asynchronously."
  (:require
    [jdbca.dirigiste :as dirigiste]
    [clojure.java.jdbc :as jdbc]
    [manifold.deferred :as d]
    [manifold.stream :as s])
  (:import
    [java.util.concurrent
     Executor]))

(def default-executor
  (dirigiste/utilization-executor 0.9 32))

(def default-rows-buffer-size 4096)

;; public API

(defn query
  "A deferred yielding a stream of rows from `db` for the java.jdbc query
  described by `q`. The deferred is realized with an error if the query does not
  succeed. Optionally takes a custom `:executor`, and a `:transducer` and
  `:buffer-size` for the returned stream."
  ([db q]
   (query db q nil))
  ([db q {:keys [executor buffer-size transducer]
          :or {executor default-executor
               buffer-size default-rows-buffer-size
               transducer nil}}]
   (let [d (d/deferred)
         s (s/stream buffer-size transducer executor)]
     (.execute ^Executor executor
       (fn []
         (try
           (jdbc/query db q
             :result-set-fn (fn [rows]
                              (d/success! d s)
                              (doseq [row rows]
                                (s/put! s row))
                              (s/close! s)))
           (catch Exception e
             (d/error! d e)))))
     d)))
