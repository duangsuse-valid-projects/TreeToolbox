package bsh.classpath;

import bsh.BshClassManager;
import bsh.classpath.BshClassPath.ClassSource;
import java.io.*;
import java.util.*;

/**
 * A classloader which can load one or more classes from specified sources. Because the classes are
 * loaded via a single classloader they change as a group and any versioning cross dependencies can
 * be managed.
 */
public class DiscreteFilesClassLoader extends BshClassLoader {
    /** Map of class sources which also implies our coverage space. */
    ClassSourceMap map;

    public static class ClassSourceMap extends HashMap {
        public void put(String name, ClassSource source) {
            super.put(name, source);
        }

        public ClassSource get(String name) {
            return (ClassSource) super.get(name);
        }
    }

    public DiscreteFilesClassLoader(BshClassManager classManager, ClassSourceMap map) {
        super(classManager);
        this.map = map;
    }

    /** */
    public Class findClass(String name) throws ClassNotFoundException {
        // Load it if it's one of our classes
        ClassSource source = map.get(name);

        if (source != null) {
            byte[] code = source.getCode(name);
            return defineClass(name, code, 0, code.length);
        } else
            // Let superclass BshClassLoader (URLClassLoader) findClass try
            // to find the class...
            return super.findClass(name);
    }

    public String toString() {
        return super.toString() + "对于文件: " + map;
    }
}
