(ns push.types.base.string
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [clojure.string :as strings])
  )


;; string-specific
; assemblers and disassemblers

; exec_string_iterate
; string_contains
; string_containschar


(defn simple-item-to-string-instruction
  "returns a standard arity-1 function, which moves the string representation of the top item from the named stack to the :string stack"
  [type]
  (let [stackname (keyword type)
        instruction-name (str "string-from" (name stackname))]
    (eval (list
      'core/build-instruction
      instruction-name
      :tags #{:string :base :conversion}
      `(d/consume-top-of ~stackname :as :arg)
      '(d/calculate [:arg] #(pr-str %1) :as :printed)
      '(d/push-onto :string :printed)))))



(def string-concat (t/simple-2-in-1-out-instruction :string "concat" 'str))


(def string-conjchar
  (core/build-instruction
    string-conjchar
    :tags #{:string :base}
    (d/consume-top-of :char :as :c)
    (d/consume-top-of :string :as :s)
    (d/calculate [:s :c] #(strings/join (list %1 %2)) :as :longer)
    (d/push-onto :string :longer)))


(def string-butlast (t/simple-1-in-1-out-instruction :string "butlast"
                      #(clojure.string/join (butlast %1))))


(def string-emptystring? (t/simple-1-in-predicate :string "emptystring?" empty?))


(def string-first
  (core/build-instruction
    string-first
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] #(first %1) :as :c)
    (d/push-onto :char :c)))


(def string-fromboolean (simple-item-to-string-instruction :boolean))
(def string-fromcode    (simple-item-to-string-instruction :code   ))
(def string-fromexec    (simple-item-to-string-instruction :exec   ))
(def string-fromfloat   (simple-item-to-string-instruction :float  ))
(def string-frominteger (simple-item-to-string-instruction :integer))


(def string-indexofchar
  (core/build-instruction
    string-indexofchar
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/calculate [:s :c] #(.indexOf %1 (int %2)) :as :where)
    (d/push-onto :integer :where)))


(def string-last
  (core/build-instruction
    string-last
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] #(last %1) :as :c)
    (d/push-onto :char :c)))


(def string-length
  (core/build-instruction
    string-length
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] #(count %1) :as :len)
    (d/push-onto :integer :len)))


(def string-nth
  (core/build-instruction
    string-nth
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :integer :as :where)
    (d/calculate [:s :where] #(if (empty? %1) 0 (mod %2 (count %1))) :as :idx)
    (d/calculate [:s :idx] #(if (empty? %1) nil (nth %1 %2)) :as :result)
    (d/push-onto :char :result)))


(def string-occurrencesofchar
  (core/build-instruction
    string-occurrencesofchar
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/calculate [:s :c] #(get (frequencies %1) %2 0) :as :count)
    (d/push-onto :integer :count)))


(def string-removechar
  (core/build-instruction
    string-removechar
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/calculate [:s :c] #(clojure.string/join (remove #{%2} %1)) :as :gone)
    (d/push-onto :string :gone)))


(def string-replace
  (core/build-instruction
    string-replace
    :tags #{:string :base}
    (d/consume-top-of :string :as :s3)
    (d/consume-top-of :string :as :s2)
    (d/consume-top-of :string :as :s1)
    (d/calculate [:s1 :s2 :s3] #(strings/replace %1 %2 %3) :as :different)
    (d/push-onto :string :different)))


(def string-replacechar
  (core/build-instruction
    string-replacechar
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c1)
    (d/consume-top-of :char :as :c2)
    (d/calculate [:s :c1 :c2] #(strings/replace %1 %2 %3) :as :different)
    (d/push-onto :string :different)))


(def string-replacefirst
  (core/build-instruction
    string-replacefirst
    :tags #{:string :base}
    (d/consume-top-of :string :as :s3)
    (d/consume-top-of :string :as :s2)
    (d/consume-top-of :string :as :s1)
    (d/calculate [:s1 :s2 :s3] #(strings/replace-first %1 %2 %3) :as :different)
    (d/push-onto :string :different)))


(def string-replacefirstchar
  (core/build-instruction
    string-replacefirstchar
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c1)
    (d/consume-top-of :char :as :c2)
    (d/calculate [:s :c1 :c2] #(strings/replace-first %1 %2 %3) :as :different)
    (d/push-onto :string :different)))


(def string-rest (t/simple-1-in-1-out-instruction :string "rest"
                      #(clojure.string/join (rest %1))))


(def string-reverse (t/simple-1-in-1-out-instruction :string "reverse" 'strings/reverse))


(def string-setchar
  (core/build-instruction
    string-setchar
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/consume-top-of :integer :as :where)
    (d/calculate [:s :where] #(if (empty? %1) 0 (mod %2 (count %1))) :as :idx)
    (d/calculate [:s :idx :c] #(strings/join (assoc (vec %1) %2 %3)) :as :result)
    (d/push-onto :string :result)))



(def string-shatter   ;; string-parse-into-chars
  (core/build-instruction
    string-shatter
    :tags #{:string :base}
    (d/consume-top-of :string :as :s1)
    (d/consume-stack :string :as :old)
    (d/calculate [:s1] #(reverse (map str (seq %1))) :as :letters)
    (d/calculate [:old :letters] #(into %1 %2) :as :new)
    (d/replace-stack :string :new)))


(def string-solid? (t/simple-1-in-predicate :string "solid?"
                          #(boolean (re-matches #"\S+" %1))))


(def string-splitonspaces
  (core/build-instruction
    string-splitonspaces
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-stack :string :as :old)
    (d/calculate [:s] #(strings/split %1 #"\s+") :as :words)
    (d/calculate [:words :old] #(into %2 (reverse %1)) :as :new)
    (d/replace-stack :string :new)))


(def string-spacey? (t/simple-1-in-predicate :string "spacey?"
                          #(boolean (re-matches #"\s+" %1))))


(def string-substring
  (core/build-instruction
    string-substring
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :integer :as :a)
    (d/consume-top-of :integer :as :b)
    (d/calculate [:s :a] #(min (count %1) (max 0 %2)) :as :cropped-a)
    (d/calculate [:s :b] #(min (count %1) (max 0 %2)) :as :cropped-b)
    (d/calculate [:s :cropped-a :cropped-b]
        #(subs %1 (min %2 %3) (max %2 %3)) :as :result)
    (d/push-onto :string :result)))



(def string-take
  (core/build-instruction
    string-take
    :tags #{:string :base}
    (d/consume-top-of :string :as :s1)
    (d/consume-top-of :integer :as :where)
    (d/calculate [:s1 :where] #(if (empty? %1) 0 (mod %2 (count %1))) :as :idx)
    (d/calculate [:s1 :idx] #(strings/join (take %2 %1)) :as :leftovers)
    (d/push-onto :string :leftovers)))


(def classic-string-type
  ( ->  (t/make-type  :string
                      :recognizer string?
                      :attributes #{:string :base})
        t/make-visible 
        t/make-equatable
        t/make-comparable
        t/make-movable
        (t/attach-instruction , string-butlast)
        (t/attach-instruction , string-concat)
        (t/attach-instruction , string-conjchar)
        (t/attach-instruction , string-emptystring?)
        (t/attach-instruction , string-first)
        (t/attach-instruction , string-fromboolean)
        (t/attach-instruction , string-fromcode)
        (t/attach-instruction , string-fromexec)
        (t/attach-instruction , string-frominteger)
        (t/attach-instruction , string-fromfloat)
        (t/attach-instruction , string-indexofchar)
        (t/attach-instruction , string-last)
        (t/attach-instruction , string-length)
        (t/attach-instruction , string-nth)
        (t/attach-instruction , string-occurrencesofchar)
        (t/attach-instruction , string-removechar)
        (t/attach-instruction , string-replace)
        (t/attach-instruction , string-replacechar)
        (t/attach-instruction , string-replacefirst)
        (t/attach-instruction , string-replacefirstchar)
        (t/attach-instruction , string-rest)
        (t/attach-instruction , string-reverse)
        (t/attach-instruction , string-setchar)
        (t/attach-instruction , string-shatter)
        (t/attach-instruction , string-solid?)
        (t/attach-instruction , string-splitonspaces)
        (t/attach-instruction , string-spacey?)
        (t/attach-instruction , string-substring)
        (t/attach-instruction , string-take)
        ))

