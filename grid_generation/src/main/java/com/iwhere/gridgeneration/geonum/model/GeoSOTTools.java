package com.iwhere.gridgeneration.geonum.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.iwhere.geosot.service.GeoSOT;
import com.iwhere.gridgeneration.Application;

/**
 * @author lfq
 * @version 1.0
 * @created 17-04-2019 10:54:24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class GeoSOTTools {

	@Autowired 
	protected GeoSOT geoSot;

	public GeoSOTTools(){

	}
	
	@Test
	public void test(){
		/*Geometry geometry = JTSFactoryFinder.getGeometryFactory( null )
				.createPoint(new Coordinate(116.123, 39.345));*/
		Geometry geometry = JTSFactoryFinder.getGeometryFactory( null )
				.createLineString(new Coordinate[]{new Coordinate(116.123, 39.345),new Coordinate(117.123, 40.345)});

		System.out.println(geometry.getCoordinate());
		getGeoNum(geometry,Byte.valueOf("10"));
	}
	
	/**
	 * 
	 * @param geometry
	 * @param geoLevel
	 */
	public List<Long> getGeoNum(Geometry geometry, byte geoLevel){
		// 返回网格编码集合
		List<Long> geoNums = new ArrayList<Long>();
		// 根据集合类型进行判断
		if (geometry.getGeometryType().equals("Point")) {
			// geometry类型为点
			long geoNum = geoSot.getGeoNum(geometry.getCoordinate().getY(), geometry.getCoordinate().getX(), geoLevel);
			geoNums.add(geoNum);
		} else if (geometry.getGeometryType().equals("LineString")) {
			// 排重用（折线情况会有重复网格编码，前条的末尾节点就是下条线的起始节点）
			Set<Long> geonumSet = new HashSet<Long>();
			// geometry类型为线
			Coordinate[] coordinates = geometry.getCoordinates();
			// GeoSOT只提供直线打码功能，折现需要拆解为多个直线
			for (int i = 0; i <= coordinates.length - 2; i++){
				// 起始点
				Coordinate startPoint = coordinates[i];
				// 结束点
				Coordinate endPoint = coordinates[i+1];
				// 线段对应网格编码
				long[] geonums = geoSot.getGridsByLine(startPoint.y, startPoint.x, endPoint.y, endPoint.x, geoLevel);
				List<Long> geoNumsL = Arrays.stream(geonums).boxed().collect(Collectors.toList());
				// 排重
				geonumSet.addAll(geoNumsL);
			}
			geoNums.addAll(geonumSet);
		}  else if (geometry.getGeometryType().equals("Polygon")) {
			// 构建geoSOT输入参数
			Coordinate[] coordinates = geometry.getCoordinates();
			double[] lngs = new double[coordinates.length];
			double[] lats = new double[coordinates.length];
			for (int i = 0; i < coordinates.length; i++){
				lngs[i] = coordinates[i].x;
				lats[i] = coordinates[i].y;
			}
			long[] geonums = geoSot.getGridsByPolygon(lats, lngs, coordinates.length, geoLevel);
			geoNums = Arrays.stream(geonums).boxed().collect(Collectors.toList());
		}
		return geoNums;
	}
}