package com.jadyer.seed.mpp.sdk.weixin.controller;

import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInImageMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInLinkMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInLocationMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInTextMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInMenuEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutCustomServiceMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutMsg;

public abstract class WeixinMsgControllerCustomServiceAdapter extends WeixinMsgControllerAdapter {
    @Override
    protected abstract WeixinOutMsg processInMenuEventMsg(WeixinInMenuEventMsg inMenuEventMsg);

    @Override
    protected WeixinOutMsg processInTextMsg(WeixinInTextMsg inTextMsg) {
        return new WeixinOutCustomServiceMsg(inTextMsg);
    }

    @Override
    protected WeixinOutMsg processInImageMsg(WeixinInImageMsg inImageMsg) {
        return new WeixinOutCustomServiceMsg(inImageMsg);
    }

    @Override
    protected WeixinOutMsg processInLocationMsg(WeixinInLocationMsg inLocationMsg) {
        return new WeixinOutCustomServiceMsg(inLocationMsg);
    }

    @Override
    protected WeixinOutMsg processInLinkMsg(WeixinInLinkMsg inLinkMsg) {
        return new WeixinOutCustomServiceMsg(inLinkMsg);
    }
}