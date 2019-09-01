(ns app.views
  (:require [re-frame.core :as re-frame]
            [app.fretboard :as fretboard]
            [app.stave :as stave]))

(defn main-panel []
  [:div.main-panel
   [stave/stave]
   [fretboard/fretboard]])
