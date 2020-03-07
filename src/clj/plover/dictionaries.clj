(ns plover.dictionaries
  (:require [clojure.string :as str]
            [clojure.data.json :as json]))

(def main-json-url
  "Download location for current development version of the plover main dictionary"
  "https://raw.githubusercontent.com/openstenoproject/plover/master/plover/assets/main.json")

(def main-json-local "resources/plover/main.json")

(def main-json-url-v311
  "Download location for v3.1.1 of the plover main dictionary"
  "https://raw.githubusercontent.com/openstenoproject/plover/v3.1.1/plover/assets/main.json")

(def main-json-local-v311 "resources/plover/main-v311.json")


(defn copy-to-local-resources
  "Copy online dictionaries to your local resources - should only be required once on each machine."
  []
  (->> (slurp main-json-url)
       (spit main-json-local))
  (->> (slurp main-json-url-v311)
       (spit main-json-local-v311)))

(defn parse-strokes [s]
  (str/split s #"/"))


(defn parse-plover-dictionary [path]
  (->> (slurp path)
       (json/read-str)
       (map #(hash-map :input (first %)
                       :output (last %)
                       :strokes (parse-strokes (first %))))))

(parse-plover-dictionary main-json-local)

(comment
  (copy-to-local-resources))