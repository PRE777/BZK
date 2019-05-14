package com.iwhere.gridgeneration.discretization.utils;

import java.util.Map;

import net.sf.json.JSONObject;

public interface AcquireFeaturesInterface {
	
	public JSONObject getFeatures(Map<String,Object> params) throws Exception;
	
}
