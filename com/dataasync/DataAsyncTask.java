package com.dataasync;

import android.os.AsyncTask;

public abstract class DataAsyncTask<Progress, Result> extends
        AsyncTask<Void, Progress, Result> {

    private final TaskCallBack<Result> mCallBack;

    public DataAsyncTask(TaskCallBack<Result> callBack) {
        mCallBack = callBack;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Result result) {
        if (mCallBack != null) {
            mCallBack.onTaskFinish(result);
        }
    }

    /**
     * PostExecute Interface
     */
    public interface TaskCallBack<Result> {
        void onTaskFinish(Result result);
    }

}