package com.iwhere.gridgeneration.discretization.utils;

import net.sf.json.JSONObject;
import org.opengis.feature.simple.SimpleFeature;

public interface GetFeaturesInterface {
	
	public JSONObject getFeatures(String url) throws Exception;

	public SimpleFeature[] getFeaturesSource(String url) throws Exception;
	
}
