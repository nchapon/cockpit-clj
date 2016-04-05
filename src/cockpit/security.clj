(ns cockpit.security
  (:require [buddy.auth.backends.session :refer [session-backend]]))

;; Create an instance
(def backend (session-backend))
