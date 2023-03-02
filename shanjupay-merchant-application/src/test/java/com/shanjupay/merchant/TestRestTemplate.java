package com.shanjupay.merchant;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.ListResourceBundle;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TestRestTemplate {

    @Resource
    private RestTemplate restTemplate;


    //测试restTemplate,采用okhttp作为第三方客户端
    @Test
    public void getHtml() {
        String url = "http://www.baidu.com";
        //向url发送http请求
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String body = responseEntity.getBody();
        System.out.println(body);
    }

    @Test
    public void TestSendCode() {
        String url = "http://localhost:56085/sailing/generate?effectiveTime=6000&name=sms";

        //请求体
        HashMap<String, Object> body = new HashMap<>();
        body.put("mobile", "1234567");
        //请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //请求消息构建
        HttpEntity<HashMap<String, Object>> hashMapHttpEntity = new HttpEntity<>(body, httpHeaders);
        //发送请求
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity, Map.class);
        Map responseBody = response.getBody();
        //将map转换为json字符串
        String s = JSON.toJSONString(responseBody);

        //解析数据
        Map<String, Object> msg = (Map<String, Object>) responseBody.get("result");
        String key = (String) msg.get("key");
        //打印结果
        log.info("请求结果为:{}", s);
        log.info("请求结果key为:{}", key);
    }


}
