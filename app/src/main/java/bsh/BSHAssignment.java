package bsh;

class BSHAssignment extends SimpleNode implements ParserConstants {
    public int operator;

    BSHAssignment(int id) {
        super(id);
    }

    public Object eval(CallStack callstack, Interpreter interpreter) throws EvalError {
        BSHPrimaryExpression lhsNode = (BSHPrimaryExpression) jjtGetChild(0);

        if (lhsNode == null) throw new InterpreterError("错误, 左节点为null");

        boolean strictJava = interpreter.getStrictJava();
        LHS lhs = lhsNode.toLHS(callstack, interpreter);
        if (lhs == null) throw new InterpreterError("错误, 左值为空");

        // For operator-assign operations save the lhs value before evaluating
        // the rhs.  This is correct Java behavior for postfix operations
        // e.g. i=1; i+=i++; // should be 2 not 3
        Object lhsValue = null;
        if (operator != ASSIGN) // assign doesn't need the pre-value
        try {
                lhsValue = lhs.getValue();
            } catch (UtilEvalError e) {
                throw e.toEvalError(this, callstack);
            }

        SimpleNode rhsNode = (SimpleNode) jjtGetChild(1);

        Object rhs;

        // implement "blocks" foo = { };
        // if ( rhsNode instanceof BSHBlock )
        //    rsh =
        // else
        rhs = rhsNode.eval(callstack, interpreter);

        if (rhs == Primitive.VOID) throw new EvalError("不能声明为未定义.", this, callstack);

        try {
            switch (operator) {
                case ASSIGN:
                    return lhs.assign(rhs, strictJava);

                case PLUSASSIGN:
                    return lhs.assign(operation(lhsValue, rhs, PLUS), strictJava);

                case MINUSASSIGN:
                    return lhs.assign(operation(lhsValue, rhs, MINUS), strictJava);

                case STARASSIGN:
                    return lhs.assign(operation(lhsValue, rhs, STAR), strictJava);

                case SLASHASSIGN:
                    return lhs.assign(operation(lhsValue, rhs, SLASH), strictJava);

                case ANDASSIGN:
                case ANDASSIGNX:
                    return lhs.assign(operation(lhsValue, rhs, BIT_AND), strictJava);

                case ORASSIGN:
                case ORASSIGNX:
                    return lhs.assign(operation(lhsValue, rhs, BIT_OR), strictJava);

                case XORASSIGN:
                    return lhs.assign(operation(lhsValue, rhs, XOR), strictJava);

                case MODASSIGN:
                    return lhs.assign(operation(lhsValue, rhs, MOD), strictJava);

                case LSHIFTASSIGN:
                case LSHIFTASSIGNX:
                    return lhs.assign(operation(lhsValue, rhs, LSHIFT), strictJava);

                case RSIGNEDSHIFTASSIGN:
                case RSIGNEDSHIFTASSIGNX:
                    return lhs.assign(operation(lhsValue, rhs, RSIGNEDSHIFT), strictJava);

                case RUNSIGNEDSHIFTASSIGN:
                case RUNSIGNEDSHIFTASSIGNX:
                    return lhs.assign(operation(lhsValue, rhs, RUNSIGNEDSHIFT), strictJava);

                default:
                    throw new InterpreterError("BSH中未实现的声明操作符");
            }
        } catch (UtilEvalError e) {
            throw e.toEvalError(this, callstack);
        }
    }

    private Object operation(Object lhs, Object rhs, int kind) throws UtilEvalError {
        /*
        Implement String += value;
        According to the JLS, value may be anything.
        In BeanShell, we'll disallow VOID (undefined) values.
        (or should we map them to the empty string?)
        */
        if (lhs instanceof String && rhs != Primitive.VOID) {
            if (kind != PLUS) throw new UtilEvalError("左值中对String使用了非+的操作符");

            return (String) lhs + rhs;
        }

        if (lhs instanceof Primitive || rhs instanceof Primitive)
            if (lhs == Primitive.VOID || rhs == Primitive.VOID)
                throw new UtilEvalError("对未定义对象或void字面的非法使用");
            else if (lhs == Primitive.NULL || rhs == Primitive.NULL)
                throw new UtilEvalError("对空对象或'null'字面的非法使用");

        if ((lhs instanceof Boolean
                        || lhs instanceof Character
                        || lhs instanceof Number
                        || lhs instanceof Primitive)
                && (rhs instanceof Boolean
                        || rhs instanceof Character
                        || rhs instanceof Number
                        || rhs instanceof Primitive)) {
            return Primitive.binaryOperation(lhs, rhs, kind);
        }

        throw new UtilEvalError(
                "操作符中非原生类型: " + lhs.getClass() + " " + tokenImage[kind] + " " + rhs.getClass());
    }
}
