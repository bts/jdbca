(ns jdbca-test
  (:require
    [clojure.test :refer :all]
    [clojure.java.jdbc :as jdbc]
    [jdbca :as j]
    [manifold.stream :as s]))

(def db {:subprotocol "h2"
         :subname "mem:jdbca"})

(deftest test-query
  (testing "erroneous query"
    (is (thrown? Exception @(j/query db "select * from nonexistent_table"))))
  (testing "successful query"
    (jdbc/with-db-connection [conn db]
      (jdbc/db-do-commands conn
        "create table things (name varchar)"
        "insert into things (name) values ('a'), ('b')")
      (is (= (->> (j/query conn "select * from things")
               deref
               (s/map :name)
               s/stream->seq)
             '("a" "b")))))
  (testing "optional transducer"
    (jdbc/with-db-connection [conn db]
      (jdbc/db-do-commands conn
        "create table things (name varchar)"
        "insert into things (name) values ('a'), ('b')")
      (is (= (->> (j/query conn "select * from things"
                    {:transducer (map #(str (:name %) "!"))})
               deref
               s/stream->seq)
             '("a!" "b!"))))))
