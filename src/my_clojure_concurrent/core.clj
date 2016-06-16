(ns my-clojure-concurrent.core)

(def account1 (ref 100))
(def account2 (ref 100))
(def barrier (java.util.concurrent.CyclicBarrier. 201))

(defn deduct [account n other]
  (if (>= (+ (- @account n) @other) 0)
    (dosync (ensure account) (ensure other))
    (alter account - n)))

(defn -main []
  
  ; test cyclic barrier
  (dotimes [_ 100](dosync (.start (Thread. #(do (.await barrier) (deduct account1 100 account2) (.await barrier))))))
  (dotimes [_ 100](dosync (.start (Thread. #(do (.await barrier) (deduct account2 100 account1) (.await barrier))))))

  (.await barrier)
  (.await barrier)
  
  (println "account1:" @account1)
  (println "account2:" @account2))
