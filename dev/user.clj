(ns user
  (:require [reloaded.repl :refer [system reset stop]]
            [cockpit.system]
            [cockpit.models.users :as users]))

(reloaded.repl/set-init! #'cockpit.system/create-system)


(defn add-users
  "doc-string"
  []
  (users/create! {:email "user@gmail.com" :password "secret"})
  (users/create! {:email "admin@gmail.com" :password "secret"}))
