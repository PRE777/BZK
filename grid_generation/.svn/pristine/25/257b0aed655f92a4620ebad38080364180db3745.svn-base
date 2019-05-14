package com.iwhere.gridgeneration.utils;

import java.io.Serializable;

public class PoiPoint implements Serializable {
	private static final long serialVersionUID = 3156227536253239112L;
	
	private Double lng;
	private Double lat;
	public PoiPoint() {
		super();
	}
	public PoiPoint(Double lng, Double lat) {
		super();
		this.lng = lng;
		this.lat = lat;
	}
	
	public static PoiPoint getPoint(String location) {
		location = PoiPoint.getLocation(location);
		String[] locations = location.split(", *");
		return new PoiPoint(Double.valueOf(locations[0]), Double.valueOf(locations[1]));
	}
	
	public static String getLocation(String location) {
		if (location.startsWith("(") || location.startsWith("[")) {
			location = location.substring(1);
		}
		if (location.endsWith(")") || location.endsWith("]")) {
			location = location.substring(0, location.length() - 1);
		}
		return location;
	}

	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	@Override
	public int hashCode() {

		int result = 1;

		long temp = Double.doubleToLongBits(lng);
		result = 31 * result + (int) (temp ^ temp >>> 32);

		temp = Double.doubleToLongBits(lat);
		result = 31 * result + (int) (temp ^ temp >>> 32);

		return result;
	}
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PoiPoint)) {
			return false;
		}
		PoiPoint other = (PoiPoint) obj;
		if (Double.doubleToLongBits(lng) != Double.doubleToLongBits(other.lng)) {
			return false;
		}
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat)) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		return "Point [lng=" + lng + ", lat=" + lat + "]";
	}
}
