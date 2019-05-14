package com.iwhere.gridgeneration.entity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class Polygon {
	private Point[] points;
	private final String type = "POLYGON";
	
	public Polygon(Point[] points){
		this.points = points;
	}
	public String toWKT(){
		String wkt = type + " ((";
		Point firstPoi = this.points[0];
		for(Point poi: this.points){
			wkt+=poi.getLng()+" "+poi.getLat()+",";
		}
		wkt+=firstPoi.getLng()+" "+ firstPoi.getLat()+"))";
		return wkt;
	}
	/**
	 * convert lower-left and upper-right points of a box to Polygon WKT format
	 * @param minLng
	 * @param minLat
	 * @param maxLng
	 * @param maxLat
	 * @return WkT
	 */
	public static String toWKT(double minLng,double minLat,double maxLng,double maxLat){
		String ldPoint = minLng+" "+minLat;
		String rdPoint = maxLng+" "+minLat;
		String ruPoint = maxLng+" "+maxLat;
		String luPoint = minLng+" "+maxLat;
		String wktBox = "POLYGON (("+ldPoint+","+rdPoint+","+ruPoint+","+luPoint+","+ldPoint+"))";
		return wktBox;
	}
	/**
	 * convert lower-left and upper-right points of a box to Polygon GeoJson format
	 * @param minLng
	 * @param minLat
	 * @param maxLng
	 * @param maxLat
	 * @return GeoJson
	 * @throws JSONException 
	 */
	public static String toGeoJson(double minLng, double minLat, double maxLng, double maxLat) throws JSONException {

		double[] ldPoint = { minLng, minLat };
		double[] rdPoint = { maxLng, minLat };
		double[] ruPoint = { maxLng, maxLat };
		double[] luPoint = { minLng, maxLat };
		double[][] points = { ldPoint, rdPoint, ruPoint, luPoint, ldPoint };
		ArrayList<double[][]> boxlist = new ArrayList<double[][]>();
		boxlist.add(points);
		JSONObject jsonBox = new JSONObject();
		jsonBox.put("type", "Polygon");
		jsonBox.put("coordinates", boxlist);
		return jsonBox.toString();
	}
}
