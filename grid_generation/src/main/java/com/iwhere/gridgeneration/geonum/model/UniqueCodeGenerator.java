package com.iwhere.gridgeneration.geonum.model;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import com.iwhere.gridgeneration.Application;
import com.iwhere.gridgeneration.utils.GeoSotTools;

/**
 * @author lfq
 * @version 1.0
 * @created 17-04-2019 10:54:24
 */
/*@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)*/
@Component
public class UniqueCodeGenerator {

	@Autowired 
	protected GeoSotTools geoSotTools;

	public UniqueCodeGenerator(){

	}
	
	@Test
	public void test(){
		/*Geometry geometry = JTSFactoryFinder.getGeometryFactory( null )
				.createPoint(new Coordinate(116.123, 39.345));*/
		Geometry geometry = JTSFactoryFinder.getGeometryFactory( null )
				.createLineString(new Coordinate[]{new Coordinate(116.123, 39.345),new Coordinate(117.123, 40.345)});

		System.out.println(geometry.getCoordinate());
		doGenerateUniqueCode(geometry,Byte.valueOf("10"));
	}
	
	/**
	 * 
	 * @param geometry
	 * @param geoLevel
	 */
	public String doGenerateUniqueCode(Geometry geometry, byte geoLevel){
		
		if (geometry.getGeometryType().equals("Point")) {
			// geometry类型为点
			Long C0 = getGeoNum(geometry.getCoordinate().getX(), geometry.getCoordinate().getY(), geoLevel);
			return String.valueOf(C0);
		} else if (geometry.getGeometryType().equals("Polygon") || geometry.getGeometryType().equals("LineString")) {
			// geometry类型为非点
			// 求取最小外包矩形
			Geometry minBoundingBox = calculateMinBoundingBox(geometry);
			// 角点坐标集合
			Coordinate[] coordinates = minBoundingBox.getCoordinates();
			// 左下角点C0
			Long C0 = getGeoNum(coordinates[0].x, coordinates[0].y, geoLevel);
			// 获取右上左下经度纬度
			String lngs = coordinates[1].x + "," + coordinates[3].x;
			String lats = coordinates[1].y + "," + coordinates[3].y;
			// 获取MN
			String MN = getMN(lngs, lats, geoLevel);
			// 根据需要可修改格式
			return C0 + "-" + MN;
		}
		return null;
	}

	/**
	 * 
	 * @param geometry
	 */
	public Geometry calculateMinBoundingBox(Geometry geometry){
		return geometry.getEnvelope();
	}

	/**
	 * 
	 * @param lng
	 * @param lat
	 * @param geoLevel
	 */
	public Long getGeoNum(double lng, double lat, byte geoLevel){
		return geoSotTools.getGeoNum(lng,lat,geoLevel);
	}

	/**
	 * 
	 * @param lngs
	 * @param lats
	 * @param geoLevel
	 */
	public String getMN(String lngs, String lats, byte geoLevel){
		String mnStr = geoSotTools.getGeoNumsOfRectangleWithRowCol(lngs,lats,geoLevel);
		return mnStr;
	}
}