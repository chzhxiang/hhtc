package com.jadyer.seed.mpp.sdk.weixin.model.menu;

public class WeixinMenu {
    private WeixinButton[] button;

    public WeixinMenu(WeixinButton[] button) {
        this.button = button;
    }

    public WeixinButton[] getButton() {
        return button;
    }

    public void setButton(WeixinButton[] button) {
        this.button = button;
    }
}