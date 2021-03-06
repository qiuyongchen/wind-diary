package com.qiuyongchen.diary.fragments;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.qiuyongchen.diary.MainActivity;

/**
 * Created by qiuyongchen on 2015/10/4.
 */

public class FragmentPageChangeListener implements OnPageChangeListener {

    private int endPosition;
    private int beginPosition;
    private int currentFragmentIndex;
    @SuppressWarnings("unused")
    private boolean isEnd;

    @Override
    public void onPageSelected(int position) {

        Animation animation = new TranslateAnimation(endPosition, position
                * MainActivity.mTabWidget, 0, 0);
        beginPosition = position * MainActivity.mTabWidget;
        currentFragmentIndex = position;
        if (animation != null) {
            animation.setFillAfter(true);
            animation.setDuration(400);
            MainActivity.mImageView
                    .startAnimation(animation);
        }

        endPosition = currentFragmentIndex
                * MainActivity.mTabWidget;

        switch (position) {
            case 0:
                Log.i("onPageSelected", "position 0");
                MainActivity.mButtonSetting.setVisibility(View.INVISIBLE);
                break;
            case 1:
                Log.i("onPageSelected", "position 1");

                MainActivity.mButtonSetting.setVisibility(View.VISIBLE);
                break;
            case 2:
                Log.i("onPageSelected", "position 2");
                /*MainActivity.mIMM.hideSoftInputFromWindow(
                        MainActivity.mLayoutRoot
                                .getWindowToken(), 0);*/
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }
}
