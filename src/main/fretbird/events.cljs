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
 ::new-note
 [check-spec-interceptor]
 (fn [db _]
   (assoc db
          :status        :playing
          :note-to-guess (theory/random-note {:accidentals (:accidentals db)})
          :user-guess    nil
          :dots          [])))

(defn ^:private toggle [s k]
  (if (contains? s k)
    (disj s k)
    (conj s k)))

(re-frame/reg-event-db
 ::toggle-accidental
 [check-spec-interceptor]
 (fn [db [_ accidental]]
   (assoc db :accidentals (toggle (:accidentals db) accidental))))

(re-frame/reg-event-db
 ::toggle-string
 [check-spec-interceptor]
 (fn [db [_ string]]
   (assoc db :strings (toggle (:strings db) string))))
