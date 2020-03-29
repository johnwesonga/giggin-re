(ns giggin-re.views
  (:require
   [re-frame.core :as re-frame]
   [giggin-re.subs :as subs]
   [reagent.core :as reagent]))

(defn header
    []
    [:header
     [:img.logo {:src "img/giggin-logo.svg" :alt "Giggin Logo"}]])

(defn footer
    []
    [:footer
     [:img.logo {:src "img/giggin-icon.png" :alt "Giggin Icon"}]])

(defn gig-item
  []
  (fn [{:keys [id img title price desc]}]
    [:div.gig {:key id}
     [:img.gig__artwork {:src img :alt title}]
     [:div.gig__body
      [:div.gig__title
       [:div.btn.btn--primary.float--right.tooltip
        {:data-tooltip "Add to Order"
         :on-click #(re-frame/dispatch [:add-item id])}
        [:i.icon.icon--plus]] title]
      [:p.gig__price price]
      [:p.gig__desc desc]]]))

(defn label-edit
  [item item-value]
  (let [edit? (reagent/atom false)
        form (reagent/atom item)]
    (fn []
      (if @edit?
        [:input {:type "text"
                 :value @form
                 :auto-focus true
                 :size 3
                 :on-change #(reset! form (-> % .-target .-value))
                 :on-blur #(reset! edit? false)}]
        [:div.price {:on-click #(reset! edit? true)}
         @form]))))

(defn gigs
  []
  (let [gigs @(re-frame/subscribe [::subs/gigs])]
    [:main
     [:div.gigs
      (for [gig (vals gigs)]
        ^{:key (:id gig)} [gig-item gig])]]))

(defn orders
  []
  (let [orders @(re-frame/subscribe [::subs/orders])
              gigs @(re-frame/subscribe [::subs/gigs])]

    [:aside
     (if (empty? orders)
       [:div.empty
        [:div.title "You don't have any orders"
         [:div.subtitle "Click on a + to add an order"]]]
      [:div.order
         [:div.body
          (for [[id quant] orders]
            [:div.item {:key id}
             [:div.img
              [:img {:src (get-in gigs [id :img])
                         :alt (get-in gigs [id :title])}]]
             [:div.content
               [:p.title (str (get-in gigs [id :title]) " \u00D7 ") [label-edit quant]]]


             [:div.action
                  [:div.price (* (get-in gigs [id :price]) quant)]
                  [:button.btn.btn--link.tooltip
                   {:data-tooltip "Remove"
                    :on-click #(re-frame/dispatch [:remove-item id])}
                   [:i.icon.icon--cross]]]])
          [:div.total
            [:hr]
            [:div.item
             [:div.content "Total"]

             [:div.action
              [:div.price @(re-frame/subscribe [::subs/totals])]]
             [:button.btn.btn--link.tooltip
              {:data-tooltip "Remove All"
               :on-click #(re-frame/dispatch [:remove-all])};;])}
              [:i.icon.icon--delete]]]]]])]))


(defn main-panel []
  [:div.container
   [header]
   [gigs]
   [orders]
   [footer]])
