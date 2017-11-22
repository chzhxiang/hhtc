package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

public class WeixinInMenuEventMsg extends WeixinInEventMsg {
    public static final String EVENT_INMENU_CLICK = "CLICK";
    public static final String EVENT_INMENU_VIEW = "VIEW";

    private String eventKey;

    public WeixinInMenuEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }
}