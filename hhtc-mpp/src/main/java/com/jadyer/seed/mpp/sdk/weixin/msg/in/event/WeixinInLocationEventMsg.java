package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

public class WeixinInLocationEventMsg extends WeixinInEventMsg {
    public static final String EVENT_INLOCATION_LOCATION = "LOCATION";
    private String latitude;
    private String longitude;
    private String precision;

    public WeixinInLocationEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }
}
