(ns fretbird.db
  (:require [fretbird.theory :as theory]))

(defn default-db []
  {:note-to-guess (theory/random-note)
   :user-guess    nil
   :dots          []})
