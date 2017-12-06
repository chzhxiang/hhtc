package com.jadyer.seed.comm.constant;

public enum CodeEnum {
    SUCCESS               (0,    "成功"),
    SYSTEM_OK             (1000, "保留码"),
    SYSTEM_BUSY           (1001, "系统繁忙"),
    SYSTEM_ERROR          (1002, "系统错误"),

    SYSTEM_NULL          (1002, "系统空指针异常"),
    SYSTEM_PARAM_NULL      (1110, "含有空参数"),
    SYSTEM_PARAM_ERROR     (1120, "参数结构错误"),
    SYSTEM_PARAM_TIME_ERROR     (1130, "参数时间结构错误"),
    SYSTEM_PARAM_DATA_ERROR     (1140, "参数数据错误"),

    SYSTEM_UNLOGIN          (1200, "请从微信菜单重新进入"),
    SYSTEM_PERMISSIONS          (1200, "你的权限不够"),


    FILE_NOT_FOUND        (1003, "文件未找到"),
    FILE_TRANSFER_FAIL    (1004, "文件传输失败"),

    DB_UPDATA               (2001,"更新数据错误"),

    HHTC_INFOR_ACCREDIT    (3100, "用户未授权"),
    HHTC_INFOR_ACCREDIT_ERROR    (3101, "用户授权失败"),
    HHTC_INFOR_PHOMENO     (3110, "用户电话号码未验证"),
    HHTC_INFOR_PHOMENO_USED     (3111, "用户电话号码已经使用"),

    HHTC_INFOR_COMMUNITY_COPY   (3120, "用户住房信息审核,重复提交"),
    HHTC_INFOR_COMMUNITY_NO   (3121, "没有通过住房审核"),
    HHTC_INFOR_COMMUNITY        (3122, "没有住房信息"),

    HHTC_INFOR_CARNUMBERFULL  (3130,"车牌审核或车牌数已满"),
    HHTC_INFOR_CARNUMBER_USED  (3131,"已经提交了车牌请求"),
    HHTC_INFOR_CARNUMBER_NO  (3132, "没有可使用的车牌"),

    HHTC_INFOR_CARPARK     (3140,"已经提交了车位请求"),
    HHTC_INFOR_CARPARK_NO   (3141, "没有可使用的车位"),

    HHTC_SMS_VERIFY_FAIL  (3200, "短信验证码错误"),
    HHTC_SMS_SEND_FAIL_1  (3210, "短信发送失败：每分钟只能发送一条"),
    HHTC_SMS_SEND_FAIL_5  (3211, "短信发送失败：每小时最多发送五条"),
    HHTC_SMS_SEND_FAIL_10 (3212, "短信发送失败：每天最多发送十条"),

    HHTC_FUNDS_DEPOSIT_NO (3310, "押金不足"),
    HHTC_FUNDS_BALANCE_NO (3320, "余额不足"),

    HHTC_ORDER_PORT_TIMEERROR (3400, "和已有订单时间重叠"),
    HHTC_ORDER_RESERVATION_FAIL (3410, "下单失败：您来晚了一步，该车位刚刚被其他人下单了"),
    HHTC_ORDER_DISRESERVATION_FAIL (3420, "订单开始时间小于三个小时，不能取消，只能申诉"),
    HHTC_ORDER_MONEY_NO             (3430,"没有钱可收取"),
    HHTC_ORDER_GOODS_TIMEOUT         (3440,"订单时间超过车位有效期"),
    HHTC_ORDER_FINE_ED         (3450,"订单超时罚金已经缴纳");

    private final int code;
    private final String msg;

    CodeEnum(int _code, String _msg){
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
        for(CodeEnum _enum : values()){
            if(_enum.getCode() == code){
                return _enum.getMsg();
            }
        }
        return null;
    }
}