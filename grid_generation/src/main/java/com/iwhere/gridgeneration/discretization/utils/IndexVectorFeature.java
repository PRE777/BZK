package com.iwhere.gridgeneration.discretization.utils;

import java.util.Map;

import net.sf.json.JSONObject;

public interface IndexVectorFeature {
	
	public void doIndex(String url,JSONObject featureArray, Map<String,Object> paramsMap) throws Exception;
	
	public void doFeatureIndex(String url,JSONObject featureArray, Map<String,Object> paramsMap) throws Exception;

}
