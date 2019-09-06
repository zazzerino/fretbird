(ns fretbird.specs
  (:require [clojure.spec.alpha :as spec]
            [fretbird.theory :as theory]))

(spec/def ::note-to-guess ::theory/note)
(spec/def ::user-guess (spec/or :user-has-guessed     ::theory/fretboard-coord
                                :user-has-not-guessed nil?))

(spec/def ::dot ::theory/fretboard-coord)
(spec/def ::dots (spec/coll-of ::dot :kind vector?))

(spec/def ::status #{:playing :correct-guess})

(spec/def ::db (spec/keys :req-un [::note-to-guess ::user-guess ::status ::dots]))
