package com.dataasync;

import org.json.JSONObject;

public class JSONResponse {
	private String data;
    private boolean isSucceed;
    private String message;

    public static final JSONResponse NIL_RESPONSE = new JSONResponse(false, "","");
    
    public JSONResponse(boolean isSucceed, String message,String data) {
        this.isSucceed = isSucceed;
        this.message = message;
        this.setData(data);
    }

    public boolean isSucceed() {
        return isSucceed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public static JSONResponse getJSONResponse(String json) {
		if (json.equals("") || json == null) {
			return JSONResponse.NIL_RESPONSE;
		}
		try {
			JSONObject object = new JSONObject(json);
			String status = object.optString(Constants.KEY_STATUS);
			String message = object.optString(Constants.KEY_MESSAGE);
			String data = object.optString(Constants.KEY_DATA);
			boolean isSucceed = status.equals(Constants.KEY_SUCCEED);
			return new JSONResponse(isSucceed, message, data);
		} catch (Exception e) {
		}
		return JSONResponse.NIL_RESPONSE;
	}
}
