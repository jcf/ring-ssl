(ns ring.util.ssl-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer [request header]]
            [ring.util.ssl :refer :all]))

(deftest test-add-x-forwarded-proto-header
  (let [m (-> (request :get "/")
              (add-x-forwarded-proto-header))]
    (is (= (get-in m [:headers "x-forwarded-proto"]) "https"))))

(deftest test-request-url
  (testing "with https scheme"
    (is (= (request-url (request :get "https://localhost/path"))
           "https://localhost/path")))

  (testing "with x-forwarded-proto"
    (is (= (request-url (-> (request :get "/path")
                            (header "x-forwarded-proto" "https")))
           "https://localhost/path"))))
