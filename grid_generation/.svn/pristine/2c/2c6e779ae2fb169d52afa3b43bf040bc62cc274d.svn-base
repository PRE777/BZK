package com.iwhere.gridgeneration.geonum.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("domUniqueCodeGeolevelStrategy")
public class DomUniqueCodeGeolevelStrategy implements GeoLevelStrategy{
    
	@Autowired
	@Qualifier("domUniqueCodeProperties")
	private RangeStrategyProperties rangeStrategyProperties;
	
	@Autowired
	private RangeStrategy rangeStrategy;

	@Override
	public byte getGeoLevel(Object factor) {
		// DOM使用区间策略
		rangeStrategy.setRangeProperties(rangeStrategyProperties);
		Byte geoLevel = rangeStrategy.doGetGeolevel((Double)factor);
		return geoLevel;
	}
}
