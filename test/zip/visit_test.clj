(ns zip.visit-test
  (:require
   [clojure.test :refer :all]
   [clojure.zip]
   [zip.visit]
   )
  )


(defn- -xor [a b] (and (or a b) (not (and a b))))


(deftest false-is-not-nil

  (testing "false node propagation"

    (is (= {:node [false true true false true true], :state nil}
           (zip.visit/visit
            (clojure.zip/vector-zip [0 1 1 2 3 5])
            nil
            [(zip.visit/visitor
              :pre [n s]
              (if (number? n) {:node (odd? n)}))])
           )
        )
    )

  (testing "false state propagation"

    (is (= {:node [0 1 1 2 3 5],
            :state (reduce -xor false [0 1 1 2 3 5])}
           (zip.visit/visit
            (clojure.zip/vector-zip [0 1 1 2 3 5])
            false
            [(zip.visit/visitor
              :pre [n s]
              (if (number? n) {:state (-xor s (odd? n))}))])
           )
        )
    )
  )
