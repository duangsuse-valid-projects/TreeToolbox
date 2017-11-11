package bsh;

/**
 * Implement casts.
 *
 * <p>I think it should be possible to simplify some of the code here by using the
 * Types.getAssignableForm() method, but I haven't looked into it.
 */
class BSHCastExpression extends SimpleNode {

    public BSHCastExpression(int id) {
        super(id);
    }

    /** @return the result of the cast. */
    public Object eval(CallStack callstack, Interpreter interpreter) throws EvalError {
        NameSpace namespace = callstack.top();
        Class toType = ((BSHType) jjtGetChild(0)).getType(callstack, interpreter);
        SimpleNode expression = (SimpleNode) jjtGetChild(1);

        // evaluate the expression
        Object fromValue = expression.eval(callstack, interpreter);
        Class fromType = fromValue.getClass();

        // TODO: need to add isJavaCastable() test for strictJava
        // (as opposed to isJavaAssignable())
        try {
            return Types.castObject(fromValue, toType, Types.CAST);
        } catch (UtilEvalError e) {
            throw e.toEvalError(this, callstack);
        }
    }
}
