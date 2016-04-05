(defproject cockpit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main cockpit.system
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [http-kit "2.1.19"]
                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [com.stuartsierra/component "0.2.3"]
                 [selmer "1.0.0"]]

  ;; nREPL by default starts in the :main namespace, we want to start in `user`
  ;; because that's where our development helper functions like (run) and
  ;; (browser-repl) live.
  :repl-options {:init-ns user}

  :profiles {:dev {:plugins [[lein-cljsbuild "1.0.6"]
                             [lein-figwheel "0.3.7"]]
                   :dependencies [[reloaded.repl "0.1.0"]]
                   :source-paths ["dev"]}})
