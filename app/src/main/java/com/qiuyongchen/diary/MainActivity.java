package com.qiuyongchen.diary;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qiuyongchen.diary.fragments.FragmentView;
import com.qiuyongchen.diary.fragments.FragmentWriteOff;
import com.qiuyongchen.diary.fragments.MyFragmentPagerAdapter;
import com.qiuyongchen.diary.fragments.MyFragmentPageChangeListener;
import com.qiuyongchen.diary.widget.SystemBarTintManager;
import com.qiuyongchen.diary.widget.lockPattern.LockPatternActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    public static ImageView mImageView;
    public static int mDPI;
    public static int mTabWidget;
    public static InputMethodManager mIMM;
    public static View mLayoutRoot;

    private ArrayList<Fragment> mFragments;
    private Button mButtonLeft;
    private Button mButtonRight;
    private ViewPager mViewPager;


    private Button c;
    // This is your preferred flag
    private static final int REQ_CREATE_PATTERN = 1;

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
        // setStatusStyle();

        setContentView(R.layout.activity_main);

        initView();

        initViewPager();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {
        mLayoutRoot = findViewById(R.id.RelativeLayoutContent);
        mViewPager = (ViewPager) findViewById(R.id.ViewPager);
        mIMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mButtonLeft = (Button) findViewById(R.id.buttonLeft);
        mButtonRight = (Button) findViewById(R.id.buttonRight);

        mButtonLeft.setOnClickListener(onClickListenerTabBar);
        mButtonRight.setOnClickListener(onClickListenerTabBar);

        mImageView = (ImageView) findViewById(R.id.imageViewBelow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDPI = dm.densityDpi;
        mTabWidget = 70 * (mDPI / 160);

        Log.i(Integer.toString(mDPI), Integer.toString(mTabWidget));
        Log.i("ActivityMain", "initView()");
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

    public void OnClickP(View view) {
        Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null,
                this, SettingActivity.class);
        startActivityForResult(intent, REQ_CREATE_PATTERN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CREATE_PATTERN: {
                if (resultCode == RESULT_OK) {
                    char[] pattern = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
                    Log.d(pattern.toString(), pattern.toString());
                }

                break;
            }// REQ_CREATE_PATTERN
        }
    }

    private void initViewPager() {
        // 存放多个Fragment的数组，每个Fragment都对应一个页面
        mFragments = new ArrayList<Fragment>();

        Fragment fb1 = new FragmentWriteOff();
        mFragments.add(fb1);

        Fragment fb2 = new FragmentView();
        mFragments.add(fb2);

        // 数组的适配器，方便管理数组
        MyFragmentPagerAdapter mFragmentPagerAdapter = new MyFragmentPagerAdapter(
                getSupportFragmentManager(), mFragments);
        mFragmentPagerAdapter.setFragments(mFragments);

        // ViewPager捕获自己的适配器和监听器
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager
                .setOnPageChangeListener(new MyFragmentPageChangeListener());

        // 起始页面
        mViewPager.setCurrentItem(0);
        Log.i("ActivityMain", "initViewPager");

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

    private View.OnClickListener onClickListenerTabBar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonLeft:
                    Log.i("buttonLeft", "");
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.buttonRight:
                    Log.i("buttonRight", "");
                    mViewPager.setCurrentItem(1);
                    break;
            }
        }

    };
}
