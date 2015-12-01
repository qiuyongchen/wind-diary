package com.qiuyongchen.diary.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qiuyongchen.diary.R;
import com.qiuyongchen.diary.data.DataSourceDiary;
import com.qiuyongchen.diary.data.DiaryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuyongchen on 2015/10/24.
 */

public class FragmentWriteOffListviewAdapter extends BaseAdapter {

    DataSourceDiary mDataSource;

    private LayoutInflater mLayoutInflater;

    private ArrayList<DiaryItem> mArrayListAll; // all the data got from
    // database
    private List<DiaryItem> mArrayList; // the 200 items that will
    // show in listview

    private int start; // the index in mArrayListAll of start item showed in
    // listview
    private int end;// the index in mArrayListAll of end item showed in listview

    public FragmentWriteOffListviewAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);

        mDataSource = new DataSourceDiary(context);

        loadFromDatabase();

    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.listview_item,
                    parent, false);
            viewHolder.textViewTime = (TextView) convertView
                    .findViewById(R.id.textViewTime);
            viewHolder.textViewContent = (TextView) convertView
                    .findViewById(R.id.textViewContent);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textViewTime.setText(mArrayList.get(position).time);
        viewHolder.textViewContent.setText(mArrayList.get(position).content);
        return convertView;
    }

    /**
     * reload all the data from database, set the new 200 items to show in the
     * observer, call notifyDataSetChanged()
     *
     * @return
     */
    public boolean loadFromDatabase() {
        mArrayListAll = mDataSource.getTodayDiary();

        mArrayList = mArrayListAll;

        this.notifyDataSetChanged();
        return false;
    }

    public class ViewHolder {
        TextView textViewTime;
        TextView textViewContent;
    }
}
