(ns app.subs
  (:require [re-frame.core :as re-frame]
            [app.theory :as theory]))

(re-frame/reg-sub
 ::note-to-guess
 (fn [db]
   (:note-to-guess db)))

(re-frame/reg-sub
 ::dots
 (fn [_ _]
   (re-frame/subscribe [::note-to-guess]))
 (fn [note _]
   [(theory/fretboard-coord note)]))
