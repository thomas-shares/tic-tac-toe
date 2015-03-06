(ns tic-tac-toe.core
  (:gen-class)
  (:use  [ring.adapter.jetty :only [run-jetty]])
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [hiccup.core :as hiccup]
            [hiccup.form :as form]
            [hiccup.element :as element]
            [ring.util.response :as resp]
            [ring.middleware.session :as session ]
            [ring.util.response :as response]))

(def picture-map {:e "/empty.png" :x "/cross.png" :o "/nought.png"})

(def blank-board
  [:e :e :e
   :e :e :e
   :e :e :e])

(def poses (range 9))

(defn available-squares [board]
  (filter #(= :e (board %)) poses))

(defn zip [rest]
  (apply map vector rest))

(defn place-piece [board piece pos]
  (assoc board pos piece))

(defn winning-partitions [board]
  (let [rows (partition 3 board)
        cols (zip rows)
        diagonals [(apply concat (partition 1 4 board))
                   (->> board
                        (drop 2)
                        (partition 1 2)
                        (take 3)
                        (apply concat))]]
    (concat rows cols diagonals)))

(defn player-has-won [player partitions]
  (some (fn [partition] (every? #(= player %) partition)) partitions))

(defn get-game-state
  "Return one of :x, :o, :ongoing, :draw"
  ([board]
     (let [partitions (winning-partitions board)]
       (cond
        (player-has-won :x partitions) :x
        (player-has-won :o partitions) :o
        (empty? (available-squares board)) :draw
        :else :ongoing))))

;; who is the opposite player?
(defn other-player [who]
  (if (= who :x) :o :x))

(defn score-board [board who]
  (condp = (get-game-state board)
    who 100
    (other-player who) -100
    :draw 0
    :ongoing 1))

(defn score-move [board who place]
  (let [new-board (place-piece board who place)
        score (score-board new-board who)]
  [place score]))

(defn choose-move [board who]
  [2 100])

;;
;; Web stuff below this point!
;;

(defn get-board [board]
  (let [rows (partition 3 board)
        game-state (get-game-state board)]
    (hiccup/html [:center
                  [:table {:border "1px solid black"}
                   (for [[row-number row] (map-indexed vector rows)]
                     [:tr
                      (for [[column-number cell] (map-indexed vector row)]
                       [:td
                        (if (and (= :e cell) (= :ongoing game-state) )
                           [:a {:href (str "/place-piece/" (+  column-number (* row-number 3)) "/x")}
                             (element/image (cell picture-map))]
                          (element/image (cell picture-map))
                          )

                        ])])]
                 (form/form-to [:post "/post"] (form/submit-button "new game" ))
                  [:h1 "game state: " (name game-state)]])))

(defn get-response [board session]
  (response/content-type
            (assoc (response/response (get-board board))
                                      :session (assoc session :board board))
            "text/html"))


(defroutes app-routes
  (GET "/" {session :session} ;(get-board-atom)
       (let [board (if (contains? session :board)
                      (:board session)
                      blank-board)]
         (response/content-type
          (assoc (response/response (get-board board))
            :session (assoc session :board board))
          "text/html" )))
  (GET "/place-piece/:pos/:piece"  {{pos :pos piece :piece} :params
                                     session :session}

         (let [players-turn  (place-piece (:board session) (keyword piece) (Integer/parseInt pos))]
           (if (or (= :x (get-game-state players-turn)) (= :draw (get-game-state players-turn)))
             ( get-response players-turn session)
             (let [ computer-turn (place-piece players-turn :o (rand-nth (available-squares players-turn)))]
               (get-response computer-turn session)))))
  (POST "/post"  {session :session}
          (assoc (response/redirect-after-post "/")
            :session (assoc session :board blank-board)))
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> app-routes
      (handler/site)
      (session/wrap-session)))

(defn make-server
  ([]
     (make-server 8000))
  ([port]
     (let [port port]
       (run-jetty (var app) {:port port :join? false}))))

(defn -main
  ([]
     (make-server 8000))
  ([port]
     (make-server (Integer/parseInt port))))
