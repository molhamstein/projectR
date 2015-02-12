package com.brainSocket.socialrosary.data;

import java.util.HashMap;

public class ServerResult {
	private static String[] errors = {ServerAccess.CONNECTION_ERROR_CODE
			,ServerAccess.ERROR_CODE_USER_ID
			,ServerAccess.ERROR_CODE_USER_NOT_ACCEPTABLE
			,ServerAccess.ERROR_CODE_USER_NOT_ACTIVATED
			,ServerAccess.RESPONCE_FORMAT_ERROR_CODE};
	
	private String flag;
	private HashMap<String, Object> pairs;

	public ServerResult(String flag) {
		super();
		this.flag = flag;
	}

	public ServerResult() {
		pairs = new HashMap<String, Object>();
	}

	public HashMap<String, Object> getPairs() {
		return pairs;
	}

	public void setPairs(HashMap<String, Object> pairs) {
		this.pairs = pairs;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public void addPair(String key, Object value) {
		this.pairs.put(key, value);
	}
	
	public Object getValue(String key) {
		return this.pairs.get(key);
	}

	public boolean isValid() { 
		for(String error : errors) {
			if(flag.equals(error))
				return false;
		}
		return true;
	}
}
