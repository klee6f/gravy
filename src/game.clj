(ns game
    (:require [specs :refer :all][clojure.spec.alpha :as s]
              [clojure.spec.gen.alpha :as gen]
              [clojure.test.check.generators :as tcgen]))

;;List of aliases the game is known by. A \n (newline) seperates each alias.
(s/def ::aliases (s/nilable string?))
;;URL pointing to the game detail resource.
(s/def ::api-details-url :specs/api-detail-url)
;;Characters related to the game.
(s/def ::characters (s/nilable (s/coll-of :specs/link)))
;;Concepts related to the game.
(s/def ::concepts (s/nilable (s/coll-of :specs/link)))
;;Date the game was added to Giant Bomb.
(s/def ::date-added :specs/date-time-string)
;;Date the game was last updated on Giant Bomb.
(s/def ::date-last-updated :specs/date-time-string) 
;;Brief summary of the game.
(s/def ::deck (s/nilable string?))
;;Description of the game.
(s/def ::description (s/nilable string?))
;;Companies who developed the game.
(s/def ::developers (s/nilable (s/coll-of :specs/link)))
;;Expected day of release. The month is represented numerically. Combine with 'expected_release_day', 'expected_release_month', 'expected_release_year' and 'expected_release_quarter' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def ::expected-release-day (s/nilable nat-int?))
;;Expected month of release. The month is represented numerically. Combine with 'expected_release_day', 'expected_release_quarter' and 'expected_release_year' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def ::expected-release-month (s/nilable nat-int?))
;;Expected quarter of release. The quarter is represented numerically, where 1 = Q1, 2 = Q2, 3 = Q3, and 4 = Q4. Combine with 'expected_release_day', 'expected_release_month' and 'expected_release_year' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def ::expected-release-quarter (s/nilable #{1 2 3 4}))
;;Expected year of release. Combine with 'expected_release_day', 'expected_release_month' and 'expected_release_quarter' for Giant Bomb's best guess release date of the game. These fields will be empty if the 'original_release_date' field is set.
(s/def ::expected-release-year (s/nilable nat-int?))
;;Characters that first appeared in the game.
(s/def ::first-appearance-characters (s/nilable (s/coll-of :specs/link)))
;;Concepts that first appeared in the game.
(s/def ::first-appearance-concepts (s/nilable (s/coll-of :specs/link)))
;;Locations that first appeared in the game.
(s/def ::first-appearance-locations (s/nilable (s/coll-of :specs/link)))
;;Objects that first appeared in the game.
(s/def ::first-appearance-objects (s/nilable (s/coll-of :specs/link)))
;;People that were first credited in the game.
(s/def ::first-appearance-people (s/nilable (s/coll-of :specs/link)))
;;Franchises related to the game.
(s/def ::franchises (s/nilable (s/coll-of :specs/link)))
;;Genres that encompass the game.
(s/def ::genres (s/coll-of :specs/link)) ;;is optional

;;For use in single item api call for game.
;;it very likely goes higher than 70000, i'm just not sure how much higher. Also I am not certain
;;about the completeness, but we'll find out soon enough
(s/def ::guid (s/with-gen #(re-matches #"3030-\d+" %)
                    #(tcgen/no-shrink (gen/fmap (partial str "3030-") (gen/large-integer* {:min 1 :max 70000})))))

;;Unique ID of the game.
(s/def ::id :specs/id)
;;Main image of the game.
(s/def ::image :specs/image)
;;List of images associated to the game.
(s/def ::images (s/coll-of :specs/image))

;;List of image tags to filter the images endpoint.
(s/def ::image-tags (s/coll-of (s/keys :req-un [:specs/api-detail-url
                                                    :specs/name
                                                    :specs/total])))

;;Characters killed in the game.
(s/def ::killed-characters (s/nilable (s/coll-of :specs/link)))
;;Locations related to the game.
(s/def ::locations (s/nilable (s/coll-of :specs/link)))
;;Name of the game.
(s/def ::name string?)
;;Number of user reviews of the game on Giant Bomb.
(s/def ::number-of-user-reviews nat-int?)
;;Objects related to the game.
(s/def ::objects (s/nilable (s/coll-of :specs/link)))
;;Rating of the first release of the game.
(s/def ::original-game-rating (s/nilable (s/coll-of (s/keys :req-un [:specs/api-detail-url
                                                                         :specs/id
                                                                         :specs/name]))))
;;Date the game was first released.
(s/def ::original-release-date (s/nilable :specs/date-string))
;;People who have worked with the game.
(s/def ::people (s/nilable (s/coll-of :specs/link)))

;;Platforms the game appeared in.
(s/def :game.platforms/abbreviation string?)
(s/def ::platforms (s/nilable (s/coll-of (s/keys :req-un [:specs/api-detail-url
                                                          :specs/id
                                                          :specs/name
                                                          :specs/site-detail-url
                                                          :game.platforms/abbreviation]))))

;;Companies who published the game.
(s/def ::publishers (s/nilable (s/coll-of :specs/link)))
;;Releases of the game.
(s/def ::releases (s/coll-of :specs/link)) ;;is optional
;;Game DLCs
(s/def ::dlcs (s/coll-of string?))
;;Staff reviews of the game.
(s/def ::reviews (s/coll-of string?))
;;Other games similar to the game.
(s/def ::similar-games (s/nilable (s/coll-of :specs/link)))
;;URL pointing to the game on Giant Bomb.
(s/def ::site-detail-url :specs/site-detail-url)
;;Themes that encompass the game.
(s/def ::themes (s/coll-of :specs/link)) ;;is optional
;;Videos associated to the game.
(s/def ::videos (s/nilable (s/coll-of :specs/link)))

(s/def ::result
  (s/or :empty empty?
        :not-empty
        (s/keys :req-un [::description
                         ::killed-characters
                         ::first-appearance-objects
                         ::characters
                         ::original-game-rating
                         ::objects
                         ::image-tags
                         ::images
                         ::first-appearance-characters
                         ::name
                         ::deck
                         ::first-appearance-concepts
                         ::videos
                         ::publishers
                         ::franchises
                         ::original-release-date
                         ::expected-release-month
                         ::similar-games
                         ::date-last-updated
                         ::first-appearance-people
                         ::api-detail-url
                         ::locations
                         ::developers
                         ::number-of-user-reviews
                         ::concepts
                         ::expected-release-quarter
                         ::first-appearance-locations
                         ::id
                         ::expected-release-year
                         ::site-detail-url
                         ::image
                         ::aliases
                         ::date-added
                         ::expected-release-day
                         ::people
                         ::platforms
                         ::guid]
                :opt-un [::releases
                         ::themes
                         ::genres])))

