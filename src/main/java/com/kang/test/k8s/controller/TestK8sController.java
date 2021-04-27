package com.kang.test.k8s.controller;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author BigKang
 * @Date 2020/11/19 4:42 下午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize 测试k8s控制器
 */

@RestController
@Slf4j
public class TestK8sController {
    
    @Value("${node.id}")
    public Integer nodeId;

    @RequestMapping("/")
    public Map index() throws UnknownHostException {
        Map<String, Object> map = new HashMap<>();
        InetAddress localHost = InetAddress.getLocalHost();
        String hostName = localHost.getHostName();
        String hostAddress = localHost.getHostAddress();
        map.put("主机名",hostName);
        map.put("主机地址",hostAddress);
        map.put("NodeId",nodeId);
        log.info("测试日志:{}", JSONUtil.toJsonStr(map));
        return map;
    }

}
