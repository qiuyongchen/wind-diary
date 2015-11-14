package com.qiuyongchen.diary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.qiuyongchen.diary.data.DataSourceDiary;
import com.qiuyongchen.diary.data.DiaryItem;
import com.qiuyongchen.diary.json.JsonCenter;
import com.qiuyongchen.diary.util.FileUtil;
import com.qiuyongchen.diary.widget.systemBarTint.SystemBarTintManager;

import java.util.ArrayList;

/**
 * Created by qiuyongchen on 2015/10/15.
 */

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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isNight) {
            setTheme(R.style.AppTheme_Night);
            isNight = false;
        } else {
            setTheme(R.style.AppTheme);
            isNight = true;
        }
        editor.putBoolean("isNight", isNight);
        editor.apply();
        recreate();
    }

    public void OnClickExportToJson(View view) {
        export_json_to_sdcard();

        Toast.makeText(this, R.string.export_complete,
                Toast.LENGTH_LONG).show();

        Log.i("onPreferenceClick", " click export_to_json");
    }

    public void OnClickExportToTxt(View view) {
        exportDatabaseToTxt();

        Toast.makeText(this, R.string.export_complete,
                Toast.LENGTH_LONG).show();

        Log.i("onPreferenceClick", " click export_to_txt");
    }

    public void OnClickImportFromJson(View view) {
        if (import_json_from_sdcard()) {

            Toast.makeText(this, R.string.import_complete,
                    Toast.LENGTH_LONG).show();

            Log.i("onPreferenceClick",
                    " click import_from_json and succeed");
        } else {
            Toast.makeText(this,
                    R.string.import_complete_fail, Toast.LENGTH_LONG)
                    .show();

            Log.i("onPreferenceClick",
                    " click import_from_json and fail");
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

    public boolean exportDatabaseToTxt() {
        String fileDirName = getString(R.string.file_dir_name).toString();
        DataSourceDiary mDataSourceDiary = new DataSourceDiary(
                this);
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
                this.getApplicationContext());
        ArrayList<DiaryItem> mArrayList = mDataSourceDiary.getAllDiary();

        // transfer ArrayList to json which is String form.
        JsonCenter mJsonCenter = new JsonCenter();
        String json = mJsonCenter.export_to_local_json(mArrayList);

        Log.i("export_json_to_sdcard",
                "try to export "
                        + String.valueOf(mDataSourceDiary.getAllDiary()
                        .size()) + " diary item(s) into database");

        FileUtil.comprobarSDCard(this);

        // write json into the file in SD card.
        FileUtil.writeToSDCardFile(getString(R.string.file_dir_name), getString(R.string.export_to_local_json_file_name), json, false);
    }

    public boolean import_json_from_sdcard() {

        // get json from sd card
        String json = FileUtil.readFromSDCardFile(getString(R.string.file_dir_name),
                getString(R.string.export_to_local_json_file_name));

        if (json == null || json == "")
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
                this.getApplicationContext());
        mDataSourceDiary.importIntoDatabase(mArrayList);

        return true;
    }

}
