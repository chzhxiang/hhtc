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

    //0--是否授权，1--是否验证电话，2--是否验证住址
    int INFOR_STATE_ACCREDIT_BIT =0;
    int INFOR_STATE_PHOMENO_BIT =1;
    int INFOR_STATE_COMMUNITY_BIT =2;
    int INFOR_STATE_CARPARK_BIT =3;
    int INFOR_STATE_CARNUMBE_BIT =4;

    //0--预约，1--进行中，2--待支付
    int ORDER_STATE_RESERVATION =0;
    int ORDER_STATE_ING =1;
    int ORDER_STATE_PAY =2;

    String SPLITFLAG ="@";
    //系统中的部分常量 且都已 S_ 开头
    //用户车牌数据最大值
    int S_CARNUMBERMAX = 2;
    //短信生命周期长度（30分钟）
    long S_SMSFILEMAX = 1800000;

    boolean ISWEIXIN = false;


}
