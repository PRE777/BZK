package com.iwhere.gridgeneration.utils;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iwhere.geosot.service.GeoSOT;

/**
 * @author lfq
 * @version 1.0
 * @created 17-04-2019 11:06:19
 */
@Component
public class GeoSotTools {

	@Autowired
	private GeoSOT geoSOT;

	public GeoSotTools(){

	}

	public void finalize() throws Throwable {

	}
	
	/**
	 * 
	 * @param lng
	 * @param lat
	 * @param geoLevel
	 */
	public Long getGeoNum(double lng, double lat, byte geoLevel){
		return geoSOT.getGeoNum(lat, lng, geoLevel);
	}
	
	/**
	 * 
	 * @param lng
	 * @param lat
	 * @param geoLevel
	 */
	public Long getGeoNumsOfLine(double lng, double lat, byte geoLevel){
		return geoSOT.getGeoNum(lat, lng, geoLevel);
	}

	/**
	 * 
	 * @param lngs
	 * @param lats
	 * @param geoLevel
	 */
	public String getGeoNumsOfRectangleWithRowCol(String lngs, String lats, byte geoLevel){
		// 经纬度拆分
        String[] lngsArray = lngs.split(",");
		String[] latsArray = lats.split(",");
		// 获取MN
		Map<String, Object> resultMap = geoSOT.getGridsByRectangle(Double.valueOf(latsArray[0])
				, Double.valueOf(lngsArray[0])
				, Double.valueOf(latsArray[1])
				, Double.valueOf(lngsArray[1])
                , geoLevel);
		return resultMap.get("cols") + "-" + resultMap.get("cols");
	}
}