package com.qiuyongchen.diary;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.qiuyongchen.diary.widget.systemBarTint.SystemBarTintManager;

/**
 * Created by qiuyongchen on 2015/10/21.
 */

public class StatisticsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MainActivity.isNight) {
            this.setTheme(R.style.AppTheme_Night);
        } else {
            this.setTheme(R.style.AppTheme);
        }
        // change the color of Kitkat 's status bar
        setStatusStyle();
        setContentView(R.layout.activity_statistics);
    }

    public void OnClickSetting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    // 用于android4.4以上平台的状态栏变色(android5.0系统已经原生支持变色）
    private void setStatusStyle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            setTranslucentStatus(true);
        } else {
            return;
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);

        if (MainActivity.isNight)
            tintManager.setStatusBarTintResource(R.color.default_primary_color_night);
        else
            tintManager.setStatusBarTintResource(R.color.default_primary_color
            );
    }

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
