package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

public class WeixinInQrcodeEventMsg extends WeixinInEventMsg {
    public static final String EVENT_INQRCODE_SUBSCRIBE = "subscribe";
    public static final String EVENT_INQRCODE_SCAN = "SCAN";
    private String eventKey;
    private String ticket;

    public WeixinInQrcodeEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}