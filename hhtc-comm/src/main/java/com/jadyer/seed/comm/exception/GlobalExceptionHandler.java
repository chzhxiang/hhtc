package com.jadyer.seed.comm.exception;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.constant.CommonResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler({HHTCException.class, Throwable.class})
    public CommonResult process(Throwable cause, HttpServletRequest request){
        LogUtil.getLogger().info("Exception Occured URL="+request.getRequestURL()+",堆栈轨迹如下", cause);
        CommonResult result = new CommonResult();
        result.setMsg(cause.getMessage());
        if(cause instanceof HHTCException){
            int code = ((HHTCException)cause).getCode();
            result.setCode(code);
            if(code==CodeEnum.HHTC_SMS_SEND_FAIL_1.getCode() || code==CodeEnum.HHTC_SMS_SEND_FAIL_5.getCode() || code==CodeEnum.HHTC_SMS_SEND_FAIL_10.getCode()){
                result.setData(cause.getMessage());
                result.setMsg(CodeEnum.getMsgByCode(code));
            }
        }else{
            result.setCode(CodeEnum.SYSTEM_ERROR.getCode());
        }
        return result;
    }
}