(ns cockpit.web
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [compojure.handler :only [site]]
            [ring.util.response :as resp]
            [selmer.parser :refer [render render-file]]))

(defroutes public-routes
  (GET "/home" [] (render-file "templates/home.html" {}))
  (POST "/login" {:keys [params]} (render-file "templates/home.html" {}))
  (GET "/view" [] (render-file "templates/view.html" {}))
  (POST "/view" {:keys [params]} (render-file "templates/view.html" {:case (:case params)}))
  (route/resources "/")
  (route/not-found "Page not found"))

(defroutes secured-routes
  (GET "/home" [] (render-file "templates/home.html" {}))
  (GET "/view" [] (render-file "templates/view.html" {}))
  (POST "/view" {:keys [params]} (render-file "templates/view.html" {:case (:case params)})))

(def app
  (wrap-defaults public-routes (-> site-defaults (assoc-in [:security :anti-forgery] false))))
