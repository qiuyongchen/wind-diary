package com.qiuyongchen.diary.date;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by redleaf on 2015/10/24.
 */
public class DateAndTime {
    /**
     * 获取调用该函数时的日期，形式为：mmddyyyy
     *
     * @return 包含该日期的String
     */
    @SuppressWarnings("deprecation")
    public static String getCurrentDate() {
        Date date = new Date();
        return (date.getMonth() + 1 < 10 ? "0" : "")
                + String.valueOf(date.getMonth() + 1)
                + (date.getDate() < 10 ? "0" : "")
                + String.valueOf(date.getDate())
                + String.valueOf(date.getYear() + 1900);
    }

    /**
     * 获取调用该函数时的时间，形式为：hh:mm:ss
     *
     * @return 包含该时间的String
     */
    @SuppressWarnings("deprecation")
    public static String getCurrentTime() {
        Date date = new Date();
        return (date.getHours() < 10 ? "0" : "")
                + String.valueOf(date.getHours()) + ":"
                + (date.getMinutes() < 10 ? "0" : "")
                + String.valueOf(date.getMinutes()) + ":"
                + (date.getSeconds() < 10 ? "0" : "")
                + String.valueOf(date.getSeconds());
    }

    public static String getFileName() {

        /**
         * get the filename（得到以日期为名单文件名） format : mm_dd_yyyy.redleaf（格式）
         */
        Calendar calendar = Calendar.getInstance();
        String filename = "";

        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mYear = calendar.get(Calendar.YEAR);

        if (mMonth <= 9) {
            filename += "0";
        }

        filename += Integer.toString(mMonth) + "_";

        if (mDay <= 9) {
            filename += "0";
        }

        filename += Integer.toString(mDay) + "_";
        filename += Integer.toString(mYear) + ".redleaf";

        return filename;
    }

    public static String getTime() {
        /** get the time（得到文本框内容被保存时的时间） format : hh:mm:ss */
        Calendar calendar = Calendar.getInstance();

        String time = "";
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        int mSecond = calendar.get(Calendar.SECOND);

        if (mHour < 10) {
            time += "0";
        }
        time += Integer.toString(mHour) + ":";
        if (mMinute < 10) {
            time += "0";
        }
        time += Integer.toString(mMinute) + ":";
        if (mSecond < 10) {
            time += "0";
        }
        time += Integer.toString(mSecond);

        return time;

    }
}
