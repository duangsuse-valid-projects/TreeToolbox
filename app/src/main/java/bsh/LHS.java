package bsh;

import java.lang.reflect.Field;

/**
 * An LHS is a wrapper for an variable, field, or property. It ordinarily holds the "left hand side"
 * of an assignment and may be either resolved to a value or assigned a value.
 *
 * <p>There is one special case here termed METHOD_EVAL where the LHS is used in an intermediate
 * evaluation of a chain of suffixes and wraps a method invocation. In this case it may only be
 * resolved to a value and cannot be assigned. (You can't assign a value to the result of a method
 * call e.g. "foo() = 5;").
 *
 * <p>
 */
class LHS implements ParserConstants, java.io.Serializable {
    NameSpace nameSpace;
    /** The assignment should be to a local variable */
    boolean localVar;

    /** Identifiers for the various types of LHS. */
    static final int VARIABLE = 0, FIELD = 1, PROPERTY = 2, INDEX = 3, METHOD_EVAL = 4;

    int type;

    String varName;
    String propName;
    Field field;
    Object object;
    int index;

    /**
     * @param localVar if true the variable is set directly in the This reference's local scope. If
     *     false recursion to look for the variable definition in parent's scope is allowed. (e.g.
     *     the default case for undefined vars going to global).
     */
    LHS(NameSpace nameSpace, String varName, boolean localVar) {
        type = VARIABLE;
        this.localVar = localVar;
        this.varName = varName;
        this.nameSpace = nameSpace;
    }

    /**
     * Static field LHS Constructor. This simply calls Object field constructor with null object.
     */
    LHS(Field field) {
        type = FIELD;
        this.object = null;
        this.field = field;
    }

    /** Object field LHS Constructor. */
    LHS(Object object, Field field) {
        if (object == null) throw new NullPointerException("架构空的LHS");

        type = FIELD;
        this.object = object;
        this.field = field;
    }

    /** Object property LHS Constructor. */
    LHS(Object object, String propName) {
        if (object == null) throw new NullPointerException("架构空的LHS");

        type = PROPERTY;
        this.object = object;
        this.propName = propName;
    }

    /** Array index LHS Constructor. */
    LHS(Object array, int index) {
        if (array == null) throw new NullPointerException("架构空的LHS");

        type = INDEX;
        this.object = array;
        this.index = index;
    }

    public Object getValue() throws UtilEvalError {
        if (type == VARIABLE) return nameSpace.getVariableOrProperty(varName, null);
        // return nameSpace.getVariable( varName );

        if (type == FIELD)
            try {
                Object o = field.get(object);
                return Primitive.wrap(o, field.getType());
            } catch (IllegalAccessException e2) {
                throw new UtilEvalError("不能读字段: " + field);
            }

        if (type == PROPERTY) {
            // return the raw type here... we don't know what it's supposed
            // to be...
            CollectionManager cm = CollectionManager.getCollectionManager();
            if (cm.isMap(object)) return cm.getFromMap(object /*map*/, propName);
            else
                try {
                    return Reflect.getObjectProperty(object, propName);
                } catch (ReflectError e) {
                    Interpreter.debug(e.getMessage());
                    throw new UtilEvalError("无属性: " + propName);
                }
        }

        if (type == INDEX)
            try {
                return Reflect.getIndex(object, index);
            } catch (Exception e) {
                throw new UtilEvalError("数组访问: " + e);
            }

        throw new InterpreterError("LHS类型");
    }

    /** Assign a value to the LHS. */
    public Object assign(Object val, boolean strictJava) throws UtilEvalError {
        if (type == VARIABLE) {
            // Set the variable in namespace according to localVar flag
            if (localVar) nameSpace.setLocalVariableOrProperty(varName, val, strictJava);
            else nameSpace.setVariableOrProperty(varName, val, strictJava);
        } else if (type == FIELD) {
            try {
                // This should probably be in Reflect.java
                ReflectManager.RMSetAccessible(field);
                field.set(object, Primitive.unwrap(val));
                return val;
            } catch (NullPointerException e) {
                throw new UtilEvalError("LHS (" + field.getName() + ") 非静态字段.");
            } catch (IllegalAccessException e2) {
                throw new UtilEvalError("LHS (" + field.getName() + ") 不能访问字段: " + e2);
            } catch (IllegalArgumentException e3) {
                String type =
                        val instanceof Primitive
                                ? ((Primitive) val).getType().getName()
                                : val.getClass().getName();
                throw new UtilEvalError(
                        "参数类型不对. " + (val == null ? "null" : type) + " 不能声明到字段 " + field.getName());
            }
        } else if (type == PROPERTY) {
            CollectionManager cm = CollectionManager.getCollectionManager();
            if (cm.isMap(object)) cm.putInMap(object /*map*/, propName, Primitive.unwrap(val));
            else
                try {
                    Reflect.setObjectProperty(object, propName, val);
                } catch (ReflectError e) {
                    Interpreter.debug("声明: " + e.getMessage());
                    throw new UtilEvalError("无相应属性: " + propName);
                }
        } else if (type == INDEX)
            try {
                Reflect.setIndex(object, index, val);
            } catch (UtilTargetError e1) { // pass along target error
                throw e1;
            } catch (Exception e) {
                throw new UtilEvalError("声明: " + e.getMessage());
            }
        else throw new InterpreterError("未知的lhs");

        return val;
    }

    public String toString() {
        return "LHS: "
                + ((field != null) ? "字段 = " + field.toString() : "")
                + (varName != null ? " 变量名 = " + varName : "")
                + (nameSpace != null ? " 命名空间 = " + nameSpace.toString() : "");
    }
}
