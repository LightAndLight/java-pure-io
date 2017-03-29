# Pure IO in Java

A "pure function" is a function that maps an input to an output and *does nothing else*.

In other words, a pure function has no side effects.

But then isn't "pure IO" an oxymoron?

No, it's not :)

---

An `IO<A>` is an instruction that, if run, would produce an `A`\*. Because `IO` actions
don't have any effects on their own, functions operating on and producing them can remain
pure.

This library provides two ways to sequence `IO` actions without running them:

- `then`: Given `IO<A> a = ...;` and  `IO<B> b = ...;`, `a.then(b)` creates a new `IO`
  action that would first do the things in `a`, ignore the result, then do the things in
  `b`.
- `bind`: Given `IO<A> a = ...;` and `Function<Unit,IO<B>> f = unit -> ...;`, `a.bind(f)`
  creates a new `IO` action that would first do the things in `a`, then do the things in
  `f(a)`

Example:

`IO.readLine()` returns an `IO` action that, if run, would return a line of user input.

`IO.writeLine()` returns an `IO` action that, if run, would write a string to the console.

`IO.readLine().bind(line -> IO.writeLine(line))` is an `IO` action that would get a line of
user input and write it to the console.

Being able to build `IO` actions isn't enough to actually make the computer do something.
That's what `IO.run` is for. It is an *impure* function that takes an `IO<A>` and actually
runs it, producing an `A`. This is the entrypoint to our pure `IO` program.

\* it could also potentially throw an error or loop forever
