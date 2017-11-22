package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

public class WeixinInCustomServiceEventMsg extends WeixinInEventMsg {
    public static final String EVENT_INCUSTOMSERVICE_KF_CREATE_SESSION = "kf_create_session";
    public static final String EVENT_INCUSTOMSERVICE_KF_CLOSE_SESSION = "kf_close_session";
    public static final String EVENT_INCUSTOMSERVICE_KF_SWITCH_SESSION = "kf_switch_session";

    private String kfAccount;
    private String toKfAccount;

    public WeixinInCustomServiceEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }

    public String getKfAccount() {
        return kfAccount;
    }

    public void setKfAccount(String kfAccount) {
        this.kfAccount = kfAccount;
    }

    public String getToKfAccount() {
        return toKfAccount;
    }

    public void setToKfAccount(String toKfAccount) {
        this.toKfAccount = toKfAccount;
    }
}