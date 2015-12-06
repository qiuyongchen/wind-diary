package com.qiuyongchen.diary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.qiuyongchen.diary.event.NightModeChangedEvent;
import com.qiuyongchen.diary.fragments.FragmentPageChangeListener;
import com.qiuyongchen.diary.fragments.FragmentPagerAdapterMain;
import com.qiuyongchen.diary.fragments.FragmentView;
import com.qiuyongchen.diary.fragments.FragmentWriteOff;
import com.qiuyongchen.diary.widget.systemBarTint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import haibison.android.lockpattern.LockPatternActivity;

/**
 * Created by qiuyongchen on 2015/10/4.
 */

public class MainActivity extends FragmentActivity {
    // This is your preferred flag
    private static final int REQ_CREATE_PATTERN = 1;
    private static final int REQ_ENTER_PATTERN = 2;

    public static ImageView mImageView;
    public static int mDPI;
    public static int mTabWidget;
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

    public void toWriteOn(View v) {
        Intent intent = new Intent(MainActivity.this, WriteOnActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isNight = sharedPreferences.getBoolean("night_mode", false);
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

        // 检查是否存在手势密码，如果存在就必须验证
        Boolean isLocked = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()).getBoolean("lock_pattern_on", false);
        if (isLocked) {
            LockPatternActivity.IntentBuilder.newPatternComparator(getApplicationContext())
                    .startForResult(MainActivity.this, REQ_ENTER_PATTERN);
        }
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
                        break;
                    case RESULT_CANCELED:
                        // The user cancelled the task
                        finish();
                        break;
                    case LockPatternActivity.RESULT_FAILED:
                        // The user failed to enter the pattern
                        finish();
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
        FragmentPagerAdapterMain mFragmentPagerAdapterMain = new FragmentPagerAdapterMain(
                getSupportFragmentManager(), mFragments);
        mFragmentPagerAdapterMain.setFragments(mFragments);

        // ViewPager捕获自己的适配器和监听器
        mViewPager.setAdapter(mFragmentPagerAdapterMain);
        mViewPager
                .setOnPageChangeListener(new FragmentPageChangeListener());

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
