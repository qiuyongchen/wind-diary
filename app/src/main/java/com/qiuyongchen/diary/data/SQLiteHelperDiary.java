package com.qiuyongchen.diary.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * help to create database and upgrade database
 *
 * @author redleaf
 */
public class SQLiteHelperDiary extends SQLiteOpenHelper {
    public static final String TABLE_DIARY = "diary";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";

    public static final String DATABASE_NAME = "diary.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * Create a helper object to create and open a database.
     */
    public SQLiteHelperDiary(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create a helper object to create and open a database.
     */
    public SQLiteHelperDiary(Context context, String name,
                             CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // TODO Auto-generated method stub
        database.execSQL("CREATE TABLE " + TABLE_DIARY + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CONTENT
                + " TEXT NOT NULL, " + COLUMN_DATE + " TEXT NOT NULL, "
                + COLUMN_TIME + " TEXT NOT NULL" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelperDiary.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIARY);
        onCreate(db);
    }

}
