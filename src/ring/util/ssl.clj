(ns ring.util.ssl
  (:require [ring.util.response :refer [header get-header]]))

(defn add-x-forwarded-proto-header
  "Add an https x-forwarded-proto header to the given request hash."
  [request]
  (header request "x-forwarded-proto" "https"))

(defn https?
  "Returns true if a request map was HTTPS."
  [request]
  (or (= (:scheme request) :https)
      (= (get-header request "x-forwarded-proto") "https")))

(defn- request-scheme
  "Returns the appropriate scheme of request."
  [request]
  (if (https? request) :https :http))

(defn request-url
  "Return the full URL of the request, with improved recognition of HTTPS
  requests."
  [request]
  (str (-> request request-scheme name)
       "://"
       (get-in request [:headers "host"])
       (:uri request)
       (if-let [query (:query-string request)]
         (str "?" query))))
