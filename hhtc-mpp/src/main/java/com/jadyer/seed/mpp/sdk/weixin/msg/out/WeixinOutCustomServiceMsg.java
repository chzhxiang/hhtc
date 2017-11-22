package com.jadyer.seed.mpp.sdk.weixin.msg.out;

import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg;

public class WeixinOutCustomServiceMsg extends WeixinOutMsg {
    public WeixinOutCustomServiceMsg(WeixinInMsg inMsg) {
        super(inMsg);
        this.msgType = "transfer_customer_service";
    }
}