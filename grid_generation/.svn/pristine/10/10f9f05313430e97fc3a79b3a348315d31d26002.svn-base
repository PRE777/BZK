package com.iwhere.gridgeneration.discretization.utils;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.GeometryClipper;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import com.iwhere.geosot.dto.req.GeoSOTReq;
import com.iwhere.geosot.dto.resp.GeoSOTResp07;
import com.iwhere.geosot.service.GeoSOTService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ClipFeatures implements ClipFeaturesInterface{

	private GeoSOTService geoSOTService = null;
	
	public ClipFeatures (GeoSOTService geoSOTService){
		this.geoSOTService = geoSOTService;
	}
	
	@Override
	public Map<String,JSONObject> clipFeatures(String geoNums
			, JSONObject featureCollection) throws Exception {
		
		Map<String,JSONObject> resultMap = new HashMap<String,JSONObject>();
		
		String[] geoNumArray = geoNums.split(",");
		
		for (int i = 0; i < geoNumArray.length; i++){
			// 每个网格都有一个FeatureCollection
			JSONObject geoNumFeatureCollection = JSONObject.fromObject(featureCollection);
			
			String geoNumInfor = geoNumArray[i];
			GeoSOTReq req = new GeoSOTReq();
			req.setGeoNum(Long.valueOf(geoNumInfor.split("-")[0]));
			req.setGeoLevel(Byte.valueOf(geoNumInfor.split("-")[1]));
			GeoSOTResp07 geoSOTResp = geoSOTService.getScopeOfGeoNum(req);
			
			double minLat = geoSOTResp.getMinLat();
			double minLng = geoSOTResp.getMinLng();
			double maxLat = geoSOTResp.getMaxLat();
			double maxLng = geoSOTResp.getMaxLng();
			
			Envelope env = new Envelope(minLng, maxLng, minLat, maxLat);
			GeometryClipper geometryClipper = new GeometryClipper(env);
			
			JSONArray featureJSONArray = geoNumFeatureCollection.getJSONArray("features");
			JSONArray copyFeatureJSONArray = JSONArray.fromObject(featureJSONArray);
			
			
			CreateGeometryTools tool = new CreateGeometryTools();
			for (int j = 0; j < featureJSONArray.size(); j++){
				JSONObject featureObject = featureJSONArray.getJSONObject(j);
				// 获取几何类型
				String geometryType = featureObject.getJSONObject("geometry").getString("type");
				Geometry featureGeometry = null;
				if ("MultiLineString".equals(geometryType)){
					featureGeometry = tool.createMLine(featureObject);
				} else if ("LineString".equals(geometryType)){
					featureGeometry = tool.createLine(featureObject);
				} else if ("MultiPoint".equals(geometryType)){
					featureGeometry = tool.createMPoint(featureObject);
				} else if ("Point".equals(geometryType)){
					featureGeometry = tool.createPoint(featureObject);
				} else {
					return resultMap;
				}
				Geometry clipedGeometry = geometryClipper.clipSafe(featureGeometry, true, 0.0d);
				
				GeometryJSON geometryJson = new GeometryJSON(6);
				StringWriter writer = new StringWriter();
				geometryJson.write(clipedGeometry, writer);
				// 切割后的几何类型
				//featureObject.put("geometry", JSONObject.fromObject(writer.toString()));
				//copyFeatureJSONArray.getJSONObject(j).put("geometry", JSONObject.fromObject(writer.toString()));
				featureJSONArray.getJSONObject(j).put("geometry", JSONObject.fromObject(writer.toString()));
			}
			resultMap.put(geoNumInfor, geoNumFeatureCollection);
		}
		return resultMap;
	}
}
