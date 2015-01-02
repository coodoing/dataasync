dataasync
=========

`dataasync` is an async package for a easy use of Android AsyncTask . 


## easy to use

### 1   **define the async task and execute it**
```
private AsyncTask<Void, Void, JSONResponse> testTask;

Map<String, String> params = new HashMap<String, String>();
if (null != params) {
	params.put("KEY", "VALUE");
	testTask = new CommonTask(params, Constants.URL, testTaskCallback);
	testTask.execute();
}

```
### 2 **callback after the task**

```
private DataAsyncTask.TaskCallBack<JSONResponse> testTaskCallback = new DataAsyncTask.TaskCallBack<JSONResponse>() {
	@Override
	public void onTaskFinish(JSONResponse response) {
		if (response.isSucceed()) {
			// TODO
		} else {
		}
	}
};

```
