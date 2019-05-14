package com.iwhere.gridgeneration.geonum.model;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class RangeStrategy {

	private RangeStrategyProperties rangeProperties;
	
	public RangeStrategy(){
	}
	
	public byte doGetGeolevel(Double factor){
		// 区间列表
		List<Double> rangeList = rangeProperties.getRange();
		// 层级列表
		List<Byte> levelList = rangeProperties.getLevel();
		// 判断是否大于最大最大值，小于最小值
		if (factor >= rangeList.get(rangeList.size()-1)){
			return levelList.get(levelList.size()-1);
		} else if (factor <= rangeList.get(0)){
			return levelList.get(0);
		}		
		// 起始索引下标值，结束索引下标值
		int minIndex = 0, maxIndex = rangeList.size() - 1;
		// 二分查找
		while (minIndex <= maxIndex){
			// 中间索引下标
			int middle = (minIndex + maxIndex)/2;
			// 刚好等于则返回
			if (factor.compareTo(rangeList.get(middle)) == 0){
				return levelList.get(middle - 1);
			}
			// 二分查找区间
			if (factor.compareTo(rangeList.get(middle)) > 0){
				
				if (factor.compareTo(rangeList.get(middle+1)) <= 0){
					// 落在区间中
					return levelList.get(middle);
				} else {
					// 未落在区间中
					minIndex =  middle + 1;
				}
			} else {
				if (factor.compareTo(rangeList.get(middle-1)) >= 0){
					// 落在区间中
					return levelList.get(middle - 1);
				} else {
					// 未落在区间中
					maxIndex = middle - 1;
				}
			}
		}
		return 0;
	}

	public RangeStrategyProperties getRangeProperties() {
		return rangeProperties;
	}

	public void setRangeProperties(RangeStrategyProperties rangeProperties) {
		this.rangeProperties = rangeProperties;
	}

}
