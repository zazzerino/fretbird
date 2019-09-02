(ns fretbird.events
  (:require [re-frame.core :as re-frame]
            [clojure.spec.alpha :as spec]
            [fretbird.db :as db]))

(defn check-and-throw [spec db]
  (when-not (spec/valid? spec db)
    (throw (ex-info (str "spec check failed: " (spec/explain-str spec db)) {}))))

(def check-spec-interceptor
  (re-frame/after (partial check-and-throw ::db/db)))

(re-frame/reg-event-db
 ::initialize-db
 [check-spec-interceptor]
 (fn [_ _]
   (db/default-db)))

(re-frame/reg-event-db
 ::fretboard-clicked
 [check-spec-interceptor]
 (fn [db [_ fretboard-coord]]
   (assoc db
          :user-guess fretboard-coord
          :dots [fretboard-coord])))
