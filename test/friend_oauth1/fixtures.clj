(ns friend-oauth1.fixtures
  (:require
   [ring.util.response :refer [response content-type]]
   [oauth.client :as oauth]))


(def client-config-fixture
  (let [consumer-token "my-consumer-token"
        consumer-token-secret "my-consumer-token-secret"
        request-uri "https://api.twitter.com/oauth/request_token"
        access-uri "https://api.twitter.com/oauth/access_token"
        authorize-uri "https://api.twitter.com/oauth/authorize"]
    {:consumer (oauth/make-consumer consumer-token
                                    consumer-token-secret
                                    request-uri
                                    access-uri
                                    authorize-uri
                                    :hmac-sha1)
     :callback {:domain "http://127.0.0.1" :path "/redirect"}}))
