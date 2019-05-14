package com.iwhere.gridgeneration.utils;

import org.springframework.util.Assert;


/**
 * ---------------------------------------------------------------------------
 * 位置相关
 * ---------------------------------------------------------------------------
 * <strong>copyright</strong>： ©版权所有 成都都在哪网讯科技有限公司<br>
 * ----------------------------------------------------------------------------
 * 
 * @author: hewei
 * @time:2016年10月28日 下午5:55:27
 *                   ---------------------------------------------------------------------------
 */
public class GeoUtils {

	public final static float EARTH_RADIUS = 6378137; // 地球半径
	
	public final static float RADIUS_MILE = 1000;


	/**
	 * 获取距离
	 * 
	 * @param from
	 *            开始点
	 * @param to
	 *            结束点
	 * @return
	 */
	public static Double getDistance(Point from, Point to) {
		if (from == null || to == null || from.getLat() == null || from.getLng() == null || to.getLat() == null
				|| to.getLng() == null) {
			return Double.NaN;
		} else {
			double lat1 = from.getLat() * Math.PI / 180.0;
			double lat2 = to.getLat() * Math.PI / 180.0;
			double a = lat1 - lat2;
			double b = (from.getLng() - to.getLng()) * Math.PI / 180.0;

			double sa2, sb2;
			sa2 = Math.sin(a / 2.0);
			sb2 = Math.sin(b / 2.0);
			return 2 * EARTH_RADIUS * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
		}
	}


	
	/**
	 * 点
	 * @author niucaiyun
	 *
	 */
	public static class Point {
		public Double lat; // 纬度
		public Double lng; // 经度

		/**
		 * 
		 */
		public Point() {
			super();
		}

		/**
		 * @param lat
		 * @param lng
		 */
		public Point(Double lat, Double lng) {
			super();
			this.lat = lat;
			this.lng = lng;
		}
		
		public Point(String location) {
			location = PoiPoint.getLocation(location);
			String[] split = location.split(", *");
			this.lat = Double.valueOf(split[1]);
			this.lng = Double.valueOf(split[0]);
		}

		/**
		 * @return the lat
		 */
		public Double getLat() {
			return lat;
		}

		/**
		 * @param lat
		 *            the lat to set
		 */
		public void setLat(Double lat) {
			this.lat = lat;
		}

		/**
		 * @return the lng
		 */
		public Double getLng() {
			return lng;
		}

		/**
		 * @param lng
		 *            the lng to set
		 */
		public void setLng(Double lng) {
			this.lng = lng;
		}

		@Override
		public String toString() {
			return "Point [lat=" + lat + ", lng=" + lng + "]";
		}
	}
}