package com.jadyer.seed.comm.exception;

import com.jadyer.seed.comm.constant.CodeEnum;

public class HHTCException extends RuntimeException {
    private static final long serialVersionUID = 601366631919634564L;
    private int code;
    private String message;

    public HHTCException(CodeEnum codeEnum){
        super(codeEnum.getMsg());
        this.code = codeEnum.getCode();
        this.message = codeEnum.getMsg();
    }

    public HHTCException(CodeEnum codeEnum, Throwable cause){
        super(codeEnum.getMsg(), cause);
        this.code = codeEnum.getCode();
        this.message = codeEnum.getMsg();
    }

    public HHTCException(int code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public HHTCException(int code, String message, Throwable cause){
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}