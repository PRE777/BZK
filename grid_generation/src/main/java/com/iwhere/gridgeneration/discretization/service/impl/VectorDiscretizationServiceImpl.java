package com.iwhere.gridgeneration.discretization.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.queryparser.xml.builders.TermQueryBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iwhere.geosot.service.GeoSOTService;
import com.iwhere.gridgeneration.biz.geosot.controller.GeoSOTController;
import com.iwhere.gridgeneration.discretization.service.VectorDiscretizationService;
import com.iwhere.gridgeneration.discretization.utils.AcquireFeaturesInterface;
import com.iwhere.gridgeneration.discretization.utils.ClipFeatures;
import com.iwhere.gridgeneration.discretization.utils.ClipFeaturesInterface;
import com.iwhere.gridgeneration.discretization.utils.DiscretizationProperties;
import com.iwhere.gridgeneration.discretization.utils.FeaturesFromES;
import com.iwhere.gridgeneration.discretization.utils.FeaturesFromShp;
import com.iwhere.gridgeneration.discretization.utils.FileFilter;
import com.iwhere.gridgeneration.discretization.utils.GetFeaturesInterface;
import com.iwhere.gridgeneration.discretization.utils.GetUUID;
import com.iwhere.gridgeneration.discretization.utils.IndexFeatures;
import com.iwhere.gridgeneration.discretization.utils.IndexVectorFeature;
//import com.vividsolutions.jts.geom.Coordinate;
//import com.vividsolutions.jts.geom.GeometryFactory;
import com.iwhere.gridgeneration.utils.ESClient;
import com.iwhere.gridgeneration.utils.base.dto.BaseResp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class VectorDiscretizationServiceImpl implements VectorDiscretizationService {
	
	private static Logger logger = LoggerFactory.getLogger(GeoSOTController.class);

	@Autowired 
	private DiscretizationProperties discretizationProperties;
	
	@Autowired
	private GeoSOTService geoSOTService;
	
	/*@Autowired
	private TransportClient transportClient;*/
	
	@Override
	public BaseResp uploadVectorFile(MultipartFile[] multipartfiles) throws Exception{
		// 存储到本地
		String savePath = discretizationProperties.getUploadPath();
		// 为上传的文件重新命名，以免被后续的同名文件覆盖
		String uuid = GetUUID.getUUID();
		StringBuffer unmodifiedName = new StringBuffer();
		StringBuffer shpFile = new StringBuffer();
        if(multipartfiles != null && multipartfiles.length != 0){
            if(null != multipartfiles && multipartfiles.length > 0){
            	int i = 0;
                //遍历并保存文件
                for(MultipartFile file : multipartfiles){
                	if (i == 0){
                		// 文件原始名称
                		unmodifiedName.append(file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf(".")));
                		//shpFile.append(savePath + file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."))+".shp");
                		shpFile.append(savePath + unmodifiedName + "_" + uuid +".shp");
                		i++;
                	}
                	try {
						file.transferTo(new File(savePath + unmodifiedName + "_" + uuid + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))));
						//file.transferTo(new File(savePath + file.getOriginalFilename()));
                	} catch (Exception e) {
                		logger.error(e.getMessage());
                		e.printStackTrace();
                		return BaseResp.error("上传矢量数据落盘失败");
					} 
                }
            }
        }
        // 读取矢量信息
        GetFeaturesInterface getFeaturesInterface = new FeaturesFromShp();
		// 返回矢量中所有要素组成的FeatureCollection的GEOJSON
		JSONObject featrueArray = null;
		try {
			featrueArray = getFeaturesInterface.getFeatures(shpFile.toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
    		e.printStackTrace();
    		return BaseResp.error("读取矢量数据失败");
		}
        // 矢量索引信息
        Map<String,Object> indexMap = new HashMap<String,Object>();
        // 文件存储路径
        indexMap.put("url", shpFile);
        // 矢量文件geojson
        indexMap.put("geojson", featrueArray.toString());
		// 唯一标识
		indexMap.put("sourceId", uuid);
		// 原始名称
		indexMap.put("originalName", unmodifiedName.toString());
		// 上传时间
		indexMap.put("uploadTime", System.currentTimeMillis());
		// 索引
		IndexResponse response = ESClient.getInstance()
				.prepareIndex(discretizationProperties.getVectorIndexName(),discretizationProperties.getVectorTypeName())
				.setSource(indexMap)
				.get();
		return BaseResp.map().append("data", indexMap);
	}

	@Override
	public BaseResp doProcessBeforeDiscretization(String sourceId, byte geoLevel) {
		// 获取获取矢量数据信息
		BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();
		TermsQueryBuilder sourceIdTerm = QueryBuilders.termsQuery("sourceId", sourceId);
		finalQuery.filter(sourceIdTerm);
		SearchResponse response1 = ESClient.getInstance()
				.prepareSearch(discretizationProperties.getVectorIndexName())
				.setTypes(discretizationProperties.getVectorTypeName())
				.setQuery(finalQuery).setSize(1).execute().actionGet();

		Map<String, Object> source = new HashMap<String, Object>();
		if (response1.getHits().getTotalHits() == 0) {
			// 表示没有数据
		} else {
			for (SearchHit hit : response1.getHits()) {
				source = hit.getSource();
			}
		}
		/*// 获取矢量文件中的要素信息
		GetFeaturesInterface getFeaturesInterface = new FeaturesFromShp();
		// 返回矢量中所有要素组成的FeatureCollection的GEOJSON
		JSONObject featrueArray = getFeaturesInterface.getFeatures(url);*/
		// 索引矢量要素
		IndexVectorFeature indexVectorFeature = new IndexFeatures(geoSOTService,ESClient.getInstance());
		// indexName: ES索引名称    typeName: ES索引类型名称   geoLevel:网格层级
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("indexName", discretizationProperties.getFeatureIndexName());
		params.put("typeName", discretizationProperties.getFeatureTypeName());
		params.put("geoLevel", geoLevel);
		params.put("uniqueId", source.get("sourceId"));
		params.put("originalName", source.get("originalName"));
		// 索引要素
		try {
			indexVectorFeature.doIndex((String)source.get("url"),JSONObject.fromObject(source.get("geojson")), params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
    		e.printStackTrace();
    		return BaseResp.error("索引要素失败");
		}
		return BaseResp.success();
	}
	
	@Override
	public BaseResp getDiscretizationVectors(String geonums, String sourceIds) throws Exception {
		// 获取要素集合
		AcquireFeaturesInterface featuresFromES = new FeaturesFromES(ESClient.getInstance());
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("indexName", discretizationProperties.getFeatureIndexName());
		params.put("typeName", discretizationProperties.getFeatureTypeName());
		params.put("geoNums", geonums);
		params.put("vectorIds", sourceIds);
		JSONObject featureCollection = featuresFromES.getFeatures(params);
		// 切取
		ClipFeaturesInterface clipFeaturesInterface = new ClipFeatures(geoSOTService);
		Map<String,JSONObject> clipedFeatures = clipFeaturesInterface.clipFeatures(geonums, featureCollection);
		return BaseResp.map().append("data", clipedFeatures);
	}

	@Override
	public BaseResp doProcessOfDiscretization(HttpServletResponse res, String geonums, String sourceId) throws Exception {
		// 获取要素集合
		AcquireFeaturesInterface featuresFromES = new FeaturesFromES(ESClient.getInstance());
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("indexName", discretizationProperties.getFeatureIndexName());
		params.put("typeName", discretizationProperties.getFeatureTypeName());
		params.put("geoNums", geonums);
		params.put("vectorIds", sourceId);
		JSONObject featureCollection = featuresFromES.getFeatures(params);
		// 切取
		ClipFeaturesInterface clipFeaturesInterface = new ClipFeatures(geoSOTService);
		Map<String,JSONObject> clipedFeatures = clipFeaturesInterface.clipFeatures(geonums, featureCollection);
		// 获取shp元数据
		String sourceShp = "";
		BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();
		TermsQueryBuilder sourceIdTerm = QueryBuilders.termsQuery("sourceId", sourceId);
		finalQuery.filter(sourceIdTerm);
		SearchResponse response1 = ESClient.getInstance().prepareSearch(discretizationProperties.getVectorIndexName())
				.setTypes(discretizationProperties.getVectorTypeName())
				.setQuery(finalQuery).setSize(1).execute().actionGet();
		
		Map<String, Object> source = new HashMap<String, Object>();
		if (response1.getHits().getTotalHits() == 0) {
			// 表示没有数据
		} else {
			for (SearchHit hit : response1.getHits()) {
				source = hit.getSource();
				sourceShp = (String)source.get("url");
			}
		}
		// 聚集所有要素
		JSONObject allFeatureCollection = new JSONObject();
		JSONArray allFeatureArray = new JSONArray();
		for (Iterator<String> it = clipedFeatures.keySet().iterator(); it.hasNext();){
			String key = it.next();
			JSONObject geoNumFeatures = clipedFeatures.get(key);
			JSONArray featureJSONArray = geoNumFeatures.getJSONArray("features");
			allFeatureArray.addAll(featureJSONArray);
			
		}
		allFeatureCollection.put("features", allFeatureArray);
		// 生成shp存储位置
		Long currentTime = System.currentTimeMillis();
		String fileName = discretizationProperties.getGeneratePath() + source.get("originalName") + currentTime;
		String targetFile = fileName + ".shp" ;
		write(sourceShp,targetFile,allFeatureCollection);
		// 打包
		String zipPath = generateZip(discretizationProperties.getGeneratePath(),(String)source.get("originalName") + currentTime);
		// 下载
		testDownload(res,zipPath,(String)source.get("originalName") + currentTime + ".zip");
		return BaseResp.map().append("targetFile", targetFile);
	}
	
	public void write(String sourceFilePath,String destfilepath, JSONObject featureCollection) {  
		try {  
	        //创建shape文件对象  
			//源shape文件  
	        ShapefileDataStore shapeDS = (ShapefileDataStore) new ShapefileDataStoreFactory().createDataStore(new File(sourceFilePath).toURI().toURL());  
	        //创建目标shape文件对象  
	        Map<String, Serializable> params = new HashMap<String, Serializable>();  
	        FileDataStoreFactorySpi factory = new ShapefileDataStoreFactory();  
	        params.put(ShapefileDataStoreFactory.URLP.key, new File(destfilepath).toURI().toURL());  
	        ShapefileDataStore ds = (ShapefileDataStore) factory.createNewDataStore(params);  
	        // 设置属性  
	        SimpleFeatureSource fs = shapeDS.getFeatureSource(shapeDS.getTypeNames()[0]);  
	        //下面这行还有其他写法，根据源shape文件的simpleFeatureType可以不用retype，而直接用fs.getSchema设置  
	        ds.createSchema(SimpleFeatureTypeBuilder.retype(fs.getSchema(), DefaultGeographicCRS.WGS84));  
	          
	        //设置writer  
	        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);  
	        //写下一条  
	        SimpleFeature feature = writer.next();  
	        JSONArray features = featureCollection.getJSONArray("features");
	        for (int i = 0; i < features.size(); i++){
	        	JSONObject featureGeoJson = features.getJSONObject(i);
	        	feature.setAttribute("the_geom", new GeometryFactory().createPoint(new Coordinate(116.123, 39.345)));  
	        	// 填充几何信息
	        	GeometryJSON geometryJson = new GeometryJSON(6);
	        	Geometry geometry = geometryJson.read(featureGeoJson.getJSONObject("geometry").toString());
	        	feature.setAttribute("the_geom", geometry);  
	        	// 填充属性信息
	        	JSONObject propertyJSON = featureGeoJson.getJSONObject("properties");
	        	for (Iterator<String> it= (Iterator<String>)propertyJSON.keySet().iterator(); it.hasNext();){
	        		String key = it.next();
	        		feature.setAttribute(key, propertyJSON.get(key));  
	        	}
	        	feature = writer.next(); 
	        }
	        writer.write();  
	        writer.close();  
	        ds.dispose();
	    } catch (Exception e) { 
	    	e.printStackTrace();
	    }  
		
	}
	
	/**
     * 打包成zip包
     */
    public String generateZip(String vectorFoldPath, String fileName) throws Exception {
        ZipOutputStream out = null;
        File vectorFold = new File(vectorFoldPath);
        File[] files = vectorFold.listFiles(new FileFilter(fileName));
        File zip = new File(vectorFoldPath+fileName+".zip");
        OutputStream os = new FileOutputStream(vectorFoldPath+fileName+".zip");
        try {
            byte[] buffer = new byte[1024];
            //生成的ZIP文件名为Demo.zip
            out = new ZipOutputStream(os);
            //需要同时下载的两个文件result.txt ，source.txt
            for (File file : files) {
                FileInputStream fis = new FileInputStream(file);
                out.putNextEntry(new ZipEntry(file.getName()));
                int len;
                //读入需要下载的文件的内容，打包到zip文件
                while ((len = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.closeEntry();
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return vectorFoldPath+fileName+".zip";
    }
	
	public void testDownload(HttpServletResponse res,String destfilepath, String fileName) {
		res.setHeader("content-type", "application/octet-stream");
		res.setContentType("application/octet-stream");
		res.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		byte[] buff = new byte[1024];
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			os = res.getOutputStream();
			bis = new BufferedInputStream(new FileInputStream(new File(destfilepath)));
			int i = bis.read(buff);
			while (i != -1) {
				os.write(buff, 0, buff.length);
				os.flush();
				i = bis.read(buff);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("success");
	}

	@Override
	public BaseResp getVectors(Integer pageNo, Integer pageSize) throws Exception {
		// 获取获取矢量数据信息
		SearchResponse response1 = ESClient.getInstance()
				.prepareSearch(discretizationProperties.getVectorIndexName())
				.setTypes(discretizationProperties.getVectorTypeName())
				.setQuery(QueryBuilders.matchAllQuery()).setFrom((pageNo-1)*pageSize).setSize(pageSize).execute().actionGet();
		
		List<Map<String, Object>> vectors = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		if (response1.getHits().getTotalHits() == 0) {
			// 表示没有数据
		} else {
			for (SearchHit hit : response1.getHits()) {
				vectors.add(hit.getSource());
			}
		}
		long recordCount = response1.getHits().getTotalHits();
		map.put("records", vectors);
		map.put("recordCount", recordCount);
		return BaseResp.map().append("data", map);
	}
	
	@Override
	public BaseResp doInsertFeature(String sourceId, Byte geolevel, String mapType, String layerName) {
		// 获取获取矢量数据信息
		BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();
		TermsQueryBuilder sourceIdTerm = QueryBuilders.termsQuery("sourceId", sourceId);
		finalQuery.filter(sourceIdTerm);
		SearchResponse response1 = ESClient.getInstance()
				.prepareSearch(discretizationProperties.getVectorIndexName())
				.setTypes(discretizationProperties.getVectorTypeName())
				.setQuery(finalQuery).setSize(1).execute().actionGet();

		Map<String, Object> source = new HashMap<String, Object>();
		if (response1.getHits().getTotalHits() == 0) {
			// 表示没有数据
		} else {
			for (SearchHit hit : response1.getHits()) {
				source = hit.getSource();
			}
		}
		/*// 获取矢量文件中的要素信息
		GetFeaturesInterface getFeaturesInterface = new FeaturesFromShp();
		// 返回矢量中所有要素组成的FeatureCollection的GEOJSON
		JSONObject featrueArray = getFeaturesInterface.getFeatures(url);*/
		// 索引矢量要素
		IndexVectorFeature indexVectorFeature = new IndexFeatures(geoSOTService,ESClient.getInstance());
		// indexName: ES索引名称    typeName: ES索引类型名称   geoLevel:网格层级
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("indexName", discretizationProperties.getFeaturesIndexName());
		params.put("typeName", discretizationProperties.getFeaturesTypeName());
		params.put("geoLevel", geolevel);
		params.put("uniqueId", source.get("sourceId"));
		params.put("originalName", source.get("originalName"));
		params.put("mapType", mapType);
		params.put("layerName", layerName);
		// 索引要素
		try {
			//indexVectorFeature.doFeatureIndex((String)source.get("url"),JSONObject.fromObject(source.get("geojson")), params);
			indexVectorFeature.doFeatureIndex((String)source.get("url"),null, params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
    		e.printStackTrace();
    		return BaseResp.error("索引要素失败");
		}
		return BaseResp.success();
	}
	
	@Override
	public BaseResp getFeatures(String geoNums, String layerName, Integer pageNum, Integer pageSize) throws Exception {
		
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		// 图层过滤
		if (null != layerName && !layerName.isEmpty()){
			TermsQueryBuilder mapTypeQuery = QueryBuilders.termsQuery("layerName", layerName.split(","));
			query.filter(mapTypeQuery);
		}
		// 网格过滤
		if (null != geoNums && !geoNums.isEmpty()){
			TermsQueryBuilder geoNumsQuery = QueryBuilders.termsQuery("geo_num.ancestors", geoNums.split(","));
			query.filter(geoNumsQuery);
		}
		// 获取要素信息
		SearchResponse response1 = ESClient.getInstance()
				.prepareSearch(discretizationProperties.getFeaturesIndexName())
				.setTypes(discretizationProperties.getFeaturesTypeName())
				.setQuery(query)
				.addSort("sortItem", SortOrder.DESC)
				.setFrom((pageNum-1)*pageSize)
				.setSize(pageSize)
				.execute()
				.actionGet();

		List<Map<String, Object>> vectors = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		if (response1.getHits().getTotalHits() == 0) {
			// 表示没有数据
		} else {
			for (SearchHit hit : response1.getHits()) {
				vectors.add(hit.getSource());
			}
		}
		long recordCount = response1.getHits().getTotalHits();
		map.put("records", vectors);
		map.put("totalPages", recordCount%pageSize==0?recordCount/pageSize:recordCount/pageSize+1);
		map.put("recordCount", recordCount);
		return BaseResp.map().append("data", map);
	}
}

