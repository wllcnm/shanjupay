package com.shanjupay.merchant.service.impl;

import com.qiniu.storage.Region;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.QiNiuUtil;
import com.shanjupay.merchant.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

    @Value("${qiniu.accessKey}")
    String accessKey;

    @Value("${qiniu.secretKey}")
    String secretKey;

    @Value("${qiniu.bucket}")
    String bucket;

    @Value("${qiniu.url}")
    String url;

    @Override
    public String uploadFile(byte[] bytes, String fileName) throws BusinessException {
        //调用common类中QiNiuUtil
        try {
            QiNiuUtil.upload2qiniu(accessKey, secretKey, bucket, Region.region2(), fileName, bytes);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorCode.E_100106);
        }
        return url + fileName;
    }
}
