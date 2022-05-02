(ns specs
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




(s/def ::game (s/keys :req-un [::status-code
                               ::error
                               ::number-of-total-results
                               ::number-of-page-results
                               ::limit
                               ::offset
                               :game/result]))
