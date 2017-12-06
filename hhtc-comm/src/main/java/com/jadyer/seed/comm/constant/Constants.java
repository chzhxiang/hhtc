package com.jadyer.seed.comm.constant;

public interface Constants {
    String WEB_SESSION_USER      = "user";
    String WEB_SESSION_WX_OPENID = "openid";
    String WEB_SESSION_WX_APPID  = "appid";
    String WEB_CURRENT_MENU      = "currentMenu";

    String MPP_BIND_TEXT    = "我是玄玉";
    String MPP_CHARSET_UTF8 = "UTF-8";

    String SECURITY_SUSPEND_MSG  = "http://jadyer.cn/x";
    String SECURITY_SHUTDOWN_MSG = "http://jadyer.cn/MMd0";

    //
    int AUDTI_TEPY_COMMUNITY = 1;
    int AUDTI_TEPY_CARPARK = 2;
    int AUDTI_TEPY_CARNUMBER = 3;
    //余额
    int AUDTI_TEPY_BALANCE = 4;
    int AUDTI_TEPY_DEPOSIT = 5;

    //0--是否授权，1--是否验证电话，2--是否验证住址
    int INFOR_STATE_ACCREDIT_BIT =0;
    int INFOR_STATE_PHOMENO_BIT =1;
    int INFOR_STATE_COMMUNITY_BIT =2;
    int INFOR_STATE_CARPARK_BIT =3;
    int INFOR_STATE_CARNUMBE_BIT =4;


    //收支轨迹：1--押金收入，2--押金支出，3--余额收入，4--余额支出
    int FUNDS_TEPY_BASE_IN = 1;
    int FUNDS_TEPY_BASE_OUT = 2;
    int FUNDS_TEPY_BALANCE_IN = 3;
    int FUNDS_TEPY_BALANCE_OUT = 4;

    String SPLITFLAG ="@";
    //系统中的部分常量 且都已 S_ 开头
    //用户车牌数据最大值
    int S_CARNUMBERMAX = 2;
    //计算月结的天数
    int S_CALCULATEdAY = 30;
    //短信生命周期长度（30分钟）
    long S_SMSFILEMAX = 1800000;
    //订单间隔时间（30分钟）
    long S_ORDERINTERVAL = 1800000;
    //车牌临时出入权限时间（15分钟）
    long S_TEMPORARYOUT_TIME = 900000;
    //订单取消的最短时间（3小时）
    long S_ORDERCANCAL_TIME = 10800000;
    //计算常量
    long S_DATE_TIMES_DAY = 86400000;
    //计算常量
    long S_DATE_TIMES_HOUR = 3600000;
    //计算常量
    long S_DATE_TIMES_HOURHALF = 1800000;
    //计算常量
    long S_DATE_TIMES_MONTH = S_DATE_TIMES_DAY*S_CALCULATEdAY;
    //罚金计算单价
    double S_FINE = 10.0;





}
