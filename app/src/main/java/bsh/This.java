package bsh;

/**
 * 'This' is the type of bsh scripted objects. A 'This' object is a bsh scripted object context. It
 * holds a namespace reference and implements event listeners and various other interfaces.
 *
 * <p>This holds a reference to the declaring interpreter for callbacks from outside of bsh.
 */
public class This implements java.io.Serializable, Runnable {
    /** The namespace that this This reference wraps. */
    NameSpace namespace;

    /**
     * This is the interpreter running when the This ref was created. It's used as a default
     * interpreter for callback through the This where there is no current interpreter instance e.g.
     * interface proxy or event call backs from outside of bsh.
     */
    transient Interpreter declaringInterpreter;

    /**
     * getThis() is a factory for bsh.This type references. The capabilities of ".this" references
     * in bsh are version dependent up until jdk1.3. The version dependence was to support different
     * default interface implementations. i.e. different sets of listener interfaces which scripted
     * objects were capable of implementing. In jdk1.3 the reflection proxy mechanism was introduced
     * which allowed us to implement arbitrary interfaces. This is fantastic.
     *
     * <p>A This object is a thin layer over a namespace, comprising a bsh object context. We create
     * it here only if needed for the namespace.
     *
     * <p>Note: this method could be considered slow because of the way it dynamically factories
     * objects. However I've also done tests where I hard-code the factory to return JThis and see
     * no change in the rough test suite time. This references are also cached in NameSpace.
     */
    static This getThis(NameSpace namespace, Interpreter declaringInterpreter) {
        try {
            Class c;
            if (Capabilities.canGenerateInterfaces()) c = Class.forName("bsh.XThis");
            else return new This(namespace, declaringInterpreter);

            return (This)
                    Reflect.constructObject(c, new Object[] {namespace, declaringInterpreter});

        } catch (Exception e) {
            throw new InterpreterError("This 内部错误1: " + e);
        }
    }

    /** Get a version of this scripted object implementing the specified interface. */
    /*
    If this type of This implements it directly return this,
    else try complain that we don't have the proxy mechanism.
    */
    public Object getInterface(Class clas) throws UtilEvalError {
        if (clas.isInstance(this)) return this;
        else throw new UtilEvalError("动态代理机制不可用" + "无法架构接口型: " + clas);
    }

    /** Get a version of this scripted object implementing the specified interfaces. */
    public Object getInterface(Class[] ca) throws UtilEvalError {
        for (int i = 0; i < ca.length; i++)
            if (!(ca[i].isInstance(this)))
                throw new UtilEvalError("动态代理机制不可用" + "无法架构接口类型: " + ca[i]);

        return this;
    }

    /*
    I wish protected access were limited to children and not also
    package scope... I want this to be a singleton implemented by various
    children.
    */
    protected This(NameSpace namespace, Interpreter declaringInterpreter) {
        this.namespace = namespace;
        this.declaringInterpreter = declaringInterpreter;
        // initCallStack( namespace );
    }

    public NameSpace getNameSpace() {
        return namespace;
    }

    public String toString() {
        return "Bsh对象的'this'引用: " + namespace;
    }

    public void run() {
        try {
            invokeMethod("run", new Object[0]);
        } catch (EvalError e) {
            declaringInterpreter.error("runnable抛出异常:" + e);
        }
    }

    /**
     * Invoke specified method as from outside java code, using the declaring interpreter and
     * current namespace. The call stack will indicate that the method is being invoked from outside
     * of bsh in native java code. Note: you must still wrap/unwrap args/return values using
     * Primitive/Primitive.unwrap() for use outside of BeanShell.
     *
     * @see bsh.Primitive
     */
    public Object invokeMethod(String name, Object[] args) throws EvalError {
        // null callstack, one will be created for us
        return invokeMethod(name, args, null /*declaringInterpreter*/, null, null, false);
    }

