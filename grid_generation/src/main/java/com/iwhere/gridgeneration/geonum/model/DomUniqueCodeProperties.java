package com.iwhere.gridgeneration.geonum.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component("domUniqueCodeProperties")
@ConfigurationProperties(prefix="uniqueCode.scale")
@PropertySource("classpath:/custom.properties") 
public class DomUniqueCodeProperties implements RangeStrategyProperties{
	
	private List<Double> range = new ArrayList<Double>();
	
	private List<Byte> level = new ArrayList<Byte>();

	public void setRange(String rangeString) {
		String[] rangeArray = rangeString.split(",");
		for (String rangeValue : rangeArray){
			range.add(Double.valueOf(rangeValue));
		}
	}

	public void setLevel(String levelString) {
		String[] levelArray = levelString.split(",");
		for (String levelValue : levelArray){
			level.add(Byte.valueOf(levelValue));
		}
	}

	@Override
	public List<Byte> getLevel() {
		return level;
	}
	
	@Override
	public List<Double> getRange() {
		return range;
	}
}
