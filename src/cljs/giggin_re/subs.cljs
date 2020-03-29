(ns giggin-re.subs
  (:require
   [re-frame.core :refer [reg-sub subscribe after]]))
  
(reg-sub
 ::orders
 (fn [db _]
   (get-in db [:orders])))

(reg-sub
 ::gigs
 (fn [db _]
   (get-in db [:gigs])))

(reg-sub
 ::id
 (fn [db _]
   (:id db)))

(reg-sub
  ::totals
  :<- [::orders]
  :<- [::gigs]
  (fn [[orders gigs] _]
    (->> orders
         (map (fn [[id quant]] (* quant (get-in gigs [id :price]))))
         (reduce +))))
