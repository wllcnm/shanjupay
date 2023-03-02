package com.shanjupay.merchant.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.merchant.api.dto.MerchantDTO;

/**
 * Created by Administrator.
 */
public interface MerchantService {

    //根据 id查询商户
    MerchantDTO queryMerchantById(Long id);

    //注册商户
    MerchantDTO creatMerchant(MerchantDTO merchantDTO) throws BusinessException;

    //商户资质申请接口,把关键参数merchantID写到接口中
    void applyMerchant(Long merchantID, MerchantDTO merchantDTO) throws BusinessException;
}
