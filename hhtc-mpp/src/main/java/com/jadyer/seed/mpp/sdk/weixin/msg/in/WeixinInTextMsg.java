package com.jadyer.seed.mpp.sdk.weixin.msg.in;

public class WeixinInTextMsg extends WeixinInMsg {
    private String content;
    private String msgId;

    public WeixinInTextMsg(String toUserName, String fromUserName, long createTime, String msgType) {
        super(toUserName, fromUserName, createTime, msgType);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}