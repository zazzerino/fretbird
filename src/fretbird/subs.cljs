(ns fretbird.subs
  (:require [re-frame.core :as re-frame]
            [fretbird.theory :as theory]))

(re-frame/reg-sub
 ::note-to-guess
 (fn [db]
   (:note-to-guess db)))

(re-frame/reg-sub
 ::user-guess
 (fn [db]
   (:user-guess db)))

(re-frame/reg-sub
 ::dots
 (fn [db]
   (:dots db)))

(defn ^:private correct-guess?
  [note coord]
  (theory/enharmonic? note (theory/note-at coord)))

(re-frame/reg-sub
 ::dot-color
 (fn []
   [(re-frame/subscribe [::note-to-guess])
    (re-frame/subscribe [::user-guess])])
 (fn [[note coord]]
   (if (correct-guess? note coord)
     "limegreen"
    "tomato")))
