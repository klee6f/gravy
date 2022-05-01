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
#_(println test-response-1)

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

#_(s/valid? ::game parsed-response-1)

#_(def test-response-2
  (client/get
   "http://www.giantbomb.com/api/game/3030-4725/"
   {:headers {"User-Agent" "Kayla's testing bot"}
    :query-params {"format" "json"
                   "field_list" "generes,name"
                   "api_key" api-key}}))

#_(def parsed-response-2
  (-> test-response-2
      :body
      (json/parse-string csk/->kebab-case-keyword)))

#_(s/valid? ::game parsed-response-2)

#_(defn test-request-3 [resource-type resource-id response-data-format resource-fileds]
  (client/get (format "http://www.giantbomb.com/api/%s/%s/?api_key=%s&format=%s&field_list=%s"
                      resource-type
                      resource-id)
              {:headers {"User-Agent" "Kayla's testing bot"}
               :query-params {"api_key" api-key
                              "format" response-data-format
                              "field_list" (s/join "," resource-fields)}}))

#_(def test "https://www.giantbomb.com/api/game/3030-4725/?api_key=ca693dbbae4199a0a9a1f639945883e4fef0f47a&format=json&field_list=genres,name")


#_(defn response-spec [results-spec]
  (s/keys :req-un [::status-code
                   ::error
                   ::number-of-total-results
                   ::number-of-page-results
                   ::limit
                   ::offset
                   ::results]))

;;todo: flush out these specs
(s/def ::date-string string?) "2007-08-28"
(s/def ::date-time-string string?) "2017-12-02 18:31:43"
(s/def ::year-string string?)
(s/def ::month-string string?)

(s/def ::url string?)

(s/def ::api-detail-url ::url)
(s/def ::id pos-int?)
(s/def ::name string?)
(s/def ::site-detail-url ::url)

(s/def ::link (s/keys :req-un [::api-detail-url
                               ::id
                               ::name
                               ::site-detail-url]))

(s/def :image/tiny-url ::url)
(s/def :image/tags string?)
(s/def :image/image-tags string?)
(s/def :image/super-url ::url)
(s/def :image/original ::url)
(s/def :image/original-url ::url)
(s/def :image/screen-url ::url)
(s/def :image/screen-large-url ::url)
(s/def :image/icon-url ::url)
(s/def :image/medium-url ::url)
(s/def :image/small-url ::url)
(s/def :image/thumb-url ::url)

;; There's some inconsistency in the API here. Both original and original-url
;; lead to roughly the same endpoint and one is used in `images` and the other
;; in `image`. Since I am working on limited data, I'm going to assume most
;; these attributes are optional.

;; Scratch that, we're going to need to investigate further. They're doing something
;; silly using both the words `:image-tags` and `:tags`. I really want both these
;; types to be the same, but perhaps they are different. *sigh* I'm going to hold
;; out hope until I have tested another couple games. 
(s/def ::image (s/keys :opt-un [:image/tags
                                :image/image-tags
                                :image/tiny-url
                                :image/super-url
                                :image/original
                                :image/original-url
                                :image/screen-url
                                :image/screen-large-url
                                :image/icon-url
                                :image/medium-url
                                :image/small-url
                                :image/thumb-url]))


;;List of aliases the game is known by. A \n (newline) seperates each alias.
(s/def :game/aliases string?)
;;URL pointing to the game detail resource.
(s/def :game/api-details-url ::api-detail-url)
;;Characters related to the game.
(s/def :game/characters (s/coll-of ::link))
;;Concepts related to the game.
(s/def :game/concepts (s/coll-of ::link))
;;Date the game was added to Giant Bomb.
(s/def :game/date-added ::date-time-string)
;;Date the game was last updated on Giant Bomb.
(s/def :game/date-last-updated ::date-time-string) 
;;Brief summary of the game.
(s/def :game/deck string?)
;;Description of the game.
(s/def :game/description string?)
;;Companies who developed the game.
(s/def :game/developers (s/coll-of ::link))
;;Expected day of release. The month is represented numerically. Combine with 'expected_release_day', 'expected_release_month', 'expected_release_year' and 'expected_release_quarter' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def :game/expected-release-day (s/nilable ::date-string)) ;;?? has not been observed
;;Expected month of release. The month is represented numerically. Combine with 'expected_release_day', 'expected_release_quarter' and 'expected_release_year' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def :game/expected-release-month (s/nilable ::month-string)) ;;?? has not been observed
;;Expected quarter of release. The quarter is represented numerically, where 1 = Q1, 2 = Q2, 3 = Q3, and 4 = Q4. Combine with 'expected_release_day', 'expected_release_month' and 'expected_release_year' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def :game/expected-release-quarter (s/nilable #{1 2 3 4})) ;;?? has not been observed
;;Expected year of release. Combine with 'expected_release_day', 'expected_release_month' and 'expected_release_quarter' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def :game/expected-release-year (s/nilable ::year-string)) ;;?? has not been observed
;;Characters that first appeared in the game.
(s/def :game/first-appearance-characters (s/coll-of ::link))
;;Concepts that first appeared in the game.
(s/def :game/first-appearance-concepts (s/coll-of ::link))
;;Locations that first appeared in the game.
(s/def :game/first-appearance-locations (s/coll-of ::link))
;;Objects that first appeared in the game.
(s/def :game/first-appearance-objects (s/coll-of ::link))
;;People that were first credited in the game.
(s/def :game/first-appearance-people (s/coll-of ::link))
;;Franchises related to the game.
(s/def :game/franchises (s/coll-of ::link))
;;Genres that encompass the game.
(s/def :game/genres (s/coll-of ::link))
;;For use in single item api call for game.
(s/def :game/guid string?) ;;"3030-4725" maybe make this one a set? or a specific spec for hyphonated numbers
;;Unique ID of the game.
(s/def :game/id ::id)
;;Main image of the game.
(s/def :game/image ::image)
;;List of images associated to the game.
(s/def :game/images (s/coll-of ::image))

