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
      (is (= (s/stream->seq @(j/query conn "select * from things"
               {:transducer (map #(str (:name %) "!"))}))
             '("a!" "b!"))))))

(deftest test-execute!
  (let [stmt ["insert into things (name) values (?)" "abc"]]
    (testing "failed execution"
      (is (thrown? Exception @(j/execute! db stmt))))
    (testing "successful execution"
      (jdbc/with-db-connection [conn db]
        (jdbc/db-do-commands conn "create table things (name varchar)")
        (is (= @(j/execute! conn stmt)
               '(1)))))))
