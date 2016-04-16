(ns cockpit.models.users
  (require [buddy.hashers :as hashers]))

(defn uuid []
  (java.util.UUID/randomUUID))

(def userstore (atom {}))

(defn create!
  "Create user"
  [user]
  (let [password (:password user)
        user-id (uuid)]
    (-> user
        (assoc :id user-id :password-hash (hashers/encrypt password))
        (dissoc :password)
        (->> (swap! userstore assoc user-id)))))


(defn password-matches?
  "doc-string"
  [email password]
  (reduce (fn [_ user]
            (if (and (= email (:email user))
                     (hashers/check password (:password user)))
              (reduced user)))
          (vals @userstore)))
