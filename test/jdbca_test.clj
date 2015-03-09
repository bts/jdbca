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
      (jdbc/db-do-commands conn "create table things")
      (is (empty? (s/stream->seq @(j/query conn "select * from things")))))))
