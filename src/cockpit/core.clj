(ns cockpit.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [compojure.handler :only [site]]
            [org.httpkit.server :refer [run-server]]
            [ring.util.response :as resp]
            [selmer.parser :refer [render render-file]]))

(defroutes all-routes
  (GET "/" [] (resp/resource-response "index.html" {:root "public"}))
  (GET "/home" [] (render-file "templates/home.html" {}))
  (GET "/view" [] (render-file "templates/view.html" {}))
  (POST "/view" {:keys [params]} (render-file "templates/view.html" {:case (:case params)}))
  (GET "/login" [] (render-file "templates/login.html" {}))
  (route/resources "/")
  (route/not-found "Page not found")) ;; resources should be in resources/public folder

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn -main [& args] ;; entry point, lein run will pick up and start from here
  (reset! server (run-server (wrap-defaults all-routes (-> site-defaults (assoc-in [:security :anti-forgery] false))) {:port 3000})))
