# jdbca

An **in-progress** asynchronous [manifold](https://github.com/ztellman/manifold) interface to the synchronous [java.jdbc](https://github.com/clojure/java.jdbc) using a [dirigiste](https://github.com/ztellman/dirigiste) [thread pool](http://ideolalia.com/dirigiste/io/aleph/dirigiste/Executor.html).

```clojure
[jdbca "0.1.0-SNAPSHOT"]
```

## Usage

```clojure
(require '[jdbca :as j])

(def jdbc-spec {:classname "org.postgresql.Driver"
                :subprotocol "postgresql"
                :subname "//localhost:5432/test"
                :user "user"
                :password ""})

(def rows (j/query jdbc-spec ["select * from dogs where name = ?" "abe"]))
```

## License

Copyright © 2015 Brian Schroeder

Distributed under the MIT License.
