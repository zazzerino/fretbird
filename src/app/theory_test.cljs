(ns app.theory-test
  (:require [cljs.test :refer [deftest is]]
            [clojure.spec.alpha :as spec]
            [app.theory :as theory]))

(deftest parse-note-test
  (let [note (theory/parse-note "C4")]
    (is (map? note))
    (is (= :c (:white-key note)))
    (is (nil? (:accidental note)))
    (is (= 4 (:octave note))))
  (let [note (theory/parse-note "gbb7")]
    (is (map? note))
    (is (= :g (:white-key note)))
    (is (= :bb (:accidental note)))
    (is (= 7 (:octave note)))))

(deftest midi-number-test
  (is (= 60 (theory/midi-number "C4")))
  (is (= 61 (theory/midi-number "C#4")))
  (is (= 62 (theory/midi-number "C##4")))
  (is (= 59 (theory/midi-number "Cb4")))
  (is (= 58 (theory/midi-number "Cbb4"))))

(deftest notename-test
  (let [parsed-note {:white-key :c :accidental nil :octave 4}]
    (is (= "C4" (theory/notename parsed-note))))
  (let [parsed-note {:white-key :c :accidental :b :octave 4}]
    (is (= "Cb4" (theory/notename parsed-note)))))

(deftest random-note-test
  (let [note (theory/random-note)]
    (is (true? (spec/valid? ::theory/note note)))))

(deftest transpose-note-test
  (let [note "C4"]
    (is (= (theory/transpose-note note 0)
           "C4"))
    (is (= (theory/transpose-note note 1)
           "C#4"))
    (is (= (theory/transpose-note note 2)
           "D4"))))

(deftest note-at-test
  (is (= (theory/note-at {:string 5 :fret 3})
         "C4")))

(deftest enharmonic?-test
  (is (theory/enharmonic? "C4" "C4"))
  (is (theory/enharmonic? "C4" "B#3"))
  (is (not (theory/enharmonic? "C4" "Db4"))))

(deftest fretboard-coord-test
  (is (= (theory/fretboard-coord "C4")
         {:string 5 :fret 3}))
  (is (= (theory/fretboard-coord "B4")
         {:string 2 :fret 0})))
