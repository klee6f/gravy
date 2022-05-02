(ns response
  (:require [game]
            [games]
            [camel-snake-kebab.core :as csk]
            [cheshire.core :as json]
            [java-time :as t]
            [clojure.string :as string]
            [clojure.spec.alpha :as s]))

(s/def ::status-code #{1 100 101 102 103 104 105})
(s/def ::error string?)
(s/def ::number-of-total-results nat-int?)
(s/def ::number-of-page-results nat-int?)
(s/def ::limit int?)
(s/def ::offset int?)
(s/def ::results any?)

(s/def ::game (s/keys :req-un [::status-code
                               ::error
                               ::number-of-total-results
                               ::number-of-page-results
                               ::limit
                               ::offset
                               :game/results]))

(s/def ::games (s/keys :req-un [::status-code
                                ::error
                                ::number-of-total-results
                                ::number-of-page-results
                                ::limit
                                ::offset
                                :games/results]))
(defn parse-response [response]
  (-> response
      :body
      (json/parse-string csk/->kebab-case-keyword)))

(defn local-date-time [date-time-string]
  (t/local-date-time (string/replace date-time-string #" " "T")))

(defn parse-dates [result]
  (cond-> result
    (:date-added result) (update :date-added local-date-time)
    (:date-last-updated result) (update :date-last-updated local-date-time)
    (:original-release-date result) (update :original-release-date t/local-date)))

(s/fdef parse-game-response :ret ::game)

(defn parse-game-response [response]
  (-> response
      parse-response
      (update :results parse-dates)))

(s/fdef parse-games-response :ret ::games)

(defn parse-games-response [response]
  (-> response
      parse-response
      (update :results #(map parse-dates %))))
