/**
 * @author lfq
 *
 */
package com.iwhere.gridgeneration.discretization.utils;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FeaturesFromShp implements GetFeaturesInterface{
	
	@Override
	public JSONObject getFeatures(String url) throws Exception{
		File file = new File(url);
		JSONObject featureCollection = new JSONObject();
		featureCollection.put("type", "FeatureCollection");
		JSONArray features = new JSONArray();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("url", file.toURI().toURL());
			DataStore dataStore = DataStoreFinder.getDataStore(map);
			((ShapefileDataStore) dataStore).setCharset(Charset.forName("GBK"));
			String typeName = dataStore.getTypeNames()[0];
			// 读取shp中的要素
			FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
			//source.getSchema().getCoordinateReferenceSystem().toWKT();
			SimpleFeature[] featureArray = (SimpleFeature[])source.getFeatures().toArray();
			// 将要素信息放入GeoJSON中
			for (int i = 0; i < featureArray.length; i++){
				// 获取geometry信息
				GeometryJSON geometryJson = new GeometryJSON(6);
				StringWriter writer = new StringWriter();
				Geometry geometry = (Geometry)featureArray[i].getDefaultGeometryProperty()
						.getValue();
				geometryJson.write(geometry, writer);
				// 索引要素
				Collection<Property> propertyCollection = featureArray[i].getProperties();
				// 要素属性值入库
				JSONObject properties = new JSONObject();
				for (Iterator<Property> propertyIt = propertyCollection.iterator(); propertyIt.hasNext();){
					Property property = propertyIt.next();
					if (!"the_geom".equals(property.getName().toString())){
						properties.put(property.getName().toString(), property.getValue());
					}
				}
				// 构建要素的GEOJSON
				JSONObject featureGeoJson = new JSONObject();
				featureGeoJson.put("type", "Feature");
				featureGeoJson.put("geometry", JSONObject.fromObject(writer.toString()));
				featureGeoJson.put("properties", properties);
				features.add(featureGeoJson);
			}
			featureCollection.put("features", features);	
			return featureCollection;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}


	@Override
	public SimpleFeature[] getFeaturesSource(String url) throws Exception {
		File file = new File(url);
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("url", file.toURI().toURL());

			DataStore dataStore = DataStoreFinder.getDataStore(map);
			//((ShapefileDataStore)dataStore).setCharset(Charset.forName("UTF-8"));
			((ShapefileDataStore)dataStore).setCharset(Charset.forName("GBK"));
			String typeName = dataStore.getTypeNames()[0];
			// 读取shp中的要素
			FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
			//source.getSchema().getCoordinateReferenceSystem().toWKT();
			SimpleFeature[] featureArray = (SimpleFeature[])source.getFeatures().toArray();

			return featureArray;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
}

