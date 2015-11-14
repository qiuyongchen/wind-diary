package com.qiuyongchen.diary.json;

import android.util.Log;

import com.qiuyongchen.diary.data.DiaryItem;
import com.qiuyongchen.diary.json.fastjson.JSON;
import com.qiuyongchen.diary.json.fastjson.JSONArray;

import java.util.ArrayList;

/**
 * Created by qiuyongchen on 2015/10/4.
 */

public class JsonCenter {

	/**
	 * encode an arraylist to a json
	 * 
	 * @return the json string
	 */
	public String export_to_local_json(ArrayList<DiaryItem> all_diary_item) {
		JsonObject mJson = new JsonObject("CLIENT", "ROOT", all_diary_item);
		String str = JSON.toJSONString(mJson);
		Log.e("to json", str);
		return str;
	}

	/**
	 * decode the json string to an arraylist
	 * 
	 * @param str
	 *            json string
	 * @return
	 */
	public ArrayList<DiaryItem> import_from_local_json(String str) {
		JSONArray mJSONArray = JSON.parseObject(str).getJSONArray(
				"all_diary_item");

		ArrayList<DiaryItem> mArrayList = new ArrayList<DiaryItem>();

		if (!mJSONArray.isEmpty()) {
			for (int i = 0; i < mJSONArray.size(); i++) {
				String item = mJSONArray.getString(i);
				mArrayList.add(new DiaryItem(JSON.parseObject(item)
						.getLongValue("_id"), String.valueOf(JSON.parseObject(item).getString(
								"content")), JSON.parseObject(item).getString("date"),
						JSON.parseObject(item).getString("time")));
			}
		}
		return mArrayList;
	}

	/**
	 * act as an object which will be used in encode json to arraylist
	 * 
	 * @author Administrator
	 *
	 */
	private class JsonObject {

		String from;
		String user;
		ArrayList<DiaryItem> all_diary_item;

		public JsonObject(String from, String user,
				ArrayList<DiaryItem> all_diary_item) {
			this.from = from;
			this.user = user;
			this.all_diary_item = all_diary_item;
		}

		/**
		 * @return the from
		 */
		@SuppressWarnings("unused")
		public String getFrom() {
			return from;
		}

		/**
		 * @param from
		 *            the from to set
		 */
		@SuppressWarnings("unused")
		public void setFrom(String from) {
			this.from = from;
		}

		/**
		 * @return the user
		 */
		@SuppressWarnings("unused")
		public String getUser() {
			return user;
		}

		/**
		 * @param user
		 *            the user to set
		 */
		@SuppressWarnings("unused")
		public void setUser(String user) {
			this.user = user;
		}

		/**
		 * @return the all_diary_item
		 */
		@SuppressWarnings("unused")
		public ArrayList<DiaryItem> getAll_diary_item() {
			return all_diary_item;
		}

		/**
		 * @param all_diary_item
		 *            the all_diary_item to set
		 */
		@SuppressWarnings("unused")
		public void setAll_diary_item(ArrayList<DiaryItem> all_diary_item) {
			this.all_diary_item = all_diary_item;
		}
	}
}
