(ns snake.core
  (:require [clojure.browser.event :as event]
            [snake.game :as game]))
                                        
(enable-console-print!)

(game/run)
