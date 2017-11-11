package bsh;

/** A formal parameter declaration. For loose variable declaration type is null. */
class BSHFormalParameter extends SimpleNode {
    public static final Class UNTYPED = null;
    public String name;
    // unsafe caching of type here
    public Class type;

    BSHFormalParameter(int id) {
        super(id);
    }

    public String getTypeDescriptor(
            CallStack callstack, Interpreter interpreter, String defaultPackage) {
        if (jjtGetNumChildren() > 0)
            return ((BSHType) jjtGetChild(0))
                    .getTypeDescriptor(callstack, interpreter, defaultPackage);
        else
            // this will probably not get used
            return "Ljava/lang/Object;"; // Object type
    }

    /** Evaluate the type. */
    public Object eval(CallStack callstack, Interpreter interpreter) throws EvalError {
        if (jjtGetNumChildren() > 0)
            type = ((BSHType) jjtGetChild(0)).getType(callstack, interpreter);
        else type = UNTYPED;

        return type;
    }
}
