(ns fretbird.events
  (:require [re-frame.core :as re-frame]
            [clojure.spec.alpha :as spec]
            [fretbird.specs :as specs]
            [fretbird.theory :as theory]
            [fretbird.db :as db]))

(defn check-and-throw [spec db]
  (when-not (spec/valid? spec db)
    (throw (ex-info (str "spec check failed: "
                         (spec/explain-str spec db))
                    {}))))

(def check-spec-interceptor
  (re-frame/after (partial check-and-throw ::specs/db)))

(re-frame/reg-event-db
 ::initialize-db
 [check-spec-interceptor]
 (fn [_ _]
   (db/default-db)))

(re-frame/reg-event-db
 ::fretboard-click
 [check-spec-interceptor]
 (fn [db [_ fretboard-coord]]
   (let [correct-guess? (theory/correct-guess? (:note-to-guess db) fretboard-coord)
         status         (if correct-guess? :correct-guess :playing)]
     (assoc db
            :user-guess  fretboard-coord
            :dots        [fretboard-coord]
            :status      status))))

(re-frame/reg-event-db
 ::new-note-button-click
 [check-spec-interceptor]
 (fn [db _]
   (assoc db
          :status        :playing
          :note-to-guess (theory/random-note)
          :user-guess    nil
          :dots          [])))
