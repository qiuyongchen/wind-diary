package com.qiuyongchen.diary.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.qiuyongchen.diary.date.DateAndTime;
import com.qiuyongchen.diary.util.ComparatorDiaryItem;

import java.util.ArrayList;
import java.util.Collections;

/**
 * to open/close database, read something from database, write something to
 * database
 *
 * @author qiuyongchen
 */
public class DataSourceDiary {
    private SQLiteDatabase mSQLiteDatabase;
    private SQLiteHelper mSQLiteHelper;

    private ContentValues value;

    public DataSourceDiary(Context context) {
        mSQLiteHelper = new SQLiteHelper(context);
        value = new ContentValues();
    }

    private void close() {
        mSQLiteHelper.close();
    }

    /**
     * 通过游标读出数据库的内容
     *
     * @param cursor 游标
     * @return 读出的所有的内容
     */
    public ArrayList<DiaryItem> cursorToArrayList(Cursor cursor) {
        ArrayList<DiaryItem> item = new ArrayList<>();
        if (cursor.moveToFirst()) {
            long _id;
            String content, date, time;
            while (!cursor.isAfterLast()) {
                _id = cursor.getInt(cursor
                        .getColumnIndex(SQLiteHelper.COLUMN_ID));
                content = cursor.getString(cursor
                        .getColumnIndex(SQLiteHelper.COLUMN_CONTENT));
                date = cursor.getString(cursor
                        .getColumnIndex(SQLiteHelper.COLUMN_DATE));
                time = cursor.getString(cursor
                        .getColumnIndex(SQLiteHelper.COLUMN_TIME));
                item.add(new DiaryItem(_id, content, date, time));
                cursor.moveToNext();
            }
        }
        return item;
    }

    public ArrayList<DiaryItem> cursorToArrayListRevese(Cursor cursor) {
        ArrayList<DiaryItem> item = new ArrayList<>();
        if (cursor.moveToLast()) {
            long _id;
            String content, date, time;
            while (!cursor.isBeforeFirst()) {
                _id = cursor.getInt(cursor
                        .getColumnIndex(SQLiteHelper.COLUMN_ID));
                content = cursor.getString(cursor
                        .getColumnIndex(SQLiteHelper.COLUMN_CONTENT));
                date = cursor.getString(cursor
                        .getColumnIndex(SQLiteHelper.COLUMN_DATE));
                time = cursor.getString(cursor
                        .getColumnIndex(SQLiteHelper.COLUMN_TIME));
                item.add(new DiaryItem(_id, content, date, time));
                cursor.moveToPrevious();
            }
        }
        return item;
    }

    public Boolean importIntoDatabase(ArrayList<DiaryItem> mArrayList) {
        this.open();

        if (mArrayList.isEmpty()) {
            return false;
        }

        mSQLiteDatabase.beginTransaction();
        try {
            for (int i = 0; i < mArrayList.size(); i++) {

                // if there already exit the same record, just skip it.
                if (isExit(mArrayList.get(i).content, mArrayList.get(i).date,
                        mArrayList.get(i).time)) {
                    Log.d("importIntoDatabase", "try to insert " + "_id:"
                            + String.valueOf(mArrayList.get(i)._id)
                            + " content:" + mArrayList.get(i).content
                            + " date:" + mArrayList.get(i).date + " time:"
                            + mArrayList.get(i).time + " but failed");
                    continue;
                }

                Log.d("importIntoDatabase",
                        "try to insert " + "_id:"
                                + String.valueOf(mArrayList.get(i)._id)
                                + " content:" + mArrayList.get(i).content
                                + " date:" + mArrayList.get(i).date + " time:"
                                + mArrayList.get(i).time + " and succeed");
                value.clear();
                value.put(SQLiteHelper.COLUMN_CONTENT,
                        mArrayList.get(i).content);
                value.put(SQLiteHelper.COLUMN_DATE, mArrayList.get(i).date);
                value.put(SQLiteHelper.COLUMN_TIME, mArrayList.get(i).time);
                mSQLiteDatabase.insert(SQLiteHelper.TABLE_DIARY, null,
                        value);
            }
            mSQLiteDatabase.setTransactionSuccessful();
        } finally {
            mSQLiteDatabase.endTransaction();
        }

        this.close();
        return true;
    }

