package com.iwhere.gridgeneration.discretization.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.iwhere.geosot.dto.req.GeoSOTReq;
import com.iwhere.geosot.dto.resp.GeoSOTResp04;
import com.iwhere.geosot.dto.resp.GeoSOTResp11;
import com.iwhere.geosot.service.GeoSOTService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GeoNumsOfFeatureUtils {

	private GeoSOTService geoSOTService = null;
	
	public GeoNumsOfFeatureUtils(GeoSOTService geoSOTService){
		this.geoSOTService = geoSOTService;
	}

	// 返回矢量线数据所经过网格的集合
	public Set<Map<String, Object>> getGridOfLineString(JSONObject geometryGeoJSON, 
			byte geolevel){
		// 线经过网格
		Set<Map<String, Object>> geonums = new HashSet<Map<String, Object>>();
		// 剖分网格编码集合
		Set<String> geonumSet = new HashSet<String>();
		// 线要素中拐点数组
		JSONArray linePoints = geometryGeoJSON.getJSONArray("coordinates");
		// 求取两个拐点间线段的剖分网格编码
		JSONArray startPoint = null;
		JSONArray endPoint = null;
		for (int i = 0; i < linePoints.size(); i++){
			// 第一个节点时
			if (startPoint == null){
				startPoint = linePoints.getJSONArray(i);
				continue;
			}
			if (endPoint == null){
				endPoint = linePoints.getJSONArray(i);;
			}
			// 求取线段网格
			GeoSOTReq req = new GeoSOTReq();
			req.setLngs(startPoint.get(0)+","+endPoint.get(0));
			req.setLats(startPoint.get(1)+","+endPoint.get(1));
			System.out.println(startPoint.get(0)+","+endPoint.get(0));
			System.out.println(startPoint.get(1)+","+endPoint.get(1));
			req.setGeoLevel(geolevel);
			GeoSOTResp11 response = geoSOTService.getGridsCoordinatesByLine(req);
			//GeoSOTResp07 response = geoSOTService.getGridsByLine(req);
			//  Map内容
			//  key: "geoNum"  value: long    
			//  key: "coordinates"  value: double[](double[0] = 左下纬度, double[1] = 左下经度, double[2] = 右上纬度, double[3] = 右上经度)
			List<Map<String, Object>> geoNumList = response.getGeoNums();
			for (int j = 0; j < geoNumList.size(); j++){
				Map<String, Object> map = geoNumList.get(j);
				// 排除重复网格
				if (!geonumSet.contains(String.valueOf(map.get("geoNum"))+"-"+geolevel)){
					map.put("geoNum",String.valueOf(map.get("geoNum"))+"-"+geolevel);
					geonums.add(map);
					geonumSet.add(String.valueOf(map.get("geoNum"))+"-"+geolevel);
				}
			}
			// 末尾观点作为下一条线段的起点
			startPoint = endPoint;
			endPoint = null;
		}
		return geonums;
	}
	
	// 返回矢量线数据所经过网格的集合
	public Set<Map<String, Object>> getGridOfMultiLineString(JSONObject geometryGeoJSON, 
			byte geolevel){
		// 多线经过网格集合
		Set<Map<String, Object>> geonums = new HashSet<Map<String, Object>>();
		Set<String> geonumSet = new HashSet<String>();
		// 多线的点信息
		JSONArray multiLines = geometryGeoJSON.getJSONArray("coordinates");
		// 多线数量
		int geometryCount = multiLines.size();
		for (int i = 0; i < geometryCount; i++){
			// 多线转换为单线处理
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("coordinates", multiLines.getJSONArray(i));

			Set<Map<String, Object>> subGeonums = 
					getGridOfLineString(jsonObject, geolevel);
			// 第一条线段
			if (0 == i){
				for (Iterator<Map<String, Object>> subit = subGeonums.iterator(); subit.hasNext(); ){
					Map<String, Object> map = subit.next();
					geonumSet.add((String)(map.get("geoNum")));
				}
				geonums.addAll(subGeonums);
				continue;
			}
			// 去重
			for (Iterator<Map<String, Object>> it = subGeonums.iterator(); it.hasNext();){
				// 网格码key
				Map<String, Object> geoNumInfo = it.next();
				// 排重
				if (!geonumSet.contains(String.valueOf(geoNumInfo.get("geoNum")))){
					geonums.add(geoNumInfo);
					geonumSet.add(String.valueOf(geoNumInfo.get("geoNum")));
				}
			}
		}
		return geonums;
	}
	
	// 返回多点所经过网格的集合
	public Set<Map<String, Object>> getGridOfMultiPoint(JSONObject geometryGeoJSON, 
			byte geolevel){
		// 多点经过网格集合
		Set<Map<String, Object>> geonums = new HashSet<Map<String, Object>>();
		Set<String> geonumSet = new HashSet<String>();
		// 多点信息
		JSONArray multiPoints = geometryGeoJSON.getJSONArray("coordinates");
		// 多点数量
		int geometryCount = multiPoints.size();
		for (int i = 0; i < geometryCount; i++){
			// 
			JSONArray point = multiPoints.getJSONArray(i);
			// 多点转换为单点处理
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("coordinates", multiPoints.getJSONArray(i));
			// 求取点网格
			GeoSOTReq req = new GeoSOTReq();
			req.setLng(point.getDouble(0));
			req.setLat(point.getDouble(1));
			req.setGeoLevel(geolevel);;
			GeoSOTResp04 response = geoSOTService.getGeoNum(req);
			//  Map内容
			//  key: "geoNum"  value: long    
			Long geoNum = response.getGeoNum();
			// 排重
			if (!geonumSet.contains(geoNum+"-"+geolevel)){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("geoNum", geoNum+"-"+geolevel);
				geonums.add(map);
				geonumSet.add(geoNum+"-"+geolevel);
			}
		}
		return geonums;
	}
	
	// 返回多点所经过网格的集合
	public Set<Map<String, Object>> getGridOfPoint(JSONObject geometryGeoJSON, 
			byte geolevel){
		// 点经过网格集合
		Set<Map<String, Object>> geonums = new HashSet<Map<String, Object>>();
		// 点信息
		JSONArray point = geometryGeoJSON.getJSONArray("coordinates");
		// 求取点网格
		GeoSOTReq req = new GeoSOTReq();
		req.setLng(point.getDouble(0));
		req.setLat(point.getDouble(1));
		req.setGeoLevel(geolevel);;
		GeoSOTResp04 response = geoSOTService.getGeoNum(req);
		//  Map内容
		//  key: "geoNum"  value: long    
		Long geoNum = response.getGeoNum();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("geoNum", geoNum+"-"+geolevel);
		geonums.add(map);
		return geonums;
	}
	
	// 返回矢量线数据所经过网格的集合
	public Set<Map<String, Object>> getGridOfPolygon(JSONObject geometryGeoJSON, 
			byte geolevel){
		// 面经过网格
		Set<Map<String, Object>> geonums = new HashSet<Map<String, Object>>();
		// 剖分网格编码集合
		Set<String> geonumSet = new HashSet<String>();
		// 面要素中拐点数组
		JSONArray polygonPoints = geometryGeoJSON.getJSONArray("coordinates").getJSONArray(0);
		// 求取两个拐点间线段的剖分网格编码
		JSONArray point = null;
		StringBuffer lngs = new StringBuffer();
		StringBuffer lats = new StringBuffer();
		for (int i = 0; i < polygonPoints.size(); i++){
			point = polygonPoints.getJSONArray(i);
			lngs.append(point.get(0));
			lats.append(point.get(1));
			if (i != polygonPoints.size()-1){
				lngs.append(",");
				lats.append(",");
			}
		}
		// 求取面网格
		GeoSOTReq req = new GeoSOTReq();
		req.setLngs(lngs.toString());
		req.setLats(lats.toString());
		req.setGeoLevel(geolevel);;
		GeoSOTResp11 response = geoSOTService.getGridsCoordinatesByPolygon(req);

		//  Map内容
		//  key: "geoNum"  value: long    
		//  key: "coordinates"  value: double[](double[0] = 左下纬度, double[1] = 左下经度, double[2] = 右上纬度, double[3] = 右上经度)
		List<Map<String, Object>> geoNumList = response.getGeoNums();
		for (int j = 0; j < geoNumList.size(); j++){
			Map<String, Object> map = geoNumList.get(j);
			// 排除重复网格
			if (!geonumSet.contains(String.valueOf(map.get("geoNum"))+"-"+geolevel)){
				map.put("geoNum",String.valueOf(map.get("geoNum"))+"-"+geolevel);
				geonums.add(map);
				geonumSet.add(String.valueOf(map.get("geoNum"))+"-"+geolevel);
			}
		}
		return geonums;
	}
	
	// 返回矢量线数据所经过网格的集合
	public Set<Map<String, Object>> getGridOfMultiPolygon(JSONObject geometryGeoJSON, 
			byte geolevel){
		// 面经过网格
		Set<Map<String, Object>> geonums = new HashSet<Map<String, Object>>();
		// 剖分网格编码集合
		Set<String> geonumSet = new HashSet<String>();
		// 面要素中拐点数组
		JSONArray polygons = geometryGeoJSON.getJSONArray("coordinates");
		// 
		for (int i = 0; i < polygons.size(); i++){
			JSONArray polygonArray = polygons.getJSONArray(i);
			JSONArray polygon = polygonArray.getJSONArray(0);
			StringBuffer lngs = new StringBuffer();
			StringBuffer lats = new StringBuffer();
			for (int j = 0; j < polygon.size(); j++){
				JSONArray point = polygon.getJSONArray(j);
				lngs.append(point.getDouble(0));
				lats.append(point.getDouble(1));
				if (j != polygon.size()-1){
					lngs.append(",");
					lats.append(",");
				}
			}
			GeoSOTReq req = new GeoSOTReq();
			req.setLngs(lngs.toString());
			req.setLats(lats.toString());
			req.setGeoLevel(geolevel);;
			GeoSOTResp11 response = geoSOTService.getGridsCoordinatesByPolygon(req);
			
			List<Map<String, Object>> geoNumList = response.getGeoNums();
			for (int j = 0; j < geoNumList.size(); j++){
				Map<String, Object> map = geoNumList.get(j);
				// 排除重复网格
				if (!geonumSet.contains(String.valueOf(map.get("geoNum"))+"-"+geolevel)){
					map.put("geoNum",String.valueOf(map.get("geoNum"))+"-"+geolevel);
					geonums.add(map);
					geonumSet.add(String.valueOf(map.get("geoNum"))+"-"+geolevel);
				}
			}
		}
		return geonums;
	}
}
