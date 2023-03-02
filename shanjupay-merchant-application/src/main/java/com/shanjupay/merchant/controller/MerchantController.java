package com.shanjupay.merchant.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.common.util.SecurityUtil;
import com.shanjupay.merchant.service.FileService;
import com.shanjupay.merchant.service.SmsService;
import com.shanjupay.merchant.vo.MerchantDetailVO;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author Administrator
 * @version 1.0
 **/

@RestController
@Api(value = "商户平台应用接口", tags = "商户平台应用接口")
public class MerchantController {

    @org.apache.dubbo.config.annotation.Reference
    MerchantService merchantService;

    @Resource
    SmsService smsService;

    @Resource
    FileService fileService;

    @Resource
    private RestTemplate restTemplate;

    @ApiOperation(value = "根据id查询商户信息")
    @GetMapping("/merchants/{id}")
    public MerchantDTO queryMerchantById(@PathVariable("id") Long id) {

        MerchantDTO merchantDTO = merchantService.queryMerchantById(id);
        return merchantDTO;
    }

    @ApiOperation("测试")
    @GetMapping(path = "/hello")
    public String hello() {
        return "hello";
    }

    @ApiOperation("测试")
    @ApiImplicitParams( //@ApiImplicitParams 为接口参数描述
            {@ApiImplicitParam(name = "age", value = "年龄", required = true, dataType = "Integer"),
                    @ApiImplicitParam(name = "name", value = "姓名", required = true, dataType = "string")}
    )
    @PostMapping(value = "/hi")
    public String hi(String name) {
        return "hi," + name;
    }


    @ApiOperation("发送验证码")
    @GetMapping("/sms")
    @ApiImplicitParam(value = "手机号", name = "phone", dataType = "string", paramType = "query")
    public String sendCode(@RequestParam String phone) {
        return smsService.sendCode(phone);
    }

    @ApiOperation("商户注册")
    @PostMapping("/merchants/register")
    @ApiImplicitParam(name = "merchantRegisterVO", value = "商户注册信息", required = true, dataType = "MerchantRegisterVO")
    public MerchantDTO register(@RequestBody MerchantRegisterVO merchantRegisterVO) {
        //校验手机号合法性
        //校验参数合法性
        if (merchantRegisterVO == null) {
            throw new BusinessException(CommonErrorCode.E_100108);
        }
        //校验手机号是否为空
        if (StrUtil.isBlank(merchantRegisterVO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        //校验手机号格式
        if (!PhoneUtil.isMobile(merchantRegisterVO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100109);
        }

        //校验验证码
        smsService.checkVerifyCode(merchantRegisterVO.getVerifiyCode(), merchantRegisterVO.getVerifiykey());
        //注册
        MerchantDTO merchantDTO = BeanUtil.copyProperties(merchantRegisterVO, MerchantDTO.class);
        merchantService.creatMerchant(merchantDTO);
        return merchantDTO;
    }

    @ApiOperation("上传证件照")
    @PostMapping("/upload")
    public String upload(@ApiParam(value = "证件照", required = true) @RequestParam("file") MultipartFile multipartFile) throws BusinessException, IOException {
        //原始文件名
        String originalFilename = multipartFile.getOriginalFilename();
        //获取扩展名
        String[] split = originalFilename.split("\\.");
        String extendName = split[1];
        //获取随机文件名
        String fileName = UUID.randomUUID().toString();
        //拼接后
        String real_fileName = fileName + "." + extendName;
        //调用fileService,返回的是文件地址
        return fileService.uploadFile(multipartFile.getBytes(), real_fileName);
    }

    @ApiOperation("资质申请")
    @PostMapping("/my/merchants/save")
    @ApiImplicitParam(name = "merchantDetailVO", value = "商户认证资料", required = true, dataType = "MerchantDetailVO", paramType = "body")
    public void saveMerchant(@RequestBody MerchantDetailVO merchantDetailVO) {
        //取出当前登录的用户ID,通过解析前端携带的Token,以获取商户ID
        Long merchantId = SecurityUtil.getMerchantId();
        //将VO转换为DTO
        MerchantDTO merchantDTO = BeanUtil.copyProperties(merchantDetailVO, MerchantDTO.class);
        //更新资质
        merchantService.applyMerchant(merchantId, merchantDTO);
    }
}
