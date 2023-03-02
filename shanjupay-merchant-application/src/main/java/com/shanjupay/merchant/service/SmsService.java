package com.shanjupay.merchant.service;

import com.shanjupay.common.domain.BusinessException;

public interface SmsService {

    String sendCode(String phone) throws BusinessException;

    void checkVerifyCode(String code, String key) throws BusinessException;
}
