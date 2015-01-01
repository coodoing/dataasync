package com.dataasync;

import java.util.Map;
import com.dataasync.HttpHandler;
import com.dataasync.DataAsyncTask;
import com.dataasync.JSONResponse;

public class CommonTask extends DataAsyncTask<Void, JSONResponse> {
    private Map<String,String> params;
    private final String url;

    public void setParams(Map<String,String> params){
    	this.params = params;
    }
    
    public CommonTask(Map<String,String> params,
    		String url,
            TaskCallBack<JSONResponse> callback) {
        super(callback);
        this.params = params;
        this.url = url;
    }

    @Override
    protected JSONResponse doInBackground(Void... params) {
        JSONResponse response = null;
        Map<String, String> param = this.params;
        String retJson = HttpHandler.postForString(url, param);
        response = JSONResponse.getJSONResponse(retJson);
        
        return response;
    }
}