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


(defn copy-to-local-resources
  "Copy online dictionaries to your local resources - should only be required once on each machine."
  []
  (->> (slurp cmudict-07b-url)
       (spit cmudict-07b-local))
  (->> (slurp syllabified-cmudict-06d-url)
       (spit syllabified-cmudict-06d-local)))


(defn parse-stressed-phonemes [s]
  (into [] (remove empty? (str/split s #" "))))


(defn parse-phonemes [s]
  (->> (str/split s #" ")
       (remove empty?)
       (mapv #(str/replace % #"[0-9]" ""))))


(defn parse-stressed-syllables [s]
  (->> (str/split s #"-")
       (mapv parse-stressed-phonemes)))


(defn parse-syllables [s]
  (->> (str/split s #"-")
       (mapv parse-phonemes)))


(defn parse-cmudict-line [s]
  (let [parts (str/split s #" " 2)
        word (str/replace (first parts) #"\([0-9]\)" "")
        stressed-phonemes (parse-stressed-phonemes (last parts))
        phonemes (mapv #(str/replace % #"[0-9]" "") stressed-phonemes)]
    {:cmudict-line      s
     :word              word
     :stressed-phonemes stressed-phonemes
     :phonemes          phonemes}))


(defn parse-syllabified-cmudict-line [s]
  (let [parts (str/split s #" " 2)
        word (str/replace (first parts) #"\([0-9]\)" "")
        stressed-syllables (parse-stressed-syllables (last parts))
        syllables (parse-syllables (last parts))]
    {:cmudict-line       s
     :word               word
     :stressed-syllables stressed-syllables
     :syllables          syllables}))


(defn parse-syllabified-dictionary
  "Parse syllabified cmudict files into something usable"
  [path]
  (->> (slurp path)
       str/split-lines
       (remove #(str/starts-with? % ";;;"))
       (remove #(str/starts-with? % "##"))
       (filter #(re-matches #"[A-Z]+.*" %))
       (map parse-syllabified-cmudict-line)))


(defn parse-cmudict-dictionary
  "Parse cmudict files into something usable"
  [path]
  (->> (slurp path)
       str/split-lines
       (remove #(str/starts-with? % ";;;"))
       (remove #(str/starts-with? % "##"))
       (filter #(re-matches #"[A-Z]+.*" %))
       (map parse-cmudict-line)))


(comment
  (copy-to-local-resources)
  (def cmudict-07b (parse-cmudict-dictionary cmudict-07b-local))
  (def syllabified-cmudict-06d (parse-syllabified-dictionary syllabified-cmudict-06d-local))
  )