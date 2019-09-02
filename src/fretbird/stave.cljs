(ns fretbird.stave
  (:require [reagent.core :as reagent]
            ["vexflow" :as Vex]
            [re-frame.core :as re-frame]
            [fretbird.subs :as subs]
            [fretbird.theory :as theory]))

(def ^:private vexflow (.-Flow Vex))

(defn ^:private remove-children [elem]
  (while (.-firstChild elem)
    (.removeChild elem (.-firstChild elem))))

(defn ^:private format-note [note]
  (clojure.string/replace note #"(\d)" "/$1"))

(defn ^:private make-vexflow-objects [{:keys [id width height]}]
  (let [renderer (doto (new (.-Renderer vexflow)
                            (.getElementById js/document id)
                            (-> vexflow .-Renderer .-Backends .-SVG))
                   (.resize width height))
        context (.getContext renderer)
        stave (doto (new (.-Stave vexflow) 0 0 (dec width))
                (.addClef "treble")
                (.setContext context)
                .draw)]
    {:renderer renderer :context context :stave stave}))

(defn ^:private add-note [{:keys [context stave note]}]
  (let [stave-note (doto (new (.-StaveNote vexflow)
                              (clj->js {:keys [(format-note note)]
                                        :duration "w"}))
                     (.setExtraLeftPx (/ (.getWidth stave) 4)))
        accidental (:accidental (theory/parse-note note))]
    (if accidental
      (.addAccidental stave-note 0 (new (.-Accidental vexflow) (name accidental))))
    (.FormatAndDraw (.-Formatter vexflow) context stave #js [stave-note])))

(defn stave []
  (let [id "stave"
        width 210
        height 130
        note (re-frame/subscribe [::subs/note-to-guess])
        vexflow-objects (atom nil)
        draw (fn []
               (remove-children (.getElementById js/document id))
               (reset! vexflow-objects (make-vexflow-objects {:id "stave" :width width :height height}))
               (add-note {:context (:context @vexflow-objects)
                          :stave (:stave @vexflow-objects)
                          :note @note}))]
    (reagent/create-class
     {:display-name "stave"
      :reagent-render (fn []
                        [:div.stave
                         [:div {:id id}]])
      :component-did-mount draw
      :component-did-update draw})))
