(defproject tic-tac-toe "0.1.0-SNAPSHOT"
  :description "Simple tic-tac-toe using both clj and cljs/"
  :url "https://github.com/thomas-shares/tic-tac-toe"
  :license {:name "Apache V2 License"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.5"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [org.clojure/clojurescript "1.7.189"]
                 [domina "1.0.3"]
                 [hiccups "0.3.0"]]
  :main ^:skip-aot tic-tac-toe.core

  :plugins [[lein-ring "0.8.12"]
            [lein-cljsbuild "1.1.2"]]

  :ring {:handler tic-tac-toe.core/app}

  :profiles {:uberjar {:aot :all}}

  :target-path "target/%s"

  :cljsbuild {
    :builds [{
        ; The path to the top-level ClojureScript source directory:
        :source-paths ["src"]
        ; The standard ClojureScript compiler options:
        ; (See the ClojureScript compiler documentation for details.)
        :compiler {
          ;:preamble ["reagent/react.js"]
          :output-to "resources/public/js/cljs.js"
          :optimizations :whitespace
          :pretty-print true}}]})
