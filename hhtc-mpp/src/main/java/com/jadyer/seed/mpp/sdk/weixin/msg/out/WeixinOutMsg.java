package com.jadyer.seed.mpp.sdk.weixin.msg.out;

import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg;

public class WeixinOutMsg {
    protected String toUserName;
    protected String fromUserName;
    protected long createTime;
    protected String msgType;

    public WeixinOutMsg() {}

    public WeixinOutMsg(WeixinInMsg inMsg) {
        this.toUserName = inMsg.getFromUserName();
        this.fromUserName = inMsg.getToUserName();
        this.createTime = (long)(System.currentTimeMillis()/1000);
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}