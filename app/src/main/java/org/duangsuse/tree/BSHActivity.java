package org.duangsuse.tree;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Created by duangsuse on 17-11-12.
 * Android Activity Beanshell binding
 */

public class BSHActivity extends Activity {
    public final Interpreter ngin;
    private String program;
    private String handler_fn;

    public String ActivityFile;

    public String onCreateCall;
    public String onPostCreateCall;
    public String onActivityResultCall;
    public String onDestroyCall;
    public String onPauseCall;
    public String onResumeCall;
    public String onPostResumeCall;
    public String onStartCall;
    public String onRestartCall;
    public String onStopCall;
    public String onNewIntentCall;
    public String onSaveInstanceStateCall;
    public String onRestoreInstanceStateCall;
    public String onApplyThemeResourceCall;
    public String onUserLeaveHintCall;
    public String onCreateOptionsMenuCall;
    public String onOptionsItemSelectedCall;
    public String onOptionsMenuClosedCall;
    public String onBackPressedCall;
    public String onKeyMultipleCall;
    public String onKeyLongPressCall;
    public String onLowMemoryCall;
    public String onWindowFocusChangedCall;
    public String onTouchEventCall;
    public String onKeyDownCall;
    public String onKeyUpCall;

    public Bundle bundle;

    public BSHActivity() {
        ngin = new Interpreter();
    }

    // Gets file path from intent and source it
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ngin.set("me", this);
            // eval script
            if ((ActivityFile = getIntent().getStringExtra(MainActivity.run_field)) != null)
                ngin.source(ActivityFile);
        } catch (Exception e) {
            ImportActivity.toast(this, e.getMessage());
        }
        // call bsh method
        if (onCreateCall != null) {
            bundle = savedInstanceState;
            InvokeMethod(onCreateCall);
        }
    }

    public void InvokeMethod(String name) {
        if (name == null) {
            return;
        }
        try {
            ngin.eval(name + "()");
        } catch (Exception e) {
            ImportActivity.toast(this, "Cannot Invoke Method: " + e.getMessage());
        }
    }

    public void SetParam(String name, Object param) {
        try {
            ngin.set("P" + name, param);
        } catch (Exception e) {
            ImportActivity.toast(this, "Failed to set param: " + e.getMessage());
        }
    }

    public void Import(String name) {
        try {
            ngin.source(
                    getExternalFilesDir(Environment.MEDIA_MOUNTED).toString() + "/" + name + ".bshL"
            );
        } catch (Exception e) {
            ImportActivity.toast(this, "Failed to import library: " + e.getMessage());
        }
    }

    // launch beanShell Activity
    public void beginActivity(String filename, int anim_in, int anim_out) {
        try {
            String file = getExternalFilesDir(Environment.MEDIA_MOUNTED).toString() + "/" + filename + ".bshA";
            Intent i = new Intent(this, BSHActivity.class);
            i.putExtra(MainActivity.run_field, file);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT > 16) {
                ActivityOptions ao = ActivityOptions.makeCustomAnimation(this, anim_in, anim_out);
                startActivity(i, ao.toBundle());
            } else
                startActivity(i);
        } catch (Exception e) {
            ImportActivity.toast(this, "Failed to Start BSH Activity: " + e.getMessage());
        }
    }

    public void beginActivity(String n) {
        beginActivity(n, android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void say(String s) {
        ImportActivity.toast(this, s);
    }

    //android bsh Runnable impl
    public abstract class BSHRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Object program_result = ngin.eval(program);
                ngin.set("Pthr", program_result);
                ngin.eval(handler_fn + "()");
            } catch (EvalError evalError) {
                evalError.printStackTrace();
            }
        }
    }

    public void go(String program, String handler_fn) {
        this.program = program;
        this.handler_fn = handler_fn;
        new Thread(new BSHRunnable() {
            @Override
            public void run() {
                super.run();
            }
        }).start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bundle = savedInstanceState;
        InvokeMethod(onPostCreateCall);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (onActivityResultCall != null) {
            SetParam("code", requestCode);
            SetParam("result", resultCode);
            SetParam("data", data);
            InvokeMethod(onActivityResultCall);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InvokeMethod(onDestroyCall);
    }

    @Override
    protected void onPause() {
        super.onPause();
        InvokeMethod(onPauseCall);
    }

    @Override
    protected void onResume() {
        super.onResume();
        InvokeMethod(onResumeCall);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        InvokeMethod(onPostResumeCall);
    }

    @Override
    protected void onStart() {
        super.onStart();
        InvokeMethod(onStartCall);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        InvokeMethod(onRestartCall);
    }

    @Override
    protected void onStop() {
        super.onStop();
        InvokeMethod(onStopCall);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (onNewIntentCall != null) {
            SetParam("intent", intent);
            InvokeMethod(onNewIntentCall);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (onSaveInstanceStateCall != null) {
            bundle = outState;
            InvokeMethod(onSaveInstanceStateCall);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (onRestoreInstanceStateCall != null) {
            bundle = savedInstanceState;
            InvokeMethod(onRestoreInstanceStateCall);
        }
    }

    @Override
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        super.onApplyThemeResource(theme, resid, first);
        if (onApplyThemeResourceCall != null) {
            SetParam("theme", theme);
            SetParam("resid", resid);
            SetParam("first", first);
            InvokeMethod(onApplyThemeResourceCall);
        }
    }

    /*@Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
    }

    @Override
    protected void onChildTitleChanged(Activity childActivity, CharSequence title) {
        super.onChildTitleChanged(childActivity, title);
    }*/

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        InvokeMethod(onUserLeaveHintCall);
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (onCreateOptionsMenuCall != null) {
            SetParam("menu", menu);
            InvokeMethod(onCreateOptionsMenuCall);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (onOptionsItemSelectedCall != null) {
            SetParam("item", item);
            InvokeMethod(onOptionsItemSelectedCall);
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
    }*/

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        if (onOptionsMenuClosedCall != null) {
            SetParam("menu", menu);
            InvokeMethod(onOptionsMenuClosedCall);
        }
    }

    /*@Override
    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        return super.onCreateThumbnail(outBitmap, canvas);
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (onKeyDownCall != null) {
            SetParam("code", keyCode);
            SetParam("event", event);
            InvokeMethod(onKeyDownCall);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (onKeyLongPressCall != null) {
            SetParam("code", keyCode);
            SetParam("event", event);
            InvokeMethod(onKeyLongPressCall);
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        if (onKeyMultipleCall != null) {
            SetParam("code", keyCode);
            SetParam("repeats", repeatCount);
            SetParam("event", event);
            InvokeMethod(onKeyMultipleCall);
        }
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (onKeyUpCall != null) {
            SetParam("code", keyCode);
            SetParam("event", event);
            InvokeMethod(onKeyUpCall);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InvokeMethod(onBackPressedCall);
    }

    /*@Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return super.onSearchRequested(searchEvent);
    }*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (onTouchEventCall != null) {
            SetParam("event", event);
            InvokeMethod(onTouchEventCall);
        }
        return super.onTouchEvent(event);
    }

    /*@Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return super.onPreparePanel(featureId, view, menu);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public View onCreatePanelView(int featureId) {
        return super.onCreatePanelView(featureId);
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
    }*/

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        InvokeMethod(onLowMemoryCall);
    }

    /*@Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig);
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }*/

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (onWindowFocusChangedCall != null) {
            SetParam("hasF", hasFocus);
            InvokeMethod(onWindowFocusChangedCall);
        }
    }
}
