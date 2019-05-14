package com.iwhere.gridgeneration.entity;

import java.math.BigDecimal;

public class Point implements Comparable<Object>{
	private double lat;
	private double lng;
	private final static String type = "POINT";
	public Point(double lat, double lng){
		this.setLat(lat);
		this.setLng(lng);
	}
	
	/**
	 * @param poi double[0] = 纬度，double[1] = 经度
	 */
	public Point(double[] poi){
		this.setLat(poi[0]);
		this.setLng(poi[1]);
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String toWKT(){
		String wkt = type+"("+this.lng +" "+ this.lat + ")";
		return wkt;
	}
	public static String toWKT(double lat,double lng){
		String wkt = type+"("+lng +" "+ lat + ")";
		return wkt;
	}

	public int compareTo(Object o) {
		Point p =(Point) o;
		// TODO Auto-generated method stub
		BigDecimal x0 = new BigDecimal(this.lng);
		BigDecimal y0 = new BigDecimal(this.lat);
		BigDecimal x1 = new BigDecimal(p.getLng());
		BigDecimal y1 = new BigDecimal(p.getLat());
		int reX = x0.compareTo(x1);
		if(reX==0){
			return y0.compareTo(y1);
		}
		return reX;
	}
}
