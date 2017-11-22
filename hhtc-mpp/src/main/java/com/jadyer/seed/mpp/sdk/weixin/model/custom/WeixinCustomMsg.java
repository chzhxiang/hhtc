package com.jadyer.seed.mpp.sdk.weixin.model.custom;

public abstract class WeixinCustomMsg {
    private String touser;

    public WeixinCustomMsg(String touser) {
        this.touser = touser;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }
}