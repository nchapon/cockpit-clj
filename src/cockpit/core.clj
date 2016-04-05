(ns cockpit.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cockpit.security :as sec]
            [ring.middleware.defaults :refer :all]
            [compojure.handler :only [site]]
            [org.httpkit.server :refer [run-server]]
            [ring.util.response :as resp]
            [selmer.parser :refer [render render-file]]
            [buddy.auth.middleware :refer [wrap-authentication]]))



(defroutes all-routes
  (GET "/" [] (resp/resource-response "index.html" {:root "public"}))
  (GET "/home" [] (render-file "templates/home.html" {}))
  (GET "/view" [] (render-file "templates/view.html" {}))
  (POST "/view" {:keys [params]} (render-file "templates/view.html" {:case (:case params)}))
  (GET "/login" [] (render-file "templates/login.html" {}))
  (route/resources "/")
  (route/not-found "Page not found")) ;; resources should be in resources/public folder


(def cockpit-handler
  (-> #'all-routes
      (wrap-authentication sec/backend)
      (wrap-defaults (-> site-defaults (assoc-in [:security :anti-forgery] false)))))


(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn -main [& args] ;; entry point, lein run will pick up and start from here
  (reset! server (run-server
                  cockpit-handler {:port 3000})))
