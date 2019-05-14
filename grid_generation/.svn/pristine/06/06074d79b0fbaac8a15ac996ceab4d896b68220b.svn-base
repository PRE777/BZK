package com.iwhere.gridgeneration.discretization.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import com.iwhere.geosot.service.GeoSOTService;
import com.iwhere.gridgeneration.utils.base.dto.BaseResp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class IndexFeatures implements IndexVectorFeature{
	
	private Client client = null;
	
	private GeoSOTService geoSOTService = null;
	
	public IndexFeatures (GeoSOTService geoSOTService, Client client){
		this.geoSOTService = geoSOTService;
		this.client = client;
	}
	
	// uniqueId、url、geoLevel、indexName、typeName
	@Override
	public void doIndex(String url, JSONObject featureCollection, Map<String,Object> paramsMap) throws Exception{
		// 文件唯一标识
		String uniqueId = (String)paramsMap.get("uniqueId");
		// 文件原始名称
		String originalName = (String)paramsMap.get("originalName");
		// 获取要素
		JSONArray featureArray = featureCollection.getJSONArray("features");
		// SimpleFeature[] featureArray = getFeatures.getFeatures((String)paramsMap.get("url"));
		GeoNumsOfFeatureUtils geoNumsOfFeatureUtils = new GeoNumsOfFeatureUtils(geoSOTService);
		// 索引时间
		long indexTime = new Date().getTime();
		// 批量入库
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		// 依次处理每个要素
		for (int i = 0; i < featureArray.size(); i++){
			// 同文件的标识（同一个文件离散化两次则产生不同标识）
			Map<String,Object> indexMap = new HashMap<String,Object>();
			indexMap.put("sourceId", uniqueId);
			// 原始名称
			indexMap.put("originalName", originalName);
			// 索引时间
			indexMap.put("indexTime", indexTime);
			// 要素的GeoJSON
			JSONObject featureGeojson = featureArray.getJSONObject(i);
			// 要素类型
			String featrueTypeStr = featureGeojson.getJSONObject("geometry").getString("type");
			// 要素所经过网格集合
			Set<Map<String, Object>> geonumSet = new HashSet<Map<String, Object>>();
			// 要素的GeoJson
			if ("MultiLineString".equals(featrueTypeStr)){
				// 要素类型为多线处理块
				geonumSet = geoNumsOfFeatureUtils.getGridOfMultiLineString(featureGeojson.getJSONObject("geometry")
						, (byte)paramsMap.get("geoLevel"));
				
			} else if ("LineString".equals(featrueTypeStr)){
				// 要素类型为线处理块
				/*geonumSet = geoNumsOfFeatureUtils.getGridOfLineString((LineString)feature
						.getDefaultGeometryProperty().getValue(), (byte)paramsMap.get("geoLevel"));*/
				geonumSet = geoNumsOfFeatureUtils.getGridOfLineString(featureGeojson.getJSONObject("geometry")
						, (byte)paramsMap.get("geoLevel"));
			} else if ("MultiPoint".equals(featrueTypeStr)){
				// 要素类型为线处理块
				geonumSet = geoNumsOfFeatureUtils.getGridOfMultiPoint(featureGeojson.getJSONObject("geometry")
						, (byte)paramsMap.get("geoLevel"));
			} else if ("Point".equals(featrueTypeStr)){
				// 要素类型为线处理块
				geonumSet = geoNumsOfFeatureUtils.getGridOfPoint(featureGeojson.getJSONObject("geometry")
						, (byte)paramsMap.get("geoLevel"));
			}
			// 
			List<String> geonumList = new ArrayList<String>();
			for (Iterator<Map<String, Object>> geoNumIt = geonumSet.iterator(); geoNumIt.hasNext();){
				geonumList.add((String)geoNumIt.next().get("geoNum"));
			}
			indexMap.put("geo_num", geonumList);
			// 构建要素的GEOJSON
			indexMap.put("featureGeoJson", featureGeojson.toString());
			// 数据源url
			indexMap.put("url", url);
			// 索引
			/*IndexResponse response = client.prepareIndex((String)paramsMap.get("indexName")
					,(String)paramsMap.get("typeName")).setSource(indexMap).get();*/
			bulkRequest.add(client.prepareIndex((String)paramsMap.get("indexName")
					,(String)paramsMap.get("typeName")).setSource(indexMap));
			if ((0 == i%1000 && i != 0) || i == featureArray.size()-1){
				BulkResponse bulkResponse = bulkRequest.get();
				bulkRequest = client.prepareBulk();
			}
		}
	}
	
	@Override
	public void doFeatureIndex(String url, JSONObject featureCollection, Map<String,Object> paramsMap) throws Exception{
		// 文件唯一标识
		String uniqueId = (String)paramsMap.get("uniqueId");
		// 文件原始名称
		String originalName = (String)paramsMap.get("originalName");
		
		 // 读取矢量信息
        GetFeaturesInterface getFeaturesInterface = new FeaturesFromShp();
		// 返回矢量中所有要素组成的FeatureCollection的GEOJSON
		try {
			featureCollection = getFeaturesInterface.getFeatures(url);
		} catch (Exception e) {
    		e.printStackTrace();
		}
		// 获取要素
		JSONArray featureArray = featureCollection.getJSONArray("features");
		// SimpleFeature[] featureArray = getFeatures.getFeatures((String)paramsMap.get("url"));
		GeoNumsOfFeatureUtils geoNumsOfFeatureUtils = new GeoNumsOfFeatureUtils(geoSOTService);
		// 索引时间
		long indexTime = new Date().getTime();
		// 批量入库
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		// 依次处理每个要素
		for (int i = 0; i < featureArray.size(); i++){
			if (i == 57){
				System.out.println("111");
			}
			System.out.println(i);
			// 同文件的标识（同一个文件离散化两次则产生不同标识）
			Map<String,Object> indexMap = new HashMap<String,Object>();
			indexMap.put("sourceId", uniqueId);
			// 原始名称
			indexMap.put("originalName", originalName);
			// 生成随机数排序
			int num = new Double(Math.random() * 10000).intValue();
			indexMap.put("sortItem", num);
			// 图种
			indexMap.put("mapType", (String)paramsMap.get("mapType"));
			// 图层名
			indexMap.put("layerName", (String)paramsMap.get("layerName"));
			// 索引时间
			indexMap.put("indexTime", indexTime);
			// 要素的GeoJSON
			JSONObject featureGeojson = featureArray.getJSONObject(i);
			// 要素类型
			if (featureGeojson.getJSONObject("geometry").isNullObject()){
				if (i == featureArray.size()-1){
					BulkResponse bulkResponse = bulkRequest.get();
					bulkRequest = client.prepareBulk();
				}
				continue;
			}
			String featrueTypeStr = featureGeojson.getJSONObject("geometry").getString("type");
			// 要素所经过网格集合
			Set<Map<String, Object>> geonumSet = new HashSet<Map<String, Object>>();
			// 要素的GeoJson
			if ("MultiLineString".equals(featrueTypeStr)){
				// 要素类型为多线处理块
				geonumSet = geoNumsOfFeatureUtils.getGridOfMultiLineString(featureGeojson.getJSONObject("geometry")
						, (byte)paramsMap.get("geoLevel"));
				
			} else if ("LineString".equals(featrueTypeStr)){
				// 要素类型为线处理块
				/*geonumSet = geoNumsOfFeatureUtils.getGridOfLineString((LineString)feature
						.getDefaultGeometryProperty().getValue(), (byte)paramsMap.get("geoLevel"));*/
				geonumSet = geoNumsOfFeatureUtils.getGridOfLineString(featureGeojson.getJSONObject("geometry")
						, (byte)paramsMap.get("geoLevel"));
			} else if ("MultiPoint".equals(featrueTypeStr)){
				// 要素类型为线处理块
				geonumSet = geoNumsOfFeatureUtils.getGridOfMultiPoint(featureGeojson.getJSONObject("geometry")
						, (byte)paramsMap.get("geoLevel"));
			} else if ("Point".equals(featrueTypeStr)){
				// 要素类型为线处理块
				geonumSet = geoNumsOfFeatureUtils.getGridOfPoint(featureGeojson.getJSONObject("geometry")
						, (byte)paramsMap.get("geoLevel"));
			} else if ("Polygon".equals(featrueTypeStr)){
				// 要素类型为线处理块
				geonumSet = geoNumsOfFeatureUtils.getGridOfPolygon(featureGeojson.getJSONObject("geometry")
						, (byte)paramsMap.get("geoLevel"));
			} else if ("MultiPolygon".equals(featrueTypeStr)){
				// 要素类型为线处理块
				geonumSet = geoNumsOfFeatureUtils.getGridOfMultiPolygon(featureGeojson.getJSONObject("geometry")
						, (byte)paramsMap.get("geoLevel"));
			} 
			// 
			List<String> geonumList = new ArrayList<String>();
			for (Iterator<Map<String, Object>> geoNumIt = geonumSet.iterator(); geoNumIt.hasNext();){
				geonumList.add((String)geoNumIt.next().get("geoNum"));
			}
			indexMap.put("geo_num", geonumList);
			// 构建要素的GEOJSON
			indexMap.put("featureGeoJson", featureGeojson.toString());
			// 数据源url
			indexMap.put("url", url);
			// 索引
			/*IndexResponse response = client.prepareIndex((String)paramsMap.get("indexName")
					,(String)paramsMap.get("typeName")).setSource(indexMap).get();*/
			bulkRequest.add(client.prepareIndex((String)paramsMap.get("indexName")
					,(String)paramsMap.get("typeName")).setSource(indexMap));
			if ((0 == i%1000 && i != 0) || i == featureArray.size()-1){
				BulkResponse bulkResponse = bulkRequest.get();
				bulkRequest = client.prepareBulk();
			}
		}
	}
}
