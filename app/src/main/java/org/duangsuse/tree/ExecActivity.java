package org.duangsuse.tree;

import android.R.drawable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;

import java.util.Locale;

import bsh.Interpreter;

import static java.lang.String.format;

public class ExecActivity extends Activity {
    EditText textBackground;
    String program;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getActionBar() != null)
            getActionBar().setIcon(drawable.ic_secure);
        textBackground = new EditText(this);
        textBackground.setGravity(Gravity.TOP);
        textBackground.setTextSize(14);
        textBackground.setText("Loading info...");
        setContentView(textBackground);
        try {
            program = Uri.decode(getIntent().getDataString());
            program = program.replaceFirst("bsh:", "");
        } catch (Exception e) {
            e.printStackTrace();
            program = "me.toast(\"Failed to get program!\")";
            ImportActivity.toast(this, e.getMessage());
        }
        new AlertDialog.Builder(this)
                .setTitle("Run Script?")
                .setMessage(program)
                .setPositiveButton(
                        "↷",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int i) {
                                Intent mIntent = new Intent(ExecActivity.this, MainActivity.class);
                                mIntent.putExtra(MainActivity.code_field, program);
                                mIntent.putExtra(MainActivity.run_field, true);
                                startActivity(mIntent);
                                finish();
                            }
                        })
                .show();
        super.onCreate(savedInstanceState);
        PackageInfo packageinfo = new PackageInfo();
        try {
            PackageManager manager = this.getPackageManager();
            packageinfo = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (Exception ignored) {
        }
        textBackground.setText(
                format(Locale.ENGLISH, "Tree System information\n" +
                        "Package name: %s\n" +
                        "Version: %s\n" +
                        "Code: %s\n" +
                        "Debug: %s\n" +
                        "Perm: %s\n" +
                        "Updated: %d\n" +
                        "Bsh Engine: %s\n" +
                        "Android API Version: %s", getPackageName(), packageinfo.versionName, String.valueOf(packageinfo.versionCode), String.valueOf(BuildConfig.DEBUG), permissionString(packageinfo.permissions), packageinfo.lastUpdateTime, Interpreter.VERSION, Build.VERSION.SDK));
        textBackground.setOnLongClickListener(
                new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        try {
                            Intent i =
                                    new Intent(
                                            android.provider.Settings
                                                    .ACTION_APPLICATION_DETAILS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            i.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(i);
                        } catch (ActivityNotFoundException ex) {
                            Intent i =
                                    new Intent(
                                            android.provider.Settings
                                                    .ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            startActivity(i);
                        }
                        return false;
                    }
                });
    }

    public String permissionString(PermissionInfo[] p) {
        if (p == null) return "(check out in settings)";
        StringBuilder tmp = new StringBuilder();
        for (PermissionInfo info : p) {
            tmp.append(info.toString());
        }
        return tmp.toString();
    }
}
