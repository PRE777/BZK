package com.iwhere.gridgeneration.biz.geosot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iwhere.geosot.dto.req.GeoSOTReq;
import com.iwhere.geosot.dto.resp.GeoSOTResp01;
import com.iwhere.geosot.dto.resp.GeoSOTResp02;
import com.iwhere.geosot.dto.resp.GeoSOTResp03;
import com.iwhere.geosot.dto.resp.GeoSOTResp04;
import com.iwhere.geosot.dto.resp.GeoSOTResp05;
import com.iwhere.geosot.dto.resp.GeoSOTResp06;
import com.iwhere.geosot.dto.resp.GeoSOTResp07;
import com.iwhere.geosot.dto.resp.GeoSOTResp08;
import com.iwhere.geosot.dto.resp.GeoSOTResp09;
import com.iwhere.geosot.dto.resp.GeoSOTResp10;
import com.iwhere.geosot.service.GeoSOTService;


/**
 * GeoSTO controller
 * <br>
 * <strong>copyright</strong>： 2015, 北京都在哪网讯科技有限公司<br>
 * <strong>Time </strong>: 2015年12月25日<br>
 * <strong>History</strong>：<br>
 * Editor　　　version　　　Time　　　　　Operation　　　　　　　Description<br>
 *
 * @author dznzyx
 * @version : 1.0.0
 */
@RestController
@RequestMapping(value="/api", method = { RequestMethod.GET, RequestMethod.POST })
public class GeoSOTController{
	
	@Autowired
	private GeoSOTService geoSOTService;

    private static Logger logger = LoggerFactory.getLogger(GeoSOTController.class);


	/**
	 * @Title
	 * @Description
	 * @return
	 * @TestUrl http://localhost:80/api/drawGridOnMap?geoLevel=10&lbLng=113.010101&lbLat=30.010101&rtLng=117.010101&rtLat=45.010101
	 */
	@RequestMapping(value = "/drawGridOnMap")
	public GeoSOTResp01 drawGridOnMap(@Validated(GeoSOTReq.IdrawGridOnMap.class) GeoSOTReq req) {
		return geoSOTService.drawGridOnMap(req);
	}
	
	/**
	 * @Title
	 * @Description
	 * @param req
	 * @return
	 * @TestUrl http://localhost:80/api/tranPointToGeoCode?geoLevel=20&lng=116.325&lat=39.9981
	 */
	@RequestMapping(value = "/tranPointToGeoCode")
	public GeoSOTResp02 tranPointToGeoCode(@Validated(GeoSOTReq.ItranPointToGeoCode.class) GeoSOTReq req) {
		return geoSOTService.tranPointToGeoCode(req);
	}
	
	/**
	 * @Title
	 * @Description
	 * @param req
	 * @return
	 * @TestUrl http://localhost:80/api/tranGeoCodeToPoint?iwCode=N51HKBD12167343
	 */
	@RequestMapping(value = "/tranGeoCodeToPoint")
	public GeoSOTResp03 tranGeoCodeToPoint(@Validated(GeoSOTReq.ItranGeoCodeToPoint.class) GeoSOTReq req) {
		return geoSOTService.tranGeoCodeToPoint(req);
	}
	
	/**
	 * @Title
	 * @Description
	 * @param req
	 * @return
	 * @TestUrl http://localhost:80/api/getGeoNum?geoLevel=20&lng=116.325&lat=39.9981
	 */
	@RequestMapping(value = "/getGeoNum")
	public GeoSOTResp04 getGeoNum(@Validated(GeoSOTReq.IgetGeoNum.class) GeoSOTReq req) {
		return geoSOTService.getGeoNum(req);
	}
	
	/**
	 * @Title
	 * @Description
	 * @param req
	 * @return
	 * @TestUrl http://localhost:80/api/getGeoNum?geoLevel=20&geoNum=88888888888888
	 */
	@RequestMapping(value = "/getMaxGeoNum")
	public GeoSOTResp04 getMaxGeoNum(@Validated(GeoSOTReq.IgetMaxGeoNum.class) GeoSOTReq req) {
		return geoSOTService.getMaxGeoNum(req);
	}
	
