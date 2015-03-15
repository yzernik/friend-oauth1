(ns friend-oauth1.test-helpers
  (:use
   compojure.core
   friend-oauth1.fixtures)
  (:require
   [clojure.string :refer [split]]
   [friend-oauth1.workflow :as oauth1]
   [cemerick.friend :as friend]
   [cemerick.url :refer [url]]
   [compojure.handler :as handler]
   [ring.util.response :refer [get-header]]
   [ring.mock.request :as ring-mock]))


(defn extract-cookie
  "Extracts cookie from headers in response and returns map of contents."
  [response]
  (let [cookie-header (get-header response "Set-Cookie")
        cookie-strs   (-> cookie-header first (split #";"))]
    (into {} (map #(split % #"=") cookie-strs))))

(defn extract-ring-session-val
  "Returns ring-session value from Set-Cookie
  header in a ring response."
  [response]
  (get (extract-cookie response) "ring-session"))

(defn make-cookie-request
  "Wraps ring-request with hash-map formatted
  properly to pass a ring-session cookie."
  [request cookie-val]
  (assoc-in request [:cookies "ring-session" :value] cookie-val))


;; via a "real" friend-authorized/authenticated app.

(declare test-app)

(defn make-session-get-request
  [path params ring-session-val]
  (-> (ring-mock/request :get path params)
      (make-cookie-request ring-session-val)
      test-app))

(defroutes test-app-routes
  (GET "/authlink" request
       (friend/authorize #{::user} "Authorized page.")))

(def test-app
  (handler/site
   (friend/authenticate
    test-app-routes
    {:allow-anon? true
     :workflows [(oauth1/workflow
                  {:client-config client-config-fixture
                   :auth-error-fn (fn [error]
                                    (ring.util.response/response error))
                   :credential-fn (fn [token]
                                    {:identity token
                                     :roles #{::user}})})]})))
