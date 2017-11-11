package bsh;

/** */
class BSHClassDeclaration extends SimpleNode {
    /**
     * The class instance initializer method name. A BshMethod by this name is installed by the
     * class delcaration into the static class body namespace. It is called once to initialize the
     * static members of the class space and each time an instances is created to initialize the
     * instance members.
     */
    static final String CLASSINITNAME = "_bshClassInit";

    String name;
    Modifiers modifiers;
    int numInterfaces;
    boolean extend;
    boolean isInterface;

    BSHClassDeclaration(int id) {
        super(id);
    }

    /** */
    public Object eval(CallStack callstack, Interpreter interpreter) throws EvalError {
        int child = 0;

        // resolve superclass if any
        Class superClass = null;
        if (extend) {
            BSHAmbiguousName superNode = (BSHAmbiguousName) jjtGetChild(child++);
            superClass = superNode.toClass(callstack, interpreter);
        }

        // Get interfaces
        Class[] interfaces = new Class[numInterfaces];
        for (int i = 0; i < numInterfaces; i++) {
            BSHAmbiguousName node = (BSHAmbiguousName) jjtGetChild(child++);
            interfaces[i] = node.toClass(callstack, interpreter);
            if (!interfaces[i].isInterface())
                throw new EvalError("类型: " + node.text + " 不是一个接口!", this, callstack);
        }

        BSHBlock block;
        // Get the class body BSHBlock
        if (child < jjtGetNumChildren()) block = (BSHBlock) jjtGetChild(child);
        else block = new BSHBlock(ParserTreeConstants.JJTBLOCK);

        try {
            return ClassGenerator.getClassGenerator()
                    .generateClass(
                            name,
                            modifiers,
                            interfaces,
                            superClass,
                            block,
                            isInterface,
                            callstack,
                            interpreter);
        } catch (UtilEvalError e) {
            throw e.toEvalError(this, callstack);
        }
    }

    public String toString() {
        return "类声明: " + name;
    }
}
