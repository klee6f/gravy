(ns core
  (:require [camel-snake-kebab.core :as csk]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [clojure.spec.alpha :as s]))

(def api-key (slurp "api.key"))

(def test-response-1 (client/get "http://www.giantbomb.com/api/game/3030-4725/"
                                 {:headers {"User-Agent" "Kayla's testing bot"}
                                  :query-params {"api_key" api-key
                                                 "format" "json"}
                                  :throw-entire-message? true}))
(println test-response-1)

(def parsed-response-1
  (-> test-response-1
      :body
      (json/parse-string csk/->kebab-case-keyword)))


(s/def ::status-code #{1 100 101 102 103 104 105})
(s/def ::error string?)
(s/def ::number-of-total-results int?)
(s/def ::number-of-page-results int?)
(s/def ::limit int?)
(s/def ::offset int?)
(s/def ::results any?)

(s/def ::game (s/keys :req-un [::status-code
                               ::error
                               ::number-of-total-results
                               ::number-of-page-results
                               ::limit
                               ::offset
                               ::results]))

(s/valid? ::game parsed-response-1)

(def test-response-2
  (client/get
   "http://www.giantbomb.com/api/game/3030-4725/"
   {:headers {"User-Agent" "Kayla's testing bot"}
    :query-params {"format" "json"
                   "field_list" "generes,name"
                   "api_key" api-key}}))

(def parsed-response-2
  (-> test-response-2
      :body
      (json/parse-string csk/->kebab-case-keyword)))

(s/valid? ::game parsed-response-2)

(defn test-request-3 [resource-type resource-id response-data-format resource-fileds]
  (client/get (format "http://www.giantbomb.com/api/%s/%s/?api_key=%s&format=%s&field_list=%s"
                      resource-type
                      resource-id)
              {:headers {"User-Agent" "Kayla's testing bot"}
               :query-params {"api_key" api-key
                              "format" response-data-format
                              "field_list" (s/join "," resource-fields)}}))

(def test "https://www.giantbomb.com/api/game/3030-4725/?api_key=ca693dbbae4199a0a9a1f639945883e4fef0f47a&format=json&field_list=genres,name")


(defn response-spec [results-spec]
  (s/keys :req-un [::status-code
                   ::error
                   ::number-of-total-results
                   ::number-of-page-results
                   ::limit
                   ::offset
                   ::results]))

(s/def date-string string?)

;;List of aliases the game is known by. A \n (newline) seperates each alias.
(s/def game/aliases string?)
;;URL pointing to the game detail resource.
(s/def game/api-details-url url?)
;;Characters related to the game.
(s/def game/characters string?)
;;Concepts related to the game.
(s/def game/concepts string?)
;;Date the game was added to Giant Bomb.
(s/def game/date-added date-string?)
;;Date the game was last updated on Giant Bomb.
(s/def game/date-last-updated date-string?)
;;Brief summary of the game.
(s/def game/deck string?)
;;Description of the game.
(s/def game/description string?)
;;Companies who developed the game.
(s/def game/developers string?)
;;Expected day of release. The month is represented numerically. Combine with 'expected_release_day', 'expected_release_month', 'expected_release_year' and 'expected_release_quarter' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def game/expected-release-day date-string?)
;;Expected month of release. The month is represented numerically. Combine with 'expected_release_day', 'expected_release_quarter' and 'expected_release_year' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def game/expected-release-month month-string?)
;;Expected quarter of release. The quarter is represented numerically, where 1 = Q1, 2 = Q2, 3 = Q3, and 4 = Q4. Combine with 'expected_release_day', 'expected_release_month' and 'expected_release_year' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def game/expected-release-quarter #{1 2 3 4 nil})
;;Expected year of release. Combine with 'expected_release_day', 'expected_release_month' and 'expected_release_quarter' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def game/expected-release-year year-string?)
;;Characters that first appeared in the game.
(s/def game/first-appearance-characters string?)
;;Concepts that first appeared in the game.
(s/def game/first-appearance-concepts string?)
;;Locations that first appeared in the game.
(s/def game/first-appearance-locations string?)
;;Objects that first appeared in the game.
(s/def game/first-appearance-objects string?)
;;People that were first credited in the game.
(s/def game/first-appearance-people string?)
;;Franchises related to the game.
(s/def game/franchises string?)
;;Genres that encompass the game.
(s/def game/genres string?)
;;For use in single item api call for game.
(s/def game/guid guid?)
;;Unique ID of the game.
(s/def game/id id?)
;;Main image of the game.
(s/def game/image any?)
;;List of images associated to the game.
(s/def game/images any?)
;;List of image tags to filter the images endpoint.
(s/def game/image-tags (s/coll-of string?))
;;Characters killed in the game.
(s/def game/killed-characters (s/coll-of string?))
;;Locations related to the game.
(s/def game/locations (s/coll-of string?))
;;Name of the game.
(s/def game/name string?)
;;Number of user reviews of the game on Giant Bomb.
(s/def game/number-of-user-reviews int?)
;;Objects related to the game.
(s/def game/objects (s/coll-of string?))
;;Rating of the first release of the game.
(s/def game/original-game-rating string?)
;;Date the game was first released.
(s/def game/original-release-date date-string?)
;;People who have worked with the game.
(s/def game/people (s/coll-of string?))
;;Platforms the game appeared in.
(s/def game/platforms #{})
;;Companies who published the game.
(s/def game/publishers (s/coll-of string?))
;;Releases of the game.
(s/def game/releases (s/coll-of string?))
;;Game DLCs
(s/def game/dlcs (s/coll-of string?))
;;Staff reviews of the game.
(s/def game/reviews (s/coll-of string?))
;;Other games similar to the game.
(s/def game/similar-games (s/coll-of string?))
;;URL pointing to the game on Giant Bomb.
(s/def game/site-detail-url url?)
;;Themes that encompass the game.
(s/def game/themes (s/coll-of string?))
;;Videos associated to the game.
(s/def game/videos (s/coll-of string?))
