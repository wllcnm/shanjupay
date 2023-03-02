package com.shanjupay.merchant.common.intercept;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.ErrorCode;
import com.shanjupay.common.domain.RestErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice  //与@execption配合使用实现全局异常处理,@ControllerAdvice 基于controller的增强类
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler  //捕获exception异常
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse processException(HttpServletRequest request,
                                              HttpServletResponse response,
                                              Exception e
    ) {
        //解析异常信息

        //如果是系统自定义的异常,直接取出
        if (e instanceof BusinessException) {
            log.error(String.valueOf(e));
            //解析
            BusinessException businessException = (BusinessException) e;
            ErrorCode errorCode = businessException.getErrorCode();

            //错误代码
            int Code = errorCode.getCode();
            //错误信息
            String desc = errorCode.getDesc();

            return new RestErrorResponse(Code, desc);
        }
        log.error(e.toString());
        log.error("系统异常");
        //统一定为999999
        return new RestErrorResponse(CommonErrorCode.UNKNOWN.getCode(), CommonErrorCode.UNKNOWN.getDesc());
    }

}
