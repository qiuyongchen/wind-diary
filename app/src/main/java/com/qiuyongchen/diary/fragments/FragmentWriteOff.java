package com.qiuyongchen.diary.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.qiuyongchen.diary.R;
import com.qiuyongchen.diary.data.DataSourceDiary;
import com.qiuyongchen.diary.date.DateAndTime;

/**
 * Created by qiuyongchen on 2015/10/4.
 */

public class FragmentWriteOff extends Fragment {
    FragmentWriteOffListviewAdapter mAdapterTodayDiary;
    private EditText mEditText;
    private Button mButtonSave;
    private ListView mListView;
    private View.OnClickListener onClickListenerButtonSave = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String content = mEditText.getText().toString();
            String date = DateAndTime.getCurrentDate();
            String time = DateAndTime.getCurrentTime();

            DataSourceDiary mDataSourceDiary = new DataSourceDiary(
                    getActivity().getApplicationContext());
            mDataSourceDiary.insert(content, date, time);

            mEditText.setText("");
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
        mButtonSave = (Button) view.findViewById(R.id.buttonSave);
        mButtonSave.setOnClickListener(onClickListenerButtonSave);

        Log.i("FragmentWriteOff", "onCreateView");
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
}
