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
 ::status
 (fn [db]
   (:status db)))

;; (re-frame/reg-sub
;;  ::status
;;  (fn []
;;    [(re-frame/subscribe [::note-to-guess])
;;     (re-frame/subscribe [::user-guess])])
;;  (fn [[note coord]]
;;    (if (theory/correct-guess? note coord)
;;      :correct-guess
;;      :playing)))
