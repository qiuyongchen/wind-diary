package com.qiuyongchen.diary.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.qiuyongchen.diary.R;
import com.qiuyongchen.diary.data.DataSourceDiary;
import com.qiuyongchen.diary.data.DiaryItem;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by qiuyongchen on 2015/10/24.
 */

public class FragmentViewListviewAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {

    DataSourceDiary mDataSource;

    private LayoutInflater mLayoutInflater;

    private ArrayList<DiaryItem> mArrayListAll; // all the data got from
    // database
    private List<DiaryItem> mArrayList; // the 200 items that will
    // show in listview

    private int[] mSectionIndexs;
    private ArrayList<DiaryItem> mSectionItems;

    private int start; // the index in mArrayListAll of start item showed in
    // listview
    private int end;// the index in mArrayListAll of end item showed in listview

    public FragmentViewListviewAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);

        mDataSource = new DataSourceDiary(context);

        loadFromDatabase();

        mSectionIndexs = getSectionIndexs();
        mSectionItems = getSectionItems();

    }

    /**
     * if there is still items above the most top item of mArrayList, add 100 or
     * less to the top of mArrayList and delete 100 or less from the bottom of
     * mArrayList
     *
     * @return the number of items added/deleted
     */
    public int addToFront() {
        // if there is still items above the typist item of mArrayList
        if (start > 0) {
            int itemsToAdd = Math.min(100, start);
            start -= itemsToAdd;
            end -= itemsToAdd;
            mArrayList = mArrayListAll.subList(start, end);
            this.notifyDataSetChanged();
            return itemsToAdd;
        }
        return 0;

    }

    /**
     * if there is still items below the most bottom item of mArrayList, add 100
     * or less to the bottom of mArrayList and delete 100 or less from the top
     * of mArrayList
     *
     * @return the number of items added/deleted
     */
    public int addToTail() {
        if (end < mArrayListAll.size()) {
            int itemsToAdd = Math.min(100, mArrayListAll.size() - end);
            start += itemsToAdd;
            end += itemsToAdd;
            mArrayList = mArrayListAll.subList(start, end);
            this.notifyDataSetChanged();
            return itemsToAdd;
        }
        return 0;

    }

    /**
     * from array, get the index of the item that contain the date data that
     * will be displayed on the header
     *
     * @return
     */
    private int[] getSectionIndexs() {

        ArrayList<Integer> sectionIndexs = new ArrayList<Integer>();

        if (!mArrayList.isEmpty()) {
            String firstLastDate = mArrayList.get(0).date;
            sectionIndexs.add(0);
            for (int i = 1; i < mArrayList.size(); i++) {
                if (!mArrayList.get(i).date.equals(firstLastDate)) {
                    firstLastDate = mArrayList.get(i).date;
                    sectionIndexs.add(i);
                }
            }
        }

        int[] intSectionIndexs = new int[sectionIndexs.size()];
        for (int i = 0; i < sectionIndexs.size(); i++) {
            intSectionIndexs[i] = sectionIndexs.get(i);
        }

        return intSectionIndexs;
    }

    /**
     * from array, get the item that contain the date data that will be
     * displayed on the header
     *
     * @return
     */
    private ArrayList<DiaryItem> getSectionItems() {
        ArrayList<DiaryItem> sectionItems = new ArrayList<DiaryItem>();
        for (int i = 0; i < mSectionIndexs.length; i++) {
            sectionItems.add(mArrayList.get(mSectionIndexs[i]));
        }
        return sectionItems;
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public long getHeaderId(int position) {
        return mArrayList.get(position).date.hashCode();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder headerViewHolder;

        if (convertView == null) {
            headerViewHolder = new HeaderViewHolder();
            convertView = mLayoutInflater.inflate(
                    R.layout.listview_item_header, parent, false);
            headerViewHolder.header = (TextView) convertView
                    .findViewById(R.id.textViewDate);
            convertView.setTag(headerViewHolder);
        } else {
            headerViewHolder = (HeaderViewHolder) convertView.getTag();
        }

        headerViewHolder.header.setText(strnumToStrdate(mArrayList
                .get(position).date));
        return convertView;
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
    public int getPositionForSection(int section) {
        if (mSectionIndexs.length == 0)
            return 0;

        if (section >= mSectionIndexs.length)
            section = mSectionIndexs.length - 1;
        else if (section < 0)
            section = 0;

        return mSectionIndexs[section];
    }

    @Override
    public Object[] getSections() {
        String[] dates = new String[mSectionItems.size()];
        for (int i = 0; i < mSectionItems.size(); i++) {
            dates[i] = mSectionItems.get(i).date;
        }
        return dates;
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndexs.length; i++) {
            if (position < mSectionIndexs[i])
                return i - 1;
        }
        return mSectionIndexs.length - 1;
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
        mArrayListAll = mDataSource.getPreviousDiary();
        int size_ = mArrayListAll.size();
        start = size_ >= 200 ? size_ - 200 : 0;
        end = size_;
        mArrayList = mArrayListAll.subList(start, end);

        this.notifyDataSetChanged();
        return false;
    }

    public String strnumToStrdate(String strnum) {
        return strnum.substring(4, 8) + "/" + strnum.substring(0, 2) + "/"
                + strnum.substring(2, 4) + "";
    }

    public class HeaderViewHolder {
        TextView header;
    }

    public class ViewHolder {
        TextView textViewTime;
        TextView textViewContent;
    }
}
