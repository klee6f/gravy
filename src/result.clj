(ns result
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test.check.generators :as tcgen]))

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
                               :game/result]))
