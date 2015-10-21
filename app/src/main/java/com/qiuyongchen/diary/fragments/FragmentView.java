package com.qiuyongchen.diary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qiuyongchen.diary.R;
import com.qiuyongchen.diary.StatisticsActivity;

/**
 * Created by qiuyongchen on 2015/10/4.
 */

public class FragmentView extends Fragment {
    public FloatingActionButton fabStatistics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.activity_main_view,
                        container, false);
        fabStatistics = (FloatingActionButton) view.findViewById(R.id.floatingButton);
        fabStatistics.setOnClickListener(onClickListenerFabStatistics);
        Log.i("FragmentView", "onCreateView");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private View.OnClickListener onClickListenerFabStatistics = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(null, null,
                    getActivity(), StatisticsActivity.class);
            startActivity(intent);
        }
    };

}
