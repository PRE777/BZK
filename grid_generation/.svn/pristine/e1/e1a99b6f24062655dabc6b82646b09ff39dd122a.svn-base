package com.iwhere.gridgeneration.biz.data.controller;

import com.iwhere.gridgeneration.biz.data.dto.req.FileReq;
import com.iwhere.gridgeneration.utils.ESSearchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iwhere.geosot.dto.resp.GeoSOTResp01;
import com.iwhere.gridgeneration.biz.data.dto.req.CreatEncodedReq;
import com.iwhere.gridgeneration.biz.data.service.IDataService;
import com.iwhere.gridgeneration.utils.base.dto.BaseResp;

import javax.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping(value = "/data")
public class DataController {
    @Autowired
    private IDataService dataService;

    /***
     * 画网格
     * @param scale
     * @param lbLng
     * @param lbLat
     * @param rtLng
     * @param rtLat
     * @return
     */
    //http://192.168.50.11:8080/gridgeneration/data/drawGridOnMap?scale=1000&lbLng=113.010101&lbLat=30.010101&rtLng=117.010101&rtLat=45.010101
    @RequestMapping(value = "/drawGridOnMap")
    public GeoSOTResp01 drawGridOnMap(int scale, Double lbLng, Double lbLat, Double rtLng, Double rtLat) {
        GeoSOTResp01 drawGridOnMap = dataService.drawGridOnMap(scale, lbLng, lbLat, rtLng, rtLat);
        return drawGridOnMap;
    }

    /***
     * 单网格点亮
     * @param scale
     * @return
     */
    @RequestMapping(value = "/getGeoNumsOfPoint")
    public BaseResp getGeoNumsOfPoint(int scale, Double lat, Double lng) {
        JSONObject data = dataService.getSingleGeoNumOfPoint(scale, lat, lng);
        return BaseResp.map().append("data", data);
    }


    /***
     * 线查询点亮
     * @param scale
     * @return
     */
    @RequestMapping(value = "/getGeoNumsOfLine")
    public BaseResp getGeoNumsOfLine(int scale, String lats, String lngs) {
        JSONArray data = dataService.getGeoNumsOfLine(scale, lats, lngs);
        return BaseResp.map().append("data", data);
    }


    /***
     * 点亮行政区划覆盖的网格
     * @param scale
     * @param points
     * @return
     */
    @RequestMapping(value = "/getGeoNumsByAdministrative")
    public BaseResp getGeoNumsByAdministrative(int scale, String points) {
        JSONArray data = dataService.getGeoNumsByAdministrative(scale, points);
        return BaseResp.map().append("data", data);
    }


    /***
     * 多边形网格点亮
     * @param scale
     * @param lats
     * @param lngs
     * @param points
     * @return
     */
    @RequestMapping(value = "/getGeoNumsByPolygon")
    public BaseResp getGeoNumsByPolygon(int scale, String lats, String lngs, String points) {
        JSONArray data = dataService.getGeoNumsByPolygon(scale, lats, lngs, points);
        return BaseResp.map().append("data", data);
    }


    /***
     * 获取服务开启状态
     * @return
     */
    @RequestMapping(value = "/getdateStatus")
    public BaseResp getdateStatus() {
        JSONObject data = dataService.getdateStatus();
        return BaseResp.map().append("data", data);
    }

    /***
     * 服务开启或者暂停
     * @param
     * @return
     */
    @RequestMapping(value = "/autoupdateStatus")
    public BaseResp autoupdateStatus(String status, String datas) {
        BaseResp baseResp = new BaseResp();
        if (datas != null && !datas.isEmpty()) {
            JSONArray objects = JSON.parseArray(datas);
            baseResp = dataService.autoupdateStatus(null, objects);
        } else {
            baseResp = dataService.autoupdateStatus(status, null);
        }

        return baseResp;
    }


    /**
     * 获取资源目录树(不含高分)
     *
     * @return
     */
    @RequestMapping(value = "/getDataSource")
    public BaseResp getDataSource() {
        JSONArray dataSource = dataService.getDataSource();
        return BaseResp.map().append("data", dataSource);
    }


    /***
     * 数据展示
     * @param dataType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getSourceData")
    public BaseResp getSourceData(String dataType, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        JSONObject data = dataService.getSourceData(dataType, pageNum, pageSize);
        return BaseResp.map().append("data", data);
    }

    /***
     * 生成编码
     * @return
     */
    @RequestMapping(value = "/creatEncoded")
    public BaseResp creatEncoded(CreatEncodedReq req) {
        JSONArray datajson = JSON.parseArray(req.getDatas());
        JSONArray result = new JSONArray();
        if (datajson != null && datajson.size() > 0) {
            result = dataService.creatEncoded(datajson);
        } else {
            return BaseResp.error(500, "没有对应的数据要打码");
        }
        return BaseResp.map().append("datas", result);

    }


