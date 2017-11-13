package org.duangsuse.tree;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* Creates init script for built-on mbsh apps
 * This will search for stub.list in App assets
 * If file present, move all files declared in it splited
 * with ';' into new path.
 * If file not present, then search for z.ensure
 * if z.ensure found, it will try to move app.zip
 * into new path, and then unzip it. if not, just toast a warning
 * and return.
 * WARN zipped stub is now DEPRECATED since minimized app config zip apk well
 */

public class PrepareStubApp {
    // static String f_zip_ensure = "z.ensure";
    static String file_package_list = "stub.list";
    Context mContext;
    String file_path;
    String main_shell_path;
    // static String f_zip = "app.zip";

    PrepareStubApp(Context ctx) {
        mContext = ctx;
        file_path = getStubAppPath();
        main_shell_path = file_path + MainActivity.main_file;
    }

    public static boolean isFile(String loc) {
        File f = new File(loc);
        return f.isFile();
    }

    public static void wrtFile(InputStream is, String loc) throws IOException {
        FileOutputStream fos = new FileOutputStream(loc);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
        }
    }

    public void try_init_stub() {
        AssetManager am = mContext.getAssets();

        if (isFile(main_shell_path)) return;
        try {
            am.open(file_package_list);
        } catch (IOException e) {
            return;
        }

        InputStream pis;
        String[] pathes;
        try {
            pis = am.open(file_package_list);
            String content = MainActivity.inputStream2String(pis);
            pathes = split_path(content);
        } catch (IOException e) {
            return;
        }
        for (String i : pathes) {
            try {
                String parent = new File(file_path + i).getParent();
                new File(parent).mkdirs();
                wrtFile(am.open(i), file_path + i);
            } catch (IOException e) {
                ImportActivity.toast(mContext, "Failed to unzip " + i);
            }
        }
    }

    public String getStubAppPath() {
        return mContext.getExternalFilesDir(Environment.MEDIA_MOUNTED).getPath() + "/";
    }

    public String[] split_path(String s) {
        String[] ret = s.split(";");
        int i = 0;
        String tmp;
        for (String l : ret)
            if (!(tmp = l.trim()).equals("")) {
                ret[i] = tmp;
                i++;
            } else ImportActivity.toast(mContext, "blank in split path at ln " + String.valueOf(i));
        return ret;
    }
}
