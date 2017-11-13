package bsh;

import java.lang.reflect.InvocationTargetException;

class BSHMethodInvocation extends SimpleNode {
    BSHMethodInvocation(int id) {
        super(id);
    }

    BSHAmbiguousName getNameNode() {
        return (BSHAmbiguousName) jjtGetChild(0);
    }

    BSHArguments getArgsNode() {
        return (BSHArguments) jjtGetChild(1);
    }

    /**
     * Evaluate the method invocation with the specified callstack and interpreter
     */
    public Object eval(CallStack callstack, Interpreter interpreter) throws EvalError {
        NameSpace namespace = callstack.top();
        BSHAmbiguousName nameNode = getNameNode();

        // Do not evaluate methods this() or super() in class instance space
        // (i.e. inside a constructor)
        if (namespace.getParent() != null
                && namespace.getParent().isClass
                && (nameNode.text.equals("super") || nameNode.text.equals("this")))
            return Primitive.VOID;

        Name name = nameNode.getName(namespace);
        Object[] args = getArgsNode().getArguments(callstack, interpreter);

        // This try/catch block is replicated is BSHPrimarySuffix... need to
        // factor out common functionality...
        // Move to Reflect?
        try {
            return name.invokeMethod(interpreter, args, callstack, this);
        } catch (ReflectError e) {
            throw new EvalError("方法调用失败: " + e.getMessage(), this, callstack);
        } catch (InvocationTargetException e) {
            String msg = "方法调用 " + name;
            Throwable te = e.getTargetException();

            /*
            Try to squeltch the native code stack trace if the exception
            was caused by a reflective call back into the bsh interpreter
            (e.g. eval() or source()
            */
            boolean isNative = true;
            if (te instanceof EvalError)
                if (te instanceof TargetError) isNative = ((TargetError) te).inNativeCode();
                else isNative = false;

            throw new TargetError(msg, te, this, callstack, isNative);
        } catch (UtilEvalError e) {
            throw e.toEvalError(this, callstack);
        }
    }
}
