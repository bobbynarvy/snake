(ns snake.elem
  (:require [snake.vars :as v]))

;; ==============
;; Basic elements
;; ==============
;;

(def snake
  "A snake is represented as a map with
  the following properties:
  :direction - a vector that describes the vertical and horizontal
  direction of the head in 2D
  :coords - a vector containing vectors of x-y coordinates,
  the first being the head and the rest being the tail"
  (atom {}))

(def score (atom 0))

(def candy
  "A candy is a simple vector containing the x-y
  coordinate of the candy"
  (atom []))

;; =============
;; Element logic
;; =============

;; -----------
;; Snake logic
;; -----------

(defn- orient
  "Gets the vertical and horizontal vector representation 
  of a given direction"
  [direction]
  (get {:up    [0 -1]
        :down  [0 1]
        :right [1 0]
        :left  [-1 0]}
       direction))

(defn continue-point
  "Resets the coordinate of a snake from the end to the
  beginning of the height/width or vice versa once the
  min/max value of height/width is touched"
  [point limit]
  (cond (< point 0) (- limit v/dim)
        (> (+ point v/dim) limit) 0
        :else point))

(defn next-head-coords
  "Gets the next x-y coordinates for the head of the snake"
  [[x y] direction]
  (let [[c-x c-y] (orient direction)]
    (if (zero? c-x)
      [x (-> (+ y (* c-y v/dim))
             (continue-point v/height))]
      [(-> (+ x (* c-x v/dim))
           (continue-point v/width)) y])))

(defn set-snake-next-position
  "Transforms the snake position by moving it one step forward"
  [{direction :direction [head & tail] :coords :as snake}]
  (let [new-head (next-head-coords head direction)
        new-tail (drop-last tail)]
    (->> (into [] (concat [new-head] [head] new-tail))
         (assoc snake :coords))))

(defn- can-change-direction?
  [direction new-direction]
  (let [[d1 d2] (sort [direction new-direction])]
    (cond (and (= d1 :left) (= d2 :right)) false
          (and (= d1 :down) (= d2 :up)) false
          (= d1 d2) false
          :else true)))

(defn change-direction
  "Changes the direction of the snake, making sure that it
  does not change to the opposite direction"
  [{direction :direction :as snake} new-direction]
  (if (can-change-direction? direction new-direction)
    (assoc snake :direction new-direction) 
    snake))

(defn grow
  "Adds one x-y coordinate to the end of the snake
  by getting the next position of the snake and setting
  the previously last coordinate of the tail as the new
  last coordinate of the tail"
  [{[head & tail] :coords :as snake}]
  (-> (set-snake-next-position snake)
      (get :coords)
      (conj (if (nil? tail)
              head
              (last tail)))
      (#(assoc snake :coords %))))

(defn init-snake [_]
  (-> {:direction :down
       :coords [[(/ v/width 2) (/ v/height 2)]]}
      (grow)
      (grow)))

(defn create-snake []
  (swap! snake init-snake))

(defn move-snake []
  (swap! snake set-snake-next-position))

(defn grow-snake []
  (swap! snake grow))

(defn change-snake-direction [direction]
  (swap! snake change-direction direction))

;; -----------
;; Candy logic
;; -----------

(defn create-random-candy
  "return a random coordinate in the canvas
  to draw a candy on; make sure that it doesn't
  fall within the coordinates of the snake"
  [except]
  (->> [(rand-int (dec (/ v/width v/dim)))
       (rand-int (dec (/ v/height v/dim)))]
       (map #(* v/dim %))
       ((fn [pair]
          (if (some (fn [coord] (= pair coord)) except)
            (create-random-candy except)
            pair)))))
(defn create-new-candy [] 
  (swap! candy #(create-random-candy (@snake :coords))))

;; -----------
;; Score logic
;; -----------

(defn inc-score []
  (swap! score inc))

(defn reset-score []
  (reset! score 0))

;; ---------------
;; Collision logic
;; ---------------

(defn has-collision? []
  (let [[[s-x s-y :as head] & tail] (@snake :coords)
        [c-x c-y] @candy]
    (cond (and (= s-x c-x) (= s-y c-y)) :candy
          (some #(= head %) tail) :self)))

(defn on-collision
  [type {score-callback :score-callback
         self-callback :self-callback}]
  (case type
    :candy (do (grow-snake)
               (create-new-candy)
               (inc-score)
               (score-callback @score))
    :self (self-callback)))
