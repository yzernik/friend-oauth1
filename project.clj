(defproject friend-oauth1 "0.1.0-SNAPSHOT"
  :description "OAuth1 workflow for Friend (https://github.com/cemerick/friend"
  :url "https://github.com/yzernik/friend-oauth1"
  :license {:name "MIT" :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.cemerick/friend "0.2.1" :exclusions [org.apache.httpcomponents/httpclient]]
                 [prismatic/schema "0.3.6"]
                 [clj-oauth "1.5.2"]]

  :profiles {:dev
             {:dependencies [[ring-mock "0.1.5"]
                             [org.clojure/tools.nrepl "0.2.5"]
                             [midje "1.6.3"]
                             [com.cemerick/url "0.1.1"]
                             [compojure "1.3.1"]]}})
