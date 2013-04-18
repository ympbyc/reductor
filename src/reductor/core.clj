(ns reductor.core
  (require [reductor.vocab :as vocab])
  (:gen-class))

(defn atom? [x]
  (or (number? x)
      (string? x)
      (keyword? x)))

(declare reduct call)

(defn reduct
  [[x & xs :as quot] stack words]
  (println stack)
  (println "")
  (println x)
  (if (empty? quot)
    {:stack stack
     :words words}

    (cond
     (atom? x) ;=>
     #(reduct xs (cons x stack) words)

     (= x 'define) ;=>
     #(reduct xs
              (drop 3 stack)
              (conj words {(-> stack rest rest first) (first stack)}))

     (symbol? x) ;=>
     #(reduct xs
              (trampoline call (words (keyword x)) stack words)
              words)

     (and (coll? x) (empty? x)) ;=>
     #(reduct xs (cons x stack) words)

     (coll? x) ;=>
     #(reduct xs (cons x stack) words))))


(defn call [x stack words]
  (cond
   (fn? x) ;=>
   (x stack words)

   true ;=>
   #(-> (trampoline reduct x stack words) :stack)))

(def dictionary (conj vocab/prelude {:call (fn [[x & xs] ws]
                                             (trampoline call x xs ws))
                                     :dip (fn [[x y & xs] ws]
                                            (cons y (trampoline call x xs ws)))
                                     :if (fn [[x y z & xs] ws]
                                           (if (= x :true)
                                             (trampoline call z xs ws)
                                             (trampoline call y xs ws)))}))

(defn -main [& _]
  ((trampoline reduct (read) [] dictionary) :stack))
