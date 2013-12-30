(ns ring.middleware.ssl)

(defn- redirect
  "Returns a Ring response for an HTTP 302 redirect."
  [url]
  {:status  302
   :headers {"Location" url}
   :body    ""})

(defn- request-url
  "Return the full URL of the request."
  [request]
  (str (-> request :scheme name)
       "://"
       (get-in request [:headers "host"])
       (:uri request)
       (if-let [query (:query-string request)]
         (str "?" query))))

(defn wrap-ssl
  "Redirect all requests to https. Looks at the scheme of the request URI, or
  the X-Forwarded-Proto header used by Heroku among others."
  [handler]
  (fn [req]
    (let [forwarded (get-in req [:headers "x-forwarded-proto"])
          https-url (request-url (assoc req :scheme :https))]
      (if (or (= forwarded "https")
              (= :https (req :scheme)))
        (handler req)
        (redirect https-url)))))
