(ns fretbird.db
  (:require [fretbird.theory :as theory]))

(defn default-db []
  {:note-to-guess (theory/random-note)
   :user-guess    nil
   :status        :playing
   :accidentals   #{:flats :sharps}
   :strings       #{1 2 3 4 5 6}
   :dots          []})
