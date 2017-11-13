package bsh;

/**
 * An internal error in the interpreter has occurred.
 */
public class InterpreterError extends RuntimeException {

    public InterpreterError(String message) {
        super(message);
    }

    public InterpreterError(String message, Throwable cause) {
        super(message, cause);
    }
}
