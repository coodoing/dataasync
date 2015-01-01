package com.dataasync;

import java.util.HashMap;
import java.util.Map;
import android.os.AsyncTask;

public class TestApp {

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
