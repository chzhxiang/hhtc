package com.jadyer.seed.mpp.sdk.weixin.model.custom;

public class WeixinCustomTextMsg extends WeixinCustomMsg {
    private String msgtype;
    private Text text;

    public WeixinCustomTextMsg(String touser, Text text) {
        super(touser);
        this.text = text;
        this.msgtype = "text";
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public static class Text{
        private String content;
        public Text(String content) {
            this.content = content;
        }
        public String getContent() {
            return content;
        }
    }
}