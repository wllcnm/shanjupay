package com.shanjupay.merchant.service;

import com.shanjupay.common.domain.BusinessException;


public interface FileService {
    String uploadFile(byte[] bytes, String fileName) throws BusinessException;
}
