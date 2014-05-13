(defproject fast-zip-visit "1.0.1"
  :description "Clojure zipper-based visitor library (fast-zip version)."
  :url "https://github.com/akhudek/zip-visit"
  :license {:name "MIT"
            :url  "http://opensource.org/licenses/MIT"}
  :dependencies [[fast-zip "0.4.0"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.6.0"]]}}
  :scm {:name "git"
        :url  "https://github.com/akhudek/zip-visit"}
  :deploy-repositories
  [["clojars" {:signing {:gpg-key "D8B883CA"}}]])
