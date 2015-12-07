package com.qiuyongchen.diary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.qiuyongchen.diary.widget.materialdesign.views.ButtonRectangle;
import com.qiuyongchen.diary.widget.systemBarTint.SystemBarTintManager;

import java.util.Arrays;

import haibison.android.lockpattern.LockPatternActivity;
import haibison.android.lockpattern.utils.AlpSettings;

/**
 * Created by qiuyongchen on 2015/12/4.
 */
public class CreateLockPatternActivity extends Activity {

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
            LockPatternFragment mLockPatternFragment = new LockPatternFragment();
            replaceFragment(R.id.settings_container, mLockPatternFragment);
        }

        // 让第三方手势密码库自动保存手势密码到Preference
        AlpSettings.Security.setAutoSavePattern(getApplicationContext(), true);
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

    /**
     * A placeholder fragment containing a settings view.
     */
    public static class LockPatternFragment extends PreferenceFragment implements
            Preference.OnPreferenceChangeListener {

        private static final int REQ_ENTER_PATTERN = 2;
        private Preference lock_pattern_on;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_lock_pattern);
            lock_pattern_on = this.findPreference("lock_pattern_on");
            lock_pattern_on.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            if (preference.getKey().equals("lock_pattern_on")) {
                SwitchPreference service = (SwitchPreference) preference;
                service.setChecked((Boolean) value);

                // 由未开启状态转为开启状态
                if ((Boolean) value) {
                    // 默认没有创建成功，手势密码不存在
                    PreferenceManager.getDefaultSharedPreferences(this.getActivity()
                            .getApplicationContext()).edit().
                            putBoolean("lock_pattern_on", false).commit();

                    this.getFragmentManager().beginTransaction().replace(R.id.settings_container,
                            new CreatePasswordFragment()).commit();

                } else {
                    // 默认没有取消成功，手势密码依旧存在
                    PreferenceManager.getDefaultSharedPreferences(this.getActivity()
                            .getApplicationContext()).edit().
                            putBoolean("lock_pattern_on", true).commit();

                    // 取消手势密码，要先验证
                    LockPatternActivity.IntentBuilder.newPatternComparator(getActivity().getApplicationContext())
                            .startForResult(this, REQ_ENTER_PATTERN);
                }

            }

            return true;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case REQ_ENTER_PATTERN: {
                    /**
                     * NOTE that there are 4 possible result codes!!!
                     */
                    switch (resultCode) {
                        case RESULT_OK:
                            // 只有成功验证手势密码后才能取消手势密码
                            PreferenceManager.getDefaultSharedPreferences(this.getActivity()
                                    .getApplicationContext()).edit().
                                    putBoolean("lock_pattern_on", false).commit();
                            break;
                        case RESULT_CANCELED:
                            // The user cancelled the task
                            break;
                        case LockPatternActivity.RESULT_FAILED:
                            // The user failed to enter the pattern
                            break;
                        case LockPatternActivity.RESULT_FORGOT_PATTERN:
                            // The user forgot the pattern and invoked your recovery Activity.
                            break;
                    }

                    /**
                     * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
                     * the number of tries that the user did.
                     */
                    int retryCount = data.getIntExtra(LockPatternActivity.EXTRA_RETRY_COUNT, 0);

                    break;
                }// REQ_ENTER_PATTERN
            }
            this.getFragmentManager().beginTransaction().replace(R.id.settings_container,
                    new LockPatternFragment()).commit();
        }

    }

    /**
     * 创建二次密码，用户忘记手势密码后，可以根据这个密码修改手势密码。
     */
    public static class CreatePasswordFragment extends Fragment {
        private static final int REQ_CREATE_PATTERN = 1;
        private ButtonRectangle mButtonAccept;
        private EditText mEditText;
        private int confirm_times_need = 3;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater
                    .inflate(R.layout.activity_lock_pattern_create_password,
                            container, false);

            mButtonAccept = (ButtonRectangle) view.findViewById(R.id.button_accept);
            mEditText = (EditText) view.findViewById(R.id.editText);

            mButtonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mEditText.getText().toString().equals("")) {
                        // 点击3次“确认”按钮
                        if (confirm_times_need != 1) {
                            confirm_times_need--;
                            mButtonAccept.setText("确认 (" + String.valueOf(confirm_times_need) + ")");
                        } else {
                            // 保存好密码
                            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                                    .putString("lock_pattern_password", mEditText.getText()
                                            .toString()).commit();
                            // 调用第三方库，创建手势密码
                            startCreateDialog();
                        }
                    }
                }
            });

            return view;
        }

        // 正式开启第三方的dialog
        public void startCreateDialog() {
            LockPatternActivity.IntentBuilder.newPatternCreator(getActivity().getApplicationContext())
                    .startForResult(this, REQ_CREATE_PATTERN);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {

            switch (requestCode) {
                case REQ_CREATE_PATTERN: {
                    if (resultCode == RESULT_OK) {
                        char[] pattern = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
                        Log.e("xxxxxxxxxxxxxxx", Arrays.toString(pattern));

                        // 只有成功创建手势密码后才能开启手势密码
                        PreferenceManager.getDefaultSharedPreferences(
                                this.getActivity().getApplicationContext()).edit().
                                putBoolean("lock_pattern_on", true).commit();
                    } else {

                    }

                    break;
                }// REQ_CREATE_PATTERN
            }
            this.getFragmentManager().beginTransaction().replace(R.id.settings_container,
                    new LockPatternFragment()).commit();
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

                        getFragmentManager().beginTransaction().replace(R.id.settings_container,
                                new LockPatternFragment()).commit();

                        return true;

                    }

                    return false;
                }
            });
        }

        @Override
        public void onPause() {
            super.onPause();
        }

    }
}

