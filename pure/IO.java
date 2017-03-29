package pure;

import java.util.function.Function;
import java.io.Console;
import java.lang.System;

public abstract class IO<A> {
    public static <A> A run(IO<A> input) {
        IO<A> result = input;
        Unit unit = new Unit();

        while (!result.isValue()) {
            Function<Unit,IO<A>> thunk = result.next();
            result = thunk.apply(unit);
        }

        return result.runIO();
    }

    public abstract A runIO();

    public boolean isValue() {
        return true;
    }

    public Function<Unit, IO<A>> next() {
        return unit -> this;
    }

    public <B> IO<B> bind(Function<A, IO<B>> f) {
        return new Bind<A,B>(this, f);
    }

    public <B> IO<B> bindLazy(Function<A, Lazy<IO<B>>> f) {
        return new BindLazy<A,B>(this, f);
    }

    public <B> IO<B> then(IO<B> next) {
        return new Then<A,B>(this, next);
    }

    public <B> IO<B> thenLazy(Lazy<IO<B>> next) {
        return new ThenLazy<A,B>(this, next);
    }

    public static <A> IO<A> pure(A a) {
        return new Pure<A>(a);
    }

    public static <A> IO<A> forever(IO<A> input) {
        return input.thenLazy(Lazy.delay(unit -> forever(input)));
    }

    private static class Pure<A> extends IO<A> {
        private A value;
        public Pure(A value) {
            this.value = value;
        }

        public A runIO() {
            return this.value;
        }
    }

    private static class Then<A,B> extends IO<B> {
        private IO<A> a;
        private IO<B> b;

        public Then(IO<A> a, IO<B> b) {
            this.a = a;
            this.b = b;
        }

        public B runIO() {
            this.a.runIO();
            return this.b.runIO();
        }

        public Function<Unit, IO<B>> next() {
            this.a.runIO();
            return this.b.next();
        }

        public boolean isValue() {
            return false;
        }
    }

    private static class ThenLazy<A,B> extends IO<B> {
        private IO<A> a;
        private Lazy<IO<B>> b;

        public ThenLazy(IO<A> a, Lazy<IO<B>> b) {
            this.a = a;
            this.b = b;
        }

        public B runIO() {
            this.a.runIO();
            return this.b.force().runIO();
        }

        public Function<Unit, IO<B>> next() {
            this.a.runIO();
            return unit -> this.b.force();
        }

        public boolean isValue() {
            return false;
        }
    }

    private static class Bind<A,B> extends IO<B> {
        private IO<A> action;
        private Function<A, IO<B>> function;

        public Bind(IO<A> action, Function<A, IO<B>> function) {
            this.action = action;
            this.function = function;
        }

        public B runIO() {
            return this.function.apply(this.action.runIO()).runIO();
        }

        public Function<Unit, IO<B>> next() {
            return unit -> this.function
                .apply(this.action.runIO())
                .next()
                .apply(unit);
        }

        public boolean isValue() {
            return false;
        };
    }

    private static class BindLazy<A,B> extends IO<B> {
        private IO<A> action;
        private Function<A, Lazy<IO<B>>> function;

        public BindLazy(IO<A> action, Function<A, Lazy<IO<B>>> function) {
            this.action = action;
            this.function = function;
        }

        public B runIO() {
            return this.function
                .apply(this.action.runIO())
                .force()
                .runIO();
        }

        public Function<Unit, IO<B>> next() {
            return unit -> this.function
                .apply(this.action.runIO())
                .force();
        }

        public boolean isValue() {
            return false;
        };
    }

    public static IO<String> readLine() {
        return new IO<String>() {
            public String runIO() {
                Console c = System.console();
                return c.readLine();
            }
        };
    }

    public static IO<Unit> writeLine(String s) {
        return new IO<Unit>() {
            public Unit runIO() {
                Console c = System.console();
                c.printf(s + "\n");
                return new Unit();
            }
        };
    }
}

