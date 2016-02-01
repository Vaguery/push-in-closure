(ns push.types.base.integer
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [push.util.code-wrangling :as fix])
  (:require [push.instructions.aspects :as aspects])
  (:require [clojure.math.numeric-tower :as math])
  )


;; arithmetic


(defn sign [i] (compare i 0))


(def integer-abs (t/simple-1-in-1-out-instruction
  "`:integer-abs` pushes the absolute value of the top `:integer` item"
  :integer "abs" 'math/abs))


(def integer-add (t/simple-2-in-1-out-instruction
  "`:integer-add` pops the top two `:integer` items, and pushes their sum"
  :integer "add" '+'))


(def integer-dec (t/simple-1-in-1-out-instruction
  "`:integer-dec` subtracts 1 from the top `:integer` item"
  :integer "dec" 'dec))


(def integer-divide
  (core/build-instruction
    integer-divide
    "`:integer-divide` pops the top two `:integer` values (call them `denominator` and `numerator`, respectively). If `denominator` is 0, it replaces the two `:integer` values; if not, it pushes their (integer) quotient."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :integer :as :denominator)
    (d/consume-top-of :integer :as :numerator)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %1 (bigint (/ %2 %1))) :as :quotient)
    (d/push-these-onto :integer [:replacement :quotient])
    (d/calculate [:denominator]
      #(if (zero? %1) ":integer-divide 0 denominator" nil) :as :warning)
    (d/record-an-error :from :warning)

    ))


(def integer-inc (t/simple-1-in-1-out-instruction
  "`:integer-inc` adds 1 to the top `:integer` item"
  :integer "inc" 'inc))


(def integer-mod
  (core/build-instruction
    integer-mod
    "`:integer-mod` pops the top two `:integer` values (call them `denominator` and `numerator`, respectively). If `denominator` is 0, it replaces the two `:integer` values; if not, it pushes `(mod numerator denominator)`."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :integer :as :denominator)
    (d/consume-top-of :integer :as :numerator)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (d/calculate [:denominator :numerator] #(fix/safe-mod %2 %1) :as :remainder)
    (d/push-these-onto :integer [:replacement :remainder])
    (d/calculate [:denominator]
      #(if (zero? %1) ":integer-mod 0 denominator" nil) :as :warning)
    (d/record-an-error :from :warning)
    ))


(def integer-multiply (t/simple-2-in-1-out-instruction
  "`:integer-multiply` pops the top two `:integer` items, and pushes their product"
  :integer  "multiply" '*'))


(def integer-sign (t/simple-1-in-1-out-instruction
  "`:integer-sign` examines the top `:integer` item, and pushes -1 if negative, 0 if zero, and 1 if positive"
  :integer  "sign" 'sign))


(def integer-subtract (t/simple-2-in-1-out-instruction
  "`:integer-subtract` pops the top two `:integer` items, and pushes their difference, subtracting the top from the second" :integer "subtract" '-'))


;; conversion


(def boolean->integer
  (core/build-instruction
    boolean->integer
    "`:boolean->integer` pops the top `:boolean`. If it's `true`, it pushes 1; if `false`, it pushes 0."
    :tags #{:base :conversion}
    (d/consume-top-of :boolean :as :arg1)
    (d/calculate [:arg1] #(if %1 1 0) :as :logic)
    (d/push-onto :integer :logic)))


(def boolean->signedint
  (core/build-instruction
    boolean->signedint
    "`:boolean->signedint` pops the top `:boolean`. If it's `true`, it pushes 1; if `false`, it pushes -1."
    :tags #{:base :conversion}
    (d/consume-top-of :boolean :as :arg1)
    (d/calculate [:arg1] #(if %1 1 -1) :as :logic)
    (d/push-onto :integer :logic)))


(def float->integer
  (core/build-instruction
    float->integer
    "`:float->integer` pops the top `:float` item, and converts it to an `:integer` value (using CLojure's `bigint` function)"
    :tags #{:numeric :base :conversion}
    (d/consume-top-of :float :as :arg1)
    (d/calculate [:arg1] #(bigint %1) :as :int)
    (d/push-onto :integer :int)))


(def char->integer
  (core/build-instruction
    char->integer
    "`:char->integer` pops the top `:char` item, and converts it to an (integer) index"
    :tags #{:base :conversion}
    (d/consume-top-of :char :as :arg1)
    (d/calculate [:arg1] #(long %1) :as :int)
    (d/push-onto :integer :int)))


(def string->integer
  (core/build-instruction
    string->integer
    "`:string->integer` pops the top `:string` item, and applies `Long/parseLong` to attempt to convert it to a fixed-point value. If successful (that is, if no exception is raised), the result is pushed to `:integer`"
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :string :as :arg)
    (d/calculate [:arg] 
      #(try (Long/parseLong %1) (catch NumberFormatException _ nil))
        :as :result)
    (d/push-onto :integer :result)))



(def integer-type
  ( ->  (t/make-type  :integer
                      :recognizer integer?
                      :attributes #{:numeric})
        aspects/make-visible 
        aspects/make-equatable
        aspects/make-comparable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-returnable
        aspects/make-storable
        (t/attach-instruction , integer-abs)
        (t/attach-instruction , integer-add)
        (t/attach-instruction , integer-dec)
        (t/attach-instruction , integer-divide)
        (t/attach-instruction , boolean->integer)
        (t/attach-instruction , char->integer)
        (t/attach-instruction , float->integer)
        (t/attach-instruction , string->integer)
        (t/attach-instruction , integer-inc)
        (t/attach-instruction , integer-mod)
        (t/attach-instruction , integer-multiply)
        (t/attach-instruction , integer-sign)
        (t/attach-instruction , boolean->signedint)
        (t/attach-instruction , integer-subtract)
        ))

