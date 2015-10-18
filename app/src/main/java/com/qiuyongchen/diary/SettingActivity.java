package com.qiuyongchen.diary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.qiuyongchen.diary.widget.lockPattern.LockPatternActivity;

public class SettingActivity extends AppCompatActivity {
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

        c = (Button) findViewById(R.id.button_c);

        setContentView(R.layout.activity_setting);

    }

    public void OnClickPP(View view) {

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

    }

}
