package com.qiuyongchen.diary.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.qiuyongchen.diary.R;
import com.qiuyongchen.diary.event.NewDiaryIntoDBEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by qiuyongchen on 2015/10/4.
 */

public class FragmentWriteOff extends Fragment {
    FragmentWriteOffListviewAdapter mAdapterTodayDiary;
    private EditText mEditText;
    private ListView mListView;
    private String content;
    private View.OnClickListener onClickListenerButtonSave = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

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
                .inflate(R.layout.activity_main_write_off,
                        container, false);

        mListView = (ListView) view
                .findViewById(R.id.list);

        mAdapterTodayDiary = new FragmentWriteOffListviewAdapter(this
                .getActivity());
        mListView.setAdapter(mAdapterTodayDiary);
        mListView.setDivider(null);
        mListView.setSelection(mAdapterTodayDiary.getCount() - 1);

        mEditText = (EditText) view.findViewById(R.id.editText);

        Log.i("FragmentWriteOff", "onCreateView");
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
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

    @Override
    public void onResume() {
        super.onResume();
        content = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString("edit_text_content", "");
        if (!content.equals(mEditText.getText().toString()))
            mEditText.setText(content);
    }

    // 监听是否有新保存的日记
    public void onEventMainThread(NewDiaryIntoDBEvent event) {
        Log.e("onEventMainThread", "got a message NewDiaryIntoDBEvent");
        mAdapterTodayDiary.loadFromDatabase();
        mAdapterTodayDiary.notifyDataSetChanged();
        mListView.setSelection(mAdapterTodayDiary.getCount() - 1);
    }
}
