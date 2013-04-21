reductor
========

A toy language that shows how easy it is to implement a powerful concatenative language, and nothing more.

```clojure
[ "examples/quot.reductor" load,

  :double (int -- int)
   [ 2 * ] define,

  :map ([a] (a -- b) -- [b])
   [ over empty?
     [ drop ]
     [ [ [ car ] [ cdr ] bi ] dip
       tuck map [call] dip swap cons ]
     if ] define,

   [ 1 2 3 4 5 ] [ double ] map . ]
```

install
-------

```
git clone https://github.com/ympbyc/reductor.git
cd reductor
lein deps
```

running
-------

```
cat examples/map.reductor | lein run
```

test
----

```bash
a=`cat examples/map.reductor | lein run`

if [ $a = "(2 4 6 8 10)" ] ; then
  echo "ok"
else
  echo "error"
fi
```
