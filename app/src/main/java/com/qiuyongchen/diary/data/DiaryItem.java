package com.qiuyongchen.diary.data;

/**
 * record in database
 *
 * @author qiuyongchen
 */
public class DiaryItem {
    public long _id;
    public String content;
    public String date;
    public String time;

    public DiaryItem() {
    }

    /**
     * @param _id
     * @param content
     * @param date
     * @param time
     */
    public DiaryItem(long _id, String content, String date, String time) {
        this._id = _id;
        this.content = content;
        this.date = date;
        this.time = time;
    }

    /**
     * @return the _id
     */
    public long get_id() {
        return _id;
    }

    /**
     * @param _id the _id to set
     */
    public void set_id(long _id) {
        this._id = _id;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }

}
