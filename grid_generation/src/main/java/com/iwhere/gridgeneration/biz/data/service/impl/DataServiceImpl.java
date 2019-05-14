package com.iwhere.gridgeneration.biz.data.service.impl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import com.iwhere.geosot.service.GeoSOT;
import com.iwhere.gridgeneration.discretization.utils.FeaturesFromShp;

import com.iwhere.gridgeneration.entity.Point;
import com.iwhere.gridgeneration.entity.Polygon;
import com.iwhere.gridgeneration.utils.DateUtils;
import com.iwhere.gridgeneration.utils.base.dto.BaseResp;

import org.locationtech.jts.geom.Coordinate;

import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iwhere.geosot.dto.req.GeoSOTReq;
import com.iwhere.geosot.dto.resp.GeoSOTResp01;
import com.iwhere.geosot.dto.resp.GeoSOTResp04;
import com.iwhere.geosot.dto.resp.GeoSOTResp07;
import com.iwhere.geosot.service.GeoSOTService;
import com.iwhere.gridgeneration.biz.data.service.IDataService;
import com.iwhere.gridgeneration.utils.BaseInterfaceUtil;
import com.iwhere.gridgeneration.utils.ESSearchUtils;
import com.iwhere.gridgeneration.utils.GeoNumUtil;
import org.springframework.web.multipart.MultipartFile;

import org.locationtech.jts.geom.Geometry;

import javax.servlet.http.HttpServletResponse;

@Service
public class DataServiceImpl implements IDataService {

    @Autowired
    private ESSearchUtils esutils;

    @Autowired
    private GeoSOTService geoSOTService;

    @Autowired
    private BaseInterfaceUtil baseInterfaceUtil;

    @Autowired
    private GeoSOT geoSOT;

    @Value("${constant.shp.path}")
    private String shpPath;

    @Value("${constant.shp.maxlevel}")
    private String maxlevel;

    @Value("${constant.shp.minlevel}")
    private String minlevel;

    @Value("${constant.shp.indexname}")
    private String indexname;


    @Autowired
    public SimpMessagingTemplate template;

    /**
     * 入库
     *
     * @throws FileNotFoundException
     */
    @Override
    public void addDataToEs(String dataType) throws Exception {
        JSONArray datas = new JSONArray();
        // 获取对应的数据
        JSONObject getgeodata = baseInterfaceUtil.getgeodata(dataType);
        if (getgeodata.getString("retmsg").equals("ok")) {
            JSONObject jsonObject = getgeodata.getJSONObject("retdata");
            if (jsonObject.isEmpty()) {
                // 表示查询成功但是没有对应的数据
            } else {
                // 表示查询成功并且有对应的数据
                JSONObject jsonObject2 = jsonObject.getJSONObject("001");

                JSONArray jsonArray = jsonObject2.getJSONArray(dataType);
                for (int i = 0; i < jsonArray.size(); i++) {
                    datas.add(jsonArray.getJSONObject(i));
                }
            }
        }

        // esutils.bulkDeleteData(dataType);
        esutils.bulkAddData(datas);
    }

    /**
     * 生成编码
     */
    @Override
    public JSONArray creatEncoded(JSONArray datajson) {
        for (int i = 0; i < datajson.size(); i++) {
            JSONObject row = datajson.getJSONObject(i);
            // 获取对应的多边形的经纬度
            String lats = row.getString("lats").replaceAll("~", ",");
            String lngs = row.getString("lngs").replaceAll("~", ",");

            // 获取编码
            GeoSOTReq geosotReq = new GeoSOTReq();
            geosotReq.setLats(lats);
            geosotReq.setLngs(lngs);
            geosotReq.setGeoLevel((byte) 16);

            // 内编码数据
            GeoSOTResp07 gridsByPolygon = geoSOTService.getGridsByPolygon(geosotReq);
            StringBuffer sb = new StringBuffer();
            sb.append(gridsByPolygon.getGeoNums()[0]).append(",").append(gridsByPolygon.getGeoNums()[1]).append("...").append(gridsByPolygon.getGeoNums()[2]).append(",").append(gridsByPolygon.getGeoNums()[3]);
            row.put("internalNum", sb.toString());
            row.put("externalNum", (long) (1 + Math.random() * 1000000000));
        }

        return datajson;

    }

