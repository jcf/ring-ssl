# ring-ssl

[![Build
Status](https://travis-ci.org/jcf/ring-ssl.png?branch=master)](https://travis-ci.org/jcf/ring-ssl)

A Clojure library designed to redirect all requests to your Ring app to
an https URL, inspired by [Rack::SSL][].

This library is useful when you don't have a webserver like Nginx
in-front of your application, for example, when deploying to Heroku.

## Installation

Assuming you're using Leiningen, add the following dependency to your
`project.clj` file.

    [ring-ssl "0.0.1"]

An example project.clj might look like:

``` clojure
(defproject my-amazing-ring-app "1.2.3"
  :description "Doing secret things perhaps on Heroku"
  :url "https://fancy-ring-app.herokuapp.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring-ssl "0.0.1"]])
```

## Usage

Add the middleware to your stack.

``` clojure
(ns my-app.handler
  (:require [[compojure.core :refer [defroutes]]
             [compojure.route :as route]
             [environ.core :refer [env]]
             [ring-ssl :refer [wrap-ssl]]))

;; Force SSL when FORCE_SSL is present. This makes it easy to disable in
;; development, and enable in production.
(def ^:private force-ssl? (contains? env :force-ssl))

;; Some routes courtesy of Compojure.
(defroutes routes
 (GET "/" [] "<h1>Hello World</h1>")
 (route/not-found "<h1>Page not found</h1>"))

;; All app stack, which will conditionally force SSL.
(def app
  (-> (routes (GET "/" "Home"))
      (cond-> force-ssl? (wrap-ssl))))
```

## TODO

- Mark cookies as secure when using HTTPS.

## License

Copyright © 2013-2014 Listora

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[Rack::SSL]: https://github.com/josh/rack-ssl
