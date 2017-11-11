package org.duangsuse.tree;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import bsh.EvalError;
import bsh.Interpreter;

public class MainActivity extends Activity {
    public static String main_file = "/main.bsh";

    public static String code_field = "code";
    public static String run_field = "run";

    public Interpreter beanshell;
    // GUI widgets
    public LinearLayout mainLayout;
    public EditText mte;
    public Button b_eval;

    // Listeners
    public onToastListener tol;
    public String onToastCall;
    public onGetScriptListener getscript;
    public String onGetScriptCall;
    public onEvalStartListener estart;
    public String onEvalStartCall;
    public onEvalFinishListener efin;
    public String onEvalFinishCall;
    public onEvalFailListener efail;
    public String onEvalFailCall;

    // saved Exceptions & Strings
    public String toastText;
    public String scriptText;
    public String evalText;
    public Object evalResult;
    public Exception evalError;

    public static String inputStream2String(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    // listeners setter
    public void setOnToastListener(onToastListener l) {
        tol = l;
    }

    public void setOnGetScriptListener(onGetScriptListener l) {
        getscript = l;
    }

    public void setOnEvalStartListener(onEvalStartListener l) {
        estart = l;
    }

    public void setOnEvalFinishListener(onEvalFinishListener l) {
        efin = l;
    }

    public void setOnEvalFailListener(onEvalFailListener l) {
        efail = l;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new PrepareStubApp(getApplicationContext()).try_init_stub();
        // Initialize GUI Widgets
        mainLayout = new LinearLayout(this);
        mte = new EditText(this);
        b_eval = new Button(this);
        // Set up GUI Widgets
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mte.setTextSize(14);
        mte.setGravity(Gravity.TOP);
        mte.setHint(
                "ʕ•̀ω•́ʔ✧ put your awesome code here\n"
                        + "ctx is app context ✨\n"
                        + "me is this activity QWQ\n"
                        + "ngin is beanshell engine ⇋\n"
                        + "lay is this layout ❏\n"
                        + "NOTE: you can set strict java mode via ngin.setStrictJava, class is not supported ✘\n"
                        + "you can import java class using import ✯\n"
                        + "long press button to get cleared engine without android stuff ✳");
        b_eval.setText("Evaluate");
        mainLayout.addView(b_eval);
        mainLayout.addView(mte);
        setContentView(mainLayout);

        // Initialize beanshell engine
        beanshell = new Interpreter();
        try {
            objputs("me", MainActivity.this);
            objputs("ctx", MainActivity.this.getApplicationContext());
            objputs("ngin", beanshell);
            objputs("lay", mainLayout);
        } catch (Exception e) {
            toast(e.getMessage());
        }

        // set script text if launch intent includes code field
        Intent launch_intent = getIntent();
        String code = launch_intent.getStringExtra(code_field);
        boolean run_now = launch_intent.getBooleanExtra(run_field, false);
        Object ret;
        if (code != null) mte.setText(code);
        // run script and return result
        if (run_now) {
            ret = eval(getScript());
            Intent data = new Intent();
            if (ret == null) {
                finish();
                return;
            }
            data.putExtra(run_field, ret.getClass().getName());
            data.putExtra(code_field, ret.toString());
            if (getParent() == null) {
                setResult(Activity.RESULT_OK, data);
            } else {
                getParent().setResult(Activity.RESULT_OK, data);
            }
            finish();
            return;
        }

        FileInputStream f_plugin = null;
        try {
            f_plugin = new FileInputStream(getMainScript());
        } catch (Exception e) {
            toast(e.getMessage());
            toast("put main.bsh in my data dir(^_^)/");
        }

        if (f_plugin != null) {
            String program = "";
            try {
                program = inputStream2String(f_plugin);
            } catch (Exception e) {
                toast(e.getMessage());
            }
            eval(program);
        }

        b_eval.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Object o = eval(getScript());
                        if (o != null) {
                            dialog(o.getClass().getName(), o.toString());
                        }
                    }
                });
        b_eval.setOnLongClickListener(
                new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        // Destroy current engine and renew w/o put
                        toast("(=_=;)");
                        beanshell = new Interpreter();
                        return false;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        setTitle("Destroying...");
        String[] fields = {"me", "ngin", "ctx", "lay"};
        for (String s : fields) {
            setTitle(s);
            destroy(s);
        }
        beanshell = null;
        super.onDestroy();
    }

    // accept a string param and toast it
    public void toast(String s) {
        toastText = s;
        if (tol != null) s = tol.onToast(s);
        if (onToastCall != null) {
            Object o = invokeMethod(onToastCall);
            if (o instanceof String) {
                s = o.toString();
            }
        }
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    // show a dialog with certain title and message
    public void dialog(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).show();
    }

    // evaluate string
    public Object eval(String s) {
        evalText = s;
        if (estart != null) estart.onEvalStart(s);
        invokeMethod(onEvalStartCall);
        try {
            Object o = beanshell.eval(s);
            evalResult = o;
            if (efin != null) efin.onEvalFinish(o);
            invokeMethod(onEvalFinishCall);
            return o;
        } catch (Exception e) {
            evalError = e;
            if (efail != null) efail.onEvalFail(e);
            invokeMethod(onEvalFailCall);
            toast(e.toString());
        }
        return null;
    }

    // destroy bsh object
    public void destroy(String name) {
        try {
            beanshell.unset(name);
        } catch (EvalError e) {
            toast("Error in destroy Object:" + e.getMessage());
        }
    }

    // get main script location
    public String getMainScript() {
        return getExternalFilesDir(Environment.MEDIA_MOUNTED).toString() + main_file;
    }

    // call a method in shell
    public Object invokeMethod(String name) {
        if (name == null) return null;
        String stmt = name + "()";
        try {
            return beanshell.eval(stmt);
        } catch (Exception e) {
            toast("Error in call method" + e.getMessage());
        }
        return null;
    }

    // put object to interpreter
    public void objputs(String id, Object obj) {
        try {
            beanshell.set(id, obj);
        } catch (Throwable e) {
            toast(e.toString());
        }
    }

    // get mtv text
    public String getScript() {
        String text = mte.getText().toString();
        scriptText = text;
        if (getscript != null) text = getscript.onGetScript(text);
        if (onGetScriptCall != null) {
            Object o = invokeMethod(onGetScriptCall);
            if (o instanceof String) {
                text = o.toString();
            }
        }
        return text;
    }

    // Listeners for plug-ins
    public interface onToastListener {
        // on Toast text, accept text, return String override
        String onToast(String text);
    }

    public interface onGetScriptListener {
        // getScript() called, accept Script content, return String override
        String onGetScript(String text);
    }

    public interface onEvalStartListener {
        // on Start Evaluate, accept script text
        void onEvalStart(String s);
    }

    public interface onEvalFinishListener {
        // on Evaluate finished with OK, accept resulting object
        void onEvalFinish(Object o);
    }

    public interface onEvalFailListener {
        // on Evaluate failed, accept thrown exception
        void onEvalFail(Exception e);
    }
}
