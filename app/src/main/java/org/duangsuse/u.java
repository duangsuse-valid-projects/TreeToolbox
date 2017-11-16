package org.duangsuse;

import org.duangsuse.tree.MainActivity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

// this is a helper class for beanshell scripts
public class u {
    // like commandline util curl
    public static String ucat(String url) throws MalformedURLException, IOException {
        String buf;
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        InputStream is = conn.getInputStream();
        buf = MainActivity.inputStream2String(is);
        return buf;
    }

    // like commandline util cat
    public static String cat(String l) throws IOException {
        FileInputStream is = new FileInputStream(l);
        return MainActivity.inputStream2String(is);
    }

    // write to special file
    public static void wrt(String loc, String txt) throws IOException {
        FileWriter fw = new FileWriter(loc);
        BufferedWriter writer = new BufferedWriter(fw);
        writer.write(txt);
    }

    // append text to special file (newline)
    public static void append(String loc, String txt, String charset) throws IOException {
        PrintWriter pwrt = new PrintWriter(loc, charset);
        pwrt.println(txt);
    }

    public static void append(String l, String t) throws IOException {
        append(l, t, "utf-8");
    }
}
