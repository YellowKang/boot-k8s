package com.kang.test.k8s;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//@SpringBootTest
class BootK8sApplicationTests {

    @Test
    void contextLoads() {
        String json = "{\n" +
                "    \"_id\" : ObjectId(\"5b52edacf8b32a3c5049d56c\"),\n" +
                "    \"煤矿ID\" : NumberLong(530125005380),\n" +
                "    \"企业名称\" : \"宜良县汤池汇源煤矿曲者露天坑\",\n" +
                "    \"监管机构\" : \"红河监察分局\",\n" +
                "    \"省\" : \"云南省\",\n" +
                "    \"市\" : \"昆明市\",\n" +
                "    \"县\" : \"宜良县\",\n" +
                "    \"证照编号\" : \"5.30125E+12\",\n" +
                "    \"颁证机构\" : \"昆明市工商行政管理局阳宗海分局\",\n" +
                "    \"颁证时间\" : \"\",\n" +
                "    \"生效时间\" : \"\",\n" +
                "    \"失效时间\" : \"\",\n" +
                "    \"证照类型\" : \"工商营业执照\",\n" +
                "    \"照片链接\" : \"/Files/CredPhotos/635716134013593750588401.jpg\",\n" +
                "    \"照片名称\" : \"635716134013593750588401.jpg\"\n" +
                "}";

        json =  json.replace("NumberLong(","");
        json =  json.replace(")","");
        json =  json.replace("ObjectId(","");
        json =  json.replace("ISODate(","");


        LinkedHashMap linkedHashMap = JSONObject.parseObject(json, LinkedHashMap.class);
        linkedHashMap.remove("_id");

        for (Object o : linkedHashMap.keySet()) {
            System.out.println(o);
        }

        ConcurrentHashMap<String,Object> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put("St",1);
    }

}
