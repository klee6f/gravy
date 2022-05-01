(ns core
  (:require [camel-snake-kebab.core :as csk]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as tcgen]
            [orchestra.spec.test :as st]
            [clojure.test.check.properties :as prop :include-macros true]))

;; https://www.giantbomb.com/forums/api-developers-3017/quick-start-guide-to-using-the-api-1427959/
;; create an api key, and toss it into the root of this project

(def api-key (slurp "api.key"))

(s/def ::status-code #{1 100 101 102 103 104 105})
(s/def ::error string?)
(s/def ::number-of-total-results nat-int?)
(s/def ::number-of-page-results nat-int?)
(s/def ::limit int?)
(s/def ::offset int?)
(s/def ::results any?)

;; todo: flush out these specs, perhaps add java-time to validate them
;; for now, this is fine. 
(s/def ::date-string string?) ;;"2007-08-28"
(s/def ::date-time-string string?) ;;"2017-12-02 18:31:43"

;;todo: this probably fails in a very ugly way when given data of the wrong
;;type and needs a try around it. 
(s/def ::url #(uri? (new java.net.URI %)))

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
;; in `image`. Having gone done the image size rabbit hole, I sense there are
;; dragons, and this works for the 200 or so sampled games, and at least validates
;; several of the (probably) common attributes. 

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
(s/def :game/aliases (s/nilable string?))
;;URL pointing to the game detail resource.
(s/def :game/api-details-url ::api-detail-url)
;;Characters related to the game.
(s/def :game/characters (s/nilable (s/coll-of ::link)))
;;Concepts related to the game.
(s/def :game/concepts (s/nilable (s/coll-of ::link)))
;;Date the game was added to Giant Bomb.
(s/def :game/date-added ::date-time-string)
;;Date the game was last updated on Giant Bomb.
(s/def :game/date-last-updated ::date-time-string) 
;;Brief summary of the game.
(s/def :game/deck (s/nilable string?))
;;Description of the game.
(s/def :game/description (s/nilable string?))
;;Companies who developed the game.
(s/def :game/developers (s/nilable (s/coll-of ::link)))
;;Expected day of release. The month is represented numerically. Combine with 'expected_release_day', 'expected_release_month', 'expected_release_year' and 'expected_release_quarter' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def :game/expected-release-day (s/nilable nat-int?))
;;Expected month of release. The month is represented numerically. Combine with 'expected_release_day', 'expected_release_quarter' and 'expected_release_year' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def :game/expected-release-month (s/nilable nat-int?))
;;Expected quarter of release. The quarter is represented numerically, where 1 = Q1, 2 = Q2, 3 = Q3, and 4 = Q4. Combine with 'expected_release_day', 'expected_release_month' and 'expected_release_year' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def :game/expected-release-quarter (s/nilable #{1 2 3 4}))
;;Expected year of release. Combine with 'expected_release_day', 'expected_release_month' and 'expected_release_quarter' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def :game/expected-release-year (s/nilable nat-int?))
;;Characters that first appeared in the game.
(s/def :game/first-appearance-characters (s/nilable (s/coll-of ::link)))
;;Concepts that first appeared in the game.
(s/def :game/first-appearance-concepts (s/nilable (s/coll-of ::link)))
;;Locations that first appeared in the game.
(s/def :game/first-appearance-locations (s/nilable (s/coll-of ::link)))
;;Objects that first appeared in the game.
(s/def :game/first-appearance-objects (s/nilable (s/coll-of ::link)))
;;People that were first credited in the game.
(s/def :game/first-appearance-people (s/nilable (s/coll-of ::link)))
;;Franchises related to the game.
(s/def :game/franchises (s/nilable (s/coll-of ::link)))
;;Genres that encompass the game.
(s/def :game/genres (s/coll-of ::link)) ;;is optional

