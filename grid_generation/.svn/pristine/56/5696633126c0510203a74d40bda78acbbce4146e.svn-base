package com.iwhere.gridgeneration.discretization.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.iwhere.gridgeneration.discretization.service.VectorDiscretizationService;
import com.iwhere.gridgeneration.discretization.utils.DiscretizationProperties;
import com.iwhere.gridgeneration.utils.base.dto.BaseResp;


@RestController
@RequestMapping(value = "/discretize")
public class VectorDiscretizationController {
	
	@Autowired 
	VectorDiscretizationService vectorDiscretizationService = null;
	
	@Autowired DiscretizationProperties discretizationProperties;
	
	@RequestMapping(value = "/uploadVectorFile")
	public BaseResp uploadVectorFile(@RequestParam("multipartFile") MultipartFile[] multipartfiles) throws Exception {
        BaseResp baseResp = vectorDiscretizationService.uploadVectorFile(multipartfiles);
		return baseResp;
	}
	
	@RequestMapping(value = "/doPreDiscretization")
	public BaseResp doPreProcessBeforeDiscretization(String sourceId, Byte geolevel) throws Exception {
		BaseResp baseResp = vectorDiscretizationService.doProcessBeforeDiscretization(sourceId,Byte.valueOf("18"));
		return baseResp;
	}
	
	@RequestMapping(value = "/getDiscretizationVectors")
	public BaseResp getDiscretizationVectors(String geonums, String sourceId) throws Exception { 
		BaseResp baseResp = vectorDiscretizationService.getDiscretizationVectors(geonums, sourceId);
		return baseResp;
	}
	
	@RequestMapping(value = "/doDiscretization")
	@ResponseBody
	public void doProcessOfDiscretization(HttpServletResponse res, String geonums, String sourceId) throws Exception { 
		BaseResp baseResp = vectorDiscretizationService.doProcessOfDiscretization(res, geonums, sourceId);
		//return baseResp;
	}
	
	@RequestMapping(value = "/getVectors")
	public BaseResp getVectors(Integer pageNo, Integer pageSize) throws Exception { 
		BaseResp baseResp = vectorDiscretizationService.getVectors(pageNo, pageSize);
		return baseResp;
	}
	
	@RequestMapping(value = "/doInsertFeature")
	public BaseResp doInsertFeature(String sourceId, Byte geolevel, String mapType, String layerName) throws Exception {
		BaseResp baseResp = vectorDiscretizationService.doInsertFeature(sourceId, geolevel, mapType, layerName);
		return baseResp;
	}
	
	@RequestMapping(value = "/getFeatures")
	public BaseResp getFeatures(String geoNums, String mapTypeLayerName, Integer pageNum, Integer pageSize) throws Exception {
		BaseResp baseResp = vectorDiscretizationService.getFeatures(geoNums, mapTypeLayerName, pageNum, pageSize);
		return baseResp;
	}
}
