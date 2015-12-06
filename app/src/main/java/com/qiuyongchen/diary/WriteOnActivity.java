package com.qiuyongchen.diary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.qiuyongchen.diary.data.DataSourceDiary;
import com.qiuyongchen.diary.date.DateAndTime;
import com.qiuyongchen.diary.event.NewDiaryIntoDBEvent;
import com.qiuyongchen.diary.widget.systemBarTint.SystemBarTintManager;

import de.greenrobot.event.EventBus;

public class WriteOnActivity extends Activity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    boolean isNight = false;
    String content;
    private EditText mEditText;
    private RelativeLayout mRelativeLayout;

    public void onClickSave(View v) {
        String content = mEditText.getText().toString();
        String date = DateAndTime.getCurrentDate();
        String time = DateAndTime.getCurrentTime();

        DataSourceDiary mDataSourceDiary = new DataSourceDiary(
                this.getApplicationContext());
        mDataSourceDiary.insert(content, date, time);

        mEditText.setText("");

        // 发布消息，“有新保存的日记啦！”
        EventBus.getDefault().post(new NewDiaryIntoDBEvent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = sharedPreferences.edit();
        isNight = sharedPreferences.getBoolean("night_mode", false);

        if (isNight) {
            this.setTheme(R.style.AppTheme_Night);
        } else {
            this.setTheme(R.style.AppTheme);
        }

        // change the color of Kitkat 's status bar
        setStatusStyle();

        setContentView(R.layout.activity_write_on);
        mEditText = (EditText) findViewById(R.id.editText);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rootRelative);
        mEditText.requestFocus();
        mEditText.setFocusable(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 将之前帮用户暂时保存的日记读取出来
        content = sharedPreferences.getString("edit_text_content", "");
        if (!content.isEmpty())
            mEditText.setText(content);

        // 文本编辑框的光标后移
        CharSequence text = mEditText.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }

        // 延时弹出键盘（等待布局的完成）
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mEditText, 0);
            }
        }, 200);

        // 监听键盘是否被隐藏
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final View activityRootView = findViewById(R.id.rootRelative);
                activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                        if (heightDiff > 100) { // 如果高度差超过100像素，就很有可能是有软键盘...

                        } else {

                            Intent intent = new Intent(WriteOnActivity.this, MainActivity.class);

                            setResult(RESULT_OK, intent);

                            finish();

                            overridePendingTransition(R.anim.hold, R.anim.fade);
                        }
                    }
                });
            }
        }, 300);

    }

    @Override
    protected void onPause() {
        super.onPause();

        // 暂时代替用户保存日记
        String strEditText = mEditText.getText().toString();
        if (!strEditText.equals(content)) {
            content = strEditText;
            mEditor.putString("edit_text_content", content);
            mEditor.commit();
        }
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
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
