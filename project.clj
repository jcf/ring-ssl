(defproject listora/ring-ssl "0.1.1"
  :description "Force SSL in your Ring application"
  :url "https://github.com/listora/ring-ssl"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring/ring-core "1.2.1"]]
  :profiles {:shared {:dependencies [[ring-mock "0.1.5"]]}
             :dev [:shared]
             :test [:shared]})
