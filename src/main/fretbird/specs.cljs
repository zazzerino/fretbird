(ns fretbird.specs
  (:require [clojure.spec.alpha :as spec]))

(def note-regex #"([a-gA-G])(#{1,2}||b{1,2})?(\d)")
(spec/def ::note (partial re-matches note-regex))

(spec/def ::fret integer?)
(spec/def ::string integer?)
(spec/def ::fretboard-coord (spec/keys :req-un [::fret ::string]))

(spec/def ::note-to-guess ::note)
(spec/def ::user-guess (spec/or :user-has-guessed     ::fretboard-coord
                                :user-has-not-guessed nil?))

(spec/def ::dot ::fretboard-coord)
(spec/def ::dots (spec/coll-of ::dot :kind vector?))

(spec/def ::accidental #{:double-flats :flats :sharps :double-sharps})
(spec/def ::accidentals (spec/coll-of ::accidental :kind set?))

(spec/def ::string #(<= 1 % 6))
(spec/def ::strings (spec/coll-of ::string :kind set?))

(spec/def ::status #{:playing :correct-guess})

(spec/def ::db (spec/keys :req-un [::note-to-guess ::user-guess ::status
                                   ::accidentals ::strings ::dots]))
