(ns app.main
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [app.db :as db]
            [app.events :as events]
            [app.views :as views]
            [app.subs :as subs]
            [app.theory :as theory]))

(defn on-reload [])

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "root")))

(defn ^:dev/after-load main []
  (re-frame/dispatch-sync [::events/initialize-db])
  (mount-root))
