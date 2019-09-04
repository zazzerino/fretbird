(ns fretbird.fretboard
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            ["fretboard-diagram" :as fd]
            [fretbird.theory :as theory]
            [fretbird.events :as events]
            [fretbird.subs :as subs]))

(defn on-click [coord inst]
  (let [string (.-string coord)
        fret   (.-fret coord)]
    (re-frame/dispatch
     [::events/fretboard-clicked {:string string :fret fret}])))

(defn make-diagram [{:keys [id dots on-click]}]
  (new fd/FretboardDiagram
       (clj->js {:id id :dots dots :onClick on-click})))

(defn fretboard []
  (let [id     "fretboard"
        dots   (re-frame/subscribe [::subs/dots])
        color  (re-frame/subscribe [::subs/dot-color])
        draw   #(make-diagram {:id       id
                               :dots     (map (fn [dot]
                                                (assoc dot :color @color))
                                              @dots)
                               :on-click on-click})]
    (reagent/create-class
     {:display-name         "fretboard"
      :reagent-render       (fn []
                              @dots ; this forces a re-render when dots is updated
                              [:div.fretboard
                               [:div {:id id}]])
      :component-did-mount  draw
      :component-did-update draw})))
