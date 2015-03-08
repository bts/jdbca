(ns jdbca
  "Functions for interacting with databases asynchronously."
  (:require
    [clojure.java.jdbc :as j]
    [manifold.deferred :as d]
    [manifold.stream :as s]))
