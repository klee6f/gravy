(ns games
  (:require [game]
            [specs]
            [clojure.spec.alpha :as s]))

;; doesn't handle dates because of time restrictions
(s/def ::filter
  (s/keys :opt-un
          [:game/aliases
           :game/api-detail-url
           #_:game/date-added
           #_:game/date-last-updated
           :game/expected-release-month
           :game/expected-release-quarter
           :game/expected-release-year
           :game/guid
           :game/id
           :game/name
           :game/number-of-user-reviews
           #_:game/original-release-date
           :game/platforms]))

;; not implemented
#_(s/def ::sort
  (s/keys :opt-un
          [:game/date-added
           :game/date-last-updated
           :game/id
           :game/name
           :game/number-of-user-reviews
           :game/original-game-rating
           :game/original-release-date]))

(s/def ::results
  (s/coll-of
   (s/keys :req-un
           [:game/aliases
            :game/api-detail-url
            :game/date-added
            :game/date-last-updated
            :game/deck
            :game/description
            :game/expected-release-day
            :game/expected-release-month
            :game/expected-release-quarter
            :game/expected-release-year
            :game/guid
            :game/id
            :game/image
            :game/image-tags
            :game/name
            :game/number-of-user-reviews
            :game/original-game-rating
            :game/original-release-date
            :game/platforms
            :game/site-detail-url])))
