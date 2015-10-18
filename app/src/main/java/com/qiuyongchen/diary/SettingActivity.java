package com.qiuyongchen.diary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.qiuyongchen.diary.widget.SystemBarTintManager;
import com.qiuyongchen.diary.widget.lockPattern.LockPatternActivity;

public class SettingActivity extends Activity {
    private Button c;
    private boolean isNight = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("UserStyle", Context.MODE_WORLD_READABLE);
        isNight = sharedPreferences.getBoolean("isNight", false);
        if (isNight) {
            this.setTheme(R.style.AppTheme_Night);
        } else {
            this.setTheme(R.style.AppTheme);
        }

        // change the color of Kitkat 's status bar
        setStatusStyle();

        setContentView(R.layout.activity_setting);

    }


    public void OnClickNight(View view) {
/*        Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null,
                this, SettingActivity.class);
        startActivityForResult(intent, REQ_CREATE_PATTERN);

        this.finish();*/
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isNight) {
            setTheme(R.style.AppTheme_Night);
            isNight = false;
        } else {
            setTheme(R.style.AppTheme);
            isNight = true;
        }
        editor.putBoolean("isNight", isNight);
        editor.commit();

        recreate();
    }


    private void setStatusStyle() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.attr.colorPrimary);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
