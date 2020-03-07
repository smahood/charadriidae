(ns frequencies.norvig
  (:require [clojure.string :as str]))

(def norvig-frequencies-url
  "Download location for the Norvig word frequency data
  Main page is located at http://www.norvig.com/mayzner.html"
  "http://norvig.com/google-books-common-words.txt")

(def norvig-frequencies-local "resources/norvig/frequencies")


(defn copy-to-local-resources
  "Copy online source to your local resources - should only be required once on each machine."
  []
  (->> (slurp norvig-frequencies-url)
       (spit norvig-frequencies-local)))


(defn parse-norvig-frequencies [path]
  (let [frequency-lines (->> (slurp path)
                             str/split-lines
                             (map #(str/split % #"\t"))
                             (mapv #(hash-map :word (first %)
                                              :frequency-count (Long/parseLong (last %)))))
        relative-frequency-divisor (->> frequency-lines
                                        (mapv :frequency-count)
                                        (apply min))]
    (->> frequency-lines
         (mapv #(assoc % :relative-frequency (quot (:frequency-count %) relative-frequency-divisor))))))


(comment
  (copy-to-local-resources)
  (parse-norvig-frequencies norvig-frequencies-local)
  )