;;List of image tags to filter the images endpoint.
(s/def :game/image-tags (s/coll-of (s/keys :req-un [::api-detail-url
                                                    ::name
                                                    ::total])))

;;Characters killed in the game.
(s/def :game/killed-characters (s/nilable (s/coll-of ::link))) ;;?? has not been observed
;;Locations related to the game.
(s/def :game/locations (s/coll-of ::link))
;;Name of the game.
(s/def :game/name string?)
;;Number of user reviews of the game on Giant Bomb.
(s/def :game/number-of-user-reviews nat-int?)
;;Objects related to the game.
(s/def :game/objects (s/coll-of ::link))
;;Rating of the first release of the game.
(s/def :game/original-game-rating (s/coll-of (s/keys :req-un [::api-detail-url
                                                              ::id
                                                              ::name])))
;;Date the game was first released.
(s/def :game/original-release-date ::date-string)
;;People who have worked with the game.
(s/def :game/people (s/coll-of ::link))

;;Platforms the game appeared in.
(s/def :platforms/abbreviation string?)
(s/def :game/platforms (s/coll-of (s/keys :req-un [::api-detail-url
                                                   ::id
                                                   ::name
                                                   ::site-detail-url
                                                   :platforms/abbreviation])))

;;Companies who published the game.
(s/def :game/publishers (s/coll-of ::link))
;;Releases of the game.
(s/def :game/releases (s/coll-of ::link))
;;Game DLCs
(s/def :game/dlcs (s/coll-of string?))
;;Staff reviews of the game.
(s/def :game/reviews (s/coll-of string?))
;;Other games similar to the game.
(s/def :game/similar-games (s/coll-of ::link))
;;URL pointing to the game on Giant Bomb.
(s/def :game/site-detail-url ::site-detail-url)
;;Themes that encompass the game.
(s/def :game/themes (s/coll-of ::link))
;;Videos associated to the game.
(s/def :game/videos (s/nilable (s/coll-of any?))) ;;?? has not been observed

(s/def :game/results
  (s/keys :req-un [:game/description
                   :game/killed-characters
                   :game/first-appearance-objects
                   :game/characters
                   :game/original-game-rating
                   :game/objects
                   :game/image-tags
                   :game/images
                   :game/first-appearance-characters
                   :game/name
                   :game/deck
                   :game/genres
                   :game/first-appearance-concepts
                   :game/videos
                   :game/publishers
                   :game/franchises
                   :game/original-release-date
                   :game/expected-release-month
                   :game/similar-games
                   :game/date-last-updated
                   :game/themes
                   :game/first-appearance-people
                   :game/api-detail-url
                   :game/locations
                   :game/developers
                   :game/number-of-user-reviews
                   :game/concepts
                   :game/expected-release-quarter
                   :game/first-appearance-locations
                   :game/releases
                   :game/id
                   :game/expected-release-year
                   :game/site-detail-url
                   :game/image
                   :game/aliases
                   :game/date-added
                   :game/expected-release-day
                   :game/people
                   :game/platforms
                   :game/guid]))

(s/def ::game (s/keys :req-un [::status-code
                               ::error
                               ::number-of-total-results
                               ::number-of-page-results
                               ::limit
                               ::offset
                               :game/results]))

(s/explain ::game parsed-response-1)
(s/valid? ::game parsed-response-1)

;;people, image, releases, first-appearance-locations, 
