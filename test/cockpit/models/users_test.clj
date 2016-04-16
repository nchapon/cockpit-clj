(ns cockpit.models.users-test
  (:require [clojure.test :refer :all]
            [cockpit.models.users :as users]))



(deftest authorize-users
  (let [user (users/create! {:email "john.doe@gmail.com" :password "secret"})]
    (testing "Accepts the correct password"
      (is (users/password-matches? "john.doe@gmail.com" "secret")))))
