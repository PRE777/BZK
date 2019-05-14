package com.iwhere.gridgeneration.discretization.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.iwhere.gridgeneration.utils.base.dto.BaseResp;

public interface VectorDiscretizationService {
	
	BaseResp getVectors(Integer pageNo, Integer pageSize) throws Exception;
	
	BaseResp uploadVectorFile(MultipartFile[] multipartfiles) throws Exception;

	BaseResp doProcessBeforeDiscretization(String sourceId, byte geoLevel) throws Exception;
	
	BaseResp getDiscretizationVectors(String geonums, String sourceId) throws Exception;
	
	BaseResp doProcessOfDiscretization(HttpServletResponse res, String geonums, String sourceId) throws Exception;
	
	BaseResp doInsertFeature(String sourceId, Byte geolevel, String mapType, String layerName) throws Exception;

	BaseResp getFeatures(String geoNums, String mapTypeLayerName, Integer pageNum, Integer pageSize) throws Exception;
	
}
