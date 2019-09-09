(ns fretbird.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [fretbird.specs :as specs]
            [fretbird.db :as db]
            [fretbird.events :as events]
            [fretbird.views :as views]
            [fretbird.subs :as subs]
            [fretbird.theory :as theory]))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "root")))

(defn ^:dev/after-load main []
  (re-frame/dispatch-sync [::events/initialize-db])
  (mount-root))
