package pure;

import java.util.function.Function;

public class Lazy<A> {
    private Function<Unit,A> thunk;
    private A value;

    public static <A> Lazy<A> delay(Function<Unit,A> thunk) {
        return new Lazy<>(thunk);
    }

    public Lazy(Function<Unit,A> thunk) {
        this.thunk = thunk;
    }

    public A force() {
        if (thunk == null) {
            return this.value;
        } else {
            this.value = this.thunk.apply(new Unit());
            this.thunk = null;
            return this.value;
        }
    }
}
