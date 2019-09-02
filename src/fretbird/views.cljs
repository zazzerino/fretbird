(ns fretbird.views
  (:require [re-frame.core :as re-frame]
            [fretbird.fretboard :as fretboard]
            [fretbird.subs :as subs]
            [fretbird.stave :as stave]))

(defn main-panel []
  [:div.main-panel
   [stave/stave]
   [fretboard/fretboard]])