    public long insert(String content, String date, String time) {
        this.open();
        value.clear();
        value.put(SQLiteHelper.COLUMN_CONTENT, content);
        value.put(SQLiteHelper.COLUMN_DATE, date);
        value.put(SQLiteHelper.COLUMN_TIME, time);
        long val = mSQLiteDatabase.insert(SQLiteHelper.TABLE_DIARY, null,
                value);
        this.close();
        return val;
    }

    public long insertBigData(String content, String date, String time) {
        this.open();
        value.clear();

        value.put(SQLiteHelper.COLUMN_DATE, date);
        value.put(SQLiteHelper.COLUMN_TIME, time);
        long val = 0;
        mSQLiteDatabase.beginTransaction();

        for (int i = 0; i < 1000; i++) {
            value.put(SQLiteHelper.COLUMN_CONTENT, "第" + String.valueOf(i));
            val = mSQLiteDatabase.insert(SQLiteHelper.TABLE_DIARY, null,
                    value);
        }
        mSQLiteDatabase.setTransactionSuccessful();
        mSQLiteDatabase.endTransaction();
        this.close();
        return val;
    }

    /**
     * to check if there had been an record the same as what we want to insert
     * to the database(be careful, there are no open() and close() in this
     * function)
     *
     * @param content
     * @param date
     * @param time
     * @return
     */
    private boolean isExit(String content, String date, String time) {
        Cursor c = mSQLiteDatabase.query("diary", null,
                "content = ? and date = ? and time = ?", new String[]{
                        content, date, time}, null, null, null);
        if (c.getCount() == 0) {
            c.close();

            return false;
        }

        c.close();
        return true;
    }

    public boolean isEmptyBetweenTwoId(long firstId, long lastId) {
        boolean flag = false;
        this.open();
        Cursor cursor = mSQLiteDatabase.rawQuery(
                "select * from diary where _id > ? and _id < ?", new String[]{
                        String.valueOf(firstId), String.valueOf(lastId)});
        if (cursor.getCount() == 0)
            flag = true;
        this.close();
        return flag;

    }

    public ArrayList<DiaryItem> getAllDiary() {
        ArrayList<DiaryItem> item = new ArrayList<DiaryItem>();
        this.open();
        Cursor cursor = mSQLiteDatabase.rawQuery(
                "select * from diary order by date, time, _id DESC", null);
        item = cursorToArrayList(cursor);
        this.close();
        return item;
    }

    public ArrayList<DiaryItem> getPreviousDiary() {
        ArrayList<DiaryItem> item = new ArrayList<DiaryItem>();
        this.open();
        Cursor cursor = mSQLiteDatabase
                .rawQuery("select * from diary where "
                                + SQLiteHelper.COLUMN_DATE
                                + " != ? order by date, time, _id DESC",
                        new String[]{String.valueOf(DateAndTime
                                .getCurrentDate())});
        item = cursorToArrayList(cursor);
        String str;
        for (DiaryItem i : item) {
            str = i.getDate();
            str = str.substring(4, 8) + str.substring(0, 4);
            i.setDate(str);
        }
        Collections.sort(item, new ComparatorDiaryItem());
        this.close();
        return item;
    }

    public ArrayList<DiaryItem> getSpecificDayDiary(String date) {
        ArrayList<DiaryItem> item = new ArrayList<DiaryItem>();
        this.open();
        Cursor cursor = mSQLiteDatabase.rawQuery("select * from diary where "
                        + SQLiteHelper.COLUMN_DATE
                        + " = ? order by date, time, _id DESC",
                new String[]{String.valueOf(date)});
        item = cursorToArrayList(cursor);
        this.close();
        return item;
    }

    public ArrayList<DiaryItem> getTodayDiary() {
        ArrayList<DiaryItem> item = new ArrayList<DiaryItem>();
        this.open();
        Cursor cursor = mSQLiteDatabase.rawQuery("select * from diary where "
                        + SQLiteHelper.COLUMN_DATE
                        + " = ? order by date, time, _id DESC",
                new String[]{DateAndTime.getCurrentDate()});
        item = cursorToArrayList(cursor);
        this.close();
        return item;
    }

    private void open() {
        mSQLiteDatabase = mSQLiteHelper.getWritableDatabase();
    }

}
