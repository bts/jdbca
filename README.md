# jdbca

An **in-progress** asynchronous [manifold](https://github.com/ztellman/manifold) interface to the synchronous [java.jdbc](https://github.com/clojure/java.jdbc) using a [dirigiste](https://github.com/ztellman/dirigiste) [thread pool](http://ideolalia.com/dirigiste/io/aleph/dirigiste/Executor.html). This library is [not yet ready](#caveats) for serious use.

```clojure
[jdbca "0.1.0-SNAPSHOT"]
```

## Usage

```clojure
(require '[jdbca :as j])
(require '[manifold.stream :as s])

;; db would be a connection pool in production code
(def db {:subprotocol "postgresql"
         :subname "//localhost:5432/bts"
         :user "bts"
         :password ""})

(-> (j/query db ["select *, pg_sleep(1) from dogs where name = ?" "abe"])
  (d/chain
    (partial s/map :name)
    s/take!)
  (d/catch
    (fn [e] "caught: " e)))
;=> << … >>
*1
;=> << "abe" >>
```

## Caveats

This project currently only contains the JDBC functionality I need in another project. Contributions are welcome.

Next on the list is transaction support. I'm currently considering a faux-monadic interface that's a blend of  [`let-flow`](https://github.com/ztellman/manifold/blob/master/docs/deferred.md#let-flow) and postgres.async's [`dosql`](https://github.com/alaisi/postgres.async#composition), where each of the bindings will be made sequentially, and any failure will result in the entire form realizing an error-deferred:

```clojure
(j/do [tx (j/begin {:isolation :serializable})
       rs1 (j/execute! tx ["insert into dogs (name) values (?)" "new dog"])
       rs2 (j/execute! tx ["insert into cats (name) values (?)" "new cat"])
       _ (j/commit!)]
  (concat rs1 rs1))
```

To simplify transaction semantics, I think the current `j/query` function should probably be changed to just return a deferred yielding a clojure sequence instead of a manifold stream.

## License

Copyright © 2015 Brian Schroeder

Distributed under the MIT License.
