# jdbca

An **in-progress** asynchronous [manifold](https://github.com/ztellman/manifold) interface to the synchronous [java.jdbc](https://github.com/clojure/java.jdbc) using a [dirigiste](https://github.com/ztellman/dirigiste) [thread pool](http://ideolalia.com/dirigiste/io/aleph/dirigiste/Executor.html).

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

## License

Copyright © 2015 Brian Schroeder

Distributed under the MIT License.
