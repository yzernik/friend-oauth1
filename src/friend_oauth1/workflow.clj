(ns friend-oauth1.workflow
  (:require
   [cemerick.friend :as friend]
   [schema.core :as s]
   [ring.util.request :as request]
   [oauth.client :as oauth])
  (:import
   [oauth.client Consumer]))


(s/defschema ClientConfig {:consumer Consumer
                           :callback {:domain String
                                      :path String}})


(defn- contains-valid-oauth1?
  [request]
  true)

(defn- is-oauth1-callback?
  [callback request]
  (or (= (request/path-info request)
         (get-in callback [:path]))
      (= (request/path-info request)
         (-> request ::friend/auth-config :login-uri))))


(defn- redirect-to-provider!
  "Redirects user to OAuth1 provider"
  [{:keys [client-config]} request]
  (let [consumer (:consumer client-config)
        tok "abcd"
        approval-uri (oauth/user-approval-uri consumer tok)
        session-with-token {}]
    (-> approval-uri
        ring.util.response/redirect)))
;; (assoc :session session-with-af-token))))


(s/defn ^:always-validate workflow
  "Workflow for OAuth1"
  [config :- {(s/required-key :client-config) ClientConfig
              s/Any s/Any}];; The rest of config.
  (fn [request]
    (when (is-oauth1-callback? config request)
      ;; Extracts code from request if we are getting here via OAuth1 callback.
      ;; http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.1.2
      (redirect-to-provider! config request))))
