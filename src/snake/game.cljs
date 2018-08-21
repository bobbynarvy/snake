(ns snake.game
  (:require [clojure.core.async :refer [<! >! chan go go-loop timeout]]
            [snake.elem :as elem]
            [snake.ui :as ui]))

(def collide (chan))

(def lifecycle (chan 1))

(def is-paused? (atom false))

(def arrow-key-codes {37 :left
                      38 :up
                      39 :right
                      40 :down})

;; ===============
;; Event callbacks
;; ===============

(defn toggle-pause
  ([]
   (swap! is-paused? not))
  ([bool]
   (reset! is-paused? bool)))

(defn start-lifecycle [e]
  (go (>! lifecycle :start)))

(defn on-keydown [callback]
  (.addEventListener js/document
                     "keydown"
                     callback
                     false))

(defn trigger-game-event [e]
  (let [key-code (.-keyCode e)]
    (cond (contains? arrow-key-codes key-code) (elem/change-snake-direction (arrow-key-codes key-code))
          (= 32 key-code) (toggle-pause))))

;; ================
;; Main game events
;; ================

(defn init []
  (on-keydown start-lifecycle)
  (toggle-pause true)
  (elem/create-snake)
  (elem/create-new-candy)
  (elem/reset-score)
  (ui/set-score-on-page 0)
  (ui/draw-snake @elem/snake))

(defn start []
  (.removeEventListener js/document "keydown" start-lifecycle)
  (on-keydown trigger-game-event)
  (toggle-pause false))

(defn end []
  (toggle-pause true))

;; =========
;; Processes
;; =========

;; Snake movement process
(defn start-snake-process []
  (go-loop [] 
    (<! (timeout 150))
    (when (false? @is-paused?) 
      (elem/move-snake))
    (let [collision (elem/has-collision?)]
      (when collision (>! collide collision)))
    (recur)))

;; Collision detection process
(defn start-collision-detection-process []
  (go-loop []
    (elem/on-collision (<! collide)
                       {:score-callback ui/set-score-on-page
                        :self-callback #(go (>! lifecycle :end))})
    (recur)))

;; Main game lifecycle
(defn start-game-lifecyle []
  (go-loop []
    (init)
    (<! lifecycle)
    (start)
    (<! lifecycle)
    (end)
    (recur)))


;; Run the game
(defn run []
  (start-snake-process)
  (start-collision-detection-process)
  (start-game-lifecyle)

  ;; Some preliminary canvas styling
  (ui/style-canvas)

  ;; Start Canvas animation
  (ui/animate elem/snake elem/candy))
