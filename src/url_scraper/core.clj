(ns url-scraper.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojurewerkz.crawlista.extraction.content
             :refer [extract-text extract-title]]
            [clojurewerkz.crawlista.extraction.links
             :refer [extract-local-urls extract-local-followable-urls]]))

(defn url?
  "True if url is a valid URL."
  ([url]
     (try
       (new java.net.URL url)
       true
       (catch Exception e
         false))))

(def url->source
  ^{:tag String
    :doc "Takes a url and returns the source code from that url"
    :arglists '([url])}
  (comp slurp io/reader))

(def url->title
  ^{:tag String
    :doc "Takes a url and returns the title of the page."
    :arglists '([url])}
  (comp extract-title url->source))

(def url->text
  ^{:tag String
    :doc "Takes a url and returns the body of the page."
    :arglists '([url])}
  (comp extract-text url->source))

(defn url->words
  "Takes a url and returns a vector of the words on that page."
  ([url] (-> url url->text (string/split #"\s+"))))

(def url->word-set
  ^{:tag clojure.lang.PersistentHashSet
    :doc "Takes a url and returns a set of the unique words on that page."
    :arglists '([url])}
  (comp set url->words))

(defn url->urls
  "Returns a seq of all local URLs at the given url."
  ([url] (let [body (url->source url)]
           (extract-local-urls body url))))

(defn url->followable-urls
  "Returns a seq of all followable local URLs at the given url."
  ([url] (let [body (url->source url)]
           (extract-local-followable-urls body url))))
