/**
 * @author lfq
 *
 */
package com.iwhere.gridgeneration.discretization.utils;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FeaturesFromES implements AcquireFeaturesInterface{
	
	private final int BITCH_SIZE = 1000;
	
	private Client client = null;
	
	public FeaturesFromES(Client client){
		this.client = client;
	}

	@Override
	public JSONObject getFeatures(Map<String,Object> params) throws Exception {
		//
		JSONObject featureCollection = new JSONObject();
		featureCollection.put("type", "FeatureCollection");
		// 索引名称
		String indexName = (String)params.get("indexName");
		// 网格集合（多个用','分隔）
		BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();
		if (params.containsKey("geoNums") && params.get("geoNums") != null){
			String[] geoNums = ((String)params.get("geoNums")).split(",");
			TermsQueryBuilder geonums = QueryBuilders.termsQuery("geo_num.ancestors", geoNums);
			finalQuery.filter(geonums);
		}
		// 指定矢量文件标识（多个用','分隔）
		if (params.containsKey("vectorIds") && params.get("vectorIds") != null){
			String[] vectorIds = ((String)params.get("vectorIds")).split(",");
			TermsQueryBuilder vectorids = QueryBuilders.termsQuery("sourceId", vectorIds);
			finalQuery.filter(vectorids);
		}
		// 查询矢量要素
		SearchResponse scrollResp = client.prepareSearch(indexName).setTypes("doc")
		        .setScroll(new TimeValue(60000))
		        .setQuery(finalQuery)
		        .setSize(BITCH_SIZE).get(); 
		JSONArray features = new JSONArray();
		do {
		    for (SearchHit hit : scrollResp.getHits().getHits()) {
		        //Handle the hit...
		    	String featureGeoJson = (String)hit.getSource().get("featureGeoJson");
		    	JSONObject feature = JSONObject.fromObject(featureGeoJson);
		    	features.add(feature);
		    }
		    scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
		} while(scrollResp.getHits().getHits().length != 0);
		featureCollection.put("features", features);
		return featureCollection;
	}
}