    /**
     * Invoke a method in this namespace with the specified args, interpreter reference, callstack,
     * and caller info.
     *
     * <p>Note: If you use this method outside of the bsh package and wish to use variables with
     * primitive values you will have to wrap them using bsh.Primitive. Consider using This
     * getInterface() to make a true Java interface for invoking your scripted methods.
     *
     * <p>This method also implements the default object protocol of toString(), hashCode() and
     * equals() and the invoke() meta-method handling as a last resort.
     *
     * <p>Note: The invoke() meta-method will not catch the Object protocol methods (toString(),
     * hashCode()...). If you want to override them you have to script them directly.
     *
     * <p>
     *
     * @see bsh.This#invokeMethod(String, Object[], Interpreter, CallStack, SimpleNode, boolean)
     * @param callstack if callStack is null a new CallStack will be created and initialized with
     *     this namespace.
     * @param declaredOnly if true then only methods declared directly in the namespace will be
     *     visible - no inherited or imported methods will be visible.
     * @see bsh.Primitive
     */
    /*
    invokeMethod() here is generally used by outside code to callback
    into the bsh interpreter. e.g. when we are acting as an interface
    for a scripted listener, etc.  In this case there is no real call stack
    so we make a default one starting with the special JAVACODE namespace
    and our namespace as the next.
    */
    public Object invokeMethod(
            String methodName,
            Object[] args,
            Interpreter interpreter,
            CallStack callstack,
            SimpleNode callerInfo,
            boolean declaredOnly)
            throws EvalError {
        /*
        Wrap nulls.
        This is a bit of a cludge to address a deficiency in the class
        generator whereby it does not wrap nulls on method delegate.  See
        Class Generator.java.  If we fix that then we can remove this.
        (just have to generate the code there.)
        */
        if (args == null) {
            args = new Object[0];
        } else {
            Object[] oa = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                oa[i] = (args[i] == null ? Primitive.NULL : args[i]);
            }
            args = oa;
        }

        if (interpreter == null) interpreter = declaringInterpreter;
        if (callstack == null) callstack = new CallStack(namespace);
        if (callerInfo == null) callerInfo = SimpleNode.JAVACODE;

        // Find the bsh method
        Class[] types = Types.getTypes(args);
        BshMethod bshMethod = null;
        try {
            bshMethod = namespace.getMethod(methodName, types, declaredOnly);
        } catch (UtilEvalError e) {
            // leave null
        }

        if (bshMethod != null) return bshMethod.invoke(args, interpreter, callstack, callerInfo);

        /*
        No scripted method of that name.
        Implement the required part of the Object protocol:
        public int hashCode();
        public boolean equals(java.lang.Object);
        public java.lang.String toString();
        if these were not handled by scripted methods we must provide
        a default impl.
        */
        // a default toString() that shows the interfaces we implement
        if (methodName.equals("toString")) return toString();

        // a default hashCode()
        if (methodName.equals("hashCode")) return Integer.valueOf(this.hashCode());

        // a default equals() testing for equality with the This reference
        if (methodName.equals("equals")) {
            Object obj = args[0];
            return this == obj ? Boolean.TRUE : Boolean.FALSE;
        }

        // Look for a default invoke() handler method in the namespace
        // Note: this code duplicates that in NameSpace getCommand()
        // is that ok?
        try {
            bshMethod = namespace.getMethod("invoke", new Class[] {null, null});
        } catch (UtilEvalError e) {
            /*leave null*/
        }

        // Call script "invoke( String methodName, Object [] args );
        if (bshMethod != null)
            return bshMethod.invoke(
                    new Object[] {methodName, args}, interpreter, callstack, callerInfo);

        throw new EvalError(
                "方法 "
                        + StringUtil.methodString(methodName, types)
                        + " 在bsh脚本对象中找不到: "
                        + namespace.getName(),
                callerInfo,
                callstack);
    }

    /**
     * Bind a This reference to a parent's namespace with the specified declaring interpreter. Also
     * re-init the callstack. It's necessary to bind a This reference before it can be used after
     * deserialization. This is used by the bsh load() command.
     *
     * <p>This is a static utility method because it's used by a bsh command bind() and the
     * interpreter doesn't currently allow access to direct methods of This objects (small hack)
     */
    public static void bind(This ths, NameSpace namespace, Interpreter declaringInterpreter) {
        ths.namespace.setParent(namespace);
        ths.declaringInterpreter = declaringInterpreter;
    }

    /**
     * Allow invocations of these method names on This type objects. Don't give bsh.This a chance to
     * override their behavior.
     *
     * <p>If the method is passed here the invocation will actually happen on the bsh.This object
     * via the regular reflective method invocation mechanism. If not, then the method is evaluated
     * by bsh.This itself as a scripted method call.
     */
    static boolean isExposedThisMethod(String name) {
        return name.equals("getClass")
                || name.equals("invokeMethod")
                || name.equals("getInterface")
                // These are necessary to let us test synchronization from scripts
                || name.equals("wait")
                || name.equals("notify")
                || name.equals("notifyAll");
    }
}
