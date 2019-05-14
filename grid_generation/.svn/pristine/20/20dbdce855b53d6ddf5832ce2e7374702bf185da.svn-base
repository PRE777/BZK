package com.iwhere.gridgeneration.discretization.utils;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GenerateSHP {
	
	/** 创建shp文件
	 *  @param filePath 生成shp文件的存放路径
	 *  @param propertyJsonArray [{"name":"POIID","class":""}]
	 *  @param GeoJSON(特征集合对象:FeatureCollection)
	 *  
	 **/
	public void write2(Map<String,Object> paramMap) {
		try {
			// 创建shape文件对象
			File file = new File((String)paramMap.get("filePath"));
			Map<String, Serializable> params = new HashMap<String, Serializable>();
			params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
			ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
			// 定义图形信息和属性信息
			SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
			tb.setCRS(DefaultGeographicCRS.WGS84);
			tb.setName("shapefile");
			// 属性信息
			String propertyInfo = (String)paramMap.get("propertyJsonArray");
			JSONArray propertyJsonArray = JSONArray.fromObject(propertyInfo);
			for (int i = 0; i < propertyJsonArray.size(); i++){
				JSONObject property = propertyJsonArray.getJSONObject(i);
				tb.add(property.getString("name"), Class.forName(property.getString("class")));
			}
	        ds.createSchema(tb.buildFeatureType());  
	        ds.setCharset(Charset.forName("GBK"));  
			// 设置Writer
			FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0],
					Transaction.AUTO_COMMIT);
			
			JSONObject featureCollectionJson = (JSONObject)paramMap.get("FeatureCollection");
			JSONArray featureJsonArray = featureCollectionJson.getJSONArray("features");
			for (int i = 0; i < featureJsonArray.size(); i++){
				JSONObject featureJson = featureJsonArray.getJSONObject(i);
				//产生要素
				SimpleFeature feature = writer.next();
				JSONObject propertiesJSONObject = featureJson.getJSONObject("properties");
				for (Iterator<String> it = propertiesJSONObject.keySet().iterator(); it.hasNext();){
					if (it.next().equals("geometry")){
						// TODO 
						feature.setAttribute("the_geom", new GeometryFactory().createPoint(new Coordinate(116.123, 39.345)));
						continue;
						
					}
					feature.setAttribute(it.next(), propertiesJSONObject.get(it.next()));
				}
			}
			writer.write();
			writer.close();
			ds.dispose();
		} catch (Exception e) {
		}
	}
}