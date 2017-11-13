package bsh;

/**
 * EvalError indicates that we cannot continue evaluating the script or the script has thrown an
 * exception.
 *
 * <p>EvalError may be thrown for a script syntax error, an evaluation error such as referring to an
 * undefined variable, an internal error.
 *
 * <p>
 *
 * @see TargetError
 */
public class EvalError extends Exception {
    SimpleNode node;

    // Note: no way to mutate the Throwable message, must maintain our own
    String message;

    CallStack callstack;

    public EvalError(String s, SimpleNode node, CallStack callstack) {
        setMessage(s);
        this.node = node;
        // freeze the callstack for the stack trace.
        if (callstack != null) this.callstack = callstack.copy();
    }

    /** Print the error with line number and stack trace. */
    public String toString() {
        String trace;
        if (node != null)
            trace =
                    " : 于行: "
                            + node.getLineNumber()
                            + " : 于文件: "
                            + node.getSourceFile()
                            + " : "
                            + node.getText();
        else
            // Users should not normally see this.
            trace = ": <未知位置>";

        if (callstack != null) trace = trace + "\n" + getScriptStackTrace();

        return getMessage() + trace;
    }

    /** Re-throw the error, prepending the specified message. */
    public void reThrow(String msg) throws EvalError {
        prependMessage(msg);
        throw this;
    }

    /**
     * The error has trace info associated with it. i.e. It has an AST node that can print its
     * location and source text.
     */
    SimpleNode getNode() {
        return node;
    }

    void setNode(SimpleNode node) {
        this.node = node;
    }

    public String getErrorText() {
        if (node != null) return node.getText();
        else return "<未知错误>";
    }

    public int getErrorLineNumber() {
        if (node != null) return node.getLineNumber();
        else return -1;
    }

    public String getErrorSourceFile() {
        if (node != null) return node.getSourceFile();
        else return "<未知文件>";
    }

    public String getScriptStackTrace() {
        if (callstack == null) return "<未知>";

        String trace = "";
        CallStack stack = callstack.copy();
        while (stack.depth() > 0) {
            NameSpace ns = stack.pop();
            SimpleNode node = ns.getNode();
            if (ns.isMethod) {
                trace = trace + "\n从方法: " + ns.getName() + " 被调用";
                if (node != null)
                    trace +=
                            " : 于行: "
                                    + node.getLineNumber()
                                    + " : 于文件: "
                                    + node.getSourceFile()
                                    + " : "
                                    + node.getText();
            }
        }

        return trace;
    }

    /** @see #toString() for a full display of the information */
    public String getMessage() {
        return message;
    }

    public void setMessage(String s) {
        message = s;
    }

    /** Prepend the message if it is non-null. */
    protected void prependMessage(String s) {
        if (s == null) return;

        if (message == null) message = s;
        else message = s + " : " + message;
    }
}
