package bsh;

/**
 * Implementation of the enhanced for(:) statement. This statement uses BshIterable to support
 * iteration over a wide variety of iterable types. Under JDK 1.1 this statement supports primitive
 * and Object arrays, Vectors, and enumerations. Under JDK 1.2 and later it additionally supports
 * collections.
 *
 * @author Daniel Leuck
 * @author Pat Niemeyer
 */
class BSHEnhancedForStatement extends SimpleNode implements ParserConstants {

    String varName;

    BSHEnhancedForStatement(int id) {
        super(id);
    }

    public Object eval(CallStack callstack, Interpreter interpreter) throws EvalError {
        Class elementType = null;
        SimpleNode expression;
        SimpleNode statement = null;
        NameSpace enclosingNameSpace = callstack.top();
        SimpleNode firstNode = ((SimpleNode) jjtGetChild(0));
        int nodeCount = jjtGetNumChildren();
        if (firstNode instanceof BSHType) {
            elementType = ((BSHType) firstNode).getType(callstack, interpreter);
            expression = ((SimpleNode) jjtGetChild(1));
            if (nodeCount > 2) {
                statement = ((SimpleNode) jjtGetChild(2));
            }
        } else {
            expression = firstNode;
            if (nodeCount > 1) {
                statement = ((SimpleNode) jjtGetChild(1));
            }
        }
        BlockNameSpace eachNameSpace = new BlockNameSpace(enclosingNameSpace);
        callstack.swap(eachNameSpace);
        final Object iteratee = expression.eval(callstack, interpreter);
        if (iteratee == Primitive.NULL) {
            throw new EvalError("一个for语句的集合, 数组, 映射, 送代器或枚举部分不能是空的.", this, callstack);
        }
        CollectionManager cm = CollectionManager.getCollectionManager();
        if (!cm.isBshIterable(iteratee)) {
            throw new EvalError("不能在类型 " + iteratee.getClass() + "上完成送代", this, callstack);
        }
        BshIterator iterator = cm.getBshIterator(iteratee);
        Object returnControl = Primitive.VOID;
        while (iterator.hasNext()) {
            try {
                Object value = iterator.next();
                if (value == null) {
                    value = Primitive.NULL;
                }
                if (elementType != null) {
                    eachNameSpace.setTypedVariable(
                            varName /*name*/,
                            elementType /*type*/,
                            value /*value*/,
                            new Modifiers());
                } else {
                    eachNameSpace.setVariable(varName, value, false);
                }
            } catch (UtilEvalError e) {
                throw e.toEvalError("for循环的送代器变量:" + varName, this, callstack);
            }
            boolean breakout = false; // switch eats a multi-level break here?
            if (statement != null) {
                // not empty statement
                Object ret = statement.eval(callstack, interpreter);
                if (ret instanceof ReturnControl) {
                    switch (((ReturnControl) ret).kind) {
                        case RETURN:
                            returnControl = ret;
                            breakout = true;
                            break;
                        case CONTINUE:
                            break;
                        case BREAK:
                            breakout = true;
                            break;
                    }
                }
            }
            if (breakout) {
                break;
            }
        }
        callstack.swap(enclosingNameSpace);
        return returnControl;
    }
}
