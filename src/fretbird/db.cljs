(ns fretbird.db
  (:require [clojure.spec.alpha :as spec]
            [fretbird.theory :as theory]))

(spec/def ::note-to-guess ::theory/note)
(spec/def ::user-guess (spec/or :guessed ::theory/fretboard-coord
                                :not-guessed nil?))

(spec/def ::dot ::theory/fretboard-coord)
(spec/def ::dots (spec/coll-of ::dot :kind vector?))

(spec/def ::db (spec/keys :req-un [::note-to-guess ::user-guess ::dots]))

(defn default-db []
  {:note-to-guess (theory/random-note)
   :user-guess nil
   :dots []})