;;For use in single item api call for game.
;;they might just be 4 digits + 4-5 digits, that's all i've observed (out of 4)
;;it very likely goes higher than 7000, i'm just not sure how much higher. Also I am not certain
;;about the completeness, but we'll find out soon enough
(s/def :game/guid (s/with-gen #(re-matches #"3030-\d+" %)
                    #(tcgen/no-shrink (gen/fmap (partial str "3030-") (gen/large-integer* {:min 1 :max 70000})))))

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
(s/def :game/killed-characters (s/nilable (s/coll-of ::link)))
;;Locations related to the game.
(s/def :game/locations (s/nilable (s/coll-of ::link)))
;;Name of the game.
(s/def :game/name string?)
;;Number of user reviews of the game on Giant Bomb.
(s/def :game/number-of-user-reviews nat-int?)
;;Objects related to the game.
(s/def :game/objects (s/nilable (s/coll-of ::link)))
;;Rating of the first release of the game.
(s/def :game/original-game-rating (s/nilable (s/coll-of (s/keys :req-un [::api-detail-url
                                                                         ::id
                                                                         ::name]))))
;;Date the game was first released.
(s/def :game/original-release-date (s/nilable ::date-string))
;;People who have worked with the game.
(s/def :game/people (s/nilable (s/coll-of ::link)))

;;Platforms the game appeared in.
(s/def :platforms/abbreviation string?)
(s/def :game/platforms (s/nilable (s/coll-of (s/keys :req-un [::api-detail-url
                                                              ::id
                                                              ::name
                                                              ::site-detail-url
                                                              :platforms/abbreviation]))))

;;Companies who published the game.
(s/def :game/publishers (s/nilable (s/coll-of ::link)))
;;Releases of the game.
(s/def :game/releases (s/coll-of ::link)) ;;is optional
;;Game DLCs
(s/def :game/dlcs (s/coll-of string?))
;;Staff reviews of the game.
(s/def :game/reviews (s/coll-of string?))
;;Other games similar to the game.
(s/def :game/similar-games (s/nilable (s/coll-of ::link)))
;;URL pointing to the game on Giant Bomb.
(s/def :game/site-detail-url ::site-detail-url)
;;Themes that encompass the game.
(s/def :game/themes (s/coll-of ::link)) ;;is optional
;;Videos associated to the game.
(s/def :game/videos (s/nilable (s/coll-of ::link)))

(s/def :game/results
  (s/or :empty empty?
        :not-empty
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
                         :game/first-appearance-concepts
                         :game/videos
                         :game/publishers
                         :game/franchises
                         :game/original-release-date
                         :game/expected-release-month
                         :game/similar-games
                         :game/date-last-updated
                         :game/first-appearance-people
                         :game/api-detail-url
                         :game/locations
                         :game/developers
                         :game/number-of-user-reviews
                         :game/concepts
                         :game/expected-release-quarter
                         :game/first-appearance-locations
                         :game/id
                         :game/expected-release-year
                         :game/site-detail-url
                         :game/image
                         :game/aliases
                         :game/date-added
                         :game/expected-release-day
                         :game/people
                         :game/platforms
                         :game/guid]
                :opt-un [:game/releases
                         :game/themes
                         :game/genres])))

(s/def ::game (s/keys :req-un [::status-code
                               ::error
                               ::number-of-total-results
                               ::number-of-page-results
                               ::limit
                               ::offset
                               :game/results]))

(s/fdef get-game :args (s/cat :guid :game/guid))

(defn get-game [guid]
  (println "getting: " guid)
  (client/get (format "http://www.giantbomb.com/api/game/%s/" guid)
              {#_#_:debug true
               :headers {"User-Agent" "Kayla's testing bot"}
               :query-params {"api_key" api-key
                              "format" "json"}
               :throw-entire-message? true}))

;; lets not get rate limited, their docs said something like 200 requests per hour and
;; we're going to be SUPER respectful of that, since generative testing could very easily
;; surpass that, and ideally should if we were being sort of thorough, but this is more a
;; proof of concept than a genuine bonified attempt to test their endpoint.

(def get-game-memo (memoize get-game))

(s/fdef parse-response :ret ::game)

(defn parse-response [response]
  (-> response
      :body
      (json/parse-string csk/->kebab-case-keyword)))

;; manual test games
(def game-ids ["3030-4725"
               "3030-20645"
               "3030-30057"
               "3030-44272"
               "3030-43393"
               "3030-68921"
               "3030-18741"
               "3030-20232"
               "3030-19783"
               "3030-69447"
               "3030-20815"
               "3030-22195"
               "3030-1539"])

(st/instrument)

;; This test really isn't doing much, but neither is the endpoint we're testing, since it
;; only really has that one option, and the rest are more about the shape of the output.
;; However, it'll have to do, and at least it exercises our specs! 

(def guid-is-returned-prop
  (prop/for-all [guid (s/gen :game/guid)]
                (let [response (parse-response (get-game-memo guid))]
                  (or (= 0 (:number-of-total-results response))
                      (= guid (:guid (:results response)))))))

;; Tested with seed 1651442293128 locally size 200, I wouldn't be shocked if there were more
;; optional or nilable categories. That's what most the issues have been.
;;
;; We're going to keep size low so as to not blow up their endpoint.

(deftest guid-is-returned-test
  (tc/quick-check 10 #_200 guid-is-returned-prop #_#_:seed 1651442293128))

