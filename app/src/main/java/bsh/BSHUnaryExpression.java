package bsh;

class BSHUnaryExpression extends SimpleNode implements ParserConstants {
    public int kind;
    public boolean postfix = false;

    BSHUnaryExpression(int id) {
        super(id);
    }

    public Object eval(CallStack callstack, Interpreter interpreter) throws EvalError {
        SimpleNode node = (SimpleNode) jjtGetChild(0);

        // If this is a unary increment of decrement (either pre or postfix)
        // then we need an LHS to which to assign the result.  Otherwise
        // just do the unary operation for the value.
        try {
            if (kind == INCR || kind == DECR) {
                LHS lhs = ((BSHPrimaryExpression) node).toLHS(callstack, interpreter);
                return lhsUnaryOperation(lhs, interpreter.getStrictJava());
            } else return unaryOperation(node.eval(callstack, interpreter), kind);
        } catch (UtilEvalError e) {
            throw e.toEvalError(this, callstack);
        }
    }

    private Object lhsUnaryOperation(LHS lhs, boolean strictJava) throws UtilEvalError {
        if (Interpreter.DEBUG) Interpreter.debug("LHS元操作");
        Object prevalue, postvalue;
        prevalue = lhs.getValue();
        postvalue = unaryOperation(prevalue, kind);

        Object retVal;
        if (postfix) retVal = prevalue;
        else retVal = postvalue;

        lhs.assign(postvalue, strictJava);
        return retVal;
    }

    private Object unaryOperation(Object op, int kind) throws UtilEvalError {
        if (op instanceof Boolean || op instanceof Character || op instanceof Number)
            return primitiveWrapperUnaryOperation(op, kind);

        if (!(op instanceof Primitive))
            throw new UtilEvalError("元操作 " + tokenImage[kind] + " 不适于对象");

        return Primitive.unaryOperation((Primitive) op, kind);
    }

    private Object primitiveWrapperUnaryOperation(Object val, int kind) throws UtilEvalError {
        Class operandType = val.getClass();
        Object operand = Primitive.promoteToInteger(val);

        if (operand instanceof Boolean)
            return Primitive.booleanUnaryOperation((Boolean) operand, kind)
                    ? Boolean.TRUE
                    : Boolean.FALSE;
        else if (operand instanceof Integer) {
            int result = Primitive.intUnaryOperation((Integer) operand, kind);

            // ++ and -- must be cast back the original type
            if (kind == INCR || kind == DECR) {
                if (operandType == Byte.TYPE) return Byte.valueOf((byte) result);
                if (operandType == Short.TYPE) return new Short((short) result);
                if (operandType == Character.TYPE) return Character.valueOf((char) result);
            }

            return Integer.valueOf(result);
        } else if (operand instanceof Long)
            return Long.valueOf(Primitive.longUnaryOperation((Long) operand, kind));
        else if (operand instanceof Float)
            return Float.valueOf(Primitive.floatUnaryOperation((Float) operand, kind));
        else if (operand instanceof Double)
            return Double.valueOf(Primitive.doubleUnaryOperation((Double) operand, kind));
        else throw new InterpreterError("发生了意外错误. 请寻找技术资瓷");
    }
}
