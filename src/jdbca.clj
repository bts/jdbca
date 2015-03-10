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
  "A deferred yielding a stream of rows from `db` for the java.jdbc query `q`.
  The deferred is realized with an error if the query does not succeed.
  Optionally takes a custom `:executor`, and a `:transducer` and `:buffer-size`
  for the returned stream."
  ([db q]
   (query db q nil))
  ([db q {:keys [executor buffer-size transducer]
          :or {executor default-executor
               buffer-size default-rows-buffer-size
               transducer nil}}]
   (let [d (d/deferred)
         s (s/stream buffer-size transducer)]
     (.execute ^Executor executor
       (fn []
         (try
           (jdbc/query db q
             :result-set-fn (fn [rows]
                              (d/success! d (s/source-only s))
                              (doseq [row rows]
                                (s/put! s row))
                              (s/close! s)))
           (catch Exception e
             (d/error! d e)))))
     d)))

(defn execute!
  "Issues a SQL statement described by the vector `stmt` to `db`, returning a
  deferred sequence of update counts, one for each param java.jdbc param group.
  Optionally takes a custom `:executor`."
  ([db stmt]
   (execute! db stmt nil))
  ([db stmt {:keys [executor]
             :or {executor default-executor}}]
     (let [d (d/deferred)]
       (.execute ^Executor executor
         (fn []
           (try
             ;; we diverge from java.jdbc here; if the user wants a transaction
             ;; then they should express that explicitly.
             (d/success! d (jdbc/execute! db stmt :transaction? false))
             (catch Exception e
               (d/error! d e)))))
       d)))
