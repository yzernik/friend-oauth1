(ns friend-oauth1.workflow
  (:require
   [cemerick.friend :as friend]
   [schema.core :as s]
   [ring.util.request :as request]
   [oauth.client :as oauth]))


(s/defschema ClientConfig {:consumer oauth/Consumer
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
  "Redirects user to OAuth1 provider."
  [consumer request]
  (let [anti-forgery-token    (util/generate-anti-forgery-token)
        session-with-af-token (assoc (:session request) (keyword anti-forgery-token) "state")]
    (-> uri-config
        (util/format-authn-uri anti-forgery-token)
        ring.util.response/redirect
        (assoc :session session-with-af-token))))


(s/defn ^:always-validate workflow
  "Workflow for OAuth1"
  [config :- {(s/required-key :client-config) ClientConfig
              s/Any s/Any}];; The rest of config.
  (fn [request]
    (when (is-oauth1-callback? config request)
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
          (redirect-to-provider! config request))))))
