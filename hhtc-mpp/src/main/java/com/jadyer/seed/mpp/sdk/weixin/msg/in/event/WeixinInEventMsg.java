package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg;

public abstract class WeixinInEventMsg extends WeixinInMsg {
    protected String event;

    public WeixinInEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType);
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}