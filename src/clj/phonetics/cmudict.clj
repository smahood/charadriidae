(ns phonetics.cmudict
  (:require [clojure.string :as str]))


(def cmudict-07b-url
  "Download location for the most reason cmudict
     Main CMU Dictionary page is located at http://www.speech.cs.cmu.edu/cgi-bin/cmudict"
  "http://svn.code.sf.net/p/cmusphinx/code/trunk/cmudict/cmudict-0.7b")

(def cmudict-07b-local "resources/cmudict/cmudict-0.7b")

(def syllabified-cmudict-06d-url
  "Download location for a version of cmudict augmented with syllable boundaries
   Main page is located at http://webdocs.cs.ualberta.ca/~kondrak/cmudict.html

   Reference:
   Susan Bartlett, Grzegorz Kondrak and Colin Cherry.
   On the Syllabification of Phonemes.
   AACL-HLT 2009. "

  "http://webdocs.cs.ualberta.ca/~kondrak/cmudict/cmudict.rep")

(def syllabified-cmudict-06d-local "resources/cmudict/syllabified-cmudict-0.7b")


(defn copy-dictionaries-to-local-resources []
  (->> (slurp cmudict-07b-url)
       (spit cmudict-07b-local))
  (->> (slurp syllabified-cmudict-06d-url)
       (spit syllabified-cmudict-06d-local)))




(defn clean-cmudict-dictionary [path]
  (->> (slurp path)
       str/split-lines
       (remove #(str/starts-with? % ";;;"))
       (filter #(re-matches #"[A-Z]+.*" %))
       (map #(str/split % #" "))
       (map #(remove empty? %))
       (map #(vector (str/replace (first %) #"\([0-9]\)" "")
                     (into [] (rest %))))
       (remove #(re-matches #".*[0-9]+.*" (first %)))
       (remove #(str/includes? % "."))
       (remove #(str/includes? % "'"))

       #_(map #(hash-map :word (str/lower-case (first %))
                         :phonemes (mapv phoneme (second %))))
       #_(map #(assoc % :phoneme-str (phoneme-str %))))
  )

(comment
  (copy-dictionaries-to-local-resources)
  (def cmudict-07b (clean-cmudict-dictionary cmudict-07b-local))

  )