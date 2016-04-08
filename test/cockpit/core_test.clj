(ns cockpit.core-test
  (:require [clojure.test :refer :all]
            [cockpit.core :refer :all]))


(deftest authenticate-user
  (testing "Authenticate user with user and password"
    (is (= "admin" (get-user-by-username-and-password "admin" "secret")))
    (is (= nil (get-user-by-username-and-password "admin" "")))))
