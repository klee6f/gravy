(ns request
  (:require [game]
            [games]
            [camel-snake-kebab.core :as csk]
            [clj-http.client :as client]
            [clojure.string :as string]
            [clojure.spec.alpha :as s]))

;; https://www.giantbomb.com/forums/api-developers-3017/quick-start-guide-to-using-the-api-1427959/
;; create an api key, and toss it into the root of this project

(def api-key (string/trim (slurp "api.key")))

(s/fdef get-game
  :args (s/cat :guid :game/guid))

(defn get-game [guid]
  (client/get (format "http://www.giantbomb.com/api/game/%s/" guid)
                {:headers {"User-Agent" "Kayla's testing bot"}
                 :query-params {"format" "json"
                                "api_key" api-key}}))

(s/fdef get-games
  :args (s/cat :opts (s/keys :opt-un [:games/filter])))

;; Does not handle date filters because that was eating up too much time
(defn get-games [opts]
  (client/get
   "http://www.giantbomb.com/api/games/"
   {:headers {"User-Agent" "Kayla's testing bot"}
    :query-params
    (merge
     (update opts :filter
             #(->> %
                   (reduce-kv (fn [c k v] (conj c (str (name (csk/->snake_case k)) ":" v))) [])
                   (string/join ".")))
     {"format" "json"
      "api_key" api-key})}))
