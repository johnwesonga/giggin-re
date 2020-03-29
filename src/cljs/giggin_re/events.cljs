(ns giggin-re.events
  (:require
   [re-frame.core :as re-frame]
   [giggin-re.db :refer [default-db]]
   [cljs.spec.alpha :as s]))


(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

 ;; now we create an interceptor using `after`
(def check-spec-interceptor (re-frame/after (partial check-and-throw :giggin-re.db/db)))

(re-frame/reg-event-fx
 ::initialize-db
 [check-spec-interceptor]
 (fn [{:keys [__]} _]                  ;; take 2 values from coeffects. Ignore event vector itself.
    {:db default-db}))


(re-frame/reg-event-db
 :add-item
 [check-spec-interceptor]
 (fn [db [_ item-id]]
   (update-in db [:orders item-id] inc)))

(re-frame/reg-event-db
 :remove-item
 [check-spec-interceptor]
 (fn [db [_ item-id]]
   (update db :orders dissoc item-id)))

(re-frame/reg-event-db
 :remove-all
 [check-spec-interceptor]
 (fn [db]
   (update db :orders {})))
