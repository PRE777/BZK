package com.iwhere.gridgeneration.biz.websocket.controller;

import com.alibaba.fastjson.JSONArray;
import com.iwhere.gridgeneration.biz.data.service.IDataService;
import com.iwhere.gridgeneration.biz.websocket.dto.ResponseMessage;
import com.iwhere.gridgeneration.utils.base.dto.BaseResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Created by niucaiyun on 2019/3/14
 */
@Controller
public class WebsocketController {

    @Autowired
    private IDataService dataService;

    /***
     * 状态监视
     * @MessageMapping注解和我们之前使用的@RequestMapping类似。
     * @SendTo注解表示当服务器有消息需要推送的时候，会对订阅了@SendTo中路径的浏览器发送消息。
     * @param
     * @return
     */
    @MessageMapping("/keepwatch")
    @SendTo("/topic/getResponse")
    public ResponseMessage keepwatch() {
        JSONArray data = dataService.keepwatch();
        return new ResponseMessage(data);
    }
}
