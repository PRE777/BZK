package com.iwhere.gridgeneration.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

import java.io.*;

/***
 * 工具
 * @author niucaiyun
 *
 */
@Component
public class BaseInterfaceUtil {
    private static Logger LOGGER = LoggerFactory.getLogger(BaseInterfaceUtil.class);
    
    @Value("${geodata.url}")
	private String geodataUrl;

    public static RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);
        factory.setReadTimeout(30000);
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }


    /**
     * 获取整个资源目录树
     * @return
     */
    public JSONObject getAggregations() {
        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();
        StringBuffer sb = new StringBuffer();
        sb.append(geodataUrl).append("metadata/aggregations?classifiercode=001");
        LOGGER.info("根据dataType获取对应的数据的url" + "---" + sb.toString());
        System.out.println("=================="+BaseInterfaceUtil.getRestTemplate());
        String t = BaseInterfaceUtil.getRestTemplate().getForObject(sb.toString(), String.class);
        JSONObject data = JSONObject.parseObject(t);
        JSONArray children = data.getJSONArray("retdata").getJSONObject(0).getJSONArray("children");
        for(int i=0; i<children.size(); i++){
            JSONObject object = new JSONObject();
            object.put("data", new JSONArray());
            object.put("value", children.getJSONObject(i).getString("caption"));
            object.put("title", children.getJSONObject(i).getString("fieldValue1"));
            array.add(object);
        }

        JSONObject object = new JSONObject();
        object.put("data", new JSONArray());
        object.put("value", "POI 数据");
        object.put("title", "999");
        array.add(object);


        obj.put("data",array);
        obj.put("value",data.getJSONArray("retdata").getJSONObject(0).getString("caption"));
        obj.put("title","01");

        return obj;
    }



    /**
     * 根据dataType获取对应的数据
     * @param dataType
     * @return
     */
    public JSONObject getgeodata(String dataType) {
        StringBuffer sb = new StringBuffer();
        sb.append(geodataUrl).append("metadata/search?classifiercode=001&datatype=").append(dataType);
        LOGGER.info("根据dataType获取对应的数据的url" + "---" + sb.toString());
        System.out.println("=================="+BaseInterfaceUtil.getRestTemplate());
        String t = BaseInterfaceUtil.getRestTemplate().getForObject(sb.toString(), String.class);
        return JSONObject.parseObject(t);
    }


    public JSONObject getgeodataBytoken(String token) {
        StringBuffer sb = new StringBuffer();
        sb.append(geodataUrl).append("product/profiles/").append(token);
        LOGGER.info("根据dataType获取对应的数据的url" + "---" + sb.toString());
        System.out.println("=================="+BaseInterfaceUtil.getRestTemplate());
        String t = BaseInterfaceUtil.getRestTemplate().getForObject(sb.toString(), String.class);
        return JSONObject.parseObject(t);
    }
    
    /***
     * 根据当前比例尺获取层级
     * @param scale
     * @return
     */
    public int scaleZoom(int scale) {
    	if (scale <= 200) return 22;
        else if (scale <= 400) return 21;
        else if (scale <= 800) return 20;
        else if (scale <= 1500) return 19;
        else if (scale <= 3000) return 18;
        else if (scale <= 5300) return 17;
        else if (scale <= 12000) return 16;
        else if (scale <= 25000) return 15;
        else if (scale <= 50000) return 14;
        else if (scale <= 100000) return 13;
        else if (scale <= 200000) return 12;
        else if (scale <= 400000) return 11;
        else if (scale <= 800000) return 10;
        else if (scale <= 1550000) return 9;
        else if (scale <= 3000000) return 8;
        else if (scale <= 6000000) return 7;
        else if (scale <= 12000000) return 6;
        else if (scale <= 25000000) return 5;
        else if (scale <= 50000000) return 4;
        else if (scale <= 100000000) return 3;
        else if (scale <= 200000000) return 2;
        else if (scale > 200000000) return 1;
        return 27;
    }


    /*public int cellsizeCaptionZoom(int cellSize) {
        if (cellSize >= 0.0694) return 22;
        else if (cellSize >= 0.138) return 21;
        else if (cellSize >= 0.27) return 20;
        else if (cellSize >= 0.54) return 19;
        else if (cellSize >= 1.07) return 18;
        else if (cellSize >= 2.15) return 17;
        else if (cellSize >= 4) return 16;
        else if (cellSize >= 8) return 15;
        else if (cellSize >= 17) return 14;
        else if (cellSize >= 35) return 13;
        else if (cellSize >= 69) return 12;
        else if (cellSize >= 139) return 11;
        else if (cellSize >= 278) return 10;
        else if (cellSize >= 549) return 9;
        else if (cellSize >= 1098) return 8;
        else if (cellSize >= 2000) return 7;
        else if (cellSize >= 4000) return 6;
        else if (cellSize >= 9000) return 5;
        else if (cellSize >= 17000) return 4;
        else if (cellSize >= 36000) return 3;
        else if (cellSize >= 71000) return 2;
        return 27;
    }*/




    /**
     * 根据当前高度获取对应层级
     * @param height
     * @return
     */
    public int heightZoom(int height) {
    	if (height > 40000000) {
    		return 6;
    	} else if (height > 15500000) {
    		return 7;
    	} else if (height > 7413243) {
    		return 8;
    	} else if (height > 4400000) {
    		return 9;
    	} else if (height > 2000000) {
    		return 10;
    	} else if (height > 1000595) {
    		return 11;
    	} else if (height > 502757) {
    		return 12;
    	} else if (height > 201517) {
    		return 13;
    	} else if (height > 120022) {
    		return 14;
    	} else if (height > 78410) {
    		return 15;
    	} else if (height > 37634) {
    		return 16;
    	} else if (height > 16634) {
    		return 17;
    	} else if (height > 8634) {
    		return 18;
    	} else if (height > 4334) {
    		return 19;
    	} else if (height > 2234) {
    		return 20;
    	} else if (height > 1134) {
    		return 21;
    	} else if (height >= 534) {
    		return 22;
    	} else {
    		return 23;
    	}
    }
    
    public String getcolor(long count) {
    	/*if (count <= 100) return "rgb(77,107,243)";
    	else if (count <= 200) return "rgb(77,171,251)";
    	else if (count <= 300) return "rgb(116,243,217)";
    	else if (count <= 400) return "rgb(244,244,149)";
    	else if (count <= 500) return "rgb(236,225,2)";
    	else if (count <= 600) return "rgb(222,101,0)";
    	return "rgb(235,18,0)";*/
    	if (count <= 2) return "77,107,243,0.5";
    	else if (count <= 4) return "77,171,251,0.5";
    	else if (count <= 8) return "116,243,217,0.5";
    	else if (count <= 10) return "244,244,149,0.5";
    	else if (count <= 12) return "236,225,2,0.5";
    	else if (count <= 14) return "222,101,0,0.5";
    	return "235,18,0,0.5";
    }


    public String getstatusname(String value) {
        if (value.equals("01")) return "open";
        else if (value.equals("02")) return "close";
        return null;
    }


    /***
     *根据数据类型获取展示参数
     * @param dataType
     * @return
     */
    public JSONArray getshowdata(String dataType) {
        File file = null;
        StringBuilder sb = new StringBuilder();
        try {
            file = ResourceUtils.getFile("classpath:static/data.json");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                String readLine = null;
                while ((readLine = br.readLine()) != null) {
                    sb.append(readLine);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JSONArray datas = JSON.parseArray(sb.toString());
        for (int i = 0; i< datas.size(); i++) {
            JSONObject obj = datas.getJSONObject(i);
            if(obj.get("title").equals(dataType)){
                return obj.getJSONArray("data");
            }
        }

        return null;
    }


    /***
     * 获取资源目录
     * @return
     */
    public JSONObject getsourceData() {
        File file = null;
        StringBuilder sb = new StringBuilder();
        try {
            file = ResourceUtils.getFile("classpath:static/datasource.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                String readLine = null;
                while ((readLine = br.readLine()) != null) {
                    sb.append(readLine);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject data = JSON.parseObject(sb.toString());
        return data;
    }
    
    /***
     * 获取元数据
     * @return
     */
    public JSONArray getsourceData2() {
        File file = null;
        StringBuilder sb = new StringBuilder();
        try {
            file = ResourceUtils.getFile("classpath:static/datasource2.json");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                String readLine = null;
                while ((readLine = br.readLine()) != null) {
                    sb.append(readLine);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JSONArray datas = JSON.parseArray(sb.toString());
        return datas;
    }

}
