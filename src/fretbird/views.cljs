(ns fretbird.views
  (:require [re-frame.core :as re-frame]
            [fretbird.fretboard :as fretboard]
            [fretbird.subs :as subs]
            [fretbird.events :as events]
            [fretbird.stave :as stave]))

(defn new-note-button []
  [:button {:on-click #(re-frame/dispatch [::events/new-note-button-click])}
   "play again"])

(defn playing-page []
  [:div.playing-page
   [stave/stave]
   [fretboard/fretboard]])

(defn correct-guess-page []
  [:div.correct-guess-page
   [stave/stave]
   [fretboard/fretboard]
   [:p "You got it!"]
   [new-note-button]])

(defn main-panel []
  (let [status (re-frame/subscribe [::subs/status])]
    [:div.main-panel
     (case @status
       :playing       [playing-page]
       :correct-guess [correct-guess-page])]))
