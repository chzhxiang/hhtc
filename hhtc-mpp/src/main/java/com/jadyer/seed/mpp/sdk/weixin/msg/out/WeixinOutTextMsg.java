package com.jadyer.seed.mpp.sdk.weixin.msg.out;

import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg;

public class WeixinOutTextMsg extends WeixinOutMsg {
    private String content;

    public WeixinOutTextMsg(WeixinInMsg inMsg) {
        super(inMsg);
        this.msgType = "text";
    }

    public String getContent() {
        return content;
    }

    public WeixinOutTextMsg setContent(String content) {
        this.content = content;
        return this;
    }
}