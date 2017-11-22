package com.jadyer.seed.mpp.sdk.weixin.model.redpack;

public class WeixinRedpackGethbinfoReqData extends WeixinRedpackReqData {
    private String appid;
    private String mch_billno;
    private String bill_type;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_billno() {
        return mch_billno;
    }

    public void setMch_billno(String mch_billno) {
        this.mch_billno = mch_billno;
    }

    public String getBill_type() {
        return bill_type;
    }

    public void setBill_type(String bill_type) {
        this.bill_type = bill_type;
    }
}