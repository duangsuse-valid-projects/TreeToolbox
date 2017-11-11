package bsh;

class BSHAmbiguousName extends SimpleNode {
    public String text;

    BSHAmbiguousName(int id) {
        super(id);
    }

    public Name getName(NameSpace namespace) {
        return namespace.getNameResolver(text);
    }

    public Object toObject(CallStack callstack, Interpreter interpreter) throws EvalError {
        return toObject(callstack, interpreter, false);
    }

    Object toObject(CallStack callstack, Interpreter interpreter, boolean forceClass)
            throws EvalError {
        try {
            return getName(callstack.top()).toObject(callstack, interpreter, forceClass);
        } catch (UtilEvalError e) {
            // e.printStackTrace();
            throw e.toEvalError(this, callstack);
        }
    }

    public Class toClass(CallStack callstack, Interpreter interpreter) throws EvalError {
        try {
            return getName(callstack.top()).toClass();
        } catch (ClassNotFoundException e) {
            throw new EvalError(e.getMessage(), this, callstack);
        } catch (UtilEvalError e2) {
            // ClassPathException is a type of UtilEvalError
            throw e2.toEvalError(this, callstack);
        }
    }

    public LHS toLHS(CallStack callstack, Interpreter interpreter) throws EvalError {
        try {
            return getName(callstack.top()).toLHS(callstack, interpreter);
        } catch (UtilEvalError e) {
            throw e.toEvalError(this, callstack);
        }
    }

    /*
    The interpretation of an ambiguous name is context sensitive.
    We disallow a generic eval( ).
    */
    public Object eval(CallStack callstack, Interpreter interpreter) throws EvalError {
        throw new InterpreterError("不知道如何模拟含糊的名字! 如果想要一个对象的话调用 toObject() 方法.");
    }

    public String toString() {
        return "模糊的名字: " + text;
    }
}
