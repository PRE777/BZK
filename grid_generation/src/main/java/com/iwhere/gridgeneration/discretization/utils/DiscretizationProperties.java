package com.iwhere.gridgeneration.discretization.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component  
@ConfigurationProperties(prefix="vector.discretization")
@Data
public class DiscretizationProperties {
	// 上传缓存目录
	private String tmp;
	// 上传存储目录
	private String uploadPath;
	// 离散存储目录
	private String generatePath;
	// 要素存储索引名称
	private String featureIndexName;
	// 要素存储类型名称
	private String featureTypeName;
	// 矢量存储索引名称
	private String vectorIndexName;
	// 矢量存储类型名称
	private String vectorTypeName;
	// 穿透查询要素存储索引名称
	private String featuresIndexName;
	// 穿透查询要素存储类型名称
	private String featuresTypeName;
}
