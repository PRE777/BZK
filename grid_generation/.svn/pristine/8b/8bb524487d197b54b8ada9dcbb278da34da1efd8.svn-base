package com.iwhere.gridgeneration.discretization.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.geotools.geometry.jts.GeometryClipper;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import com.iwhere.geosot.dto.req.GeoSOTReq;
import com.iwhere.geosot.dto.resp.GeoSOTResp07;
import com.iwhere.geosot.service.GeoSOTService;

public class DiscretizeVector {
	
	private GeoSOTService geoSOTService = null;
	
	public DiscretizeVector(GeoSOTService geoSOTService){
		this.geoSOTService = geoSOTService;
	}
	
	public List<Geometry> doDiscretizeVector(Map<String,Object> paramsMap,List<Geometry> geometryList) throws Exception{
		// 切后的geometry
		List<Geometry> clipedGeometryList = new ArrayList<Geometry>();
		// 剖分网格编码
		String geonum = (String)paramsMap.get("geonum");
		String[] geonumInfor = geonum.split("-");
		// 获取网格范围
		GeoSOTReq req = new GeoSOTReq();
		req.setGeoNum(Long.valueOf(geonumInfor[0]));
		req.setGeoLevel(Byte.valueOf(geonumInfor[1]));
		GeoSOTResp07 geoSOTResp = geoSOTService.getScopeOfGeoNum(req);
		
		double minLat = geoSOTResp.getMinLat();
		double minLng = geoSOTResp.getMinLng();
		double maxLat = geoSOTResp.getMaxLat();
		double maxLng = geoSOTResp.getMaxLng();
		// 构建切取范围
		Envelope env = new Envelope(minLng, maxLng, minLat, maxLat);
		GeometryClipper geometryClipper = new GeometryClipper(env);
		
		for (int i = 0; i < geometryList.size(); i++){
			Geometry clipedGeometry = geometryClipper.clipSafe(geometryList.get(i), true, 0.0d);
			clipedGeometryList.add(clipedGeometry);
		}
		return clipedGeometryList;
	}
}
