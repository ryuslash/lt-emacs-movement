(ns lt.emacs-movement
  (:require [lt.objs.editor :as editor]
            [lt.objs.editor.pool :as pool]
            [lt.objs.command :as cmd]
            [lt.object :as object])
  (:require-macros [lt.macros :refer [behavior]]))

(def target-column false)

(behavior ::set-target-column
          :triggers #{:emacs-vertical-movement}
          :reaction (fn [this column]
                      (if (not target-column)
                        (set! target-column column))))

(behavior ::unset-target-column
          :triggers #{:emacs-horizontal-movement :change}
          :reaction (fn [this]
                      (if target-column
                        (set! target-column false))))

(cmd/command
 {:command :emacs-movement.forward-line
  :desc "Emacs Movement: Go one line forward"
  :exec (fn []
          (when-let [ed (pool/last-active)]
            (let [pos (editor/->cursor ed)]
              (object/raise ed :emacs-vertical-movement (:ch pos))
              (editor/move-cursor ed {:line (+ 1 (:line pos))
                                      :ch target-column}))))})

(cmd/command
 {:command :emacs-movement.backward-line
  :desc "Emacs Movement: Go one line back"
  :exec (fn []
          (when-let [ed (pool/last-active)]
            (let [pos (editor/->cursor ed)]
              (object/raise ed :emacs-vertical-movement (:ch pos))
              (editor/move-cursor ed {:line (- (:line pos) 1)
                                      :ch target-column}))))})

(cmd/command
 {:command :emacs-movement.forward-char
  :desc "Emacs Movement: Go one character forward"
  :exec (fn []
          (when-let [ed (pool/last-active)]
            (let [pos (editor/->cursor ed)]
              (object/raise ed :emacs-horizontal-movement)
              (editor/move-cursor ed {:line (:line pos)
                                      :ch (+ (:ch pos) 1)}))))})

(cmd/command
 {:command :emacs-movement.backward-char
  :desc "Emacs Movement: Go one character forward"
  :exec (fn []
          (when-let [ed (pool/last-active)]
            (let [pos (editor/->cursor ed)]
              (object/raise ed :emacs-horizontal-movement)
              (editor/move-cursor ed {:line (:line pos)
                                      :ch (- (:ch pos) 1)}))))})
