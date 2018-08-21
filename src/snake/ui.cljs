(ns snake.ui
  (:require [snake.vars :as v]))

(defonce canvas (.getElementById js/document "game"))

(defonce ctx (.getContext canvas "2d"))

(defn style-canvas []
  (do (aset ctx "canvas" "height" v/height)
      (aset ctx "canvas" "width" v/width)))

(defn empty-canvas []
  (.clearRect ctx 0 0 v/height v/width))

(defn set-ctx-color! [color]
  (set! (.-fillStyle ctx) color))

(defn draw-square [[x y] dim]
  (.fillRect ctx x y dim dim))

(defn draw-squares [dim & coords]
  (doseq [pair coords]
    (draw-square pair dim)))

(defn set-score-on-page [score]
  (set! (.-textContent (.getElementById js/document "score")) score))

(defn draw-snake [{coords :coords}]
  (set-ctx-color! "#000000")
  (apply draw-squares v/dim coords))

(defn draw-candy [candy]
  (set-ctx-color! "#ffce47")
  (draw-square candy v/dim))

(defn animate [snake candy]
  (do
    (empty-canvas)
    (draw-candy @candy)
    (draw-snake @snake)
    (.requestAnimationFrame js/window (fn [] (animate snake candy)))))
