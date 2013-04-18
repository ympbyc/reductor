(ns reductor.vocab)

(def prelude {:swap (fn [[x y & xs] ws]
                      (concat (list y x) xs))
              :rot (fn [[x y z & xs] ws]
                     (concat (list z x y) xs))
              :dup (fn [[x & xs] ws]
                     (concat (list x x) xs))
              :over (fn [[x y & xs] ws]
                      (concat [y x y] xs))
              :tuck (fn [[x y & xs] ws]
                      (concat [x y x] xs))
              :drop (fn [[x & xs] _] xs)
              :. (fn [[x & _] _] x)
              :car (fn [[x & xs] ws]
                     (cons (first x) xs))
              :cdr (fn [[x & xs] ws]
                     (cons (rest x) xs))
              :cons (fn [[x y & xs] ws]
                      (cons (cons x y) xs))

              :empty? (fn [[x & xs] ws]
                        (cons (if (empty? x) :true :false)
                              xs))

              :* (fn [[x y & xs] _] (cons (* x y) xs))})
