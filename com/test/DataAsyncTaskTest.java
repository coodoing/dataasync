package com.test;

import java.util.HashMap;
import java.util.Map;

import com.dataasync.CommonTask;
import com.dataasync.Constants;
import com.dataasync.DataAsyncTask;
import com.dataasync.JSONResponse;
import com.dataasync.DataAsyncTask.TaskCallBack;

import android.os.AsyncTask;

@SuppressWarnings("unused")
public class DataAsyncTaskTest {

	private AsyncTask<Void, Void, JSONResponse> testTask;
	private DataAsyncTask.TaskCallBack<JSONResponse> testTaskCallback = new DataAsyncTask.TaskCallBack<JSONResponse>() {

		@Override
		public void onTaskFinish(JSONResponse response) {
			if (response.isSucceed()) {
				// TODO
			} else {
			}
		}
	};

	public void test() {
		Map<String, String> params = new HashMap<String, String>();
		if (null != params) {
			params.put("KEY", "VALUE");
			testTask = new CommonTask(params, Constants.URL, testTaskCallback);
			testTask.execute();
		}
	}
}
