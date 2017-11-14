package org.duangsuse.tree;

import android.R.drawable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.InputStream;

import bsh.Interpreter;

public class ImportActivity extends Activity {
    public static String name_field = "f";
    public static String run_field = "r";
    public EditText mEditText;
    public String program;
    public Interpreter bsh;
    public Intent s;
    public Intent shortcutIntent;

    public static Uri getUriWithLoc(String loc) {
        return new Uri.Builder().path(loc).build();
    }

    public static void toast(Context ctx, String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle b) {
        try {
            getActionBar().setIcon(drawable.ic_input_add);
        } catch (NullPointerException ignored) {
        }
        if (getIntent().getBooleanExtra(run_field, false)) {
            try {
                readFile(new FileInputStream(getIntent().getStringExtra(name_field)));
            } catch (Exception e) {
                toast(this, name_field + " field not found, check your intent");
            }
            exec(program);
            super.onCreate(b);
            finish();
            return;
        }
        InputStream fileinput = null;
        try {
            //noinspection ConstantConditions
            fileinput = getContentResolver().openInputStream(getIntent().getData());
        } catch (Exception e) {
            // toast(this, "File path not found, check your intent.");
        }
        try {
            if (fileinput == null)
                fileinput = openFileInput(getIntent().getStringExtra(name_field));
        } catch (Exception e) {
            toast(this, "neither file path nor file url found");
            finish();
            return;
        }
        readFile(fileinput);

        s = new Intent();
        mEditText = new EditText(this);
        bsh = new Interpreter();
        try {
            bsh.set("me", this);
        } catch (Exception e) {
            toast(this, e.getMessage());
        }
        setContentView(mEditText);
        mEditText.setGravity(Gravity.TOP);
        mEditText.setTextSize(14);
        mEditText.setHintTextColor(Color.GREEN);
        mEditText.setHint("me -> this activity\ns and shortcutIntent -> intent to set up");
        mEditText.setText(
                "import android.content.*;\n"
                        + "import android.*;\n"
                        + "import "
                        + getPackageName()
                        + ".*;\n"
                        + "me.shortcutIntent = new Intent(me, ImportActivity.class);\n"
                        + "me.s.putExtra(Intent.EXTRA_SHORTCUT_INTENT, me.shortcutIntent);\n"
                        + "me.s.setAction(\"com.android.launcher.action.INSTALL_SHORTCUT\");\n"
                        + "me.s.putExtra(Intent.EXTRA_SHORTCUT_NAME, \"bshFooScript\");\n"
                        + "me.s.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(me, R.drawable.ic_media_play));\n"
                        + "me.shortcutIntent.putExtra(me.name_field, me.getIntent().getData().getPath());\n"
                        + "me.shortcutIntent.putExtra(me.run_field, true);\n"
                        + "me.sendBroadcast(me.s);");
        super.onCreate(b);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Ok ✔");
        menu.add("Execute, not shortcut ⇒");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String t = item.getTitle().toString();
        if (t.startsWith("O")) {
            try {
                bsh.eval(mEditText.getText().toString());
            } catch (Exception e) {
                toast(this, e.getMessage());
            }
        } else {
            exec(program);
        }
        return super.onOptionsItemSelected(item);
    }

    public void exec(String code) {
        Intent mIntent = new Intent(ImportActivity.this, MainActivity.class);
        mIntent.putExtra(MainActivity.code_field, code);
        mIntent.putExtra(MainActivity.run_field, true);
        startActivity(mIntent);
        finish();
    }

    public void readFile(InputStream is) {
        try {
            program = MainActivity.inputStream2String(is);
        } catch (Exception e) {
            Toast.makeText(ImportActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
