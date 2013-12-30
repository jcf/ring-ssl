(ns ring.middleware.ssl-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer [request header]]
            [ring.middleware.ssl :refer :all]))

(def default-response {:status 200 :headers {} :body "Body"})
(def handler (wrap-ssl (constantly default-response)))

(deftest test-wrap-ssl
  (let [response (handler (request :get "/"))]
    (are [x y] (= x y)
         (:status response) 302
         (:headers response) {"Location" "https://localhost/"}
         (:body response) "")))

(deftest test-wrap-ssl-with-proxy-header
  (let [response (handler (-> (request :get "/")
                              (header "x-forwarded-proto" "https")))]
    (are [x y] (= x y)
         (:status response) 200
         (:headers response) {}
         (:body response) "Body")))

(deftest test-wrap-ssl-with-https-scheme
  (let [response (handler (request :get "https://example.com/"))]
    (are [x y] (= x y)
         (:status response) 200
         (:headers response) {}
         (:body response) "Body")))
