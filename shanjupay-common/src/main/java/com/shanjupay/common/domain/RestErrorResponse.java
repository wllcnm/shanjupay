package com.shanjupay.common.domain;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@ApiModel(value = "RestErrorResponse", description = "错误响应参数包装")
@Data
@AllArgsConstructor
public class RestErrorResponse {

    private Integer errCode;

    private String errMessage;


}
