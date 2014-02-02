(ns ring.middleware.ssl
  (:require [ring.util.response :as response]
            [ring.util.ssl :as util]
            [ring.util.request :as request]))

(defn- idempotent-request?
  [{:keys [request-method]}]
  (some #(= request-method %) [:get :head]))

(defn- https-url
  "Extracts the URL from req, and converts it to an https URL."
  [req]
  (request/request-url (assoc req :scheme :https)))

(defn- add-strict-transport-security
  [req]
  (response/header req
                   "strict-transport-security"
                   "max-age=31536000; includeSubdomains"))

(defn- https-redirect
  [req]
  {:status (if (idempotent-request? req) 301 307)
   :headers {"Content-Type" "text/html"
             "Location" (https-url req)}})

(defn- build-secure-handler [original-handler req]
  (if (util/https? req) original-handler https-redirect))

(defn wrap-ssl
  "Redirect all requests to https. Looks at the scheme of the request URI, or
  the X-Forwarded-Proto header used by Heroku among others.

  Adds Strict-Transport-Security header to ensure future requests use https."
  [handler]
  (fn [req]
    (let [secure-handler (build-secure-handler handler req)]
      (-> req secure-handler add-strict-transport-security))))
