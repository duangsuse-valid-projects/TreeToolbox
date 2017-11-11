package bsh;

import java.util.*;

public class StringUtil {

    public static String[] split(String s, String delim) {
        Vector v = new Vector();
        StringTokenizer st = new StringTokenizer(s, delim);
        while (st.hasMoreTokens()) v.addElement(st.nextToken());
        String[] sa = new String[v.size()];
        v.copyInto(sa);
        return sa;
    }

    public static String[] bubbleSort(String[] in) {
        Vector v = new Vector();
        for (int i = 0; i < in.length; i++) v.addElement(in[i]);

        int n = v.size();
        boolean swap = true;
        while (swap) {
            swap = false;
            for (int i = 0; i < (n - 1); i++)
                if (((String) v.elementAt(i)).compareTo(((String) v.elementAt(i + 1))) > 0) {
                    String tmp = (String) v.elementAt(i + 1);
                    v.removeElementAt(i + 1);
                    v.insertElementAt(tmp, i);
                    swap = true;
                }
        }

        String[] out = new String[n];
        v.copyInto(out);
        return out;
    }

    public static String maxCommonPrefix(String one, String two) {
        int i = 0;
        while (one.regionMatches(0, two, 0, i)) i++;
        return one.substring(0, i - 1);
    }

    public static String methodString(String name, Class[] types) {
        StringBuffer sb = new StringBuffer(name + "(");
        if (types.length > 0) sb.append(" ");
        for (int i = 0; i < types.length; i++) {
            Class c = types[i];
            sb.append(((c == null) ? "null" : c.getName()) + (i < (types.length - 1) ? ", " : " "));
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Split a filename into dirName, baseName
     *
     * @return String [] { dirName, baseName } public String [] splitFileName( String fileName ) {
     *     String dirName, baseName; int i = fileName.lastIndexOf( File.separator ); if ( i != -1 )
     *     { dirName = fileName.substring(0, i); baseName = fileName.substring(i+1); } else baseName
     *     = fileName;
     *     <p>return new String[] { dirName, baseName }; }
     */

    /** Hack - The real method is in Reflect.java which is not public. */
    public static String normalizeClassName(Class type) {
        return Reflect.normalizeClassName(type);
    }
}
