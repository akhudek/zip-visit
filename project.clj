(defproject zip-visit "1.0.1"
  :description "Clojure zipper-based visitor library."
  :url "https://github.com/akhudek/zip-visit"
  :license {:name "MIT"
            :url  "http://opensource.org/licenses/MIT"}
  :dependencies []
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.6.0"]]}}
  :scm {:name "git"
        :url  "https://github.com/akhudek/zip-visit"}
  :deploy-repositories
  [["clojars" {:signing {:gpg-key "D8B883CA"}}]])
