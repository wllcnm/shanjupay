package com.shanjupay.merchant.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.MerchantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator.
 */
@org.apache.dubbo.config.annotation.Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    MerchantMapper merchantMapper;

    @Override
    public MerchantDTO queryMerchantById(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        MerchantDTO merchantDTO = BeanUtil.copyProperties(merchant, MerchantDTO.class);
        return merchantDTO;
    }

    @Override
    @Transactional
    public MerchantDTO creatMerchant(MerchantDTO merchantDTO) throws BusinessException {
        //校验参数合法性
        if (merchantDTO == null) {
            throw new BusinessException(CommonErrorCode.E_100108);
        }
        //校验手机号是否为空
        if (StrUtil.isBlank(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        //校验手机号格式
        if (!PhoneUtil.isMobile(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100109);
        }
        //校验手机号是否唯一
        LambdaQueryWrapper<Merchant> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Merchant::getMobile, merchantDTO.getMobile());
        Integer count = merchantMapper.selectCount(lambdaQueryWrapper);
        if (count > 0) {
            throw new BusinessException(CommonErrorCode.E_100113);
        }

        Merchant merchant = new Merchant();
        merchant.setMobile(merchantDTO.getMobile());
        merchant.setAuditStatus("0");
        merchantMapper.insert(merchant);
        merchantDTO.setId(merchant.getId());
        return merchantDTO;
    }

    @Override
    public void applyMerchant(Long merchantID, MerchantDTO merchantDTO) throws BusinessException {
        if (merchantDTO == null || merchantDTO == null) {
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        //校验商户ID合法性
        Merchant merchant = merchantMapper.selectById(merchantID);
        if (merchantDTO == null) {
            throw new BusinessException(CommonErrorCode.E_200002);
        }
        //合法,更新
        Merchant merchantEntity = BeanUtil.copyProperties(merchantDTO, Merchant.class);
        //更新前将ID和手机号手动设置到Entity
        merchantEntity.setMobile(merchant.getMobile());//使用数据库中原来的手机号
        merchantEntity.setId(merchantID); //实体类中id可能为空,所以必须将id设置进去
        merchantEntity.setAuditStatus("1"); //将审核状态更新为1
        merchantEntity.setTenantId(merchant.getTenantId()); //租户ID也不能改变
        //调用mapper更新商户表
        merchantMapper.updateById(merchantEntity);
    }
}
