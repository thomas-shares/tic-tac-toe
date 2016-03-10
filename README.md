# tic-tac-toe

This is a sample demonstrating a simple minimax AI for playing Tic Tac Toe
(AKA Noughts And Crosses). It uses Reader Conditionals from Clojure 1.7 to expose
both a Clojure implementation (that lives on the server) and a ClojureScript
implementation (that runs on the client).

The common code is in game.cljc and is used both by core.clj and core.cljs.

## Usage

    ```lein cljsbuild once```

    ```lein ring server```

Now point your web browser to <http://localhost:3000> for the Clojure version
and <http://localhost:3000/cljs> for the ClojureScript version.

## License

Copyright © 2014, 2015 IBM

Distributed under the Apache v2 license.
