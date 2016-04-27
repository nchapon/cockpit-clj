(ns cockpit.core-test
  (:require [clojure.test :refer :all]
            [cockpit.core :refer :all]
            [cockpit.models.users :as users]))

(defn create-user-fixture [f]
        (users/create! {:email "user@gmail.com" :password "secret"})
        (f)
        (swap! users/userstore {}))

(use-fixtures :once create-user-fixture)

(deftest authenticate-user
  (let [req {:session {}, :form-params {"email" "user@gmail.com", "password" "secret"}}]
      (testing "Authenticate user with user and password"
        (is (= {:identity "user@gmail.com"}  (:session (post-login req)))))))
