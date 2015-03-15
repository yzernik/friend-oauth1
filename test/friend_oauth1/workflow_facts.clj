(ns friend-oauth1.workflow-facts
  (:use
   midje.sweet
   friend-oauth1.test-helpers
   friend-oauth1.fixtures)
  (:require
   [friend-oauth1.workflow :as oauth1]
   [cemerick.friend :as friend]
   [cemerick.url :as url]
   [ring.util.response :refer [get-header]]
   [ring.mock.request :as ring-mock]))


(fact
 "A login request redirects to the authorization uri"
 
 (let [auth-redirect  (test-app (ring-mock/request :get "/login"))
       location       (get-header auth-redirect "Location")
       redirect-query (-> location url/url :query clojure.walk/keywordize-keys)]


   (println redirect-query)
   
   (:status auth-redirect)                               => 302
   (re-find #"/redirect" (:redirect_uri redirect-query)) => "/redirect"
   (:client_id redirect-query)                           => "my-client-id"
   (nil? (:state redirect-query))                        => false))
