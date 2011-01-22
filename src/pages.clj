(ns pages
  "Dynamically generated pages"
  (:use clojure.contrib.pprint)
  (:use clojure.contrib.json)
  (:use clojure.contrib.logging)
  (:require context)
  (:require [clj-http.client :as client])
  (:use hiccup.core))

(defn dump-request
  "Dumps the given Ring request"
  [r]
  (html
    [:html
     [:head
      [:title "Request Dump"]
      [:link {:rel "stylesheet"
              :href (context/link "/stylesheet.css")
              :type "text/css"}]]
     [:body
      [:h1 "The Ring request"]
      [:p "This is a dynamic page containing a dump of the Ring request"]
      [:h2 "Generated by src/pages.clj"]
      [:pre
       (with-out-str (pprint r))]]]))

(defn rooms
  "Display the list of available chats for an api type"
  [api]
  (let [rooms (get
    (client/get (str "http://liftchat.alpha.vmforce.com/api/rooms/" api ".json"))
    :body)]
    (html [:html
           [:head
            [:title (str "Rooms for api" api)]
            [:body
             (map (fn [k] [:li [:a {:href (str "/app/transcript/" api "/" k)} k]]) (keys (read-json rooms false)))
             ]
            ]])
    )
  )

(defn chat-transcript
  "chat transcript for a room in lifchat"
  [api, room]


  (let [rooms (get
    (client/get (str "http://liftchat.alpha.vmforce.com/api/rooms/" api ".json"))
    :body)]
    (let [transcript (read-json (get
      (client/get (str "http://liftchat.alpha.vmforce.com/api/transcript/" api "/" (get (read-json rooms false) room) ".json")) :body) false)]
      (info transcript)

      (map (fn [m] (info m)) (get transcript "messages"))

      (html [:html
             [:head
              [:title (str "Transcript for api" api " room " room)]
              [:body
               (map (fn [m] [:li (str "User:" (get m "user") " Msg:" (get m "msg"))]) (get transcript "messages"))
               ]
              ]])
      )
    )
  )

