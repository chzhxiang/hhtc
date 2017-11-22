package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

public class WeixinInFollowEventMsg extends WeixinInEventMsg {
    public static final String EVENT_INFOLLOW_SUBSCRIBE = "subscribe";
    public static final String EVENT_INFOLLOW_UNSUBSCRIBE = "unsubscribe";

    public WeixinInFollowEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }
}