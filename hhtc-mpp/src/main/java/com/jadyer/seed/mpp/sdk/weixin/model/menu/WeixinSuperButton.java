package com.jadyer.seed.mpp.sdk.weixin.model.menu;

public class WeixinSuperButton extends WeixinButton {
    private WeixinButton[] sub_button;

    public WeixinSuperButton(String name, WeixinButton[] sub_button) {
        super(name);
        this.sub_button = sub_button;
    }

    public WeixinButton[] getSub_button() {
        return sub_button;
    }

    public void setSub_button(WeixinButton[] sub_button) {
        this.sub_button = sub_button;
    }
}