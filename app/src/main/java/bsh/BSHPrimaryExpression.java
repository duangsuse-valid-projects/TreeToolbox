package bsh;

class BSHPrimaryExpression extends SimpleNode {
    BSHPrimaryExpression(int id) {
        super(id);
    }

    /** Evaluate to a value object. */
    public Object eval(CallStack callstack, Interpreter interpreter) throws EvalError {
        return eval(false, callstack, interpreter);
    }

    /** Evaluate to a value object. */
    public LHS toLHS(CallStack callstack, Interpreter interpreter) throws EvalError {
        Object obj = eval(true, callstack, interpreter);

        if (!(obj instanceof LHS)) throw new EvalError("不能声明到:", this, callstack);
        else return (LHS) obj;
    }

    /*
    Our children are a prefix expression and any number of suffixes.
    <p>

    We don't eval() any nodes until the suffixes have had an
    opportunity to work through them.  This lets the suffixes decide
    how to interpret an ambiguous name (e.g. for the .class operation).
    */
    private Object eval(boolean toLHS, CallStack callstack, Interpreter interpreter)
            throws EvalError {
        Object obj = jjtGetChild(0);
        int numChildren = jjtGetNumChildren();

        for (int i = 1; i < numChildren; i++)
            obj = ((BSHPrimarySuffix) jjtGetChild(i)).doSuffix(obj, toLHS, callstack, interpreter);

        /*
        If the result is a Node eval() it to an object or LHS
        (as determined by toLHS)
        */
        if (obj instanceof SimpleNode)
            if (obj instanceof BSHAmbiguousName)
                if (toLHS) obj = ((BSHAmbiguousName) obj).toLHS(callstack, interpreter);
                else obj = ((BSHAmbiguousName) obj).toObject(callstack, interpreter);
            else
            // Some arbitrary kind of node
            if (toLHS)
                // is this right?
                throw new EvalError("不能声明到前缀.", this, callstack);
            else obj = ((SimpleNode) obj).eval(callstack, interpreter);

        // return LHS or value object as determined by toLHS
        if (obj instanceof LHS)
            if (toLHS) return obj;
            else
                try {
                    return ((LHS) obj).getValue();
                } catch (UtilEvalError e) {
                    throw e.toEvalError(this, callstack);
                }
        else return obj;
    }
}
