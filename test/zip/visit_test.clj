(ns zip.visit-test
  (:require
   [clojure.test :refer :all]
   [clojure.zip :as z]
   [zip.visit :as zv]))

(defn -xor [a b] (and (or a b) (not (and a b))))

(deftest false-is-not-nil
  (testing "false node propagation"
    (is (= {:node [false true true false true true], :state nil}
           (zv/visit
             (z/vector-zip [0 1 1 2 3 5])
             nil
             [(zv/visitor :pre [n s] (if (number? n) {:node (odd? n)}))]))))

  (testing "false state propagation"
    (is (= {:node [0 1 1 2 3 5],
            :state (reduce -xor false [0 1 1 2 3 5])}
           (zv/visit
             (z/vector-zip [0 1 1 2 3 5])
             false
             [(zv/visitor :pre [n s] (if (number? n) {:state (-xor s (odd? n))}))])))))
