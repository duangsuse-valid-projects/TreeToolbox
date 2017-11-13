package bsh;

class BSHTypedVariableDeclaration extends SimpleNode {
    public Modifiers modifiers;

    BSHTypedVariableDeclaration(int id) {
        super(id);
    }

    private BSHType getTypeNode() {
        return ((BSHType) jjtGetChild(0));
    }

    Class evalType(CallStack callstack, Interpreter interpreter) throws EvalError {
        BSHType typeNode = getTypeNode();
        return typeNode.getType(callstack, interpreter);
    }

    BSHVariableDeclarator[] getDeclarators() {
        int n = jjtGetNumChildren();
        int start = 1;
        BSHVariableDeclarator[] bvda = new BSHVariableDeclarator[n - start];
        for (int i = start; i < n; i++) {
            bvda[i - start] = (BSHVariableDeclarator) jjtGetChild(i);
        }
        return bvda;
    }

    /** evaluate the type and one or more variable declarators, e.g.: int a, b=5, c; */
    public Object eval(CallStack callstack, Interpreter interpreter) throws EvalError {
        try {
            NameSpace namespace = callstack.top();
            BSHType typeNode = getTypeNode();
            Class type = typeNode.getType(callstack, interpreter);

            BSHVariableDeclarator[] bvda = getDeclarators();
            for (int i = 0; i < bvda.length; i++) {
                BSHVariableDeclarator dec = bvda[i];

                // Type node is passed down the chain for array initializers
                // which need it under some circumstances
                Object value = dec.eval(typeNode, callstack, interpreter);

                try {
                    namespace.setTypedVariable(dec.name, type, value, modifiers);
                } catch (UtilEvalError e) {
                    throw e.toEvalError(this, callstack);
                }
            }
        } catch (EvalError e) {
            e.reThrow("钦定类型的变量声明");
        }

        return Primitive.VOID;
    }

    public String getTypeDescriptor(
            CallStack callstack, Interpreter interpreter, String defaultPackage) {
        return getTypeNode().getTypeDescriptor(callstack, interpreter, defaultPackage);
    }
}
