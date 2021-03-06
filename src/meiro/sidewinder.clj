(ns meiro.sidewinder
  "Sidewinder algorithm is like binary-tree, but randomly chooses a position in
  a horizontal corridor to move south from when moving south. This algorithm's
  bias creates vertical paths and will always have a single corridor along
  the southern edge."
  (:require [meiro.core :as m]
            [clojure.data.generators :as gen]))

(def ^:private weights
  "Constants allow for different weights for each direction.
  Higher south weight has longer vertical corridors.
  Higher east weight has longer horizontal corridors."
  {:south 4 :east 5})

(defn possible-directions
  "Determine which directions are valid from the provided pos."
  [maze pos]
  (vals
    (filter
      #(m/in? maze (first %))
      {(m/south pos) :south (m/east pos) :east})))

(defn- link-neighbor
  "Link to a random neighbor to south or east.
  When linking to south, the link will be created from any position in the
  current east-west corridor, not necessarily from `pos`."
  [maze pos]
  (let [directions (possible-directions maze pos)]
    (if (seq directions)
      (case (gen/weighted (select-keys weights directions))
        :east (m/link maze pos (m/east pos))
        :south (let [from (rand-nth (m/path-west maze pos))]
                 (m/link maze from (m/south from))))
      maze)))

(defn create
  "Create a random maze using the sidewinder algorithm."
  [grid]
  (reduce
    link-neighbor
    grid
    (for [row (range (count grid)) col (range (count (first grid)))]
      [row col])))
