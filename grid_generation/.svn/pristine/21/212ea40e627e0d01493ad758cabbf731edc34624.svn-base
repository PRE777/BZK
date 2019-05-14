package com.iwhere.gridgeneration.geonum.model;

import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lfq
 * @version 1.0
 * @created 18-04-2019 9:04:24
 */
public abstract class DataUniqueCode {
	
	@Autowired
	protected UniqueCodeGenerator uniqueCodeGenerator;
	
	protected GeoLevelStrategy geoLevelStrategy;
	
	public DataUniqueCode(){

	}

	/**
	 * 
	 * @param data
	 */
	public String generateUniqueCode(Object data){
		// 解析数据文件
		Map<String,Object> parsedData = parseSpaceInfor(data);
		// 几何图形
		Geometry geometry = (Geometry)parsedData.get("geometry");
		// 决定编码层级的因子
		Object factor = parsedData.get("factor");
		// 获取编码层级
		Byte geoLevel = getGeoLevel(factor);
		// 获取统一码
		String uniqueCode = uniqueCodeGenerator.doGenerateUniqueCode(geometry, geoLevel);
		return uniqueCode;
	}

	/**
	 * 
	 * @param data
	 */
	public abstract Map<String,Object> parseSpaceInfor(Object data);

	/**
	 * 
	 * @param factor
	 */
	public abstract Byte getGeoLevel(Object factor);
}