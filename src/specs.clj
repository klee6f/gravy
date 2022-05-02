(ns specs
  (:require [java-time :as t]
            [clojure.spec.alpha :as s]))

(s/def ::date-string
  #(try (t/local-date %)
        (catch Exception e false)))

(s/def ::date-time-string
  #(try (t/local-date %)
        (catch Exception e false)))

(s/def ::date t/local-date?)
(s/def ::date-time t/local-date-time?)

(s/def ::url #(try (uri? (new java.net.URI %))
                   (catch Exception e false)))

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
