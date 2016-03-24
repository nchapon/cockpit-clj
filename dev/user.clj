(ns user
  (:require [reloaded.repl :refer [system reset stop]]
            [cockpit.system]))

(reloaded.repl/set-init! #'cockpit.system/create-system)
