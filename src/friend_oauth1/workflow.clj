(ns friend-oauth1.workflow
  (:require
   [oauth.client :as oauth]
   [friend-oauth2.util :as util]
   [cemerick.friend :as friend]
   [clj-http.client :as client]
   [schema.core :as s]
   [ring.util.request :as request]))


(s/defschema ClientConfig {:client-id     String
                           :client-secret String
                           :callback {:domain String
                                      :path   String}})





(s/defn ^:always-validate workflow
  "Workflow for OAuth1"
  [config :- {(s/required-key :client-config) ClientConfig
              s/Any s/Any}];; The rest of config.
  (fn [request]
    (when (is-oauth2-callback? config request)
      ;; Extracts code from request if we are getting here via OAuth2 callback.
      ;; http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.1.2
      (let [{:keys [state code error]} (:params request)
            session-state        (util/extract-anti-forgery-token request)]
        (if (and (not (nil? code))
                 (= state session-state))
          (when-let [access-token (request-token config code)]
            (when-let [auth-map ((:credential-fn config default-credential-fn)
                                 {:access-token access-token})]
              (vary-meta auth-map merge {::friend/workflow :oauth2
                                         ::friend/redirect-on-auth? true
                                         :type ::friend/auth})))
          
          (let [auth-error-fn (:auth-error-fn config)]
            (if (and error auth-error-fn)
              (auth-error-fn error)
              (redirect-to-provider! config request))))))))
