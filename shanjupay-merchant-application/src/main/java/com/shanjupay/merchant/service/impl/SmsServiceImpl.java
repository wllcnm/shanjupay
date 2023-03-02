package com.shanjupay.merchant.service.impl;

import com.alibaba.fastjson.JSON;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.merchant.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Value("${sms.url}")
    String url;

    @Value("${sms.effectiveTime}")
    String effectiveTime;


    @Resource
    RestTemplate restTemplate;

    @Override
    public String sendCode(String phone) throws BusinessException {
        String sms_url = url + "/generate?name=sms&effectiveTime=" + effectiveTime;

        //请求体
        HashMap<String, Object> body = new HashMap<>();
        body.put("mobile", phone);
        //请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //请求消息构建
        HttpEntity<HashMap<String, Object>> hashMapHttpEntity = new HttpEntity<>(body, httpHeaders);
        //发送请求
        ResponseEntity<Map> response = null;
        Map responseBody = null;
        try {
            response = restTemplate.exchange(sms_url, HttpMethod.POST, hashMapHttpEntity, Map.class);
            responseBody = response.getBody();

        } catch (RestClientException e) {
            throw new BusinessException(CommonErrorCode.E_100107);
        }
        //将map转换为json字符串
        String s = JSON.toJSONString(responseBody);
        if (responseBody == null || responseBody.get("result") == null) {
            throw new BusinessException(CommonErrorCode.E_100107);
        }
        //解析数据
        Map<String, Object> msg = (Map<String, Object>) responseBody.get("result");
        String key = (String) msg.get("key");
        //打印结果
        log.info("请求结果为:{}", s);
        log.info("请求结果key为:{}", key);
        return key;
    }

    /*
     * 校验手机验证码
     *
     * */
    @Override
    public void checkVerifyCode(String code, String key) throws BusinessException {
        String ver_url = url + "/verify?name=sms&verificationCode=" + code + "&verificationKey=" + key;

        Map responseBody = null;
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(ver_url, HttpMethod.POST, HttpEntity.EMPTY, Map.class);
            responseBody = responseEntity.getBody();
        } catch (RestClientException e) {
            throw new BusinessException(CommonErrorCode.E_100102);
        }

        if (responseBody.get("result") == null || responseBody == null || !(boolean) responseBody.get("result")) {
            throw new BusinessException(CommonErrorCode.E_100102);
        }

    }
}
