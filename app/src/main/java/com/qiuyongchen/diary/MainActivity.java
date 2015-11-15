package com.qiuyongchen.diary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.qiuyongchen.diary.fragments.FragmentView;
import com.qiuyongchen.diary.fragments.FragmentWriteOff;
import com.qiuyongchen.diary.fragments.MyFragmentPageChangeListener;
import com.qiuyongchen.diary.fragments.MyFragmentPagerAdapter;
import com.qiuyongchen.diary.widget.systemBarTint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Created by qiuyongchen on 2015/10/4.
 */

public class MainActivity extends FragmentActivity {
    // This is your preferred flag
    private static final int REQ_CREATE_PATTERN = 1;
    public static ImageView mImageView;
    public static int mDPI;
    public static int mTabWidget;
    public static InputMethodManager mIMM;
    public static boolean isNight = false;
    public static View mLayoutRoot;
    public static ImageView mButtonSetting;
    private ArrayList<Fragment> mFragments;
    private Button mButtonLeft;
    private Button mButtonRight;
    private ViewPager mViewPager;
    private Button c;
    private SharedPreferences sharedPreferences;
    private View.OnClickListener onClickListenerAppBar = new View.OnClickListener() {
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
                case R.id.buttonSetting:
                    Log.i("buttonSetting", "");
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                    break;
            }
        }

    };

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
        mImageView = (ImageView) findViewById(R.id.imageViewBelow);
        mButtonLeft = (Button) findViewById(R.id.buttonLeft);
        mButtonRight = (Button) findViewById(R.id.buttonRight);
        mButtonSetting = (ImageView) findViewById(R.id.buttonSetting);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        mButtonLeft.setOnClickListener(onClickListenerAppBar);
        mButtonRight.setOnClickListener(onClickListenerAppBar);
        mButtonSetting.setOnClickListener(onClickListenerAppBar);

        mDPI = dm.densityDpi;
        mTabWidget = 50 * (mDPI / 160);

        Log.i(Integer.toString(mDPI), Integer.toString(mTabWidget));
        Log.i("ActivityMain", "initView()");
    }

    private void initViewPager() {
        Fragment fb1 = new FragmentWriteOff();
        Fragment fb2 = new FragmentView();
        // 存放多个Fragment的数组，每个Fragment都对应一个页面
        mFragments = new ArrayList<Fragment>();
        mFragments.add(fb1);
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

        if (isNight)
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