    /***
     * 表格中的数据入库
     * @param dataType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addDataToEs")
    public BaseResp addDataToEs(String dataType) throws Exception {
        dataService.addDataToEs(dataType);
        return BaseResp.success();
    }


    /***
     * 信息高效检索
     * @param dataType
     * @param startProductDate
     * @param endProductDate
     * @param scale
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/getInformationHighlyActive")
    public BaseResp getInformationHighlyActive(String dataType, String geonums, String startProductDate, String endProductDate, String cellSize, Integer scale, String geoResName, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize) {
        JSONObject data = dataService.getInformationHighlyActive(dataType, geonums, pageNum, pageSize, startProductDate, endProductDate, scale, cellSize, geoResName);
        return BaseResp.map().append("data", data);
    }


    /**
     * 数据分布
     *
     * @return
     */
    @RequestMapping(value = "/getDataDistribute")
    public BaseResp getDataDistribute(String dataTypes, String geonums, Double leftTopLat, Double leftTopLng, Double rightBotmLat, Double rightBotmLng, Integer height, String typecode, String time, String showtype) {
        JSONObject data = dataService.getDataDistribute(dataTypes, geonums, leftTopLat, leftTopLng, rightBotmLat, rightBotmLng, height, typecode, time, showtype);
        return BaseResp.map().append("data", data);
    }






    /***
     * 空间统计
     * @param dataType
     * @param geonums
     * @param startProductDate
     * @param endProductDate
     * @param height
     * @return
     */
    @RequestMapping(value = "/getDataStatisticsInfor")
    public BaseResp getDataStatisticsInfor(String dataType, String geonums, String startProductDate, String endProductDate, Integer height, String typecode) {
        JSONArray data = dataService.getDataStatisticsInfor(startProductDate, endProductDate, dataType, geonums, height, typecode);
        return BaseResp.map().append("data", data);
    }


    /***
     * 时间统计
     * @param dataType
     * @param geonums
     * @param startProductDate
     * @param endProductDate
     * @param height
     * http://localhost:8080/gridgeneration/data/getDataStatisticsInforByTime?height=150000&geonums=523341696298123264-14,523341765017600000-14,523341902456553472-14,523342108614983680-14&dataType=000&startProductDate=2017-01-01&endProductDate=2018-12-25
     * @return
     */
    @RequestMapping(value = "/getDataStatisticsInforByTime")
    public BaseResp getDataStatisticsInforByTime(String dataType, String geonums, String startProductDate, String endProductDate, Integer height, String typecode) {
        JSONArray data = dataService.getDataStatisticsInforByTime(startProductDate, endProductDate, dataType, geonums, height, typecode);
        return BaseResp.map().append("data", data);
    }


    /***
     * 数据分布，分时统计
     * @param dataType
     * @param geonums
     * @param startProductDate
     * @param height
     * @param typecode
     * @param arrays
     * @return
     */
    @RequestMapping(value = "/getDataStatisticsInforByTimeTotle")
    public BaseResp getDataStatisticsInforByTimeTotle(String dataType, String geonums, String startProductDate, Integer height, String typecode, String arrays) {
        JSONArray data = dataService.getDataStatisticsInforByTimeTotle(startProductDate, dataType, geonums, height, typecode, arrays);
        return BaseResp.map().append("data", data);
    }





    /**
     * 数据关联整合资源目录树
     *
     * @return
     */
    @RequestMapping(value = "/getRelevanceDataSource")
    public BaseResp getRelevanceDataSource() {
        JSONArray dataSource = dataService.getRelevanceDataSource();
        return BaseResp.map().append("data", dataSource);
    }


    /***
     * 获取对应的高分数据
     * @param dataType
     * @param geonums
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/getInformationYggf")
    public BaseResp getInformationYggf(String dataType, String geonums, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize) {
        JSONObject data = dataService.getInformationYggf(dataType, geonums, pageNum, pageSize);
        return BaseResp.map().append("data", data);
    }


    /***
     * 数据分布状态
     * @param createStartTime
     * @param createEndTime
     * @param dataTypes
     * @param grids
     * @param height
     * @return
     */
    @RequestMapping(value = "/getDataDistributionStatus")
    public BaseResp getDataDistributionStatus(Long createStartTime, Long createEndTime, String dataTypes, String grids, Integer height) {
        JSONArray data = dataService.getDataDistributionStatus(createStartTime, createEndTime, dataTypes, grids, height);
        return BaseResp.map().append("data", data);
    }


    /***
     * 数据的打包下载
     * @param res
     * @param token
     */
    @RequestMapping(value = "/dataGriddingDistribution")
    @ResponseBody
    public void dataGriddingDistribution(HttpServletResponse res, String token) {

        BaseResp baseResp = dataService.dataGriddingDistribution(res, token);
    }


    /***
     * 获取展示参数名称
     * @param
     * @return
     */
	/*@RequestMapping(value = "/getSourceDataMeta")
	public BaseResp getSourceDataMeta(String dataType){
		JSONArray data = dataService.getSourceDataMeta(dataType);
		return BaseResp.map().append("data", data);
		
	}*/
    /*@RequestMapping(value = "/getscaleZoom")
    public int scaleZoom(int scale) {
        Integer scaleZoom = dataService.scaleZoom(scale);
        return scaleZoom;
    }*/


    /***
     * 录入shp文件数据
     * @param req
     */
    @RequestMapping(value = "/insertDataByShp")
    public void insertDataByShp(FileReq req) {
        if (req.getShpFiles() == null || req.getShpFiles().length == 0) {
            throw new RuntimeException("文件为空");
        } else {
            try {
                dataService.insertDataByShp(req.getShpFiles());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/getSpecial")
    public BaseResp getSpecial() {

        JSONArray datas = dataService.getSpecial();
        return BaseResp.map().append("datas", datas);
    }


    /**
     * 数据关联整合
     *
     * @return
     */
    @RequestMapping(value = "/getVectorSourceCatalogue")
    public BaseResp getVectorSourceCatalogue() {
        JSONArray dataSource = dataService.getVectorSourceCatalogue();
        return BaseResp.map().append("data", dataSource);
    }

}