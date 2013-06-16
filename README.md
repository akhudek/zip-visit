# zip-visit

A Clojure library implementing functional visitors over zippers. This library
was inspired partly by http://www.ibm.com/developerworks/library/j-treevisit/
and my own needs for walking and modifying tree data structures in clojure.

## Usage


```clojure
(require '[zip.visit :refer :all])
```

Visitors operate over zippers, let's require that:

```clojure
(require '[clojure.zip :as z])
```

Zippers can operate over any tree or sequence type data. As an example, we
will walk over XML. Let's load a short HTML example into a zipper:

```clojure
(require '[clojure.xml :as xml] '[clojure.string :as string])

(def s "<div><span id='greeting'>Hello</span> <span id='name'>Mr. Foo</span>!</div>")
(def root (z/xml-zip (xml/parse (java.io.ByteArrayInputStream. (.getBytes s)))))
```

The visit function provides events for pre-order, post-order, and partial in-order
traversal. To illustrate this, let's create an event printer from scratch.

```clojure
(defn printer [evt n s] (println evt (str n s)))
```

Vistors are just functions that take three arguments. The first is the event, either
``:pre``, ``:post``, or ``:in``. The second is the current node and the third is
the current state. We will explain the state in detail later. For now, let's
visit our HTML.

```clojure
user=> (visit root nil [printer])

:pre {:tag :div, :attrs nil, :content [{:tag :span, :attrs {:id "greeting"}, :content ["Hello"]} {:tag :span, :attrs {:id "name"}, :content ["Mr. Foo"]} "!"]}
:pre {:tag :span, :attrs {:id "greeting"}, :content ["Hello"]}
:pre Hello
:post Hello
:post {:tag :span, :attrs {:id "greeting"}, :content ["Hello"]}
:in {:tag :div, :attrs nil, :content [{:tag :span, :attrs {:id "greeting"}, :content ["Hello"]} {:tag :span, :attrs {:id "name"}, :content ["Mr. Foo"]} "!"]}
:pre {:tag :span, :attrs {:id "name"}, :content ["Mr. Foo"]}
:pre Mr. Foo
:post Mr. Foo
:post {:tag :span, :attrs {:id "name"}, :content ["Mr. Foo"]}
:in {:tag :div, :attrs nil, :content [{:tag :span, :attrs {:id "greeting"}, :content ["Hello"]} {:tag :span, :attrs {:id "name"}, :content ["Mr. Foo"]} "!"]}
:pre !
:post !
:post {:tag :div, :attrs nil, :content [{:tag :span, :attrs {:id "greeting"}, :content ["Hello"]} {:tag :span, :attrs {:id "name"}, :content ["Mr. Foo"]} "!"]}
{:node {:tag :div, :attrs nil, :content [{:tag :span, :attrs {:id "greeting"}, :content ["Hello"]} {:tag :span, :attrs {:id "name"}, :content ["Mr. Foo"]} "!"]}, :state nil}
```

The ``visit`` function does all the work and takes as arguments: the zipper,
the initial state, and a sequence of visitor functions. It returns a map with
two keys: ``:node``, and ``:state``. If visitor functions modify the tree,
then ``:node`` contains the final modified zipper. Similarly, ``:state`` is
the state at the end of the walk. In this example, we get back the original
unchanged input zipper.

### Modifying Data

Now let's talk about modifying the tree. Say we want to modify the span
with id #name. Consider the following function.

```clojure
(defn replace-element [id replacement]
    (visitor :pre [n s]
      (if (= (:id (:attrs n)) id) {:node replacement})))
```

There is a fair bit going on here, so let's break it down into pieces. On the top
most level we have defined a function, ``replace-element`` that returns a visitor
function. The ``replace-element`` function takes an id and replacement string.

Next, we introduce one of zip-visit's helper functions: ``visitor``. This is a
macro that creates a visitor function which fires only on a particular event. As
in our first example, ``n`` is the tree node and ``s`` is the state. In this
example our visitor will only fire in a pre-order walk.

Finally, the core of the function checks that the id matches, and if so, it replaces
the current node with the desired replacement. If a visitor function returns nothing,
the tree is unmodified. However, if it returns a map, either the tree, the state,
or both are modified. To modify the tree we supply a new value for the ``:node`` key.

Let's try it:

```clojure
user=> (:node (visit root nil [(replace-element "name" "Mr. Smith")]))

{:tag :div, :attrs nil, :content [{:tag :span, :attrs {:id "greeting"}, :content ["Hello"]} "Mr. Smith" "!"]}
```

But what if we wanted to modify other parts of the tree? Do we need to
do multiple walks? Create huge conditionals in our visitor? Fortunately not!
Remember that visit takes a seq of visitor functions. What happens when we
supply more then one function? At every step, each function is applied in the order
is appears in the seq. Successive functions get the node and state values
from the previous functions.

```clojure
user=> (:node (visit root nil [(replace-element "greeting" "Greetings") (replace-element "name" "Mr. Smith")]))

{:tag :div, :attrs nil, :content ["Greetings" "Mr. Smith" "!"]}
```

### State

Ok, so what about state? The state is completely user defined and evolves along
with the zippered data during the walk. As an example, let's collect all the spans
in our example data.

```clojure
(defvisitor collect-spans :pre
    [n s]
    (if (= :span (:tag n)) {:state (conj s (:content n))}))
```

Here we introduce the second visit helper macro, ``defvisitor``. This is the same
as ``visitor`` but binds the result to a symbol. The ``collect-spans`` function
expects state that is just a set.

```clojure
user=> (:state (visit root #{} [collect-spans]))

#{["Mr. Foo"] ["Hello"]}
```

And remember, we can mix and match any visitor functions:

```clojure
user=> (visit root #{} [collect-spans (replace-element "name" "Mr. Smith")])
{:node {:tag :div, :attrs nil, :content [{:tag :span, :attrs {:id "greeting"}, :content ["Hello"]} "Mr. Smith" "!"]}, :state #{["Mr. Foo"] ["Hello"]}}
```

Just be aware of the order!

```clojure
user=> (visit root #{} [(replace-element "name" "Mr. Smith") collect-spans])

{:node {:tag :div, :attrs nil, :content [{:tag :span, :attrs {:id "greeting"}, :content ["Hello"]} "Mr. Smith" "!"]}, :state #{["Hello"]}}
```


## License

The MIT License (MIT)

Copyright (c) 2013 Alexander K. Hudek

See LICENSE file.
