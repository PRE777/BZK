package com.iwhere.gridgeneration.biz.data.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iwhere.geosot.dto.resp.GeoSOTResp01;
import com.iwhere.gridgeneration.utils.base.dto.BaseResp;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface IDataService {

	void addDataToEs(String string) throws Exception;

	JSONArray creatEncoded(JSONArray datajson);

	JSONArray getDataSource();

	JSONArray getSourceDataMeta(String dataType);

	JSONObject getSourceData(String dataType, Integer pageNum, Integer pageSize) throws Exception;

	/*Integer scaleZoom(int scale);*/

	GeoSOTResp01 drawGridOnMap(int scale, Double lbLng, Double lbLat, Double rtLng, Double rtLat);

	JSONObject getInformationHighlyActive(String dataType, String geonums, Integer pageNum, Integer pageSize, String startProductDate, String endProductDate,
			Integer scale, String cellSize, String geoResName);

	JSONObject getSingleGeoNumOfPoint(int scale, Double lat, Double lng);

	JSONArray getGeoNumsByPolygon(int scale, String lats, String lngs, String points);

	JSONArray getGeoNumsOfLine(int scale, String lats, String lngs);

	JSONArray getDataStatisticsInfor(String startProductDate, String endProductDate, String dataType, String geonums, Integer height, String typecode);

	JSONArray getDataDistributionStatus(Long createStartTime, Long createEndTime, String dataTypes, String grids,
			Integer height);

	JSONArray getRelevanceDataSource();
	
	JSONArray getVectorSourceCatalogue();

	JSONObject getInformationYggf(String dataType, String geonums, Integer pageNum, Integer pageSize);

	JSONObject getDataDistribute(String dataTypes, String geoNums, Double leftTopLat, Double leftTopLng, Double rightBotmLat,
			Double rightBotmLng, Integer height, String typecode, String dayTime, String showtype);

	JSONArray keepwatch();

	JSONArray getDataStatisticsInforByTime(String startProductDate, String endProductDate, String dataType,
			String geonums, Integer height, String typecode);

	BaseResp autoupdateStatus(String status, JSONArray json);

    JSONObject getdateStatus();

    JSONArray getGeoNumsByAdministrative(int scale, String points);

	void insertDataByShp(MultipartFile[] shpFiles) throws Exception;

    JSONArray getSpecial();

	BaseResp dataGriddingDistribution(HttpServletResponse res, String token);

	JSONArray getDataStatisticsInforByTimeTotle(String startProductDate, String dataType, String geonums, Integer height, String typecode, String arrays);
}
