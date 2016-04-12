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


(def authdata
  "Global var that stores valid users with their
   respective passwords."
  {:admin "secret"
   :test "secret"})

(defn get-user-by-username-and-password
  [username password]
  (let [found-password (get authdata (keyword username))]
    (if (= found-password password) username)))

(defn post-login [request]
   (let [email (get-in request [:form-params "email"])
        password (get-in request [:form-params "password"])
        session (:session request)]
    (if-let [username (get-user-by-username-and-password email password)]
      (let [next-url (get-in request [:query-params :next] "/")
            updated-session (assoc session :identity (keyword username))]
        (-> (resp/redirect next-url)
            (assoc :session updated-session)))
      (render-file "templates/login.html"  {:message "Erreur Authentification"}))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Routes and middlewares ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn debug-ring [request]
  (let [email (get-in request [:form-params "email"])
        password (get-in request [:form-params "password"])
        session (:session request)]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (str  "Request " request)}))

(defroutes all-routes
  (GET "/" [] home)
  (GET "/home" [] home)
  (GET "/view" [] (render-file "templates/view.html" {}))
  (POST "/view" {:keys [params]} (render-file "templates/view.html" {:case (:case params)}))
  (GET "/login" [] (render-file "templates/login.html" {}))
  (POST "/login" [] post-login)
  (GET "/debug" [] debug-ring)
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
