(ns fretbird.views
  (:require [re-frame.core :as re-frame]
            [fretbird.fretboard :as fretboard]
            [fretbird.subs :as subs]
            [fretbird.events :as events]
            [fretbird.stave :as stave]))

;; (defn string1-checkbox []
;;   [:div.string1-checkbox
;;    [:label {:html-for "string1"}] "string 1"
;;    [:input {:type "checkbox"
;;             :name "string1"}]])

(defn make-accidental-checkbox [acc-key sub-key]
  (let [use-acc?   (re-frame/subscribe [sub-key])
        class-name (str (name acc-key) "-checkbox")]
    [:div {:class class-name}
     [:label {:html-for class-name} (name acc-key)]
     [:input {:type "checkbox"
              :name class-name
              :checked @use-acc?
              :on-change #(re-frame/dispatch [::events/toggle-accidental acc-key])}]]))

(defn sharps-checkbox []
  (make-accidental-checkbox :sharps ::subs/use-sharps?))

(defn flats-checkbox []
  (make-accidental-checkbox :flats ::subs/use-flats?))

(defn new-note-button []
  [:button.reset-button
   {:on-click #(re-frame/dispatch [::events/new-note])}
   "new note"])

(defn opts-panel []
  [:div.opts-panel
   [sharps-checkbox]
   [flats-checkbox]
   #_[string1-checkbox]])

(defn playing-page []
  [:div.playing-page
   [stave/stave]
   [fretboard/fretboard]
   [opts-panel]
   [new-note-button]])

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
