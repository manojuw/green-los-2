package com.mixo.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GeneralUtility {
	
	private GeneralUtility() {}
	
	public static boolean validateJsonPayloadParameter(JSONObject payload, String paramter) {
		if(payload == null) return false;
		if(!payload.has(paramter)) return false;
		if(null == payload.opt(paramter)) return false;
		if(!(payload.opt(paramter) instanceof JSONObject)) return false;
		return !payload.getJSONObject(paramter).isEmpty();
	}
	
	public static boolean isJsonValid(String json) {
		try {
			new JSONObject(json);
			return true;
		} catch (JSONException exception) {
			try {
				new JSONArray(json);
				return true;
			} catch (JSONException ne) {
				return false;
			}
		}
	}

	public static boolean validateJsonArrayPayloadParameter(JSONObject payload, String paramter) {
		if(payload == null) return false;
		if(!payload.has(paramter)) return false;
		if(null == payload.opt(paramter)) return false;
		if(!(payload.opt(paramter) instanceof JSONArray)) return false;
		return !payload.getJSONArray(paramter).isEmpty();
	}

}
