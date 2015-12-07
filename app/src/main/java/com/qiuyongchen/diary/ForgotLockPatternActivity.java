package com.qiuyongchen.diary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.qiuyongchen.diary.widget.materialdesign.views.ButtonRectangle;
import com.qiuyongchen.diary.widget.systemBarTint.SystemBarTintManager;

public class ForgotLockPatternActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNight = sharedPreferences.getBoolean("night_mode", false);
        if (isNight) {
            this.setTheme(R.style.AppTheme_Night);
        } else {
            this.setTheme(R.style.AppTheme);
        }

        // change the color of Kitkat 's status bar
        setStatusStyle();

        setContentView(R.layout.activity_setting);
        if (savedInstanceState == null) {
            EnterLockPatternPassword mEnterLockPatternPassword = new EnterLockPatternPassword();
            replaceFragment(R.id.settings_container, mEnterLockPatternPassword);
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void replaceFragment(int viewId, android.app.Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }

    public static class EnterLockPatternPassword extends Fragment {
        private ButtonRectangle mButtonAccept;
        private EditText mEditText;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater
                    .inflate(R.layout.activity_lock_pattern_forgot,
                            container, false);

            mButtonAccept = (ButtonRectangle) view.findViewById(R.id.button_accept);
            mEditText = (EditText) view.findViewById(R.id.editText);

            mButtonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mEditText.getText().toString().equals("")) {
                        // 取出之前用户输入的验证密码
                        String lockPatternPassword = PreferenceManager.
                                getDefaultSharedPreferences(getActivity().getApplicationContext())
                                .getString("lock_pattern_password", "");

                        // 如果输入了正确的验证密码，取消手势密码，并跳转到MainActivity
                        if (mEditText.getText().toString().equals(lockPatternPassword)) {
                            PreferenceManager.
                                    getDefaultSharedPreferences(getActivity().
                                            getApplicationContext()).edit().
                                    putBoolean("lock_pattern_on", false).commit();

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);

                            // 因为这个Activity的特殊性，不需要的时候必须关闭
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), getString(
                                            R.string.enter_lock_pattern_password_wrong),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            return view;
        }

        @Override
        public void onResume() {
            super.onResume();

            // 用户按下返回按钮时，可以从这个fragment返回上一个fragment
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                        getActivity().finish();

                        return true;

                    }

                    return false;
                }
            });
        }
    }
}
