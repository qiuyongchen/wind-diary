package com.qiuyongchen.diary.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.qiuyongchen.diary.date.DateAndTime;

import java.util.ArrayList;

/**
 * to open/close database, read something from database, write something to
 * database
 *
 * @author redleaf
 */
public class DataSourceDiary {
    private SQLiteDatabase mSQLiteDatabase;
    private SQLiteHelperDiary mSQLiteHelperDiary;

    private ContentValues value;

    public DataSourceDiary(Context context) {
        mSQLiteHelperDiary = new SQLiteHelperDiary(context);
        value = new ContentValues();
    }

    private void close() {
        mSQLiteHelperDiary.close();
    }

    public ArrayList<DiaryItem> cursorToArrayList(Cursor cursor) {
        ArrayList<DiaryItem> item = new ArrayList<>();
        if (cursor.moveToFirst()) {
            long _id;
            String content, date, time;
            while (!cursor.isAfterLast()) {
                _id = cursor.getInt(cursor
                        .getColumnIndex(SQLiteHelperDiary.COLUMN_ID));
                content = cursor.getString(cursor
                        .getColumnIndex(SQLiteHelperDiary.COLUMN_CONTENT));
                date = cursor.getString(cursor
                        .getColumnIndex(SQLiteHelperDiary.COLUMN_DATE));
                time = cursor.getString(cursor
                        .getColumnIndex(SQLiteHelperDiary.COLUMN_TIME));
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
                        .getColumnIndex(SQLiteHelperDiary.COLUMN_ID));
                content = cursor.getString(cursor
                        .getColumnIndex(SQLiteHelperDiary.COLUMN_CONTENT));
                date = cursor.getString(cursor
                        .getColumnIndex(SQLiteHelperDiary.COLUMN_DATE));
                time = cursor.getString(cursor
                        .getColumnIndex(SQLiteHelperDiary.COLUMN_TIME));
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
                value.put(SQLiteHelperDiary.COLUMN_CONTENT,
                        mArrayList.get(i).content);
                value.put(SQLiteHelperDiary.COLUMN_DATE, mArrayList.get(i).date);
                value.put(SQLiteHelperDiary.COLUMN_TIME, mArrayList.get(i).time);
                mSQLiteDatabase.insert(SQLiteHelperDiary.TABLE_DIARY, null,
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
        value.put(SQLiteHelperDiary.COLUMN_CONTENT, content);
        value.put(SQLiteHelperDiary.COLUMN_DATE, date);
        value.put(SQLiteHelperDiary.COLUMN_TIME, time);
        long val = mSQLiteDatabase.insert(SQLiteHelperDiary.TABLE_DIARY, null,
                value);
        this.close();
        return val;
    }

    public long insertBigData(String content, String date, String time) {
        this.open();
        value.clear();

        value.put(SQLiteHelperDiary.COLUMN_DATE, date);
        value.put(SQLiteHelperDiary.COLUMN_TIME, time);
        long val = 0;
        mSQLiteDatabase.beginTransaction();

        for (int i = 0; i < 1000; i++) {
            value.put(SQLiteHelperDiary.COLUMN_CONTENT, "第" + String.valueOf(i));
            val = mSQLiteDatabase.insert(SQLiteHelperDiary.TABLE_DIARY, null,
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
                                + SQLiteHelperDiary.COLUMN_DATE
                                + " != ? order by date, time, _id DESC",
                        new String[]{String.valueOf(DateAndTime
                                .getCurrentDate())});
        item = cursorToArrayList(cursor);
        this.close();
        return item;
    }

    public ArrayList<DiaryItem> getSpecificDayDiary(String date) {
        ArrayList<DiaryItem> item = new ArrayList<DiaryItem>();
        this.open();
        Cursor cursor = mSQLiteDatabase.rawQuery("select * from diary where "
                        + SQLiteHelperDiary.COLUMN_DATE
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
                        + SQLiteHelperDiary.COLUMN_DATE
                        + " = ? order by date, time, _id DESC",
                new String[]{DateAndTime.getCurrentDate()});
        item = cursorToArrayList(cursor);
        this.close();
        return item;
    }

    private void open() {
        mSQLiteDatabase = mSQLiteHelperDiary.getWritableDatabase();
    }

}
