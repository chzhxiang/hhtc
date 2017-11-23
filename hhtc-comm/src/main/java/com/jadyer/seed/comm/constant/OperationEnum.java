package com.jadyer.seed.comm.constant;

public enum OperationEnum {
    FANS_INFOR               (1,    "粉丝个人信息"),
    SYSTEM_OK             (1000, "保留码"),
    SYSTEM_BUSY           (1001, "系统繁忙"),
    SYSTEM_ERROR          (1002, "系统错误"),
    FILE_NOT_FOUND        (1003, "文件未找到"),
    FILE_TRANSFER_FAIL    (1004, "文件传输失败"),

    DB_UPDATA               (2001,"更新数据错误"),

    HHTC_INFOR_ACCREDIT    (3100, "用户未授权"),

    HHTC_SMS_VERIFY_FAIL  (3100, "短信验证未通过"),
    HHTC_SMS_SEND_FAIL_1  (3110, "短信发送失败：每分钟只能发送一条"),
    HHTC_SMS_SEND_FAIL_5  (3111, "短信发送失败：每小时最多发送五条"),
    HHTC_SMS_SEND_FAIL_10 (3112, "短信发送失败：每天最多发送十条"),





    HHTC_UNREG_CAR_OWNER  (3001, "未注册车主"),
    HHTC_UNREG_CAR_PARK   (3002, "未注册车位主"),
    HHTC_UNLOGIN          (3003, "请从微信菜单重新进入"),
    HHTC_GOODS_MATCH_FAIL (3005, "未匹配到车位"),
    HHTC_GOODS_ORDER_FAIL (3006, "下单失败：您来晚了一步，该车位刚刚被其他人下单了"),
    HHTC_UNREG            (3007, "既未注册车主也未注册车位主"),
    HHTC_PUBLISH_NO_MONEY (3008, "押金已足，可直接发布车位"),
    HHTC_NEED_NO_MONEY    (3009, "余额不足");

    private final int code;
    private final String msg;

    OperationEnum(int _code, String _msg){
        this.code = _code;
        this.msg = _msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static String getMsgByCode(int code){
        for(OperationEnum _enum : values()){
            if(_enum.getCode() == code){
                return _enum.getMsg();
            }
        }
        return null;
    }


}
