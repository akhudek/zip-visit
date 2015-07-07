(defproject zip-visit "1.1.0"
  :description "Clojure zipper-based visitor library."
  :url "https://github.com/akhudek/zip-visit"
  :license {:name "MIT"
            :url  "http://opensource.org/licenses/MIT"}
  :dependencies []
  :profiles {:provided
             {:dependencies [[org.clojure/clojure "1.7.0"]
                             [org.clojure/clojurescript "0.0-3308"]]}
             :dev
             {:dependencies [[org.clojure/tools.nrepl "0.2.10"]]}}
  :scm {:name "git"
        :url  "https://github.com/akhudek/zip-visit"}
  :deploy-repositories
  [["clojars" {:signing {:gpg-key "D8B883CA"}}]])
