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

(def correct-color "limegreen")
(def incorrect-color "tomato")

(re-frame/reg-sub
 ::dot-color
 (fn []
   [(re-frame/subscribe [::note-to-guess])
    (re-frame/subscribe [::user-guess])])
 (fn [[note coord]]
   (if (theory/correct-guess? note coord)
     correct-color
     incorrect-color)))

(re-frame/reg-sub
 ::color-dots
 (fn []
   [(re-frame/subscribe [::dots])
    (re-frame/subscribe [::dot-color])])
 (fn [[dots color]]
   (map (fn [dot]
          (assoc dot :color color))
        dots)))

(re-frame/reg-sub
 ::status
 (fn [db]
   (:status db)))

(re-frame/reg-sub
 ::accidentals
 (fn [db]
   (:accidentals db)))

(re-frame/reg-sub
 ::use-sharps?
 (fn []
   (re-frame/subscribe [::accidentals]))
 (fn [accidentals]
   (contains? accidentals :sharps)))

(re-frame/reg-sub
 ::use-flats?
 (fn []
   (re-frame/subscribe [::accidentals]))
 (fn [accidentals]
   (contains? accidentals :flats)))

(re-frame/reg-sub
 ::strings
 (fn [db]
   (:strings db)))

(re-frame/reg-sub
 ::use-string-1?
 (fn []
   (re-frame/subscribe [::strings]))
 (fn [strings]
   (contains? strings 1)))
