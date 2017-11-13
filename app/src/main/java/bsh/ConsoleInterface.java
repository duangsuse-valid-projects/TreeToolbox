package bsh;

import java.io.*;

/**
 * The capabilities of a minimal console for BeanShell. Stream I/O and optimized print for output.
 *
 * <p>A simple console may ignore some of these or map them to trivial implementations. e.g. print()
 * with color can be mapped to plain text.
 *
 * @see bsh.util.GUIConsoleInterface
 */
public interface ConsoleInterface {
    public Reader getIn();

    public PrintStream getOut();

    public PrintStream getErr();

    public void println(Object o);

    public void print(Object o);

    public void error(Object o);
}
