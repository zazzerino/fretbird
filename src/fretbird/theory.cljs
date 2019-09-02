(ns fretbird.theory
  (:require [clojure.spec.alpha :as spec]))

(defn ^:private downcase-keyword [s]
  (if-not (nil? s)
    (-> s clojure.string/lower-case keyword)))

(defn ^:private upcase-string [k]
  (->> k name clojure.string/upper-case))

(def ^:private note-regex #"([a-gA-G])(#{1,2}||b{1,2})?(\d)")

(spec/def ::note #(re-matches note-regex %))

(defn parse-note [note]
  (if-let [[_ white-key accidental octave] (re-matches note-regex note)]
    {:white-key (downcase-keyword white-key)
     :accidental (downcase-keyword accidental)
     :octave (js/parseInt octave)}))

(defn midi-number [note]
  (let [white-key-offsets {:c 0 :d 2 :e 4 :f 5 :g 7 :a 9 :b 11}
        accidental-offsets {:bb -2 :b -1 :# 1 :## 2}
        {:keys [white-key accidental octave]} (parse-note note)]
    (+ (white-key-offsets white-key)
       (accidental-offsets accidental)
       (* 12 (inc octave)))))

(defn notename [parsed-note]
  (let [{:keys [white-key accidental octave]} parsed-note]
    (str (upcase-string white-key)
         (if-let [accidental' accidental]
           (name accidental'))
         octave)))

(defn random-note
  ([{:keys [lowest-note highest-note]}]
   (let [white-keys [:c :d :e :f :g :a :b]
         accidentals [:bb :b nil :# :##]
         octaves (range (:octave (parse-note lowest-note))
                        (inc (:octave (parse-note highest-note))))
         gen-note #(notename {:white-key (rand-nth white-keys)
                              :accidental (rand-nth accidentals)
                              :octave (rand-nth octaves)})]
     (first (filter (fn [note]
                      (let [low-midi (midi-number lowest-note)
                            high-midi (midi-number highest-note)
                            note-midi (midi-number note)]
                        (<= low-midi note-midi high-midi)))
                    (repeatedly gen-note)))))
  ([]
   (random-note {:lowest-note "E3"
                 :highest-note "G#5"})))

(defn transpose-note [note half-steps]
  (let [chromatic-sharps [:c :c# :d :d# :e :f :f# :g :g# :a :a# :b]
        chromatic-flats [:c :db :d :eb :e :f :gb :g :ab :a :bb :b]
        {:keys [white-key accidental octave]} (parse-note note)
        pitch (downcase-keyword (apply str (drop-last note)))
        index-sharps (.indexOf chromatic-sharps pitch)
        index-flats (.indexOf chromatic-flats pitch)
        [v i] (if (neg? index-sharps)
                [chromatic-flats index-flats]
                [chromatic-sharps index-sharps])
        offset (+ i half-steps)
        transposed-pitch (nth v (mod (+ (mod offset 12) 12) 12))
        transposed-octave (+ octave (Math/floor (/ offset 12)))]
    (str (clojure.string/capitalize (name transposed-pitch)) transposed-octave)))

(defn fretboard-notes []
  (let [tuning ["E5" "B4" "G4" "D4" "A3" "E3"]]
    (for [string (range 1 7)
          fret (range 0 5)]
      {:string string
       :fret fret
       :note (transpose-note (nth tuning (dec string)) fret)})))

(spec/def ::fret integer?)
(spec/def ::string integer?)
(spec/def ::fretboard-coord (spec/keys :req-un [::fret ::string]))

(defn note-at [fretboard-coord]
  (-> (filter (fn [m]
                (and (= (:fret m)
                        (:fret fretboard-coord))
                     (= (:string m)
                        (:string fretboard-coord))))
              (fretboard-notes))
      first
      :note))

(defn enharmonic? [& notes]
  (let [midi (midi-number (first notes))]
    (every? #(= midi (midi-number %)) notes)))

(defn fretboard-coord [note]
  (-> (filter #(enharmonic? note (:note %))
              (fretboard-notes))
      first
      (select-keys [:string :fret])))
