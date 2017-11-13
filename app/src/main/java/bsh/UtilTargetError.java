package bsh;

/**
 * UtilTargetError is an error corresponding to a TargetError but thrown by a utility or other class
 * that does not have the caller context (Node) available to it. See UtilEvalError for an
 * explanation of the difference between UtilEvalError and EvalError.
 * <p>
 * <p>
 *
 * @see UtilEvalError
 */
public class UtilTargetError extends UtilEvalError {
    public Throwable t;

    public UtilTargetError(String message, Throwable t) {
        super(message);
        this.t = t;
    }

    public UtilTargetError(Throwable t) {
        this(null, t);
    }

    /**
     * Override toEvalError to throw TargetError type.
     */
    public EvalError toEvalError(String msg, SimpleNode node, CallStack callstack) {
        if (msg == null) msg = getMessage();
        else msg = msg + ": " + getMessage();

        return new TargetError(msg, t, node, callstack, false);
    }
}
