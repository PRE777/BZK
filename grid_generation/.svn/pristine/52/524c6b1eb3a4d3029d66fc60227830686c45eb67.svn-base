package com.iwhere.gridgeneration.discretization.utils;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/** 

 * Class GeometryDemo.java

 * Description Geometry 几何实体的创建，读取操作

 * Company mapbar

 * author Chenll E-mail: Chenll@mapbar.com

 * Version 1.0

 * Date 2012-2-17 上午11:08:50

 */
public class CreateGeometryTools {

    private GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
   
    /**
     * create a point
     * @return
     */
    public Point createPoint(JSONObject feature){
    	// 获取节点
    	JSONArray pointJson = feature.getJSONObject("geometry")
    			.getJSONArray("coordinates");
        Coordinate coord = new Coordinate(pointJson.getDouble(0),pointJson.getDouble(1));
        Point point = geometryFactory.createPoint(coord);
        return point;
    }
    
    /**
     * create a point
     * @return
     */
    public MultiPoint createMPoint(JSONObject feature){
    	// 获取节点
    	JSONArray pointJson = feature.getJSONObject("geometry")
    			.getJSONArray("coordinates");
    	Coordinate[] coords1  = new Coordinate[pointJson.size()];
    	for (int j = 0; j < pointJson.size(); j++){
    		JSONArray point = pointJson.getJSONArray(j);
			Coordinate coordinate = new Coordinate(point.getDouble(0), point.getDouble(1));
			coords1[j] = coordinate;
    	}
        MultiPoint multiPoint = geometryFactory.createMultiPointFromCoords(coords1);
        return multiPoint;
    }

    /**
     *
     * create a line
     * @return
     */
    public LineString createLine(JSONObject feature){
    	// 获取节点
    	JSONArray line = feature.getJSONObject("geometry")
    			.getJSONArray("coordinates");
    	Coordinate[] coords1  = new Coordinate[line.size()];
    	for (int j = 0; j < line.size(); j++){
			JSONArray point = line.getJSONArray(j);
			Coordinate coordinate = new Coordinate(point.getDouble(0), point.getDouble(1));
			coords1[j] = coordinate;
		}
    	LineString lineString = geometryFactory.createLineString(coords1);
        return lineString;
    }
   
    /**
     * create multiLine
     * @return
     */
    public MultiLineString createMLine(JSONObject feature){
    	// 获取节点
    	JSONArray multiLine = feature.getJSONObject("geometry")
    			.getJSONArray("coordinates");
    	
    	LineString[] lineStrings = new LineString[multiLine.size()];
    	
    	for (int i = 0; i < multiLine.size(); i++){
    		JSONArray line = multiLine.getJSONArray(i);
    		Coordinate[] coords1  = new Coordinate[line.size()];
    		for (int j = 0; j < line.size(); j++){
    			JSONArray point = line.getJSONArray(j);
    			Coordinate coordinate = new Coordinate(point.getDouble(0), point.getDouble(1));
    			coords1[j] = coordinate;
    		}
    		LineString line1 = geometryFactory.createLineString(coords1);
    		lineStrings[i] = line1;
    	}
        MultiLineString ms = geometryFactory.createMultiLineString(lineStrings);
        return ms;
    }
  
    /**
     * create a Circle  创建一个圆，圆心(x,y) 半径RADIUS
     * @param x
     * @param y
     * @param RADIUS
     * @return
     */
    public Polygon createCircle(double x, double y, final double RADIUS){
        final int SIDES = 32;//圆上面的点个数
        Coordinate coords[] = new Coordinate[SIDES+1];
        for( int i = 0; i < SIDES; i++){
            double angle = ((double) i / (double) SIDES) * Math.PI * 2.0;
            double dx = Math.cos( angle ) * RADIUS;
            double dy = Math.sin( angle ) * RADIUS;
            coords[i] = new Coordinate( (double) x + dx, (double) y + dy );
        }
        coords[SIDES] = coords[0];
        LinearRing ring = geometryFactory.createLinearRing( coords );
        Polygon polygon = geometryFactory.createPolygon( ring, null );
        return polygon;
    }

}