    /***
     * 获取元数据类型
     */
    @Override
    public JSONArray getDataSource() {
        JSONArray datas = new JSONArray();
        JSONObject data = baseInterfaceUtil.getAggregations();
        datas.add(data);

        return datas;
    }

    /**
     * 获取数据元素，根据数据类型
     */
    @Override
    public JSONArray getSourceDataMeta(String dataType) {
        JSONArray array = baseInterfaceUtil.getshowdata(dataType);
        return array;

    }

    /***
     * 获取资源数据包括表头
     *
     * @throws Exception
     */
    @Override
    public JSONObject getSourceData(String dataType, Integer pageNum, Integer pageSize) throws Exception {
        // 根据类型动态获取该些数据
        JSONArray array1 = new JSONArray();

        JSONArray selectByExample = baseInterfaceUtil.getshowdata(dataType);

        JSONObject object1 = new JSONObject();
        object1.put("type", "checkbox");
        object1.put("fixed", "left");
        object1.put("title", "全选");
        object1.put("width", "4.5%");
        array1.add(object1);

        JSONObject object2 = new JSONObject();
        object2.put("type", "numbers");
        object2.put("align", "center");
        object2.put("title", "序号");
        object2.put("width", "4.5%");
        array1.add(object2);

        for (int i = 0; i < selectByExample.size(); i++) {
            JSONObject obj = selectByExample.getJSONObject(i);
            JSONObject object = new JSONObject();
            if (obj.getString("propertyName").equals("01")) {
                object.put("field", obj.getString("fieldName")); // es的属性名称
                object.put("title", obj.getString("showName")); // 前端显示的展示数据名称
                object.put("edit", "text");
                object.put("align", "center");
                array1.add(object);
            }

        }


        JSONObject data = new JSONObject();

        // 表头数据
        data.put("gaugeOutfit", array1); // 表头数据
        // 获取数据，包括分页
        JSONArray datas = new JSONArray(); // 查询出的数据装进这里
        // 获取对应的数据
        JSONObject getgeodata = baseInterfaceUtil.getgeodata(dataType);
        if (getgeodata.getString("retmsg").equals("ok")) {
            JSONObject jsonObject = getgeodata.getJSONObject("retdata");
            if (jsonObject.isEmpty()) {
                // 表示查询成功但是没有对应的数据
            } else {
                // 表示查询成功并且有对应的数据
                if (jsonObject.containsKey("001")) {
                    // 表示有001数据类型数据
                    JSONObject jsonObject2 = jsonObject.getJSONObject("001");
                    JSONArray jsonArray = jsonObject2.getJSONArray(dataType);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        datas.add(jsonArray.getJSONObject(i));
                    }
                }

                if (jsonObject.containsKey("002")) {
                    // 表示有002数据类型的数据
                    JSONObject jsonObject2 = jsonObject.getJSONObject("002");
                    JSONArray jsonArray = jsonObject2.getJSONArray(dataType);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        datas.add(jsonArray.getJSONObject(i));
                    }
                }

                if (jsonObject.containsKey("003")) {
                    // 表示有002数据类型的数据
                    JSONObject jsonObject2 = jsonObject.getJSONObject("003");
                    JSONArray jsonArray = jsonObject2.getJSONArray(dataType);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        datas.add(jsonArray.getJSONObject(i));
                    }
                }
            }
        }

        // 总数
        data.put("count", datas.size());
        // 总页数
        data.put("pageCount", datas.size() % pageSize == 0 ? datas.size() / pageSize : datas.size() / pageSize + 1);

        // 获取显示当前页数据
        JSONArray array = new JSONArray();
        int to = pageNum * pageSize;
        if (datas.size() >= pageNum - 1 * pageSize && datas.size() < pageNum * pageSize) {
            to = datas.size();
        }
        for (int i = 0; i < to; i++) {
            JSONObject jsondata = datas.getJSONObject(i);
            // 获取对应内部数据
            //object.put("geoResID", jsondata.getString("geoResID"));
            jsondata.put("lats", jsondata.getDouble("lonlatExtentBottom") + "~" + jsondata.getDouble("lonlatExtentTop"));
            jsondata.put("lngs", jsondata.getDouble("lonlatExtentLeft") + "~" + jsondata.getDouble("lonlatExtentRight"));
            //object.put("producerName", jsondata.get("producerName"));
            //object.put("geoResName", jsondata.get("geoResName"));

            if (jsondata.getJSONObject("extraProperties").get("scale") != null) {
                jsondata.put("scale1", jsondata.getJSONObject("extraProperties").getInteger("scale") % 10000 == 0 ? "1:" + jsondata.getJSONObject("extraProperties").getInteger("scale") / 10000 + "万" : jsondata.getJSONObject("extraProperties").getInteger("scale"));
            } else {
                jsondata.put("scale", "");
            }

            array.add(jsondata);

        }
        data.put("datas", array);
        return data;
    }

    /**
     * 获取层级
     */
   /* @Override
    public Integer scaleZoom(int scale) {
        int scaleZoom = baseInterfaceUtil.scaleZoom(scale);
        return scaleZoom;
    }*/

    /***
     * 画网格
     */
    @Override
    public GeoSOTResp01 drawGridOnMap(int scale, Double lbLng, Double lbLat, Double rtLng, Double rtLat) {
        // 比例尺换算层级
        int scaleZoom = baseInterfaceUtil.heightZoom(scale);

        GeoSOTReq geosotreq = new GeoSOTReq();
        geosotreq.setGeoLevel((byte) scaleZoom);
        geosotreq.setLbLng(lbLng);
        geosotreq.setLbLat(lbLat);
        geosotreq.setRtLat(rtLat);
        geosotreq.setRtLng(rtLng);

        GeoSOTResp01 drawGridOnMap = geoSOTService.drawGridOnMap(geosotreq);
        return drawGridOnMap;
    }

    /**
     * 高效信息检索
     */
    @Override
    public JSONObject getInformationHighlyActive(String dataType, String geonums, Integer pageNum, Integer pageSize, String startProductDate, String endProductDate, Integer scale, String cellSize, String geoResName) {
        Integer geolevel = null;
        // 转换网格
        List<String> asList = Arrays.asList(geonums.split(","));
        Set<String> ancestorGrid = new HashSet<String>();
        for (String string : asList) {
            List<String> ancestorGrids = GeoNumUtil.getAncestorGrids(string, 3);
            for (String string2 : ancestorGrids) {
                ancestorGrid.add(string2);
            }
        }


        //遍历网格获取3-22的父网格

        System.out.println("多网格画区域父网格的个数============" + ancestorGrid.size());
        // 获取表内数据
        JSONObject data = esutils.getInformationHighlyActive(dataType, asList, startProductDate, endProductDate, geolevel, scale, pageNum, pageSize, cellSize, geoResName, ancestorGrid);
        JSONArray arrays = new JSONArray();
        // 获取表头数据
        JSONArray selectByExample = baseInterfaceUtil.getshowdata(dataType);

        JSONArray resutls = data.getJSONArray("data");
        for(int i=0; i<resutls.size(); i++)
        data.put("{\n" + "            Map<String, Object> map = (HashMap)resutls.get(i);\n" + "            JSONObject object = new JSONObject();\n" + "            for (int y = 0; y < selectByExample.size(); y++) {\n" + "                JSONObject obj = selectByExample.getJSONObject(y);\n" + "                if (obj.getString(\"propertyName\").equals(\"01\")) {\n" + "                    object.put(obj.getString(\"fieldName\"),map.get(obj.getString(\"fieldName\")));\n" + "                }\n" + "            }\n" + "            arrays.add(object);\n" + "\n" + "        }datas",arrays);

        JSONArray array1 = new JSONArray();

        for (int i = 0; i < selectByExample.size(); i++) {
            JSONObject obj = selectByExample.getJSONObject(i);
            JSONObject object = new JSONObject();
            if (obj.getString("propertyName").equals("01")) {
                object.put("field", obj.getString("fieldName")); // es的属性名称
                object.put("title", obj.getString("showName")); // 前端显示的展示数据名称
                object.put("edit", "text");
                object.put("align", "center");
                array1.add(object);
            }

        }

        data.put("gaugeOutfit", array1); // 表头数据

        return data;
    }

    /**
     * 点亮单网格/多网格
     */
    @Override
    public JSONObject getSingleGeoNumOfPoint(int scale, Double lat, Double lng) {
        // 对应成对的数据
        JSONObject data = new JSONObject();
        // 根据高度获取对应的层级
        int geolevel = baseInterfaceUtil.heightZoom(scale);

        // 根据经纬度获取网格
        GeoSOTReq geosotReq = new GeoSOTReq();
        geosotReq.setLat(lat);
        geosotReq.setLng(lng);
        geosotReq.setGeoLevel((byte) geolevel);
        GeoSOTResp04 geoNum = geoSOTService.getGeoNum(geosotReq);
        data.put("geonum", geoNum.getGeoNum() + "-" + geolevel);
        // 根据网格获取对应的网格的坐标点
        geosotReq.setGeoNum(geoNum.getGeoNum());
        GeoSOTResp07 scopeOfGeoNum = geoSOTService.getScopeOfGeoNum(geosotReq);
        data.put("minlat", scopeOfGeoNum.getMinLat());
        data.put("maxlat", scopeOfGeoNum.getMaxLat());
        data.put("minlng", scopeOfGeoNum.getMinLng());
        data.put("maxlng", scopeOfGeoNum.getMaxLng());

        return data;

    }

    /**
     * 点亮多边形
     */
    @Override
    public JSONArray getGeoNumsByPolygon(int scale, String lats, String lngs, String points) {
        // 存放结果
        JSONArray data = new JSONArray();
        // 根据高度获取对应层级
        int geolevel = baseInterfaceUtil.heightZoom(scale);
        // 处理点
        StringBuffer Lats = new StringBuffer();
        StringBuffer Lngs = new StringBuffer();
        if (points != null) {
            JSONArray parseArray = JSON.parseArray(points);
            // 遍历获取点
            for (int i = 0; i < parseArray.size(); i++) {
                JSONObject jsonObject = parseArray.getJSONObject(i);
                Lats.append(jsonObject.get("lat")).append(",");
                Lngs.append(jsonObject.get("lng")).append(",");
            }
        }
        lats = Lats.toString().substring(0, Lats.toString().length() - 1);
        lngs = Lngs.toString().substring(0, Lngs.toString().length() - 1);
        // 获取多边形覆盖的网格
        GeoSOTReq geosotReq = new GeoSOTReq();
        geosotReq.setLats(lats);
        geosotReq.setLngs(lngs);
        geosotReq.setGeoLevel((byte) geolevel);
        GeoSOTResp07 gridsByPolygon = geoSOTService.getGridsByPolygon(geosotReq);
        long[] geoNums = gridsByPolygon.getGeoNums();

        data = getGeonumsPoints(geoNums, geolevel);
        return data;
    }

    /**
     * 线查询网格点亮
     */
    @Override
    public JSONArray getGeoNumsOfLine(int scale, String lats, String lngs) {
        // 存放结果
        JSONArray data = new JSONArray();
        // 根据高度获取对应层级
        int geolevel = baseInterfaceUtil.heightZoom(scale);
        // 获取多边形覆盖的网格
        GeoSOTReq geosotReq = new GeoSOTReq();
        geosotReq.setLats(lats);
        geosotReq.setLngs(lngs);
        geosotReq.setGeoLevel((byte) geolevel);
        GeoSOTResp07 gridsByLine = geoSOTService.getGridsByLine(geosotReq);
        long[] geoNums = gridsByLine.getGeoNums();
        data = getGeonumsPoints(geoNums, geolevel);
        return data;
    }

    /***
     * 数据分布统计
     */
    @Override
    public JSONArray getDataStatisticsInfor(String startProductDate, String endProductDate, String dataType, String geonums, Integer height, String typecode) {
        // 高度换算网格层级
        int geolevel = baseInterfaceUtil.heightZoom(height);
        JSONArray data = esutils.getDataStatisticsInfor(geonums, dataType, endProductDate, startProductDate, geolevel, typecode);
        return data;
    }

    /***
     * 分布状态
     */
    @Override
    public JSONArray getDataDistributionStatus(Long createStartTime, Long createEndTime, String dataTypes, String grids, Integer height) {
        // 高度换算网格层级
        int geolevel = baseInterfaceUtil.heightZoom(height);
        JSONArray data = esutils.getDataDistributionStatus(grids, dataTypes, createEndTime, createStartTime, geolevel);
        return data;
    }


    /**
     * 获取资源目录
     */
    @Override
    public JSONArray getRelevanceDataSource() {
        JSONArray datas = new JSONArray();
        JSONObject aggregations = baseInterfaceUtil.getAggregations();
        datas.add(aggregations);
        JSONObject data = baseInterfaceUtil.getsourceData();
        datas.add(data);
        return datas;
    }
    
    /**
     * 获取矢量资源目录
     */
    @Override
    public JSONArray getVectorSourceCatalogue() {
        JSONArray datas = baseInterfaceUtil.getsourceData2();
        return datas;
    }

    /**
     * 网格关联高分数据查询
     */
    @Override
    public JSONObject getInformationYggf(String dataType, String geonums, Integer pageNum, Integer pageSize) {
        JSONObject data = new JSONObject();
        // 获取表内数据
        //获取对应数据
        // 转换网格
        //获取网格码的祖先网格
        List<String> asList = Arrays.asList(geonums.split(","));
        Set<String> ancestorGrid = new HashSet<String>();
        for (String string : asList) {
            List<String> ancestorGrids = GeoNumUtil.getAncestorGrids(string, 3);
            for (String string2 : ancestorGrids) {
                ancestorGrid.add(string2);
            }
        }

        System.out.println("多网格画区域父网格的个数============" + ancestorGrid.size());
        data = esutils.getInformationYggf(dataType, asList, pageNum, pageSize, ancestorGrid);
        // 获取高分表头数据

        JSONArray selectByExample = baseInterfaceUtil.getshowdata(dataType);
        JSONArray array1 = new JSONArray();
        for (int i = 0; i < selectByExample.size(); i++) {
            JSONObject obj = selectByExample.getJSONObject(i);
            if (obj.getString("propertyName").equals("02")) {
                JSONObject object = new JSONObject();
                object.put("field", obj.getString("fieldName")); // es的属性名称
                object.put("title", obj.getString("showName")); // 前端显示的展示数据名称
                object.put("edit", "text");
                object.put("align", "center");
                array1.add(object);
            }

        }

        JSONObject obj = new JSONObject();
        obj.put("field", ""); // es的属性名称
        obj.put("title", "操作"); // 前端显示的展示数据名称
        obj.put("toolbar", "#barDemo");
        obj.put("align", "center");
        array1.add(obj);

        data.put("gaugeOutfit", array1); // 表头数据

        return data;
    }


    /***
     * 获取网格内对应数据分布
     */
    @Override
    public JSONObject getDataDistribute(String dataTypes, String geonums, Double leftTopLat, Double leftTopLng, Double rightBotmLat, Double rightBotmLng, Integer height, String typecode, String dayTime, String showtype) {

        int geolevel = 0;
        if (height != null) {
            //根据高度换算出层级
            geolevel = baseInterfaceUtil.heightZoom(height);
        }
        JSONObject obj = new JSONObject();

        if (typecode !=null && typecode.equals("000")) {
            //查询es,拿到全球的高度以及中心点位置
            obj = esutils.getTypeDataBzk(typecode);
        }

        JSONArray datas = esutils.getDataDistribute(dataTypes, typecode, geonums, geolevel, leftTopLat, leftTopLng, rightBotmLat, rightBotmLng, dayTime, showtype);
        obj.put("datas", datas);
        return obj;
    }

    /***
     * 状态监控
     */
    @Override
    public JSONArray keepwatch() {
        //获取全球状态下，网格数据在当前时间一天内录入的数据
        JSONArray data = esutils.keepwatch();
        return data;
    }


    /***
     * 时间统计
     */
    @Override
    public JSONArray getDataStatisticsInforByTime(String startProductDate, String endProductDate, String dataType, String geonums, Integer height, String typecode) {
        // 高度换算网格层级
        int geolevel = baseInterfaceUtil.heightZoom(height);
        JSONArray data = esutils.getDataStatisticsInforByTime(geonums, dataType, endProductDate, startProductDate, geolevel, typecode);
        return data;
    }


    /***
     * 更改服务器
     */
    @Override
    public BaseResp autoupdateStatus(String status, JSONArray json) {
        //服务更新开启
        BaseResp baseResp = esutils.updateStatus(status, json);
        return baseResp;
    }

    /***
     * 获取服务状态
     * @return
     */
    @Override
    public JSONObject getdateStatus() {
        JSONObject object = esutils.getdateStatus();
        return object;
    }

    /***
     * 点亮行政区划覆盖网格
     * @param scale
     * @param points
     * @return
     */
    @Override
    public JSONArray getGeoNumsByAdministrative(int scale, String points) {
        // 根据高度获取对应层级
        int geolevel = baseInterfaceUtil.heightZoom(scale);
        JSONArray array = JSON.parseArray(points);
        double[] Lats = new double[array.size()];
        double[] Lngs = new double[array.size()];
        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            Lats[i] = Double.parseDouble(jsonObject.getString("lat"));
            Lngs[i] = Double.parseDouble(jsonObject.getString("lng"));
        }

        //打码
        long[] gridsByPolygon = geoSOT.getGridsByPolygon(Lats, Lngs, array.size(), (byte) geolevel);

        JSONArray data = getGeonumsPoints(gridsByPolygon, geolevel);
        return data;
    }


    /***
     * 录入shp文件数据
     * @param
     */
    @Override
    public void insertDataByShp(MultipartFile[] shpFiles) throws Exception {
        File pathFile = new File(shpPath, System.currentTimeMillis() + File.separator);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String filePath = pathFile.getAbsolutePath();
        List<MultipartFile> files = Arrays.asList(shpFiles);
        for (MultipartFile file : files) {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            System.out.println("上传的文件名为=====" + fileName);
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            System.out.println("上传的后缀名为=====" + suffixName);
            // 文件上传路径
            File dest = new File(filePath + File.separator + fileName);
            try {
                file.transferTo(dest);
                System.out.println("文件上传成功");
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        setESGeoNum(filePath);
        delAllFile(filePath);
    }

    /***
     * 获取特殊区域
     * @return
     */
    @Override
    public JSONArray getSpecial() {
        JSONArray getspecial = esutils.getspecial();
        return getspecial;
    }

    /**
     * 数据的打包下载
     * @param res
     * @param token
     * @return
     */
    @Override
    public BaseResp dataGriddingDistribution(HttpServletResponse res, String token) {
        //根据token获取数据
        JSONObject object = baseInterfaceUtil.getgeodataBytoken(token);
        JSONArray retdata = object.getJSONArray("retdata");
        //服务器上的路径
        String url = null;
        if(retdata !=null){
            //表示有数据
            url = retdata.getJSONObject(0).getString("link");

        }else{
            return null;
        }
        //后缀名
        String fileName = url.split("=")[1];
        //File vectorFold = new File(url);

        InputStream inputStream = null;
        OutputStream out = null;
        try {
           //根据文件在服务器的路径读取该文件转化为流
            URL url1 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)url1.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 100000);
            inputStream = conn.getInputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];

            res.setHeader("content-type", "application/octet-stream");
            res.setContentType("application/octet-stream");
            res.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            out = res.getOutputStream();
            int b = 0;
            while (b != -1){
                b = inputStream.read(buffer);
                //写到输出流(out)中
                out.write(buffer,0,b);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                out.close();
                out.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        System.out.println("success");
        return null;
    }


    /***
     * 数据分类（统计）
     * @param startProductDate
     * @param dataType
     * @param geonums
     * @param height
     * @param typecode
     * @param arrays
     * @return
     */
    @Override
    public JSONArray getDataStatisticsInforByTimeTotle(String startProductDate, String dataType, String geonums, Integer height, String typecode, String arrays) {
        int geolevel = baseInterfaceUtil.heightZoom(height);
        JSONArray data = esutils.getDataStatisticsInforByTimeTotle(startProductDate, dataType, geonums, geolevel, typecode, arrays);
        return data;
    }


    public JSONArray getGeonumsPoints(long[] geoNums, int geolevel) {
        JSONArray array = new JSONArray();
        for (long l : geoNums) {
            JSONObject obj = new JSONObject();
            obj.put("geonum", l + "-" + geolevel);
            // 根据网格码获取网格的坐标
            GeoSOTReq geosotReq1 = new GeoSOTReq();
            geosotReq1.setGeoNum(l);
            geosotReq1.setGeoLevel((byte) geolevel);
            GeoSOTResp07 scopeOfGeoNum = geoSOTService.getScopeOfGeoNum(geosotReq1);
            obj.put("minlat", scopeOfGeoNum.getMinLat());
            obj.put("maxlat", scopeOfGeoNum.getMaxLat());
            obj.put("minlng", scopeOfGeoNum.getMinLng());
            obj.put("maxlng", scopeOfGeoNum.getMaxLng());
            array.add(obj);
        }
        return array;
    }


    public void setESGeoNum(String shpPath) throws Exception {
        File FilePath = new File(shpPath);
        String path = FilePath.getAbsolutePath();
        File dir = new File(path);
        if (!dir.isDirectory()) {
            System.out.println("输入的数据不是目录");
        }
        FileFilter shpFilter = new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".shp")) {
                    return true;
                }
                return false;
            }
        };
        File[] files = dir.listFiles(shpFilter);
        for (File file : files) {
            System.out.println(file.getAbsolutePath());
            String layerName = file.getName().split("\\.")[0];
            readVectorFile(file.getAbsolutePath(), layerName);

        }
        delAllFile(path);
    }


    public BaseResp readVectorFile(String vectorFile, String layerName) throws Exception {
        long startTime = System.currentTimeMillis();
        // register all the format drivers that are desired

        FeaturesFromShp featuresFromShp = new FeaturesFromShp();

        SimpleFeature[] featureArray = featuresFromShp.getFeaturesSource(vectorFile);

        //遍历
        for (int i = 0; i < featureArray.length; i++){
            JSONObject country = new JSONObject();
            // 获取geometry信息
            Geometry geometry = (Geometry)featureArray[i].getDefaultGeometryProperty().getValue();
            //获取属性

            //封装属性
            Collection<Property> propertyCollection = featureArray[i].getProperties();
            // 要素属性值入库
            for (Iterator<Property> propertyIt = propertyCollection.iterator(); propertyIt.hasNext();){
                Property property = propertyIt.next();
                if (!"the_geom".equals(property.getName().toString())){
                    country.put(property.getName().toString(), property.getValue());
                }
            }
           country.put("createTime", DateUtils.getDateTime());

            //判断多边形的形状
            String geometryType = geometry.getGeometryType();
            Set<String> aggregationGrid = new HashSet<>();
            if(geometryType.equals("MultiPolygon")) {
                //表示多面
                int numGeometries = geometry.getNumGeometries();//一共多少个面
                for(int y=0; y<numGeometries; y++){
                    Geometry geometryN = geometry.getGeometryN(i);
                    Geometry mbr = geometryN.getEnvelope();
                    // fetch minimum bounding rectangle
                    Coordinate[] mbrPoints = mbr.getCoordinates();

                    StringBuffer sblats = new StringBuffer();
                    StringBuffer sblngs = new StringBuffer();

                    for (int z = 0; z < mbrPoints.length; z++) {
                        sblats.append(mbrPoints[z].y).append(",");
                        sblngs.append(mbrPoints[z].x).append(",");
                    }

                    sblats.append(mbrPoints[0].y);
                    sblngs.append(mbrPoints[0].x);

                    GeoSOTReq geoSOTReq = new GeoSOTReq();
                    geoSOTReq.setLats(sblats.toString());
                    geoSOTReq.setLngs(sblngs.toString());
                    for(int z=3; z<22; z++){
                        geoSOTReq.setGeoLevel((byte) z);
                        long[] geoNums = geoSOTService.getGridsByPolygon(geoSOTReq).getGeoNums();
                        List<String> geonums = new ArrayList<>();
                        for(Long geonum: geoNums){
                            geonums.add(geonum+"-"+z);
                        }

                        country.put("geonum-"+z, geonums);

                        if(z==3){
                            //聚合后的网格码数
                            aggregationGrid = aggregationGrid(geometryN, geoNums, 3, 22);
                        }

                    }


                    country.put("geoNums", aggregationGrid);

                    ESSearchUtils.insert(country, "special_data_bzk");

                }
            }else if (geometryType.equals("Polygon")){
                Geometry geometryN = geometry.getGeometryN(0);
                Geometry mbr = geometryN.getEnvelope();
                // fetch minimum bounding rectangle
                Coordinate[] mbrPoints = mbr.getCoordinates();

                StringBuffer sblats = new StringBuffer();
                StringBuffer sblngs = new StringBuffer();

                for (int z = 0; z < mbrPoints.length; z++) {
                    sblats.append(mbrPoints[z].y).append(",");
                    sblngs.append(mbrPoints[z].x).append(",");
                }

                sblats.append(mbrPoints[0].y);
                sblngs.append(mbrPoints[0].x);

                GeoSOTReq geoSOTReq = new GeoSOTReq();
                geoSOTReq.setLats(sblats.toString());
                geoSOTReq.setLngs(sblngs.toString());
                geoSOTReq.setGeoLevel((byte) 3);

                long[] geoNums = geoSOTService.getGridsByPolygon(geoSOTReq).getGeoNums();

                //聚合后的网格码数
                aggregationGrid = aggregationGrid(geometryN, geoNums, 3, 22);

                country.put("geoNums", aggregationGrid);

                ESSearchUtils.insert(country, "bzk_data");
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("read shpfile Time:" + (endTime - startTime));
        return BaseResp.success();
    }


    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 删除文件夹里面的文件
                flag = true;
            }
        }
        return flag;
    }


    // 深度遍历聚合
    public Set<String> aggregationGrid(Geometry polygon, long[] geoNums, int y, int x) {
        Set<String> set = new HashSet<String>();
        Stack<Map<String, Object>> quadtree = new Stack<Map<String, Object>>();

        // 遍历网格把层级和网格组队添加到栈里
        for (long num : geoNums) {
            Map<String, Object> numRoot = new HashMap<String, Object>();
            numRoot.put("num", num);
            numRoot.put("level", (byte) y);
            quadtree.push(numRoot);
        }

        // 解决栈里的数据，当栈里有数据的时候就弹出一个数据
        while (!quadtree.isEmpty()) {
            Map<String, Object> node = quadtree.pop();
            long geoNum = (long) node.get("num");
            byte nodeLevel = (byte) node.get("level");

            // 根据网格和层级获取对应多边形的点
            double[] nodeLocs = geoSOT.getRectangleCoordinateByGeoNum(geoNum, nodeLevel);
            Geometry polygonmin;
            try {
                polygonmin = new WKTReader().read(Polygon.toWKT(nodeLocs[1], nodeLocs[0], nodeLocs[3], nodeLocs[2]).toString());
            } catch (ParseException e) {
                //System.err.println("Create grid Geometry failed" + e.getMessage());
                continue;
            }

            // 判断这个网格形成的矩形跟原来的多边形的关系
            // 原图形包含这个网格图形
            if (polygon.contains(polygonmin)) {
                set.add(geoNum + "-" + nodeLevel);
                continue;
            }
            // 如果原图形跟这个网格图形相交，将网格图形切分
            if (polygon.intersects(polygonmin)) {
                // 如果已经到了最大层级，不能再大了
                if (nodeLevel == (byte) x) {
                    double[] center = geoSOT.getCenterCoordinateByGeoNum(geoNum, nodeLevel);
                    Geometry cePoint;
                    try {
                        cePoint = new WKTReader().read(Point.toWKT(center[0], center[1]));

                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        continue;
                    }
                    // 如果多边形包含网格码所在区域的中心点，就要了
                    if (polygon.contains(cePoint)) {
                        set.add(geoNum + "-" + nodeLevel);
                    }
                } else {
                    // 不是最大层级，还可以再往下划分网格
                    byte sonLevel = (byte) (nodeLevel + 1);
                    // 获取子网格
                    long[] sonNums = geoSOT.getSonGrids(geoNum, nodeLevel, sonLevel, 1024);
                    for (long sonNum : sonNums) {
                        Map<String, Object> sonNode = new HashMap<String, Object>();
                        sonNode.put("num", sonNum);
                        sonNode.put("level", sonLevel);
                        quadtree.push(sonNode);
                    }
                }

            }
        }
        return set;
    }

}
