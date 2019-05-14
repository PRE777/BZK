package com.iwhere.gridgeneration.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class GeoNumUtil {

	public static List<String> getAncestorGrids(String geoNumStr, Integer minLevel){
		List<String> ancestorGrids = new ArrayList<String>();
		String stringToTokenize = geoNumStr;
		int last = stringToTokenize.lastIndexOf("-");
		String geoNum = stringToTokenize.substring(0, last);
		String geoLevel = stringToTokenize.substring(last+1);
		Long num = Long.valueOf(geoNum);
		int level = Integer.valueOf(geoLevel);
		
		if (num.longValue() == 0){
			for (int index = level; index >= minLevel; index--){
				ancestorGrids.add("0-"+index);
				//System.out.println("0-"+index);
			}
		} else {
			String binaryStr = Long.toBinaryString(num);
			StringBuffer fianlBinaryStr = new StringBuffer();
			if (binaryStr.length() <= 64){
				StringBuffer zeros = new StringBuffer();
				for (int i = 0; i < 64-binaryStr.length(); i++){
					zeros.append("0");
				}
				fianlBinaryStr = zeros.append(binaryStr);
			}
			for (int index = level, position = 0; index >= minLevel; index--,position++){
				String tempStr = fianlBinaryStr.substring(0, 2*(level-position));
				StringBuffer zeros = new StringBuffer();
				for (int i = 0; i < 64-tempStr.length(); i++){
					zeros.append("0");
				}
				fianlBinaryStr = new StringBuffer(tempStr).append(zeros);
				Long tempGeoNum = parseLong(fianlBinaryStr.toString(),2);
				ancestorGrids.add(String.valueOf(tempGeoNum)+"-"+(level-position));
				//System.out.println(String.valueOf(tempGeoNum)+"-"+(level-position));
			}
		}
		return ancestorGrids;
	}
	
	public static void main(String[] args){
		getAncestorGrids("0-25",23);
	}
	
	private static long parseLong(String s, int base) {
	    return new BigInteger(s, base).longValue();
	}
}