	/**
	 * @Title
	 * @Description
	 * @param req
	 * @return
	 * @TestUrl http://localhost:80/api/getParentGeoNum?geoLevel=20&parentLevel=19&geoNum=888888888
	 */
	@RequestMapping(value = "/getParentGeoNum")
	public GeoSOTResp04 getParentGeoNum(@Validated(GeoSOTReq.IgetParentGeoNum.class) GeoSOTReq req) {
		return geoSOTService.getParentGeoNum(req);
	}
    /**
     * @Title
     * @Description
     * @param req
     * @return 
     * @TestUrl http://localhost:80/api/getIWcode?geoLevel=20&lng=116.325&lat=39.9981
     */
    @RequestMapping(value = "/getIWcode")
    public GeoSOTResp05 getIWcode(@Validated(GeoSOTReq.IgetIWcode.class) GeoSOTReq req) {
    	return geoSOTService.getIWcode(req);
    }
    
    /**
     * @Title
     * @Description
     * @param req
     * @return
     * @TestUrl http://localhost:80/api/getCenterPoint?geoLevel=20&lng=116.325&lat=39.9981
     */
    @RequestMapping(value = "/getCenterPoint")
    public GeoSOTResp06 getCenterPoint(@Validated(GeoSOTReq.IgetCenterPoint.class) GeoSOTReq req) {
    	return geoSOTService.getCenterPoint(req);
    }
    
    /**
     * @Title
     * @Description
     * @param req
     * @return
     * @TestUrl http://localhost:80/api/getScopeOfGeoNum?geoLevel=20&geoNum=430700446989516800
     */
    @RequestMapping(value = "/getScopeOfGeoNum")
    public GeoSOTResp07 getScopeOfGeoNum(@Validated(GeoSOTReq.IgetScopeOfGeoNum.class) GeoSOTReq req) {
    	return geoSOTService.getScopeOfGeoNum(req);
    }	
    
    /**
     * @Title
     * @Description
     * @param req
     * @return
     * @TestUrl http://localhost:80/api/getGridsByPolygon?geoLevel=10&lats=40,41,42&lngs=110,112,113
     */
/*    @RequestMapping(value = "/getGridsByPolygon")
    public GeoSOTResp07 getGridsByPolygon(GeoSOTReq req) {
    	return geoSOTService.getGridsByPolygon(req);
    }*/
    /**
     * @Title
     * @Description
     * @param req
     * @return
     * @TestUrl http://localhost:80/api/getGridsByLine?geoLevel=10&lats=40,41&lngs=110,111
     */
  /*  @RequestMapping(value = "/getGridsByLine")
    public GeoSOTResp07 getGridsByLine(GeoSOTReq req) {
    	return geoSOTService.getGridsByLine(req);
    }*/
    
    /**
     * @Title
     * @Description
     * @param req
     * @return
     * @TestUrl http://localhost:80/api/getGridsByAggregation?geoNums=532550655936561152, 532551755448188928, 532552854959816704, 532553954471444480, 532555053983072256, 532556153494700032, 532557253006327808, 532558352517955584, 532559452029583360, 532560551541211136, 758856537211928576, 758858736235184128, 532563850076094464, 532564949587722240, 532566049099350016, 532567148610977792&geoLevel=12
     */
    @RequestMapping(value = "/getGridsByAggregation")
    public GeoSOTResp07 getGridsByAggregation(GeoSOTReq req) {
    	return geoSOTService.getGridsByAggregation(req);
    }
    
    /**
     * @Title 根据经纬度和层级 返回九宫格网格编码 
     * @Description
     * @param req
     * @return
     * @TestUrl http://localhost:80/api/getJiugong?geoLevel=20&lng=116.325&lat=39.9981
     */
    @RequestMapping(value = "/getJiugong")
    public GeoSOTResp07 getJiugong(@Validated(GeoSOTReq.IgetJiugong.class)GeoSOTReq req) {
    	return geoSOTService.getJiugong(req);
    }
    
