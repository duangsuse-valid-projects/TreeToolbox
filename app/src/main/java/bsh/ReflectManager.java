package bsh;

import bsh.Capabilities.Unavailable;

/**
 * ReflectManager is a dynamically loaded extension that supports extended reflection features
 * supported by JDK1.2 and greater.
 *
 * <p>In particular it currently supports accessible method and field access supported by JDK1.2 and
 * greater.
 */
public abstract class ReflectManager {
    private static ReflectManager rfm;

    /**
     * Return the singleton bsh ReflectManager.
     *
     * @throws Unavailable
     */
    public static ReflectManager getReflectManager() throws Unavailable {
        if (rfm == null) {
            Class clas;
            try {
                clas = Class.forName("bsh.reflect.ReflectManagerImpl");
                rfm = (ReflectManager) clas.newInstance();
            } catch (Exception e) {
                throw new Unavailable("没有反射管理器: " + e);
            }
        }

        return rfm;
    }

    /**
     * Reflect Manager Set Accessible. Convenience method to invoke the reflect manager.
     *
     * @throws Unavailable
     */
    public static boolean RMSetAccessible(Object obj) throws Unavailable {
        return getReflectManager().setAccessible(obj);
    }

    /**
     * Set a java.lang.reflect Field, Method, Constructor, or Array of accessible objects to
     * accessible mode.
     *
     * @return true if the object was accessible or false if it was not.
     */
    public abstract boolean setAccessible(Object o);
}
