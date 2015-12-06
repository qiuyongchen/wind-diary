package com.qiuyongchen.diary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.qiuyongchen.diary.data.DataSourceDiary;
import com.qiuyongchen.diary.data.DiaryItem;
import com.qiuyongchen.diary.event.NightModeChangedEvent;
import com.qiuyongchen.diary.json.JsonCenter;
import com.qiuyongchen.diary.util.FileUtil;
import com.qiuyongchen.diary.widget.materialdesign.widgets.Dialog;
import com.qiuyongchen.diary.widget.systemBarTint.SystemBarTintManager;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


/**
 * Created by qiuyongchen on 2015/10/15.
 */

public class SettingActivity extends Activity {
    private static final int REQ_CREATE_PATTERN = 1;

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
            SettingsFragment mSettingsFragment = new SettingsFragment();
            replaceFragment(R.id.settings_container, mSettingsFragment);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    // 监听夜间模式
    public void onEventMainThread(NightModeChangedEvent event) {
        Log.e("onEvent", "got a message");
        if (event.getNightMode()) {
            this.recreate();
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

    /**
     * A placeholder fragment containing a settings view.
     */
    public static class SettingsFragment extends PreferenceFragment implements
            Preference.OnPreferenceClickListener {
        private Preference night_mode;
        private Preference export_to_json;
        private Preference export_to_txt;
        private Preference import_from_json;
        private Preference lock_pattern_test;
        private Preference about;

        public SettingsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_setting);

            night_mode = this.findPreference("night_mode");
            export_to_json = this.findPreference("export_to_json");
            export_to_txt = this.findPreference("export_to_txt");
            import_from_json = this.findPreference("import_from_json");
            lock_pattern_test = this.findPreference("lock_pattern_test");
            about = this.findPreference("about");

            night_mode.setOnPreferenceClickListener(this);
            export_to_json.setOnPreferenceClickListener(this);
            export_to_txt.setOnPreferenceClickListener(this);
            import_from_json.setOnPreferenceClickListener(this);
            lock_pattern_test.setOnPreferenceClickListener(this);
            about.setOnPreferenceClickListener(this);

        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == night_mode) {
                EventBus.getDefault().post(new NightModeChangedEvent(true)); // 发出夜间模式改变的消息
                Log.e("onPreferenceClick", "preference == night_mode");

            } else if (preference == export_to_json) {
                export_json_to_sdcard();
                Toast.makeText(getActivity(), R.string.export_complete,
                        Toast.LENGTH_LONG).show();
                Log.i("onPreferenceClick", " click export_to_json");

            } else if (preference == export_to_txt) {
                exportDatabaseToTxt();
                Toast.makeText(getActivity(), R.string.export_complete,
                        Toast.LENGTH_LONG).show();
                Log.i("onPreferenceClick", " click export_to_txt");

            } else if (preference == import_from_json) {
                if (import_json_from_sdcard()) {
                    Toast.makeText(getActivity(), R.string.import_complete,
                            Toast.LENGTH_LONG).show();
                    Log.i("onPreferenceClick",
                            " click import_from_json and succeed");

                } else {
                    Toast.makeText(getActivity(),
                            R.string.import_complete_fail, Toast.LENGTH_LONG)
                            .show();
                    Log.i("onPreferenceClick",
                            " click import_from_json and fail");

                }
            } else if (preference == lock_pattern_test) {
                Intent intent = new Intent(this.getActivity(), CreateLockPatternActivity.class);
                startActivity(intent);

            } else if (preference == about) {
                String title = getString(R.string.about);
                String message = getString(R.string.about_content);
                Dialog dialog = new Dialog(this.getActivity(), title, message);
                Log.i("FragmentMenu", dialog.getMessage());
                dialog.show();
                Log.i("onPreferenceClick", " click about");
            }

            return false;
        }

        public boolean exportDatabaseToTxt() {
            String fileDirName = getString(R.string.file_dir_name);
            DataSourceDiary mDataSourceDiary = new DataSourceDiary(
                    this.getActivity().getApplication());
            ArrayList<DiaryItem> mArrayList = mDataSourceDiary.getAllDiary();

            while (!mArrayList.isEmpty()) {

                DiaryItem oneItem = mArrayList.get(0);
                mArrayList.remove(0);

                String date = oneItem.date;
                String text = oneItem.time + "\n    " + oneItem.content
                        + "\n\n";
                int num = mArrayList.size();
                for (int i = 0; i < num; i++) {
                    DiaryItem item = mArrayList.get(i);
                    // if this item's date is the same as the 'oneItem', it will
                    // be
                    // deleted from the array.

                    if (item.date.equals(date)) {
                        Log.e(date, item.date);
                        text += item.time + "\n    " + item.content + "\n\n";
                        mArrayList.remove(i);
                        i--;
                        num--;
                    }
                }

                FileUtil.writeToSDCardFile(fileDirName, date + ".txt",
                        text, false);
            }

            return true;
        }

        public void export_json_to_sdcard() {

            // get the export ArrayList from database.
            DataSourceDiary mDataSourceDiary = new DataSourceDiary(
                    this.getActivity().getApplicationContext());
            ArrayList<DiaryItem> mArrayList = mDataSourceDiary.getAllDiary();

            // transfer ArrayList to json which is String form.
            JsonCenter mJsonCenter = new JsonCenter();
            String json = mJsonCenter.export_to_local_json(mArrayList);

            Log.i("export_json_to_sdcard",
                    "try to export "
                            + String.valueOf(mDataSourceDiary.getAllDiary()
                            .size()) + " diary item(s) into database");

            FileUtil.comprobarSDCard(this.getActivity().getApplication());

            // write json into the file in SD card.
            FileUtil.writeToSDCardFile(getString(R.string.file_dir_name), getString(R.string.export_to_local_json_file_name), json, false);
        }

        public boolean import_json_from_sdcard() {

            // get json from sd card
            String json = FileUtil.readFromSDCardFile(getString(R.string.file_dir_name),
                    getString(R.string.export_to_local_json_file_name));

            if (json == null || json.isEmpty())
                return false;

            // transfer the json to ArrayList<DiaryItem> in JsonCenter
            JsonCenter mJsonCenter = new JsonCenter();
            ArrayList<DiaryItem> mArrayList = mJsonCenter
                    .import_from_local_json(json);

            Log.d("import_json_from_sdcard",
                    "try to import " + String.valueOf(mArrayList.size())
                            + "diary items into database");

            // insert all of these diaryitem got above into database
            DataSourceDiary mDataSourceDiary = new DataSourceDiary(
                    this.getActivity().getApplicationContext());
            mDataSourceDiary.importIntoDatabase(mArrayList);

            return true;
        }

    }
}
