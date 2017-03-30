import pure.IO;
import pure.Lazy;
import pure.Unit;


public class Example {
    public static <A> IO<Unit> buzz(IO<A> input) {
        // I/O programs are first-order (can be passed to functions)
        IO<Unit> sayBuzz = IO.writeLine("buzz");
        return sayBuzz
            .then(input
            .then(sayBuzz));
    }

    public static void main(String[] args) {
        IO<Unit> script =
            IO.writeLine("hello")
            .then(IO.writeLine("what's your name?")
            .then(IO.writeLine("(enter your name)")
            .then(IO.readLine()
                .bind(str -> IO.writeLine("hello " + str)))));

        // Build bigger programs from smaller ones
        IO<Unit> program = 
            script
            .then(IO.writeLine("----")
            .then(buzz(script)));

        IO.run(
            program
            // Stack-safe infinite recursion. `IO.forever` is data, not a while loop ;)
            .then(IO.forever(IO.writeLine("a")))
        );
    }
}
