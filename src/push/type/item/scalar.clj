(ns push.type.item.scalar
  (:require [push.instructions.core :as i]
            [push.type.core :as t]
            [push.instructions.dsl :as d]
            [push.util.code-wrangling :as fix]
            [push.instructions.aspects :as aspects]
            [clojure.math.numeric-tower :as nt]
            [push.util.numerics :as math]
            [push.util.exotics :as x]
            [push.type.definitions.complex :as complex]
            ))


;; INSTRUCTIONS



(def scalar-abs (i/simple-1-in-1-out-instruction
  "`:scalar-abs` pushes the abs of the top `:scalar` item"
  :scalar "abs" 'nt/abs))



(def scalar-add
  (i/build-instruction
    scalar-add
    "`:scalar-add` pops the top two `:scalar` values and pushes their sum to `:scalar`. If there is a runtime error (for example if the arguments are a rational and a `bigdec` value, or the result is `NaN`) an `:error` is pushed."

    (d/consume-top-of :scalar :as :arg2)
    (d/consume-top-of :scalar :as :arg1)
    (d/calculate [:arg1 :arg2] +' :as :prelim)
    (d/calculate [:prelim] #(if %1 (Double/isNaN %1) false) :as :nan)
    (d/calculate [:nan :prelim] #(when-not %1 %2) :as :sum)
    (d/return-item :sum)
    (d/calculate [:nan] #(when %1 ":scalar-add produced NaN") :as :warning)
    (d/record-an-error :from :warning)
    ))



(def scalar-arccosine
  (i/build-instruction
    scalar-arccosine
    "`:scalar-arccosine` pops the top `:scalar` value, and if it is between -1.0 and 1.0 it returns the arccos(theta), for theta between 0.0 and π; otherwise it consumes the argument and adds an :error"

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(or (> %1 1) (< %1 -1)) :as :bad-arg?)
    (d/calculate [:bad-arg? :arg] #(when-not %1 (Math/acos %2)) :as :result)
    (d/calculate [:bad-arg?] #(when %1 ":scalar-arccosine bad argument") :as :warning)
    (d/return-item :result)
    (d/record-an-error :from :warning)))



(def scalar-arcsine
  (i/build-instruction
    scalar-arcsine
    "`:scalar-arcsine` pops the top `:scalar` value, and if it is between -1.0 and 1.0 it returns the asin(theta), for theta between 0.0 and π; otherwise it consumes the argument and adds an :error"

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(or (> %1 1) (< %1 -1)) :as :bad-arg?)
    (d/calculate [:bad-arg? :arg] #(when-not %1 (Math/asin %2)) :as :result)
    (d/calculate [:bad-arg?] #(when %1 ":scalar-arcsine bad argument") :as :warning)
    (d/return-item :result)
    (d/record-an-error :from :warning)))



(def scalar-arctangent (i/simple-1-in-1-out-instruction
  ":`scalar-arctangent` pops the top `:scalar` and pushes atan(theta) (assuming the angle lies between ±π/2) to `:scalar`"
  :scalar "arctangent" #(Math/atan %)))



(def scalar-ceiling (i/simple-1-in-1-out-instruction
  "`:scalar-ceiling` pops the top `:scalar` value, and pushes the next-largest integer value"
  :scalar "ceiling" 'nt/ceil))



(def scalar-cosine (i/simple-1-in-1-out-instruction
  "`:scalar-cosine` pushes the cosine of the top `:scalar` item, read as radians"
  :scalar "cosine" #(Math/cos %)))



(def scalar-dec (i/simple-1-in-1-out-instruction
  ":`scalar-dec` reduces the top `:scalar` value by 1"
  :scalar "dec" 'dec'))



(def scalar-divide
  (i/build-instruction
    scalar-divide
    "`:scalar-divide` pops the top two `:scalar` values (call them `denominator` and `numerator`, respectively). If `denominator` is 0 or the result is `NaN`, it consumes the arguments but pushes an `:error`; if not, it pushes their quotient."

    (d/consume-top-of :scalar :as :denominator)
    (d/consume-top-of :scalar :as :numerator)
    (d/calculate [:numerator :denominator] #(/ %1 %2) :as :quotient)
    (d/calculate [:quotient] #(if %1 (Double/isNaN %1) false) :as :nan)
    (d/calculate [:nan :quotient] #(when-not %1 %2) :as :quotient)
    (d/return-item :quotient)
    (d/calculate [:nan] #(when %1 ":scalar-divide produced NaN") :as :warn2)
    (d/record-an-error :from :warn2)
    ))



(def scalar-E
  (i/build-instruction
    scalar-E
    "`:scalar-E` pushes the value E to the :scalar stack."

    (d/calculate [] #(Math/E) :as :e)
    (d/return-item :e)))




(def scalar-infinite?
  (i/build-instruction
    scalar-infinite?
    "`:scalar-infinite?` pops the top `:scalar` and pushes `true` or `false` depending on whether it is (positive or negative) infinite."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(math/infinite? %1) :as :result)
    (d/return-item :result)))




(def scalar-floor (i/simple-1-in-1-out-instruction
  "`:scalar-floor` pops the top `:scalar` value, and pushes the next-smaller integer value"
  :scalar "floor" 'nt/floor))



(def scalar-fractional
  (i/build-instruction
    scalar-fractional
    "`:scalar-fractional` pushes just the fractional part of the top `:scalar`."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(mod (nt/abs %1) 1) :as :result)
    (d/return-item :result)))



(def scalar-inc (i/simple-1-in-1-out-instruction
  "`:scalar-inc` adds 1 to the top `:scalar` item"
  :scalar "inc" 'inc'))



(def scalar-ln
  (i/build-instruction
    scalar-ln
    "`:scalar-ln` pops the top `:scalar` value. If it is a strictly positive (non-zero) value, its natural logarithm is pushed; otherwise, the argument is consumed but an error is pushed to the :error stack."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #( (complement pos?) %1) :as :bad-arg?)
    (d/calculate [:bad-arg? :arg] #(when-not %1 (Math/log %2)) :as :result)
    (d/calculate [:bad-arg?] #(when %1 ":scalar-ln bad argument") :as :warning)
    (d/return-item :result)
    (d/record-an-error :from :warning)))



(def scalar-ln1p
  (i/build-instruction
    scalar-ln1p
    "`:scalar-ln1p` pops the top `:scalar` value. If it is a value greater than -1.0, `(Math/log1p x)` is pushed; otherwise, it consumes the argument and an error is pushed to the :error stack."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg]
      #(<= %1 -1) :as :bad-arg?)
    (d/calculate [:bad-arg? :arg] #(when-not %1 (Math/log1p %2)) :as :result)
    (d/calculate [:bad-arg?] #(when %1 ":scalar-ln1p bad argument") :as :warning)
    (d/return-item :result)
    (d/record-an-error :from :warning)))




(def scalar-log10
  (i/build-instruction
    scalar-log10
    "`:scalar-log10` pops the top `:scalar` value. If it is a strictly positive (non-zero) value, its base-10 logarithm is pushed; otherwise, the argument is consumed but an error is pushed to the :error stack."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg]
      #( (complement pos?) %1) :as :bad-arg?)
    (d/calculate [:bad-arg? :arg] #(when-not %1 (Math/log10 %2)) :as :result)
    (d/calculate [:bad-arg?] #(when %1 ":scalar-log10 bad argument") :as :warning)
    (d/return-item :result)
    (d/record-an-error :from :warning)))



(def scalar-modulo
  (i/build-instruction
    scalar-modulo
    "`:scalar-modulo` pops the top two `:scalar` values (call them `denominator` and `numerator`, respectively). If `denominator` is zero, it discards the arguments and produces an `:error`. If the result would be `NaN`, it pushes an `:error`. Otherwise, it pushes `(mod numerator denominator)`."

    (d/consume-top-of :scalar :as :denominator)
    (d/consume-top-of :scalar :as :numerator)
    (d/calculate [:numerator :denominator] mod :as :remainder)
    (d/calculate [:remainder] #(if %1 (Double/isNaN %1) false) :as :nan)
    (d/calculate [:nan :remainder] #(when-not %1 %2) :as :remainder)
    (d/return-item :remainder)
    (d/calculate [:nan] #(when %1 ":scalar-modulo produced NaN") :as :warn2)
    (d/record-an-error :from :warn2)
    ))




(def scalar-multiply
  (i/build-instruction
    scalar-multiply
    "`:scalar-multiply` pops the top two `:scalar` values and pushes their product to `:scalar`. If there is a runtime error (for example if the arguments are a rational and a `bigdec` value, or the result is `NaN`) an `:error` is pushed."

    (d/consume-top-of :scalar :as :arg2)
    (d/consume-top-of :scalar :as :arg1)
    (d/calculate [:arg1 :arg2] *' :as :prelim)
    (d/calculate [:prelim] #(if %1 (Double/isNaN %1) false) :as :nan)
    (d/calculate [:nan :prelim] #(when-not %1 %2) :as :product)
    (d/calculate [:nan] #(when %1 ":scalar-multiply produced NaN") :as :warning)
    (d/return-item :product)
    (d/record-an-error :from :warning)
    ))



(def scalar-π
  (i/build-instruction
    scalar-π
    "`:scalar-π` pushes the value π to the :scalar stack."

    (d/calculate [] #(Math/PI) :as :pi)
    (d/return-item :pi)))



(defn oversized-for-scalar-power?
  "Takes two numeric arguments (`base` and `exponent`), and calculates `(*' exponent (log base))`. If `base` is a rational, it uses `(*' exponent (count (str base)))` instead. If the result is over 2^15, it returns `true`."
  [base exponent]
  (let [base-size (if (rational? base) (count (str base)) (Math/log base))]
    (> (nt/abs (*' exponent base-size)) 32768)))


(def scalar-power
  (i/build-instruction
    scalar-power
    "`:scalar-power` pops the top two `:scalar` values (call them `exponent` and `base` respectively). It calculates `(numeric-tower/expt base exponent)`. If the absolute value of the product of the exponent and `(Math/log base)` is more than 2^16, no result is returned; instead, an `:error` is pushed. Unlike most `:scalar` instructions, if the value is positive or negative `Infinity`, an error is also produced."

    (d/consume-top-of :scalar :as :exp)
    (d/consume-top-of :scalar :as :base)
    (d/calculate [:base :exp]
      #(cond (zero? %1) false
             (math/infinite? %1) false
             (math/infinite? %2) false
             (oversized-for-scalar-power? %1 %2) true
             :else false) :as :oversized?)
    (d/calculate [:base :exp :oversized?] #(when-not %3 (nt/expt %1 %2)) :as :prelim)
    (d/calculate [:prelim]
      #(or (nil? %1)
           (math/infinite? %1)
           (Double/isNaN %1)
           ) :as :bad-result)
    (d/calculate [:bad-result :prelim] #(when-not %1 %2) :as :result)
    (d/calculate [:bad-result] #(when %1 ":scalar-power out of bounds") :as :warning)
    (d/return-item :result)
    (d/record-an-error :from :warning)
  ))



(def scalar-reciprocal
  (i/build-instruction
    scalar-reciprocal
    "`:scalar-reciprocal` pushes the reciprocal of the top `:scalar`."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(/ 1 %1) :as :reciprocal)
    (d/return-item :reciprocal)))



(def scalar-round (i/simple-1-in-1-out-instruction
  "`:scalar-round` pops the top `:scalar` value, and pushes the closest integer value"
  :scalar "round" 'nt/round))


(def scalar-sign (i/simple-1-in-1-out-instruction
  "`:scalar-sign` pops the top `:scalar` item and pushes -1 if it's negative, 0 if it's zero, and 1 if it's positive"
  :scalar "sign" #(compare %1 0)))



(def scalar-sine (i/simple-1-in-1-out-instruction
  "`:scalar-sine` pushes the sine of the top `:scalar` item, read as an angle in radians"
  :scalar "sine" #(Math/sin %1)))



(def scalar-sqrt
  (i/build-instruction
    scalar-sqrt
    "`:scalar-sqrt` pops the top `:scalar` value. If it's not negative, its square root is pushed to `:scalar`; if it is complex, then a `:complex` is pushed; otherwise, the argument is consumed and an error is pushed to the `:error` stack."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] neg? :as :complex?)
    (d/calculate [:complex? :arg]
      #(if %1 (complex/complexify 0 (nt/sqrt (nt/abs %2))) (nt/sqrt %2)) :as :result)
    (d/return-item :result)))



(def scalar-subtract
  (i/build-instruction
    scalar-subtract
    "`:scalar-subtract` pops the top two `:scalar` values and pushes their difference to `:scalar`, subtracting the top item from the second. If there is a runtime error (for example if the arguments are a rational and a `bigdec` value, or the result is `NaN`) an `:error` is pushed."

    (d/consume-top-of :scalar :as :arg2)
    (d/consume-top-of :scalar :as :arg1)
    (d/calculate [:arg1 :arg2] -' :as :prelim)
    (d/calculate [:prelim] #(if %1 (Double/isNaN %1) false) :as :nan)
    (d/calculate [:nan :prelim] #(when-not %1 %2) :as :diff)
    (d/calculate [:nan] #(when %1 ":scalar-subtract produced NaN") :as :warning)
    (d/return-item :diff)
    (d/record-an-error :from :warning)
    ))



(def scalar-tangent
  (i/build-instruction
    scalar-tangent
    "`:scalar-tangent` pops the top `:scalar` value and calculates tan(theta). If the result is a non-infinite number, it pushes that to :scalar; otherwise, it consumes the argument and pushes an :error"

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg]
      #(Double/isNaN (Math/tan %1)) :as :bad-arg?)
    (d/calculate [:bad-arg? :arg] #(when-not %1 (Math/tan %2)) :as :result)
    (d/calculate [:bad-arg?] #(when %1 ":scalar-tangent bad argument") :as :warning)
    (d/return-item :result)
    (d/record-an-error :from :warning)))




;; EXOTICS



(def integer-totalistic3
  (i/build-instruction
    integer-totalistic3
    "`:integer-totalistic3` pops the top `:scalar`. It is turned into an integer using `(bigint x)`. Then each digit is replaced by the sum of its current value and the two neighbors to the right, modulo 10, wrapping cyclically around the number. An infinite argument produces a result of 0."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(if (math/infinite? %1) 0 %1) :as :safe)
    (d/calculate [:safe] #(x/rewrite-digits (bigint %1) 3) :as :result)
    (d/return-item :result)))



;; SCALING



(def scalar-few
  (i/build-instruction
    scalar-few
    "`:scalar-few` pops the top `:scalar` value, and pushes `(rem x 10)`."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(math/few %1) :as :scaled)
    (d/return-item :scaled)
    ))


(def scalar-lots
  (i/build-instruction
    scalar-lots
    "`:scalar-lots` pops the top `:scalar` value, and pushes `(rem x 10000)`."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(math/lots %1) :as :scaled)
    (d/return-item :scaled)))



(def scalar-many
  (i/build-instruction
    scalar-many
    "`:scalar-many` pops the top `:scalar` value, and pushes `(rem x 1000)`."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(math/many %1) :as :scaled)
    (d/return-item :scaled)))



(def scalar-bunch
  (i/build-instruction
    scalar-bunch
    "`:scalar-bunch` pops the top `:scalar` value, and pushes `(rem x 100)`."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(math/bunch %1) :as :scaled)
    (d/return-item :scaled)))




; ;; CONVERSIONS



(def boolean->float
  (i/build-instruction
    boolean->float
    "`:boolean->float` pops the top `:boolean` value; if it is `true`, it pushes 1.0, and if `false` it pushes `0.0`"

    (d/consume-top-of :boolean :as :arg)
    (d/calculate [:arg] #(if %1 1.0 0.0) :as :result)
    (d/return-item :result)))



(def boolean->signedfloat
  (i/build-instruction
    boolean->signedfloat
    "`:boolean->signedfloat` pops the top `:boolean` value; if it is `true`, it pushes 1.0, and if `false` it pushes `-1.0`"

    (d/consume-top-of :boolean :as :arg)
    (d/calculate [:arg] #(if %1 1.0 -1.0) :as :result)
    (d/return-item :result)))



(def boolean->integer
  (i/build-instruction
    boolean->integer
    "`:boolean->integer` pops the top `:boolean`. If it's `true`, it pushes 1; if `false`, it pushes 0."

    (d/consume-top-of :boolean :as :arg1)
    (d/calculate [:arg1] #(if %1 1 0) :as :logic)
    (d/return-item :logic)))



(def boolean->signedint
  (i/build-instruction
    boolean->signedint
    "`:boolean->signedint` pops the top `:boolean`. If it's `true`, it pushes 1; if `false`, it pushes -1."

    (d/consume-top-of :boolean :as :arg1)
    (d/calculate [:arg1] #(if %1 1 -1) :as :logic)
    (d/return-item :logic)))



(def char->integer
  (i/build-instruction
    char->integer
    "`:char->integer` pops the top `:char` item, and converts it to an (integer) index"

    (d/consume-top-of :char :as :arg1)
    (d/calculate [:arg1] long :as :int)
    (d/return-item :int)))


;; SUBTYPES


(def scalar-bigdec?
  (i/build-instruction
    scalar-bigdec?
    "`:scalar-bigdec?` pops the top `:scalar` item, and pushes `true` if it is a Clojure bigdec"

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(instance? java.math.BigDecimal %1) :as :result)
    (d/return-item :result)))



(def scalar-float?
  (i/build-instruction
    scalar-float?
    "`:scalar-float?` pops the top `:scalar` item, and pushes `true` if it is a Clojure float or is a BigDecimal"

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg]
      #(or (float? %1) (instance? java.math.BigDecimal %1)) :as :result)
    (d/return-item :result)))



(def scalar-integer?
  (i/build-instruction
    scalar-integer?
    "`:scalar-integer?` pops the top `:scalar` item, and pushes `true` if it is a Clojure integer"

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] integer? :as :result)
    (d/return-item :result)))



(def scalar-ratio?
  (i/build-instruction
    scalar-ratio?
    "`:scalar-ratio?` pops the top `:scalar` item, and pushes `true` if it is a ratio"

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] ratio? :as :result)
    (d/return-item :result)))



(def scalar-type
  ( ->  (t/make-type  :scalar
                      :recognized-by number?
                      :attributes #{:numeric})
        aspects/make-set-able
        aspects/make-comparable
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible
        (t/attach-instruction , char->integer)
        (t/attach-instruction , integer-totalistic3)
        (t/attach-instruction , scalar-abs)
        (t/attach-instruction , scalar-add)
        (t/attach-instruction , scalar-arccosine)
        (t/attach-instruction , scalar-arcsine)
        (t/attach-instruction , scalar-arctangent)
        (t/attach-instruction , scalar-bigdec?)
        (t/attach-instruction , scalar-bunch)
        (t/attach-instruction , scalar-ceiling)
        (t/attach-instruction , scalar-cosine)
        (t/attach-instruction , scalar-dec)
        (t/attach-instruction , scalar-divide)
        (t/attach-instruction , scalar-E)
        (t/attach-instruction , scalar-few)
        (t/attach-instruction , scalar-float?)
        (t/attach-instruction , scalar-floor)
        (t/attach-instruction , scalar-fractional)
        (t/attach-instruction , scalar-inc)
        (t/attach-instruction , scalar-infinite?)
        (t/attach-instruction , scalar-integer?)
        (t/attach-instruction , scalar-ln)
        (t/attach-instruction , scalar-ln1p)
        (t/attach-instruction , scalar-lots)
        (t/attach-instruction , scalar-log10)
        (t/attach-instruction , scalar-many)
        (t/attach-instruction , scalar-modulo)
        (t/attach-instruction , scalar-multiply)
        (t/attach-instruction , scalar-π)
        (t/attach-instruction , scalar-power)
        (t/attach-instruction , scalar-ratio?)
        (t/attach-instruction , scalar-reciprocal)
        (t/attach-instruction , scalar-round)
        (t/attach-instruction , scalar-sign)
        (t/attach-instruction , scalar-sine)
        (t/attach-instruction , scalar-sqrt)
        (t/attach-instruction , scalar-subtract)
        (t/attach-instruction , scalar-tangent)
        (t/attach-instruction , boolean->float)
        (t/attach-instruction , boolean->signedfloat)
        (t/attach-instruction , boolean->integer)
        (t/attach-instruction , boolean->signedint)
        ))
