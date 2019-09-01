(ns app.fretboard
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            ["fretboard-diagram" :as fretboard-diagram]
            [app.theory :as theory]
            [app.subs :as subs]))

(defn fretboard []
  (let [id "fretboard"
        dots (re-frame/subscribe [::subs/dots])
        make-diagram #(new fretboard-diagram/FretboardDiagram
                           (clj->js {:id id :dots @dots}))]
    (reagent/create-class
     {:display-name "fretboard"
      :reagent-render (fn []
                        [:div.fretboard
                         ;; [:p (str @dots)]
                         [:div {:id id}]])
      :component-did-mount make-diagram
      :component-did-update make-diagram})))
