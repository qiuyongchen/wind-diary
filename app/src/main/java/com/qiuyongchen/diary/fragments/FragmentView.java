package com.qiuyongchen.diary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.qiuyongchen.diary.R;
import com.qiuyongchen.diary.SettingActivity;
import com.qiuyongchen.diary.StatisticsActivity;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;

/**
 * Created by qiuyongchen on 2015/10/4.
 */

public class FragmentView extends Fragment implements AbsListView.OnScrollListener {
    public static boolean NEEDTOREFLESH;
    private final int LOAD_STATE_IDLE = 0;// 没有在加载，并且服务器上还有数据没加载
    private final int LOAD_STATE_LOADING = 1;// 正在加载状态
    private final int LOAD_STATE_FINISH = 2;// 表示服务器上的全部数据都已加载完毕
    public FloatingActionButton fabStatistics;
    FragmentViewListviewAdapter mAdapterPreviousDiary;
    private ExpandableStickyListHeadersListView mListView;
    private int loadState = LOAD_STATE_IDLE;// 记录加载的状态
    private Handler handler = new Handler();
    private View.OnClickListener onClickListenerFabStatistics = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(null, null,
                    getActivity(), StatisticsActivity.class);
            startActivity(intent);
        }
    };

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

        NEEDTOREFLESH = false;

        mListView = (ExpandableStickyListHeadersListView) view
                .findViewById(R.id.list);

        mAdapterPreviousDiary = new FragmentViewListviewAdapter(this
                .getActivity());
        mListView.setAdapter(mAdapterPreviousDiary);
        mListView.setDivider(null);
        mListView.setSelection(mAdapterPreviousDiary.getCount() - 1);
        mListView.setOnScrollListener(this);

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

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        Log.i("onScroll", "firstVisibleItem" + firstVisibleItem
                + " visibleItemCount" + visibleItemCount + " totalItemCount"
                + totalItemCount);

        // if the listview scroll to the top
        if (firstVisibleItem == 0) {
            Log.i("onScroll", "firstVisibleItem" + firstVisibleItem);
            loadState = LOAD_STATE_LOADING;
            int num = mAdapterPreviousDiary.addToFront();
            if (num > 0)
                mListView.setSelection(num);
            loadState = LOAD_STATE_IDLE;

        }

        // if the listview scroll to the botton
        if (firstVisibleItem + visibleItemCount == totalItemCount) {
            if (loadState == LOAD_STATE_IDLE) {
                loadState = LOAD_STATE_LOADING;
                Log.i("onScroll", "firstVisibleItem" + firstVisibleItem
                        + " visibleItemCount" + visibleItemCount
                        + " totalItemCount" + totalItemCount);
                int mfirstVisibleItem = firstVisibleItem;
                int num = mAdapterPreviousDiary.addToTail();
                if (num > 0)
                    mListView.setSelection(mfirstVisibleItem - num);
                loadState = LOAD_STATE_IDLE;
                // loadMore();
            }
        }

    }

    public void onScrollStateChanged(AbsListView arg0, int scrollState) {
        Log.i("onScrollStateChanged", scrollState + "");
    }

}
