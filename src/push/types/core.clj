(ns push.types.core
  (:require [push.interpreter.interpreter-core :as i])
  (:require [push.instructions.instructions-core :as core])
  (:require [push.instructions.dsl :as dsl]))


(defrecord PushType [stackname recognizer attributes instructions])


(defn make-type
  "Create a PushType record from a stackname (keyword), with
  optional :recognizer :attributes and :instructions"
  [stackname & {
    :keys [recognizer attributes instructions] 
    :or {attributes #{} instructions []}}]
  (->PushType stackname recognizer attributes instructions))


(defn attach-function
  [pushtype function]
  (let [old-instructions (:instructions pushtype)]
    (assoc pushtype :instructions (conj old-instructions function))))


;;;; type-associated instructions


;; :visible


(defn stackdepth-instruction
  "returns a new x-stackdepth instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-stackdepth")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:visible}
      `(push.instructions.dsl/count-of ~typename :as :depth)
      '(push.instructions.dsl/push-onto :integer :depth)))))


(defn empty?-instruction
  "returns a new x-empty? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-empty?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:visible}
      `(push.instructions.dsl/count-of ~typename :as :depth)
      '(push.instructions.dsl/calculate [:depth] #(zero? %1) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


(defn make-visible
  "takes a PushType and adds the :visible attribute, and the
  :pushtype-stackdepth and :pushtype-empty? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      (attach-function (stackdepth-instruction pushtype))
      (attach-function (empty?-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :visible))))


;; :comparable


(defn equal?-instruction
  "returns a new x-equal? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-equal?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:equatable}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(= %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


(defn notequal?-instruction
  "returns a new x-notequal? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-notequal?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:equatable}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(not= %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


(defn make-equatable
  "takes a PushType and adds the :equatable attribute, and the
  :pushtype-equal? and :pushtype-notequal? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      (attach-function (equal?-instruction pushtype))
      (attach-function (notequal?-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :equatable))))


;; comparable


(defn lessthan?-instruction
  "returns a new x-<? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "<?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(< %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


(defn lessthanorequal?-instruction
  "returns a new x≤? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "≤?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(<= %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


(defn greaterthanorequal?-instruction
  "returns a new x≥? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "≥?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(>= %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


(defn greaterthan?-instruction
  "returns a new x>? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) ">?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(> %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


(defn min-instruction
  "returns a new x-min instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-min")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(min %1 %2) :as :winner)
      `(push.instructions.dsl/push-onto ~typename :winner)))))


(defn max-instruction
  "returns a new x-max instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-max")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(max %1 %2) :as :winner)
      `(push.instructions.dsl/push-onto ~typename :winner)))))


(defn make-comparable
  "takes a PushType and adds the :comparable attribute, and the
  :pushtype>?, :pushtype≥?, :pushtype<?, :pushtype≤?, :pushtype-min and
  :pushtype-max instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (attach-function (lessthan?-instruction pushtype))
      (attach-function (lessthanorequal?-instruction pushtype))
      (attach-function (greaterthan?-instruction pushtype))
      (attach-function (greaterthanorequal?-instruction pushtype))
      (attach-function (min-instruction pushtype))
      (attach-function (max-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :comparable))))


;; movable


(defn dup-instruction
  "returns a new x-dup instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-dup")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:combinator}
      `(push.instructions.dsl/save-top-of ~typename :as :arg1)
      `(push.instructions.dsl/push-onto ~typename :arg1)))))


(defn flush-instruction
  "returns a new x-flush instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-flush")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:combinator}
      `(push.instructions.dsl/delete-stack ~typename)))))


(defn pop-instruction
  "returns a new x-pop instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-pop")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:combinator}
      `(push.instructions.dsl/delete-top-of ~typename)))))


(defn rotate-instruction
  "returns a new x-rotate instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-rotate")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:combinator}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg3)
      `(push.instructions.dsl/push-onto ~typename :arg2)
      `(push.instructions.dsl/push-onto ~typename :arg1)
      `(push.instructions.dsl/push-onto ~typename :arg3)))))


(defn shove-instruction
  "returns a new x-shove instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-shove")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:combinator}
      '(push.instructions.dsl/consume-top-of :integer :as :index)
      `(push.instructions.dsl/consume-top-of ~typename :as :shoved-item)
      `(push.instructions.dsl/insert-as-nth-of ~typename :shoved-item :at :index)))))



(defn swap-instruction
  "returns a new x-swap instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-swap")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:combinator}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/push-onto ~typename :arg1)
      `(push.instructions.dsl/push-onto ~typename :arg2)))))


(defn yank-instruction
  "returns a new x-yank instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-yank")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:combinator}
      '(push.instructions.dsl/consume-top-of :integer :as :index)
      `(push.instructions.dsl/count-of ~typename :as :how-many)
      `(push.instructions.dsl/consume-nth-of ~typename :at :index :as :yanked-item)
      `(push.instructions.dsl/push-onto ~typename :yanked-item)))))


(defn yankdup-instruction
  "returns a new x-yankdup instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-yankdup")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:combinator}
      '(push.instructions.dsl/consume-top-of :integer :as :index)
      `(push.instructions.dsl/count-of ~typename :as :how-many)
      `(push.instructions.dsl/save-nth-of ~typename :at :index :as :yanked-item)
      `(push.instructions.dsl/push-onto ~typename :yanked-item)))))


(defn make-movable
  "takes a PushType and adds the :movable attribute, and the
  :pushtype-dup, :pushtype-flush, :pushtype-pop, :pushtype-rotate,
  :pushtype-shove, :pushtype-swap, :pushtype-yank and
  :pushtype-yankdup instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (attach-function (dup-instruction pushtype))
      (attach-function (flush-instruction pushtype))
      (attach-function (pop-instruction pushtype))
      (attach-function (rotate-instruction pushtype))
      (attach-function (shove-instruction pushtype))
      (attach-function (swap-instruction pushtype))
      (attach-function (yank-instruction pushtype))
      (attach-function (yankdup-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :movable))))