    /**
     * @Title 根据经纬度和层级 返回九宫格网格坐标范围
     * @Description
     * @param req
     * @return
     * @TestUrl http://localhost:80/api/getJiugongScope?geoLevel=20&lng=116.325&lat=39.9981
     */
    @RequestMapping(value = "/getJiugongScope")
    public GeoSOTResp07 getJiugongScope(@Validated(GeoSOTReq.IgetJiugong.class)GeoSOTReq req) {
    	return geoSOTService.getJiugongScope(req);
    }
	/**
	 * @Title
	 * @Description 根据网格编码返回指定层级的子网格编码
	 * @param req
	 * @return
	 * @TestUrl http://localhost:80/api/getChildrenGeoNums?geoLevel=15&childrenLevel=19&geoNum=526498943937282048
	 */
	@RequestMapping(value = "/getChildrenGeoNums")
	public GeoSOTResp08 getChildrenGeoNums(@Validated(GeoSOTReq.IgetChildrenGeoNums.class) GeoSOTReq req) {
		return geoSOTService.getChildrenGeoNums(req);
	}
	/**
	 * @Title
	 * @Description 根据父网格返回下一层级网格坐标范围
	 * @param req 指定网格的编码和层级
	 * @return
	 * @TestUrl http://localhost:80/api/getSonCoordinates?geoLevel=15&geoNum=526498943937282048
	 */
	@RequestMapping(value = "/getSonCoordinates")
	public GeoSOTResp09 getSonCoordinates (@Validated(GeoSOTReq.IgetSonCoordinates.class) GeoSOTReq req){
		return geoSOTService.getSonCoordinates(req);
	}
	/**
	 * @Title
	 * @Description 根据矩形范围返回指定层级的网格编码
	 * @param req 矩形的左下坐标和右上坐标为参数
	 * @return
	 * @TestUrl http://localhost:80/api/getGeoNumsByRect?lbLat=39&lbLng=116&rtLat=40&rtLng=117&geoLevel=15
	 */
	@RequestMapping(value = "/getGeoNumsByRect")
	public GeoSOTResp08 getGeoNumsByRect(@Validated(GeoSOTReq.IgetGeoNumsByRect.class) GeoSOTReq req){
		return geoSOTService.getGeoNumsByRect(req);
	}
	/**
	 * @Title
	 * @Description 根据用户经纬度返回多个层级编码
	 * @param req 用户经纬度
	 * @return
	 * @TestUrl http://localhost:80/api/getGeoNums?lat=40.24940711905&lng=108.528561891&minLevel=20&maxLevel=14
	 */
	@RequestMapping(value = "/getGeoNums")
	public GeoSOTResp10 getGeoNums(@Validated(GeoSOTReq.IgetGeoNums.class) GeoSOTReq req){
		return geoSOTService.getGeoNums(req);
	}
	/**
	 * @Title
	 * @Description 根据用户经纬度返回周边25个网格，中心网格在第一
	 * @param req
	 * @return
	 * @TestUrl http://localhost:80/api/get25GeoNums?lat=40.24940711905&lng=108.528561891&geoLevel=15
	 */
	@RequestMapping(value = "/get25GeoNums")
	public GeoSOTResp08 get25GeoNums(@Validated(GeoSOTReq.Iget25GeoNums.class) GeoSOTReq req){
		return geoSOTService.get25GeoNums(req);
	}
	/**
	 * @Title
	 * @Description 根据用户经纬度返回制定行列数的网格数组（行列数相同），中心网格在第一
	 * @param req
	 * @return http://localhost:80/api/getGeoNumsByRow?lat=40.24940711905&lng=108.528561891&geoLevel=15&row=5
	 */
	@RequestMapping(value = "/getGeoNumsByRow")
	public GeoSOTResp09 getGeoNumsByRow(@Validated(GeoSOTReq.IgetGeoNumsByRow.class) GeoSOTReq req){
		return geoSOTService.getGeoNumsByRow(req);	
	}


    /**
     * 功能：获取坐标范围内的geonum
     * url：http://localhost:80/api/getGeoNumsOfScope
     * coordinates:[{"Lng":116.62570953369142,"Lat":40.03211975097656},{"Lng":116.5144729614258,"Lat":39.96894836425781},{"Lng":116.62227630615236,"Lat":39.889984130859375},{"Lng":116.6861343383789,"Lat":39.93873596191406},{"Lng":116.62570953369142,"Lat":40.03211975097656}]
     * type
     * geoLevel:14
     **/
//    @RequestMapping(value="/getGeoNumsOfScope")
//    public GeoSOTResp11 getGeoNumsOfScope(String coordinates,String type,byte geoLevel){
//        ArrayList<CoordinateBean> coordinatelist;
//        coordinatelist = aj.getCoordinates(coordinates);
//
//        return geoSOTService.getGeoNumsOfScope(coordinatelist,type,geoLevel);
//    }

}
