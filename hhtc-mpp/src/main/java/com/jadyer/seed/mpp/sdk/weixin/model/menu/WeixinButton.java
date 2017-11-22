package com.jadyer.seed.mpp.sdk.weixin.model.menu;

public abstract class WeixinButton {
    private String name;

    public WeixinButton(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}