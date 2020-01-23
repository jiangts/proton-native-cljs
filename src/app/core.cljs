(ns app.core
  (:require [uix.core.alpha :as uix.core]
            [clojure.string :as str]))

(def react (js/require "react"))
(def proton-native (js/require "proton-native"))

(def App (.-App proton-native))
(def Window (.-Window proton-native))
(def View (.-View proton-native))
(def Text (.-Text proton-native))
(def TouchableOpacity (.-TouchableOpacity proton-native))

(defn workspace-item [{:keys [on-press]}]
  [:> TouchableOpacity {:on-press on-press}
   [:> View {:style {:width 40
                     :height 40
                     :border-radius 7
                     :background-color "#fff"}}]])

(defn workspace-switcher []
  [:> View {:style {:width 60
                    :background-color "#242424"
                    :align-items :center}}
   (for [idx (range 3)]
     ^{:key idx}
     [:> View {:style {:margin-top 16}}
      [workspace-item {:on-press identity}]])])

(defn channel-item [props text]
  [:> View {:style {:padding-top 8
                    :padding-bottom 8
                    :padding-left 16}}
   [:> Text {:style {:color "#fff"}}
    (str "# " text)]])

(defn sidebar-main [{:keys [channels]}]
  [:> View {:style {:flex 1}}
   (for [chan-name channels]
     ^{:key chan-name}
     [channel-item {} chan-name])])

(defn sidebar []
  [:> View {:style {:width 240
                    :background-color "#000"
                    :flex-direction :row}}
   [workspace-switcher]
   [sidebar-main {:channels ["dev" "product" "random" "general"]}]])

(defn avatar [{:keys [src]}]
  [:> View {:style {:width 40
                    :height 40
                    :border-radius 7
                    :background-color "#242424"}}])

(defn message-item [{:keys [author posted-at body]}]
  [:> View {:style {:flex-direction :row
                    :padding-left 16
                    :padding-top 8
                    :padding-bottom 8}}
   [avatar {:src (:avatar author)}]
   [:> View {:style {:margin-left 8}}
    [:> View {:style {:flex-direction :row
                      :align-items :flex-end}}
     [:> Text {:style {:font-size 14
                       :font-weight 600}}
      (:name author)]
     [:> Text {:style {:font-size 12
                       :margin-left 8
                       :color "#666"}}
      (.toLocaleTimeString ^js/Date posted-at)]]
    [:> Text {:style {:margin-top 8}}
     body]]])

(defn messages-view [{:keys [messages]}]
  [:> View {:style {:flex 1
                    :background-color "#eee"}}
   (for [msg messages]
     ^{:key (:posted-at msg)}
     [message-item msg])])

(def messages
  [{:author {:name "Roman Liutikov"
             :avatar ""}
    :posted-at (js/Date.)
    :body "Hello!"}
   {:author {:name "Roman Liutikov"
             :avatar ""}
    :posted-at (js/Date. (- (js/Date.now) 1000))
    :body "Hello!"}])

(defn chat-view []
  [:> View {:style {:flex 1}}
   [messages-view {:messages messages}]
   [:> View {:style {:height 140
                     :background-color "#fff"}}]])

(defn app []
  [:> App
   [:> Window {:style {:width 900
                       :height 600
                       :backgroundColor "#fff"}}
    [:> View {:style {:flex 1
                      :flex-direction :row}}
     [sidebar]
     [chat-view]]]])

;; For some reason root components should be React class
;; to make hot-reloading work
(def root
  (uix.core/create-class
    {:prototype {:render #(uix.core/as-element (app))}}))

(defn main []
  (.registerComponent
    (.-AppRegistry proton-native)
    "app"
    (.createElement react root)))

(defn update!
  "Hot reloading"
  []
  (.updateProxy (.-AppRegistry proton-native) root))
