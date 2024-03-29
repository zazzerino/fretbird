(ns fretbird.theory
  (:require [fretbird.specs :as specs]))

(defn ^:private downcase-keyword [s]
  (if-not (nil? s)
    (-> s clojure.string/lower-case keyword)))

(defn parse-note [note]
  (if-let [[_ white-key accidental octave] (re-matches specs/note-regex note)]
    {:white-key  (downcase-keyword white-key)
     :accidental (downcase-keyword accidental)
     :octave     (js/parseInt octave)}))

(defn midi-number [note]
  (let [white-key-offsets  {:c 0 :d 2 :e 4 :f 5 :g 7 :a 9 :b 11}
        accidental-offsets {:bb -2 :b -1 :# 1 :## 2}
        {:keys [white-key accidental octave]} (parse-note note)]
    (+ (white-key-offsets white-key)
       (accidental-offsets accidental)
       (* 12 (inc octave)))))

(defn notename [parsed-note]
  (let [{:keys [white-key accidental octave]} parsed-note]
    (str (->> white-key name clojure.string/upper-case)
         (if accidental (name accidental))
         octave)))

(defn ^:private accidental-key [accidental]
  (case accidental
    :double-flats :bb
    :flats :b
    :sharps :#
    :double-sharps :##
    nil))

(defn ^:private accidental-syms [s]
  (mapv accidental-key s))

(defn random-note
  ([{:keys [lowest-note highest-note accidentals]
     :or   {lowest-note  "E3"
            highest-note "G#5"
            accidentals  [:sharps :flats]}}]
   (let [white-keys  [:c :d :e :f :g :a :b]
         accidentals (conj (accidental-syms accidentals) nil)
         octaves     (range (:octave (parse-note lowest-note))
                            (inc (:octave (parse-note highest-note))))
         gen-note    #(notename {:white-key  (rand-nth white-keys)
                                 :accidental (rand-nth accidentals)
                                 :octave     (rand-nth octaves)})]
     (first (filter (fn [note]
                      (let [low-midi  (midi-number lowest-note)
                            high-midi (midi-number highest-note)
                            note-midi (midi-number note)]
                        (<= low-midi note-midi high-midi)))
                    (repeatedly gen-note)))))
  ([]
   (random-note {})))

(defn transpose-note [note half-steps]
  (let [chromatic-sharps  [:c :c# :d :d# :e :f :f# :g :g# :a :a# :b]
        chromatic-flats   [:c :db :d :eb :e :f :gb :g :ab :a :bb :b]
        {:keys [white-key accidental octave]}
                          (parse-note note)
        pitch             (downcase-keyword (apply str (drop-last note)))
        index-sharps      (.indexOf chromatic-sharps pitch)
        index-flats       (.indexOf chromatic-flats pitch)
        [v i] (if (neg? index-sharps)
                [chromatic-flats  index-flats]
                [chromatic-sharps index-sharps])
        offset            (+ i half-steps)
        transposed-pitch  (nth v (mod (+ (mod offset 12) 12) 12))
        transposed-octave (+ octave (Math/floor (/ offset 12)))]
    (str (clojure.string/capitalize (name transposed-pitch))
         transposed-octave)))

(def standard-tuning ["E5" "B4" "G4" "D4" "A3" "E3"])
(def drop-d-tuning ["E5" "B4" "G4" "D4" "A3" "D3"])

(defn fretboard-notes
  ([{:keys [tuning frets]
     :or   {tuning  standard-tuning
            frets   4}}]
   (let [strings (count tuning)]
     (for [string (range 1 (inc strings))
           fret   (range 0 (inc frets))]
       {:string string
        :fret   fret
        :note   (transpose-note (nth tuning (dec string)) fret)})))
  ([]
   (fretboard-notes {})))

(defn note-at [fret-coord]
  (-> (filter (fn [m]
                (and (= (:fret m)
                        (:fret fret-coord))
                     (= (:string m)
                        (:string fret-coord))))
              (fretboard-notes))
      first
      :note))

(defn enharmonic? [& notes]
  (let [midi (midi-number (first notes))]
    (every? #(= midi (midi-number %)) notes)))

(defn fretboard-coord
  ([note {:keys [tuning frets]
          :as   opts}]
   (-> (filter #(enharmonic? note (:note %))
               (fretboard-notes opts))
       first
       (select-keys [:string :fret])))
  ([note]
   (fretboard-coord note {})))

(defn correct-guess?
  [note coord]
  (if (and note coord)
    (enharmonic? note (note-at coord))))

(defn notes-on-string
  ([string {:keys [tuning frets]
            :as   opts}]
   (filter #(= (:string %)
               string)
           (fretboard-notes opts)))
  ([string]
   (notes-on-string string {})))
