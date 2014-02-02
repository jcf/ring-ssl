(ns ring.middleware.ssl-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer [request header]]
            [ring.util.response :refer [get-header]]
            [ring.util.ssl :as util]
            [ring.middleware.ssl :refer :all]))

(def ^:private default-response
  {:status 200 :headers {} :body "Body"})

(def ^:private handler
  (wrap-ssl (constantly default-response)))

(deftest test-wrap-ssl
  (testing "redirect of non-HTTPS GET request"
    (let [response (handler (request :get "/"))]
      (is (= (:status response) 301))
      (is (= (get-header response "location") "https://localhost/"))))

  (testing "redirect of non-HTTPS POST request"
    (let [response (handler (request :post "/"))]
      (is (= (:status response) 307))
      (is (= (get-header response "location") "https://localhost/"))))

  (testing "recognition of an HTTPS scheme"
    (let [response (handler (request :get "https://example.com/"))]
      (is (= (:status response) 200))
      (is (nil? (get-header response "location")))))

  (testing "recognition of x-forwarded-proto"
    (let [response (handler (-> (request :get "http://example.com")
                                (util/add-x-forwarded-proto-header)))]
      (is (= (:status response) 200))
      (is (nil? (get-header response "location")))))

  (testing "adding Strict-Transport-Security header to redirects"
    (let [response (handler (request :get "/"))]
      (is (= (get-header response "strict-transport-security")
             "max-age=31536000; includeSubdomains"))))

  (testing "adding Strict-Transport-Security header to non-redirects"
    (let [response (handler (-> (request :get "/")
                                (util/add-x-forwarded-proto-header)))]
      (is (= (get-header response "strict-transport-security")
             "max-age=31536000; includeSubdomains")))))
