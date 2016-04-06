(ns cockpit.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cockpit.security :as sec]

            [clojure.java.io :as io]
            [ring.middleware.defaults :refer :all]
            [compojure.handler :only [site]]
            [org.httpkit.server :refer [run-server]]
            [ring.util.response :as resp]
            [selmer.parser :refer [render render-file]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))



;;;;;;;;;;;;;;;;;
;; Controllers ;;
;;;;;;;;;;;;;;;;;

(defn home
  "doc-string"
  [request]
  (if-not (authenticated? request)
    (throw-unauthorized)
    (render-file "templates/home.html" {})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Routes and middlewares ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defroutes all-routes
  (GET "/" [] home)
  (GET "/home" [] (render-file "templates/home.html" {}))
  (GET "/view" [] (render-file "templates/view.html" {}))
  (POST "/view" {:keys [params]} (render-file "templates/view.html" {:case (:case params)}))
  (GET "/login" [] (render-file "templates/login.html" {}))
  (route/resources "/")
  (route/not-found "Page not found")) ;; resources should be in resources/public folder


;; User defined unauthorized handler
;;
;; This function is responsible for handling
;; unauthorized requests (when unauthorized exception
;; is raised by some handler)

(defn unauthorized-handler
  [request metadata]
  (cond
    ;; If request is authenticated, raise 403 instead
    ;; of 401 (because user is authenticated but permission
    ;; denied is raised).
    (authenticated? request)
    (-> (render (slurp (io/resource "error.html")) request)
        (assoc :status 403))
    ;; In other cases, redirect the user to login page.
    :else
    (let [current-url (:uri request)]
      (resp/redirect (format "/login?next=%s" current-url)))))

;; Create an instance of auth backend.

(def auth-backend
  (session-backend {:unauthorized-handler unauthorized-handler}))



(def app
  (-> #'all-routes
      (wrap-authorization auth-backend)
      (wrap-authentication auth-backend)
      (wrap-defaults (-> site-defaults (assoc-in [:security :anti-forgery] false)))))
