(ns core-test
  (:require [game]
            [request :as request]
            [response :as response]
            [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.test.check :as tc]
            [orchestra.spec.test :as st]
            [clojure.test.check.properties :as prop :include-macros true]
            [clojure.test.check.generators :as tcgen]))

(st/instrument)

;; This test really isn't doing much, but neither is the endpoint we're testing, since it
;; only really has that one option, and the rest are more about the shape of the output.
;; However, it'll have to do, and at least it exercises our specs! 

(def guid-is-returned-prop
  (prop/for-all [guid (s/gen :game/guid)]
                (let [game-response (-> guid
                                        request/get-game
                                        response/parse-game-response)]
                  (is (or (= 0 (:number-of-total-results game-response))
                          (= guid (:guid (:results game-response))))))))

;; Tested with seed 1651442293128 locally size 200, I wouldn't be shocked if there were more
;; optional or nilable categories. That's what most the issues have been.
;;
;; We're going to keep size low so as to not blow up their endpoint.

(deftest guid-is-returned-test
  (tc/quick-check 100 guid-is-returned-prop))

(def search-finds-existing-games-prop
  (prop/for-all
   [guid (s/gen :game/guid)
    game-filter (tcgen/elements [:api-details-url :guid :name])]
   (let [{{guid-1 :guid :as game-results} :results}
         (response/parse-game-response (request/get-game guid))
         {:keys [results]}
         (-> game-results
             (select-keys [game-filter])
             (hash-map :filters)
             request/get-games
             response/parse-games-response)]
     (is (or (< 99 (count results))
             (some #{guid} (map :guid results)))))))

(deftest search-finds-existing-games-test
  (tc/quick-check 100 search-finds-existing-games-prop))




(comment
  (guid-is-returned-test)
  (search-finds-existing-games-test)

  (def rimworld (response/parse-game-response (request/get-game "3030-44272")))

  (keys (:results rimworld))

  (def temp (request/get-games {:filter (select-keys (:results rimworld) [:name])}))

  (def rimworld-games (response/parse-games-response temp))

  (count (:results rimworld-games))
  (keys (first (:results rimworld-games)))
  (:guid (first (:results rimworld-games)))

  (count (:results (response/parse-response temp)))

  (:original-release-date (:results (response/parse-response temp))))
