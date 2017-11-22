package com.jadyer.seed.mpp.sdk.weixin.msg.out;

import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg;

public class WeixinOutImageMsg extends WeixinOutMsg {
    private String mediaId;

    public WeixinOutImageMsg(WeixinInMsg inMsg) {
        super(inMsg);
        this.msgType = "image";
    }

    public String getMediaId() {
        return mediaId;
    }

    public WeixinOutImageMsg setMediaId(String mediaId) {
        this.mediaId = mediaId;
        return this;
    }
}