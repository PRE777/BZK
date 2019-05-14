package com.iwhere.gridgeneration.geonum.model;

import java.util.ArrayList;
import java.util.List;

public interface RangeStrategyProperties {
	
	List<Double> range = new ArrayList<Double>();
	
	List<Byte> level = new ArrayList<Byte>();

	public List<Double> getRange();
	
	public List<Byte> getLevel();
}
