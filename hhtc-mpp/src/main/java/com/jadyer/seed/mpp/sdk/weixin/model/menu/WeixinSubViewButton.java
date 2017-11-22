package com.jadyer.seed.mpp.sdk.weixin.model.menu;

public class WeixinSubViewButton extends WeixinButton {
    private String type;
    private String url;

    public WeixinSubViewButton(String name, String url) {
        super(name);
        this.url = url;
        this.type = "view";
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}