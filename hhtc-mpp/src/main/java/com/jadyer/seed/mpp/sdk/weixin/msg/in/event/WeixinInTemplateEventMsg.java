package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

public class WeixinInTemplateEventMsg extends WeixinInEventMsg {
    public static final String EVENT_INTEMPLATE_TEMPLATESENDJOBFINISH = "TEMPLATESENDJOBFINISH";
    public static final String EVENT_INTEMPLATE_TEMPLATEFANMSGREAD = "TEMPLATEFANMSGREAD";
    public static final String EVENT_INTEMPLATE_STATUS_SUCCESS = "success";
    public static final String EVENT_INTEMPLATE_STATUS_BLOCK = "failed:user block";
    public static final String EVENT_INTEMPLATE_STATUS_FAILED = "failed: system failed";
    private String msgID;
    private String status;

    public WeixinInTemplateEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
