(ns app.db
  (:require [clojure.spec.alpha :as spec]
            [app.theory :as theory]))

(spec/def ::note-to-guess ::theory/note)
(spec/def ::dot ::theory/fretboard-coord)
(spec/def ::dots (spec/coll-of ::dot :kind vector?))
(spec/def ::db (spec/keys :req-un [::note-to-guess ::dots]))

(defn default-db []
  {:note-to-guess (theory/random-note)
   :dots []})
