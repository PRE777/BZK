package com.iwhere.gridgeneration.geonum.model;

import java.util.HashMap;
import java.util.Map;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@Component
public class DomDataUniqueCode extends DataUniqueCode {
	
	@Autowired
	@Qualifier("domUniqueCodeGeolevelStrategy")
	private GeoLevelStrategy geoLevelStrategy;

	@Override
	public Map<String, Object> parseSpaceInfor(Object data) {
		// 返回结果map key:geometry ——> value:Geometry  key:factor ——> value:Object 
		Map<String,Object> resultMap = new HashMap<String,Object>();
		// 根据不同的数据结构进行不同的解析 START
		JSONObject dataJson = (JSONObject)data;
		// 获取经纬度节点信息

		/*String[] latArray =  dataJson.getString("lats").split("~");
        String[] lngArray =  dataJson.getString("lngs").split("~");*/
        // TODO 需要根据参数具体分析一下 #构建几何对象(DOM数据的几何图形为Polygon)#
        /*Coordinate[] coordinates = new Coordinate[lngArray.length + 1];
        for (int i = 0; i < lngArray.length; i++){
        	coordinates[i] = new Coordinate(116.123, 39.345);
        }*/
		Coordinate[] coordinates = new Coordinate[]{new Coordinate(dataJson.getDouble("lonlatExtentLeft"), dataJson.getDouble("lonlatExtentBottom")),new Coordinate(dataJson.getDouble("lonlatExtentLeft"), dataJson.getDouble("lonlatExtentTop"))
        		,new Coordinate(dataJson.getDouble("lonlatExtentRight"), dataJson.getDouble("lonlatExtentTop")),new Coordinate(dataJson.getDouble("lonlatExtentRight"), dataJson.getDouble("lonlatExtentBottom")),new Coordinate(dataJson.getDouble("lonlatExtentLeft"), dataJson.getDouble("lonlatExtentBottom"))};
        // 根据不同的数据结构进行不同的解析 END
        // 构建geometry
        Geometry geometry = JTSFactoryFinder.getGeometryFactory(null)
				.createPolygon(coordinates);
        resultMap.put("geometry", geometry);
        // TODO 找出数据中决定剖分层级的因子
        Object factor = dataJson.get("scale");
        //Double factor = 20000.0d;
        resultMap.put("factor", factor);
		return resultMap;
	}

	@Override
	public Byte getGeoLevel(Object factor) {
		// TODO Auto-generated method stub
		return geoLevelStrategy.getGeoLevel(factor);
	}

}
