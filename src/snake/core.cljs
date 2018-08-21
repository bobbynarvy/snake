(ns snake.core
  (:require [clojure.browser.event :as event]
            [snake.game :as game]))
                                        
(enable-console-print!)

(game/run)

;; TO DO
;; - self-collision *
;; - candy should not be made on snake path *
;; - score *
;; - start/stop/pause *
;; - refactor
;; - comments
