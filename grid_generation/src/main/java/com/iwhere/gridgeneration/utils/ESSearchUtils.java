package com.iwhere.gridgeneration.utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.alibaba.fastjson.JSON;
import com.iwhere.geosot.dto.resp.GeoSOTResp01;
import com.iwhere.gridgeneration.geonum.model.DataUniqueCode;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iwhere.geosot.dto.req.GeoSOTReq;
import com.iwhere.geosot.dto.resp.GeoSOTResp07;
import com.iwhere.geosot.service.GeoSOT;
import com.iwhere.geosot.service.GeoSOTService;
import com.iwhere.gridgeneration.entity.Point;
import com.iwhere.gridgeneration.entity.Polygon;
import com.iwhere.gridgeneration.utils.base.dto.BaseResp;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@Component
public class ESSearchUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(ESSearchUtils.class);

    @Autowired
    private GeoSOTService geoSOTService;

    @Autowired
    private GeoSOT geoSOT;

    @Autowired
    private BaseInterfaceUtil baseInterfaceUtil;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private DataUniqueCode dataUniqueCode;


    public BaseResp updateStatus(String status, JSONArray json) {
        ExecutorService exe = Executors.newFixedThreadPool(5);
        TermQueryBuilder termQuery = QueryBuilders.termQuery("typecode", "aaa");
        SearchResponse actionGet = ESClient.getInstance().prepareSearch("type_data_bzk").setQuery(termQuery).setSize(1).execute().actionGet();
        Map<String, Object> source = actionGet.getHits().getAt(0).getSource();
        String string = (String) source.get("status");

        if (json == null) {
            String getstatusname = baseInterfaceUtil.getstatusname(status);
            if (getstatusname.equals("open")) {
                //表示状态开启
                //开启一个线程，查看是否有预存储的数据
                Future future = exe.submit(() -> {
                    TermQueryBuilder termQuery1 = QueryBuilders.termQuery("typecode", "bbb");
                    SearchResponse actionGet1 = ESClient.getInstance().prepareSearch("type_data_bzk").setQuery(termQuery1).setSize(1).execute().actionGet();
                    if (actionGet1.getHits().getTotalHits() == 0) {
                        //表示没有预存储数据
                    } else {
                        //表示有预存储的数据，则取出来进行数据入库
                        for (SearchHit hit : actionGet1.getHits()) {
                            Map<String, Object> source1 = hit.getSource();
                            String data = (String) source1.get("data");
                            JSONArray arrays = JSON.parseArray(data);

                            BulkRequestBuilder bulkRequest = ESClient.getInstance().prepareBulk();  //批量添加数据
                            JSONObject returndata = new JSONObject();
                            returndata.put("size", arrays.size());
                            int z = 0;
                            for (int i = 0; i < arrays.size(); i++) {
                                JSONObject jsonObject = arrays.getJSONObject(i);
                                bulkRequest.add(ESClient.getInstance().prepareIndex("bzk_data", "doc").setSource(jsonObject, XContentType.JSON));
                            }

                            //执行批量
                            BulkResponse bulkResponse = bulkRequest.get();
                            //将原数据删除
                            DeleteResponse response = ESClient.getInstance().prepareDelete("type_data_bzk", "doc", hit.getId()).get();

                            if (bulkResponse.hasFailures()) {
                                int index = 0;
                                for (Iterator<BulkItemResponse> it = bulkResponse.iterator(); it.hasNext(); index++) {
                                    //JSONObject jsonObject = (JSONObject) parseArray.get(index);
                                    BulkItemResponse rep = it.next();
                                    if (rep.isFailed()) {
                                        z++;
                                    }
                                }
                            }
                            returndata.put("success", arrays.size() - z);
                            returndata.put("err", z);
                            System.out.print(returndata.toString());
                            template.convertAndSend("/topic/updateStatus", returndata);
                        }

                    }
                });
                exe.shutdown();
            }

            //表示修改服务
            UpdateRequest updateRequest = null;
            try {
                updateRequest = new UpdateRequest("type_data_bzk", "doc", actionGet.getHits().getAt(0).getId()).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).doc(XContentFactory.jsonBuilder().startObject().field("status", getstatusname).endObject());
                ESClient.getInstance().update(updateRequest).get();
                return BaseResp.map().append("process", "success");
            } catch (Exception e) {
                e.printStackTrace();
                return BaseResp.error(500, "状态修改失败");
            }


        } else {
            //表示有数据，此时判断库里的状态是否开启
            if (string.equals("open")) {
                //状态开启，数据录入
                //开启一个线程
                Future future = exe.submit(() -> {
                    BulkRequestBuilder bulkRequest = ESClient.getInstance().prepareBulk();  //批量添加数据
                    JSONObject returndata = new JSONObject();
                    returndata.put("size", json.size());
                    int z = 0;
                    for (int i = 0; i < json.size(); i++) {
                        JSONObject jsonObject = json.getJSONObject(i);
                        bulkRequest.add(ESClient.getInstance().prepareIndex("bzk_data", "doc").setSource(jsonObject, XContentType.JSON));
                    }

                    //执行批量
                    BulkResponse bulkResponse = bulkRequest.get();

                    if (bulkResponse.hasFailures()) {
                        int index = 0;
                        for (Iterator<BulkItemResponse> it = bulkResponse.iterator(); it.hasNext(); index++) {
                            //JSONObject jsonObject = (JSONObject) parseArray.get(index);
                            BulkItemResponse rep = it.next();
                            if (rep.isFailed()) {
                                z++;
                            }
                        }
                    }
                    returndata.put("success", json.size() - z);
                    returndata.put("err", z);
                    template.convertAndSend("/topic/updateStatus", returndata);
                });

                return BaseResp.map().append("process", "success");

            } else {
                //状态没有开启，预存储
                //开启一个线程
                //服务处于关闭状态
                //先入库

                Future future = exe.submit(() -> {
                    JSONObject object = new JSONObject();
                    object.put("typecode", "bbb");
                    object.put("update_time", DateUtils.getDateTime(new Date().getTime()));
                    object.put("create_time", DateUtils.getDateTime(new Date().getTime()));
                    object.put("data", json.toString());
                    IndexResponse indexResponse = ESClient.getInstance().prepareIndex("type_data_bzk", "doc").setSource(object, XContentType.JSON).get();
                });

                return BaseResp.map().append("process", "success");

            }
        }
    }


    /**
     * 批量插入遥感的数据到es里
     *
     * @param
     * @param list
     * @param
     * @return
     */
    public BaseResp bulkAddData(JSONArray list) {
        BulkRequestBuilder bulkRequest = ESClient.getInstance().prepareBulk();
        for (int i = 0; i < list.size(); i++) {
            System.out.println("读取第" + i + "条数据");
            // 获取对应的行数据
            JSONObject row = list.getJSONObject(i);
            String dataType = row.getString("dataType");
            JSONObject extraProperties = row.getJSONObject("extraProperties");
            Double scale = 50000d;
            if(dataType.equals("001") || dataType.equals("002") || dataType.equals("003")){
                if (extraProperties.containsKey("scale")) {
                    scale = extraProperties.getDouble("scale");
                }
            }
            row.put("scale",scale);

            String externalCoding = dataUniqueCode.generateUniqueCode(row);

            /*String externalCoding = getExternalCoding(row.getDouble("lonlatExtentLeft"), row.getDouble("lonlatExtentBottom"), row.getDouble("lonlatExtentRight"), row.getDouble("lonlatExtentTop"), scale);*/
            row.put("externalCoding", externalCoding);

            Geometry polygon;
            // 获取区域
            try {
                polygon = new WKTReader().read(Polygon.toWKT(row.getDouble("lonlatExtentLeft"), row.getDouble("lonlatExtentBottom"), row.getDouble("lonlatExtentRight"), row.getDouble("lonlatExtentTop")));

            } catch (ParseException e) {
                System.err.println("Create grid Geometry failed" + e.getMessage());
                continue;
            }


            for (int y = 3; y < 20; y++) {
                GeoSOTReq geosotReq = new GeoSOTReq();
                geosotReq.setRtLat(row.getDouble("lonlatExtentTop"));
                geosotReq.setLbLat(row.getDouble("lonlatExtentBottom"));
                geosotReq.setRtLng(row.getDouble("lonlatExtentRight"));
                geosotReq.setLbLng(row.getDouble("lonlatExtentLeft"));
                geosotReq.setGeoLevel((byte) y);

                //GeoSOTResp08 geoNumsByRect = geoSOTService.getGeoNumsByRect(geosotReq);
                Map<String, Object> gridsByRectangle = geoSOT.getGridsByRectangle(row.getDouble("lonlatExtentTop"), row.getDouble("lonlatExtentLeft"), row.getDouble("lonlatExtentBottom"), row.getDouble("lonlatExtentRight"), (byte) y);
                long[] geoNums = (long[]) gridsByRectangle.get("geoNums");

                //long[] geoNums = geoNumsByRect.getGeoNums();
                List<String> geonums = new ArrayList<String>();
                for (long l : geoNums) {
                    String geonum = String.valueOf(l) + "-" + y;
                    geonums.add(geonum);
                }
                row.put("geonum-" + y, geonums);
                row.put("create_time", DateUtils.getDateTime());
                if (y == 3) {
                    // 深度遍历聚合
                    List<String> gridNums = aggregationGrid(polygon, geoNums, y);
                    row.put("geoNums", gridNums);
                }
            }
            // 网格聚合

            bulkRequest.add(ESClient.getInstance().prepareIndex("bzk_data", "doc").setSource(row, XContentType.JSON));

        }

        // 执行插入
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            int index = 0;
            for (Iterator<BulkItemResponse> it = bulkResponse.iterator(); it.hasNext(); index++) {
                JSONObject jsonObject = list.getJSONObject(index);
                BulkItemResponse rep = it.next();
                if (rep.isFailed()) {
                    LOGGER.info("该数据没有添加成功" + jsonObject.getString("geoResID"));
                }
            }
        }

        return BaseResp.success();
    }

    /***
     * 获取内编码数据
     * @param lonlatExtentLeft
     * @param lonlatExtentBottom
     * @param lonlatExtentRight
     * @param lonlatExtentTop
     * @return
     */
    private String getExternalCoding(Double lonlatExtentLeft, Double lonlatExtentBottom, Double lonlatExtentRight, Double lonlatExtentTop, int scale) {
        //根据scale 获取层级
        int geolevel = baseInterfaceUtil.scaleZoom(scale);

        GeoSOTReq geosotReq = new GeoSOTReq();
        geosotReq.setRtLat(lonlatExtentTop);
        geosotReq.setLbLat(lonlatExtentBottom);
        geosotReq.setRtLng(lonlatExtentRight);
        geosotReq.setLbLng(lonlatExtentLeft);
        geosotReq.setGeoLevel((byte) geolevel);

        //画线
        //输出：{lngs:[lon1,lon2,lon3],lats:[lat1,lat2,lat3,lat4]} (经度是升序排列，纬度是降序排列)
        GeoSOTResp01 geoSOTResp01 = geoSOTService.drawGridOnMap(geosotReq);
        List<Double> lats = geoSOTResp01.getLats();
        List<Double> lons = geoSOTResp01.getLons();

        //取出前两个经度
        Double lng1 = lons.get(0);
        Double lng2 = lons.get(1);

        //取出后两个纬度
        Double lat1 = lats.get(lats.size() - 1);
        Double lat2 = lats.get(lats.size() - 2);

        //计算出左下角网格中心点
        Double lat = (lat1 + lat2) / 2;
        Double lng = (lng1 + lng2) / 2;

        //求单网格网格码
        long geoNum = geoSOT.getGeoNum(lat, lng, (byte) geolevel);
        return String.valueOf(geoNum) + "-" + String.valueOf(lats.size() - 1) + "-" + String.valueOf(lons.size() - 1);
    }

    /***
     * 高效信息检索
     *
     * @param dataType
     * @param startProductDate
     * @param endProductDate
     * @param geolevel
     * @param pageNum
     * @param pageSize
     * @param ancestorGrid
     * @return
     */
    public JSONObject getInformationHighlyActive(String dataType, List<String> asList, String startProductDate, String endProductDate, Integer geolevel, Integer scale, Integer pageNum, Integer pageSize, String cellSize, String geoResName, Set<String> ancestorGrid) {
        int skip = (pageNum - 1) * pageSize;
        JSONObject data = new JSONObject();
        // 设置bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (dataType.equals("999")) {
            if (startProductDate != null && !startProductDate.equals("")) {
                RangeQueryBuilder from = QueryBuilders.rangeQuery("create_time").gte(startProductDate);
                boolQuery.must(from);
            }

            if (endProductDate != null && endProductDate.equals("")) {
                RangeQueryBuilder to = QueryBuilders.rangeQuery("create_time").lte(endProductDate);
                boolQuery.must(to);
            }

            // 处理网格码
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("geoNums", asList);
            boolQuery.filter(termsQuery);
            FieldSortBuilder order = SortBuilders.fieldSort("create_time").order(SortOrder.DESC);
            SearchResponse response = ESClient.getInstance().prepareSearch("iwhere_poi").setTypes("doc").setFetchSource(null, "geoNums").setQuery(boolQuery).addSort(order).setFrom(skip).setSize(pageSize).execute().actionGet();
            if (response.getHits().getTotalHits() == 0) {
                // 表示没有数据
                return data;
            } else {
                data.put("count", response.getHits().getTotalHits());
                // 循环数据
                JSONArray array = new JSONArray();
                for (SearchHit hit : response.getHits()) {
                    Map<String, Object> source = hit.getSource();
                    String coordinate = (String) source.get("location");
                    Double lat = Double.parseDouble(coordinate.split(",")[0]);
                    source.put("lat", lat);
                    Double lng = Double.parseDouble(coordinate.split(",")[1]);
                    source.put("lng", lng);
                    source.put("createTime", source.get("create_time"));
                    array.add(source);
                }
                data.put("data", array);

            }
        } else {
            //查询bzk数据
            if (startProductDate != null && !startProductDate.equals("")) {
                RangeQueryBuilder from = QueryBuilders.rangeQuery("productDate").gte(startProductDate.split("-")[0]+startProductDate.split("-")[1]);
                boolQuery.must(from);
            }

            if (endProductDate != null && !startProductDate.equals("")) {
                RangeQueryBuilder to = QueryBuilders.rangeQuery("productDate").lte(endProductDate.split("-")[0]+endProductDate.split("-")[1]);
                boolQuery.must(to);
            }
            // 处理dataType

            TermQueryBuilder dataTypeQuery = QueryBuilders.termQuery("dataType", dataType);
            // dataType条件过滤
            boolQuery.filter(dataTypeQuery);



            // 处理网格
            // 网格向下查
            TermsQueryBuilder geonumQuery = QueryBuilders.termsQuery("geoNums", asList);

            // 祖先网格向上查
            TermsQueryBuilder ancestorGridQuery = QueryBuilders.termsQuery("geoNums.geo_nums", ancestorGrid);
            BoolQueryBuilder boolQuery2 = QueryBuilders.boolQuery();
            boolQuery2.should(geonumQuery);
            boolQuery2.should(ancestorGridQuery);
            boolQuery.must(boolQuery2);
            // 比例尺处理
            BoolQueryBuilder nestedbool = QueryBuilders.boolQuery();
            if (scale != null && !scale.equals("") && !scale.equals("null")) {
                  TermQueryBuilder termQuery = QueryBuilders.termQuery("extraProperties.scale", scale);
                  nestedbool.should(termQuery);
            }

            // 处理分辨率
            if (cellSize != null && !cellSize.equals("") && !cellSize.equals("null")) {
                TermQueryBuilder termQuery = QueryBuilders.termQuery("extraProperties.cellSize", cellSize);
                nestedbool.should(termQuery);
            }

            // 处理图幅号
            if (geoResName != null && !geoResName.equals("")) {
                TermQueryBuilder termQuery = QueryBuilders.termQuery("extraProperties.tileNumber", geoResName);
                // dataType条件过滤
                nestedbool.should(termQuery);
            }


            NestedQueryBuilder extraProperties = QueryBuilders.nestedQuery("extraProperties", nestedbool, ScoreMode.None);
            boolQuery.filter(extraProperties);
            // 条件准备完毕，准备查询
            SearchResponse response = ESClient.getInstance().prepareSearch("bzk_data").setTypes("doc")
                    .setFetchSource(null, new String[]{"geoNums", "geonum-3", "geonum-4", "geonum-5", "geonum-6", "geonum-7", "geonum-8", "geonum-9", "geonum-10", "geonum-11", "geonum-12", "geonum-13", "geonum-14", "geonum-15", "geonum-16", "geonum-17", "geonum-18", "geonum-19", "geonum-20", "geonum-21", "geonum-22"})
                    .setPostFilter(boolQuery).setFrom(skip).setSize(pageSize).execute().actionGet();

            if (response.getHits().getTotalHits() == 0) {
                // 表示没有数据
                return data;
            } else {
                data.put("pageCount", response.getHits().getTotalHits() % pageSize == 0 ? response.getHits().getTotalHits() / pageSize : response.getHits().getTotalHits() / pageSize + 1);
                data.put("count", response.getHits().getTotalHits());
                // 循环数据
                JSONArray datas = new JSONArray();
                for (SearchHit hit : response.getHits()) {
                    Map<String, Object> source = hit.getSource();
                    source.put("lats", source.get("lonlatExtentBottom") + "~" + source.get("lonlatExtentTop"));
                    source.put("lngs", source.get("lonlatExtentLeft") + "~" + source.get("lonlatExtentRight"));
                    Map<String, Object> object = (Map<String, Object>) source.get("extraProperties");
                    for(Map.Entry<String, Object> entry: object.entrySet()){
                        source.put(entry.getKey(), entry.getValue());
                    }
                    if (object.get("scale") != null) {
                        source.put("scale1", Integer.parseInt((String) object.get("scale")) % 10000 == 0 ? "1:" + Integer.parseInt((String) object.get("scale")) / 10000 + "万" : object.get("scale"));
                    }
                    datas.add(source);
                    data.put("data", datas);
                }

            }

        }
        return data;

    }

    /***
     * 通过网格查询获取对应的数据
     *
     * @param list
     * @param pageNum
     * @param pageSize
     * @return
     */
    public JSONObject getYggfData(List<String> list, Integer pageSize, Integer pageNum) {
        JSONObject object = new JSONObject();
        int from = (pageNum - 1) * pageSize;
        JSONArray datas = new JSONArray();
        TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("geo_nums.geonums", list);
        SearchResponse response = ESClient.getInstance().prepareSearch("yggf_data").setQuery(termsQuery).setFrom(from).setSize(pageSize).execute().actionGet();
        if (response.getHits().getTotalHits() == 0) {
            // 表示没有数据
            return object;
        } else {
            object.put("count", response.getHits().getTotalHits());
            for (SearchHit hit : response.getHits()) {
                Map<String, Object> source = hit.getSource();
                JSONObject obj = new JSONObject();
                obj.put("ID", hit.getId());
                obj.put("SatelliteID", source.get("SatelliteID"));
                obj.put("SensorID", source.get("SensorID"));
                obj.put("SceneID", source.get("SceneID"));
                obj.put("OrbitID", source.get("OrbitID"));
                obj.put("ProductID", source.get("ProductID"));
                obj.put("ProduceTime", source.get("ProduceTime"));
                obj.put("ProductFormat", source.get("ProductFormat"));
                datas.add(obj);
            }
        }
        object.put("datas", datas);
        return object;
    }

    // 深度遍历聚合
    public List<String> aggregationGrid(Geometry polygon, long[] geoNums, int y) {
        List<String> list = new ArrayList<String>();
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
                polygonmin = new WKTReader().read(Polygon.toWKT(nodeLocs[1], nodeLocs[0], nodeLocs[3], nodeLocs[2]));
            } catch (ParseException e) {
                System.err.println("Create grid Geometry failed" + e.getMessage());
                continue;
            }

            // 判断这个网格形成的矩形跟原来的多边形的关系
            // 原图形包含这个网格图形
            if (polygon.contains(polygonmin)) {
                list.add(geoNum + "-" + nodeLevel);
                continue;
            }
            // 如果原图形跟这个网格图形相交，将网格图形切分
            if (polygon.intersects(polygonmin)) {
                // 如果已经到了最大层级，不能再大了
                if (nodeLevel == (byte) 20) {
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
                        list.add(geoNum + "-" + nodeLevel);
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
        return list;
    }

    public void bulkDeleteData(String dataType) {
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(ESClient.getInstance()).filter(QueryBuilders.termQuery("dataType", dataType)).source("bzk_data").get();

        long deleted = response.getDeleted();
        LOGGER.info("一共删除" + dataType + "数据类型" + deleted + "条数据");
    }

    /***
     * 统计数据
     *
     * @param grids
     * @param dataTypes
     * @param endProductDate
     * @param startProductDate
     * @return
     */
    public JSONArray getDataStatisticsInfor(String grids, String dataTypes, String endProductDate, String startProductDate, Integer geolevel, String typecode) {
        JSONArray datas = new JSONArray();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 时间处理
        if (endProductDate != null) {
            RangeQueryBuilder lte = QueryBuilders.rangeQuery("create_time").lte(endProductDate);
            boolQuery.filter(lte);
        }
        if (startProductDate != null) {
            RangeQueryBuilder gte = QueryBuilders.rangeQuery("create_time").gte(startProductDate);
            boolQuery.filter(gte);
        }
        // 网格处理
        if (grids != null && !grids.equals("")) {
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("geonum-" + geolevel, Arrays.asList(grids.split(",")));
            boolQuery.filter(termsQuery);
        }

        //表示特殊区域查询
        if (typecode != null) {
            Map<String, Object> map = getspecialById(typecode, geolevel);
            List<String> o = (ArrayList) map.get("geonum-" + geolevel);
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("geonum-" + geolevel, o);
            boolQuery.filter(termsQuery);
        }


        if (dataTypes.equals("000") || dataTypes.equals("999")) {

        } else {
            TermQueryBuilder termQuery = QueryBuilders.termQuery("dataType", dataTypes);
            boolQuery.filter(termQuery);
        }

        // 表示查询所有所以不进行处理，表示查询poi数据所以bzk数据不处理
        // 查询bzk
        // 分组处理

        TermsAggregationBuilder Aggregation = AggregationBuilders.terms("asdas").field("dataType");
        if (dataTypes.equals("000") || !dataTypes.equals("999")) {
            //表示查询保障库数据
            SearchResponse response = ESClient.getInstance().prepareSearch("bzk_data").setQuery(boolQuery).addAggregation(Aggregation).setSize(0).execute().actionGet();

            Aggregations aggregations = response.getAggregations();
            Terms aggregation = aggregations.get("asdas");
            List<Bucket> buckets = aggregation.getBuckets();
            for (Bucket bucket : buckets) {
                JSONObject obj = new JSONObject();
                obj.put("dataType", bucket.getKey());
                // 根据dataType获取对应的名称
                JSONObject objects = baseInterfaceUtil.getAggregations();
                    JSONArray data = objects.getJSONArray("data");
                        for (int y = 0; y < data.size(); y++) {
                            JSONObject jsonObject = data.getJSONObject(y);
                            if (jsonObject.getString("title").equals(bucket.getKey())) {
                                obj.put("dataTypeName", jsonObject.getString("value"));
                                break;
                            }
                        }


                obj.put("count", bucket.getDocCount());
                datas.add(obj);
            }
        }


        if (dataTypes.equals("000") || dataTypes.equals("999")) {
            // 组织，poi数据
            BoolQueryBuilder boolQuery1 = QueryBuilders.boolQuery();
            TermQueryBuilder termQuery = QueryBuilders.termQuery("dataType", "999");
            boolQuery1.filter(termQuery);

            // 处理网格，处理时间过滤
            if (startProductDate != null) {
                RangeQueryBuilder from = QueryBuilders.rangeQuery("create_time").gte(startProductDate);
                boolQuery1.filter(from);
            }

            if (endProductDate != null) {
                RangeQueryBuilder to = QueryBuilders.rangeQuery("create_time").lte(endProductDate);
                boolQuery1.filter(to);
            }

            // 处理网格码
            if (grids != null && !grids.equals("")) {
                TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("geoNums", Arrays.asList(grids.split(",")));
                boolQuery1.filter(termsQuery);
            }

            SearchResponse response1 = ESClient.getInstance().prepareSearch("iwhere_poi").setTypes("doc").setQuery(boolQuery).execute().actionGet();
            if (response1.getHits().getTotalHits() != 0) {
                // 表示有数据
                JSONObject obj = new JSONObject();
                obj.put("dataType", "999");
                obj.put("dataTypeName", "poi数据");
                obj.put("count", response1.getHits().getTotalHits());
                datas.add(obj);
            }
        }
        return datas;
    }

    /***
     * 数据分布状态
     *
     * @param grids
     * @param dataTypes
     * @param createEndTime
     * @param createStartTime
     * @param geolevel
     * @return
     */
    public JSONArray getDataDistributionStatus(String grids, String dataTypes, Long createEndTime, Long createStartTime, int geolevel) {

        JSONArray datas = new JSONArray();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("typecode", "special");
        boolQuery.mustNot(termQueryBuilder);

        // 时间处理
        if (createEndTime != null) {
            RangeQueryBuilder lte = QueryBuilders.rangeQuery("create_time").lte(createEndTime);
            boolQuery.filter(lte);
        }

        if (createStartTime != null) {
            RangeQueryBuilder gte = QueryBuilders.rangeQuery("create_time").gte(createStartTime);
            boolQuery.filter(gte);
        }

        // 网格处理
        if (grids != null) {
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("geonum-" + geolevel, Arrays.asList(grids.split(",")));
            boolQuery.filter(termsQuery);
        }

        // 数据类型处理
        if (dataTypes != null) {
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("dataType", Arrays.asList(dataTypes.split(",")));
            boolQuery.filter(termsQuery);
        }


        // 分组处理
        TermsAggregationBuilder Aggregation = AggregationBuilders.terms("groupsgeonum").field("geonum-" + geolevel).subAggregation(AggregationBuilders.terms("groupsdataType").field("dataType"));

        // 查询
        long l1 = System.currentTimeMillis();
        SearchResponse response = ESClient.getInstance().prepareSearch("bzk_data").setQuery(boolQuery).addAggregation(Aggregation).setSize(0).execute().actionGet();

        long l2 = System.currentTimeMillis();
        System.out.println(l2 - l1 + "===========================" + "查询时间一共");
        Aggregations aggregations = response.getAggregations();
        Terms aggregation = aggregations.get("groupsgeonum");

        long l3 = System.currentTimeMillis();
        System.out.println(l3 - l2 + "===========================" + "提起桶的时间");
        List<Bucket> buckets = aggregation.getBuckets();
        if (buckets.size() > 0) {
            for (Bucket bucket : buckets) {
                JSONObject obj = new JSONObject();
                String geonumlevel = (String) bucket.getKey();
                obj.put("geoNum", geonumlevel.split("-")[0]);
                // 根据网格码，获取网格码的坐标
                GeoSOTReq geosotReq = new GeoSOTReq();
                geosotReq.setGeoNum(Long.parseLong(geonumlevel.split("-")[0]));
                geosotReq.setGeoLevel((byte) geolevel);
                GeoSOTResp07 scopeOfGeoNum = geoSOTService.getScopeOfGeoNum(geosotReq);
                obj.put("minlat", scopeOfGeoNum.getMinLat());
                obj.put("maxlat", scopeOfGeoNum.getMaxLat());
                obj.put("minlng", scopeOfGeoNum.getMinLng());
                obj.put("maxlng", scopeOfGeoNum.getMaxLng());
                obj.put("count", bucket.getDocCount());
                datas.add(obj);
            }
        }
        long l4 = System.currentTimeMillis();
        System.out.println(l4 - l3 + "===========================" + "遍历桶内数据的时间");
        return datas;
    }

    public JSONArray test() {
        FieldSortBuilder order = SortBuilders.fieldSort("create_time").order(SortOrder.ASC);
        SearchResponse response = ESClient.getInstance().prepareSearch("iwhere_poi").setTypes("doc").addSort(order).setSize(10).execute().actionGet();
        if (response.getHits().getTotalHits() != 0) {
            System.out.println("bdkcsbdkcjsdc");
        }

        return null;
    }

    public JSONObject getGeonums(String countryCode) {

        return null;
    }

    public JSONObject getInformationYggf(String dataType, List<String> asList, Integer pageNum, Integer pageSize, Set<String> ancestorGrid) {

        JSONObject data = new JSONObject();
        // 查询对应网格下的高分数据
        // 处理对应数据类型
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        TermQueryBuilder termQuery = QueryBuilders.termQuery("dataType", dataType);
        boolQuery.filter(termQuery);
        // 处理网格
        // 向下查
        TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("geoNums.geonums", asList);

        // 祖先网格向上查
        TermsQueryBuilder ancestorGridQuery = QueryBuilders.termsQuery("geoNums", ancestorGrid);


        BoolQueryBuilder boolQuery2 = QueryBuilders.boolQuery();
        boolQuery2.should(termsQuery);
        boolQuery2.should(ancestorGridQuery);
        boolQuery.must(boolQuery2);
        // 执行查询
        SearchResponse response = ESClient.getInstance().prepareSearch("yggf_data_bzk").setTypes("doc").setQuery(boolQuery).setSize(pageSize).setFrom((pageNum - 1) * pageSize).execute().actionGet();
        JSONArray array = new JSONArray();
        if (response.getHits().getTotalHits() == 0) {
            // 表示没有对应高分数据
            data.put("count", 0);
            data.put("datas", array);
        } else {
            data.put("count", response.getHits().getTotalHits());
            for (SearchHit hit : response.getHits()) {
                JSONObject obj = new JSONObject();
                Map<String, Object> source = hit.getSource();
                obj.put("TopLeft", source.get("TopLeftLongitude") + "," + source.get("TopLeftLatitude"));
                obj.put("TopRigh", source.get("TopRightLongitude") + "," + source.get("TopRightLatitude"));
                obj.put("BottomRight", source.get("BottomRightLongitude") + "," + source.get("BottomRightLatitude"));
                obj.put("BottomLeft", source.get("BottomLeftLongitude") + "," + source.get("BottomLeftLatitude"));
                obj.put("createTime", source.get("createTime"));
                obj.put("GFUrl", source.get("GFUrl"));
                obj.put("geoNums", source.get("geoNums"));
                array.add(obj);
            }
            data.put("datas", array);
        }
        return data;
    }

    /***
     * 获取全球的数据统计
     *
     * @param typecode
     * @return
     */
    public JSONArray getDataDistribute(String dataTypes, String typecode, String geonum, Integer geolevel, Double leftTopLat, Double leftTopLng, Double rightBotmLat, Double rightBotmLng, String dayTime, String showtype) {
        // 根据高度获取对应的层级
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("typecode", "special");
        boolQuery.mustNot(termQueryBuilder);

        // 处理类型
        TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("dataType", Arrays.asList(dataTypes.split(",")));
        boolQuery.filter(termsQuery);
        // 入库时间处理

        //查询数据量总量
        if(showtype !=null){
            if(showtype.equals("01")){
                RangeQueryBuilder  lte = QueryBuilders.rangeQuery("create_time").lte(dayTime + " 23:59:59");
                boolQuery.filter(lte);
            }else{
                //查询当天数据更新量
                RangeQueryBuilder  lte = QueryBuilders.rangeQuery("create_time").lte(dayTime + " 23:59:59");
                boolQuery.filter(lte);
                RangeQueryBuilder gte = QueryBuilders.rangeQuery("create_time").gte(dayTime + " 00:00:00");
                boolQuery.filter(gte);
            }
        }

        //处理网格码
        List<String> geoNums = new ArrayList<String>();
        //表示查询全球
        if (typecode != null && typecode.equals("000")) {
            // 查询全球的数据，第七层数据
            long l1 = System.currentTimeMillis();
            TermsAggregationBuilder field = AggregationBuilders.terms("agg").field("geonum-7").size(100000);
            SearchResponse actionGet = ESClient.getInstance().prepareSearch("bzk_data").setTypes("doc").setQuery(boolQuery).addAggregation(field).execute().actionGet();
            long l2 = System.currentTimeMillis();
            System.out.println(l2 - l1 + "====================聚合查询的时间");
            Terms aggregation = actionGet.getAggregations().get("agg");
            List<Bucket> buckets = aggregation.getBuckets();
            long l3 = System.currentTimeMillis();
            System.out.println(l3 - l2 + "====================获取桶内数据的时间");

            JSONArray organizeData = getOrganizeData(buckets, 7, "02");

            long l4 = System.currentTimeMillis();
            System.out.println(l4 - l3 + "====================遍历桶内数据的时间");
            return organizeData;
        } else if (typecode != null && !typecode.equals("000")) {
            // 表示重点区域
            Map<String, Object> map = getspecialById(typecode, geolevel);
            geoNums = (ArrayList) map.get("geonum-" + geolevel);
        } else if (typecode == null) {
            //表示按照屏幕坐标进行查询
            if (geonum == null) {
                // 屏幕坐标查询，屏幕坐标转换成网格码
                Map<String, Object> gridsByRectangle = geoSOT.getGridsByRectangle(leftTopLat, leftTopLng, rightBotmLat, rightBotmLng, geolevel.byteValue());
                long[] geonums = (long[]) gridsByRectangle.get("geoNums");
                for (long l : geonums) {
                    geoNums.add(l + "-" + geolevel);
                }
            } else {
                geoNums = Arrays.asList(geonum.split(","));
            }

        }

        // 网格查询
        TermsQueryBuilder termsgeoQuery = QueryBuilders.termsQuery("geonum-" + geolevel, geoNums);
        boolQuery.filter(termsgeoQuery);
        // 网格查询，或者屏幕坐标查询
        TermsAggregationBuilder field = AggregationBuilders.terms("agg").field("geonum-" + geolevel);

        long l1 = System.currentTimeMillis();
        SearchResponse actionGet = ESClient.getInstance().prepareSearch("bzk_data").setTypes("doc").setQuery(boolQuery).addAggregation(field).execute().actionGet();
        long l2 = System.currentTimeMillis();
        System.out.println(l2 - l1 + "====================聚合查询的时间");

        Terms aggregation = actionGet.getAggregations().get("agg");
        List<Bucket> buckets = aggregation.getBuckets();
        long l3 = System.currentTimeMillis();
        System.out.println(l3 - l2 + "====================获取桶内数据的时间");
        JSONArray organizeData = getOrganizeData(buckets, geolevel, "02");
        long l4 = System.currentTimeMillis();
        System.out.println(l4 - l3 + "====================遍历桶内数据的时间");
        return organizeData;
    }

    /***
     * 获取对应的中心点和高度
     *
     * @param typecode
     * @return
     */
    public JSONObject getTypeDataBzk(String typecode) {
        JSONObject obj = new JSONObject();
        SearchResponse actionGet = ESClient.getInstance().prepareSearch("type_data_bzk").setTypes("doc").setQuery(QueryBuilders.termQuery("typecode", typecode)).execute().actionGet();
        Map<String, Object> source = actionGet.getHits().getAt(0).getSource();
        obj.put("centrePointlat", source.get("centrePoint").toString().split(",")[0]);
        obj.put("centrePointlng", source.get("centrePoint").toString().split(",")[1]);
        obj.put("height", source.get("height"));
        return obj;
    }

    public JSONArray getOrganizeData(List<Bucket> buckets, Integer geolevel, String types) {
        JSONArray datas = new JSONArray();
        if (types.equals("01")) {
            for (Bucket bucket : buckets) {
                JSONObject obj = new JSONObject();
                obj.put("geonum", bucket.getKey().toString().split("-")[0]);
                obj.put("count", bucket.getDocCount());
                obj.put("color", "236,225,2");
                // 根据网格码，获取网格的坐标点
                GeoSOTReq Req = new GeoSOTReq();
                Req.setGeoLevel(geolevel.byteValue());
                Req.setGeoNum(Long.parseLong(bucket.getKey().toString().split("-")[0]));
                GeoSOTResp07 scopeOfGeoNum = geoSOTService.getScopeOfGeoNum(Req);
                obj.put("minlat", scopeOfGeoNum.getMinLat());
                obj.put("minlng", scopeOfGeoNum.getMinLng());
                obj.put("centrelat", (scopeOfGeoNum.getMinLat() + scopeOfGeoNum.getMaxLat()) / 2.0);
                obj.put("centrelng", (scopeOfGeoNum.getMinLng() + scopeOfGeoNum.getMaxLng()) / 2.0);
                obj.put("maxlat", scopeOfGeoNum.getMaxLat());
                obj.put("maxlng", scopeOfGeoNum.getMaxLng());
                datas.add(obj);
            }
        } else {
            for (Bucket bucket : buckets) {
                JSONObject obj = new JSONObject();
                obj.put("geonum", bucket.getKey().toString().split("-")[0]);
                obj.put("count", bucket.getDocCount());
                obj.put("color", baseInterfaceUtil.getcolor((Long) bucket.getDocCount()));
                // 根据网格码，获取网格的坐标点
                GeoSOTReq Req = new GeoSOTReq();
                Req.setGeoLevel(geolevel.byteValue());
                Req.setGeoNum(Long.parseLong(bucket.getKey().toString().split("-")[0]));
                GeoSOTResp07 scopeOfGeoNum = geoSOTService.getScopeOfGeoNum(Req);
                obj.put("minlat", scopeOfGeoNum.getMinLat());
                obj.put("minlng", scopeOfGeoNum.getMinLng());
                obj.put("centrelat", (scopeOfGeoNum.getMinLat() + scopeOfGeoNum.getMaxLat()) / 2.0);
                obj.put("centrelng", (scopeOfGeoNum.getMinLng() + scopeOfGeoNum.getMaxLng()) / 2.0);
                obj.put("maxlat", scopeOfGeoNum.getMaxLat());
                obj.put("maxlng", scopeOfGeoNum.getMaxLng());
                datas.add(obj);
            }

        }

        JSONObject obj = new JSONObject();
        obj.put("geonum", "532550655936561152");
        obj.put("count", "12");
        obj.put("color", "236,225,2");
        // 根据网格码，获取网格的坐标点
        /*
         * GeoSOTReq Req = new GeoSOTReq();
         * Req.setGeoLevel(geolevel.byteValue());
         * Req.setGeoNum(Long.parseLong(bucket.getKey().toString().split("-")[0]
         * )); GeoSOTResp07 scopeOfGeoNum = geoSOTService.getScopeOfGeoNum(Req);
         */
        obj.put("minlat", 40.0);
        obj.put("minlng", 116.0);
        obj.put("centrelat", (40.0 + 40.004444444444445) / 2.0);
        obj.put("centrelng", (116.0 + 116.00444444444445) / 2.0);
        obj.put("maxlat", 40.004444444444445);
        obj.put("maxlng", 116.00444444444445);
        datas.add(obj);
        return datas;
    }

    /***
     * 状态监视
     *
     * @return
     */
    public JSONArray keepwatch() {
        JSONArray datas = new JSONArray();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("typecode", "special");
        boolQuery.mustNot(termQueryBuilder);

        RangeQueryBuilder lte = QueryBuilders.rangeQuery("create_time").lte(new Date().getTime());
        RangeQueryBuilder gte = QueryBuilders.rangeQuery("create_time").gte(new Date().getTime() - 86400000);
        boolQuery.filter(lte);
        boolQuery.filter(gte);
        TermsAggregationBuilder field = AggregationBuilders.terms("agg").field("geonum-7");
        SearchResponse actionGet = ESClient.getInstance().prepareSearch("bzk_data").setTypes("doc").setQuery(boolQuery).addAggregation(field).execute().actionGet();
        Terms aggregation = actionGet.getAggregations().get("agg");

        List<Bucket> buckets = aggregation.getBuckets();
        datas = getOrganizeData(buckets, 7, "01");
        return datas;
    }

    public JSONArray getDataStatisticsInforByTime(String geonums, String dataType, String endProductDate, String startProductDate, int geolevel, String typecode) {
        JSONArray datas = new JSONArray();
        BoolQueryBuilder boolQuerybzk = QueryBuilders.boolQuery();
        BoolQueryBuilder boolQueryiwhere = QueryBuilders.boolQuery();
        List<String> geoNums = new ArrayList<>();
        // 网格处理
        if (geonums != null && !geonums.equals("")) {
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("geonum-" + geolevel, Arrays.asList(geonums.split(",")));
            boolQuerybzk.filter(termsQuery);
            boolQueryiwhere.filter(termsQuery);
        }
        //表示特殊区域查询
        if (typecode != null) {
            Map<String, Object> map = getspecialById(typecode, geolevel);
            List<String> o = (ArrayList) map.get("geonum-" + geolevel);
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("geonum-" + geolevel, o);
            boolQuerybzk.filter(termsQuery);
            boolQueryiwhere.filter(termsQuery);
        }

        if(dataType !=null && !dataType.equals("000")){
            boolQuerybzk.filter(QueryBuilders.termQuery("dataType",dataType));
        }
        boolQuerybzk.filter(QueryBuilders.rangeQuery("create_time").lte(endProductDate+" 23:59:59").gte(startProductDate+" 00:00:00"));

        boolQueryiwhere.filter(QueryBuilders.rangeQuery("create_time").lte(endProductDate+" 23:59:59").gte(startProductDate+" 00:00:00"));

        // 时间分组处理
        // 开始时间/结束时间处理

        SearchRequestBuilder request1 = ESClient.getInstance().prepareSearch("iwhere_poi").setQuery(boolQueryiwhere);
        SearchRequestBuilder request2 = ESClient.getInstance().prepareSearch("bzk_data").setQuery(boolQuerybzk);

        DateHistogramAggregationBuilder dateHistogramAggregationBuilderbzk = null;
        DateHistogramAggregationBuilder dateHistogramAggregationBuilderiwhere = null;

        if(startProductDate.split("-")[1].equals(endProductDate.split("-")[1])){
            //月一样，按天聚合
            //时间聚合
            dateHistogramAggregationBuilderbzk = AggregationBuilders
                    .dateHistogram("agg")
                    .field("create_time")
                    .format("yyyy-MM-dd")
                    .dateHistogramInterval(DateHistogramInterval.DAY)
                    .minDocCount(0)
                    .extendedBounds(new ExtendedBounds(startProductDate, endProductDate));

            dateHistogramAggregationBuilderiwhere = AggregationBuilders
                    .dateHistogram("agg")
                    .field("create_time")
                    .format("yyyy-MM-dd")
                    .dateHistogramInterval(DateHistogramInterval.DAY)
                    .minDocCount(0)
                    .extendedBounds(new ExtendedBounds(startProductDate, endProductDate));
        }else{
            //月不一样
            //判断年是否一样
            if(startProductDate.split("-")[0].equals(endProductDate.split("-")[0])){
                dateHistogramAggregationBuilderbzk = AggregationBuilders
                        .dateHistogram("agg")
                        .field("create_time")
                        .format("yyyy-MM-dd")
                        .dateHistogramInterval(DateHistogramInterval.MONTH)
                        .minDocCount(0)
                        .extendedBounds(new ExtendedBounds(startProductDate, endProductDate));

                dateHistogramAggregationBuilderiwhere = AggregationBuilders
                        .dateHistogram("agg")
                        .field("create_time")
                        .format("yyyy-MM-dd")
                        .dateHistogramInterval(DateHistogramInterval.MONTH)
                        .minDocCount(0)
                        .extendedBounds(new ExtendedBounds(startProductDate, endProductDate));
            }else{
                //按年聚合
                dateHistogramAggregationBuilderbzk = AggregationBuilders
                        .dateHistogram("agg")
                        .field("create_time")
                        .format("yyyy-MM-dd")
                        .dateHistogramInterval(DateHistogramInterval.YEAR)
                        .minDocCount(0)
                        .extendedBounds(new ExtendedBounds(startProductDate, endProductDate));

                dateHistogramAggregationBuilderiwhere = AggregationBuilders
                        .dateHistogram("agg")
                        .field("create_time")
                        .format("yyyy-MM-dd")
                        .dateHistogramInterval(DateHistogramInterval.YEAR)
                        .minDocCount(0)
                        .extendedBounds(new ExtendedBounds(startProductDate, endProductDate));
            }
        }




        request2.addAggregation(dateHistogramAggregationBuilderbzk);
        request1.addAggregation(dateHistogramAggregationBuilderiwhere);

        if(dataType.equals("000")){  //表示查询所有类型
                SearchResponse response2 = request2.setSize(0).execute().actionGet();
                JSONArray getagg = getagg(response2);
                SearchResponse response1 = request1.setSize(0).execute().actionGet();
                JSONArray getagg1 = getagg(response1);
                for (int i = 0; i < getagg.size(); i++) {
                    JSONObject obj = new JSONObject();
                    JSONObject jsonObject = getagg.getJSONObject(i);
                    JSONObject jsonObject1 = getagg1.getJSONObject(i);
                    obj.put("dataTime", jsonObject.getString("dataTime"));
                    obj.put("docCount", jsonObject.getInteger("docCount") + jsonObject1.getInteger("docCount"));
                    datas.add(obj);
                }
            }else{
            if (!dataType.equals("999")) {
                //表示不查询poi
                SearchResponse response2 = request2.setSize(0).execute().actionGet();
                datas = getagg(response2);
            }else{
                //查询poi
                SearchResponse response1 = request1.setSize(0).execute().actionGet();
                datas = getagg(response1);
            }
        }
        return datas;
    }


    public JSONArray getagg(SearchResponse response) {
        // 获取数据
        JSONArray array = new JSONArray();
        Histogram range = response.getAggregations().get("agg");

        for (Histogram.Bucket entry : range.getBuckets()) {
            JSONObject object = new JSONObject();
            object.put("dataTime", entry.getKeyAsString().split("T")[0]);
            object.put("docCount", entry.getDocCount());

            array.add(object);
        }
        return array;
    }

    /***
     * 获取服务开启状态
     * @return
     */
    public JSONObject getdateStatus() {
        JSONObject object = new JSONObject();
        TermQueryBuilder termQuery = QueryBuilders.termQuery("typecode", "aaa");
        SearchResponse actionGet = ESClient.getInstance().prepareSearch("type_data_bzk").setQuery(termQuery).setSize(1).execute().actionGet();
        if (actionGet.getHits().getTotalHits() == 0) {
            //表示没有数据
            object.put("status", "02");  //表示关闭状态
            //新增一条服务状态处于关闭状态的数据
            JSONObject object1 = new JSONObject();
            object1.put("typecode", "aaa");
            object1.put("status", "close");
            object1.put("update_time", DateUtils.getDateTime(new Date().getTime()));
            object1.put("create_time", DateUtils.getDateTime(new Date().getTime()));
            //新增
            IndexResponse indexResponse = ESClient.getInstance().prepareIndex("type_data_bzk", "doc").setSource(object1, XContentType.JSON).get();

        } else {
            Map<String, Object> source = actionGet.getHits().getAt(0).getSource();
            String string = (String) source.get("status");
            if (string.equals("open")) {
                object.put("status", "01");
            } else {
                object.put("status", "02");
            }
        }

        return object;
    }


    public static boolean find(String countryCode, JSONObject country2, String indexname) {
        TermQueryBuilder termQuery = QueryBuilders.termQuery("countryCode", countryCode);
        SearchResponse actionGet = ESClient.getInstance().prepareSearch(indexname).setPostFilter(termQuery).setSize(1).execute().actionGet();
        if (actionGet.getHits().getTotalHits() == 0) {
            System.out.println("新增一条数据");
            //没有数据，新增
            IndexResponse response = ESClient.getInstance().prepareIndex(indexname, "doc").setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).setSource(country2, XContentType.JSON).get();
        } else {
            System.out.println("更新一条数据");
            //有数据进行update
            String id = actionGet.getHits().getAt(0).getId();
            Map<String, Object> source = actionGet.getHits().getAt(0).getSource();
            List<String> object = (List<String>) source.get("geoNums");
            /*Set<String> object2 = (Set<String>) country2.get("geoNums");
            for (String string : object2) {
                object.add(string);
            }*/

            UpdateRequest updateRequest;
            try {
                updateRequest = new UpdateRequest(indexname, "countryCode", id).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).doc(XContentFactory.jsonBuilder().startObject().field("geoNums", object).endObject());
                ESClient.getInstance().update(updateRequest).get();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }


    //入库
    public static boolean insert(JSONObject country, String indexname) {
        IndexResponse response = ESClient.getInstance().prepareIndex(indexname, "doc").setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).setSource(country, XContentType.JSON).get();

        if (response.getId() != null) {
            return true;
        } else {
            return false;
        }
    }

    /***
     * 获取特殊行政区划列表
     * @return
     */
    public JSONArray getspecial() {
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("typecode", "special");
        SearchResponse actionGet = ESClient.getInstance().prepareSearch("bzk_data").setQuery(termQueryBuilder).setFetchSource(null, new String[]{"geoNums", "geonum-3", "geonum-4", "geonum-5", "geonum-6", "geonum-7", "geonum-8", "geonum-9", "geonum-10", "geonum-11", "geonum-12", "geonum-13", "geonum-14", "geonum-15", "geonum-16", "geonum-17", "geonum-18", "geonum-19", "geonum-20", "geonum-21", "geonum-22"}).setSize(100).execute().actionGet();
        JSONArray array = new JSONArray();

        if (actionGet.getHits().getTotalHits() != 0) {
            for (SearchHit hit : actionGet.getHits()) {
                JSONObject object = new JSONObject();
                Map<String, Object> source = hit.getSource();
                object.put("name", source.get("name"));
                object.put("value", source.get("countryCod"));

                array.add(object);
            }
        }

        return array;
    }


    public Map<String, Object> getspecialById(String id, Integer geolevel) {

        String[] strings = new String[20];
        strings[0] = "geoNums";
        int a = 1;
        for (int i = 1; i < 20; i++) {
            if (i + 2 != geolevel) {
                strings[a] = "geonum-" + i + 2;
            }
            a++;
        }
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("typecode", "special");
        QueryBuilders.termQuery("countryCod.keyword", id);
        SearchResponse actionGet = ESClient.getInstance().prepareSearch("bzk_data").setQuery(termQueryBuilder).setFetchSource(null, strings).setSize(1).execute().actionGet();

        Map<String, Object> source = actionGet.getHits().getAt(0).getSource();
        return source;
    }


    /***
     * 数据分类（总数统计）
     * @param startProductDate
     * @param dataType
     * @param geonums
     * @param geolevel
     * @param typecode
     * @param arrays
     * @return
     */
    public JSONArray getDataStatisticsInforByTimeTotle(String startProductDate, String dataType, String geonums, int geolevel, String typecode, String arrays) {
        JSONArray datas = new JSONArray();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("typecode", "special");
        boolQuery.mustNot(termQueryBuilder);
        List<String> geoNums = new ArrayList<>();
        // 网格处理
        if (geonums != null && !geonums.equals("")) {
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("geonum-" + geolevel, Arrays.asList(geonums.split(",")));
            boolQuery.filter(termsQuery);
        }
        //表示特殊区域查询
        if (typecode != null) {
            Map<String, Object> map = getspecialById(typecode, geolevel);
            List<String> o = (ArrayList) map.get("geonum-" + geolevel);
            TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("geonum-" + geolevel, o);
            boolQuery.filter(termsQuery);
        }

        //根据开始时间获取当月最后一天时间
        String[] split = startProductDate.split("-");
        Date endMonthDate = CalendarUtils.getEndMonthDate(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        RangeQueryBuilder ltestart = QueryBuilders.rangeQuery("create_time").lte(endMonthDate);

        // 时间处理
        if (startProductDate != null) {
            RangeQueryBuilder lte = QueryBuilders.rangeQuery("create_time").lte(startProductDate);
            boolQuery.filter(lte);
        }


        // 时间分组处理
        // 开始时间/结束时间处理

        SearchRequestBuilder request1 = ESClient.getInstance().prepareSearch("iwhere_poi").setQuery(boolQuery);
        SearchRequestBuilder request2 = ESClient.getInstance().prepareSearch("bzk_data").setQuery(boolQuery);

        // 类型处理
        int count = 0;
        if(dataType.equals("000")){  //全选
            int response2 = (int) request2.setSize(0).execute().actionGet().getHits().getTotalHits();
            int response1 = (int) request1.setSize(0).execute().actionGet().getHits().getTotalHits();
            count = response2+response1;
        }else{
            //单选
            if (!dataType.equals("999")) {
                //单选的不是poi
                count = (int) request2.setSize(0).execute().actionGet().getHits().getTotalHits();
            }else{
                //单选poi数据
                count = (int) request1.setSize(0).execute().actionGet().getHits().getTotalHits();
            }
        }

        if(count==0){
            datas = JSON.parseArray(arrays);
        }else{
            for(int i=0; i<JSON.parseArray(arrays).size(); i++){
                JSONObject obj = new JSONObject();
                JSONObject jsonObject = JSON.parseArray(arrays).getJSONObject(i);
                obj.put("dataTime",jsonObject.getString("dataTime"));
                if(i==0){
                    //第一条数据
                    obj.put("docCount",jsonObject.getInteger("docCount")+count);
                    datas.add(obj);
                }else{
                    obj.put("docCount",JSON.parseArray(arrays).getJSONObject(i).getInteger("docCount")+count+JSON.parseArray(arrays).getJSONObject(i-1).getInteger("docCount"));
                    datas.add(obj);
                }
            }
        }
        return datas;
    }
}
