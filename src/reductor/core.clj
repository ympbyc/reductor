(ns reductor.core
  (require [reductor.vocab :as vocab])
  (:gen-class))

(defn atom? [x]
  (or (number? x)
      (string? x)
      (keyword? x)))

(declare reduct call dictionary)

(defn visualize [stack]
  (apply str (map (fn [_] "|") stack)))

(defn reduct
  [[x & xs :as quot] stack words]
  ;;(println (visualize stack))
  ;;(println "")
  ;;(println x)
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

     (= x 'load) ;=>
     #(reduct xs
              (drop 1 stack)
              ((trampoline reduct
                           (read-string (slurp (first stack)))
                           [] dictionary) :words))

     (symbol? x) ;=>
     (let [word (words (keyword x))
           _  (if (nil? word) (throw (Throwable. (str "unresoleved symbol " x))))
           stk (trampoline call word stack words)]
      (if (empty? xs)
        {:stack stk :words words} ;tail call?
        #(reduct xs stk words)))

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
                                           (if (= z :true)
                                             (trampoline call y xs ws)
                                             (trampoline call x xs ws)))}))

(defn -main [& _]
  ((trampoline reduct (read) [] dictionary) :stack))
