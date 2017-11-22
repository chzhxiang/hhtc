package com.jadyer.seed.mpp.sdk.weixin.controller;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinConstants;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInImageMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInLinkMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInLocationMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInTextMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInCustomServiceEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInFollowEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInLocationEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInMenuEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInQrcodeEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInTemplateEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutImageMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutTextMsg;

abstract class WeixinMsgControllerAdapter extends WeixinMsgController {
    @Override
    protected abstract WeixinOutMsg processInMenuEventMsg(WeixinInMenuEventMsg inMenuEventMsg);

    @Override
    protected WeixinOutMsg processInTextMsg(WeixinInTextMsg inTextMsg) {
        return new WeixinOutTextMsg(inTextMsg).setContent(inTextMsg.getContent());
    }

    @Override
    protected WeixinOutMsg processInImageMsg(WeixinInImageMsg inImageMsg) {
        return new WeixinOutImageMsg(inImageMsg).setMediaId(inImageMsg.getMediaId());
    }

    @Override
    protected WeixinOutMsg processInLocationMsg(WeixinInLocationMsg inLocationMsg) {
        return new WeixinOutTextMsg(inLocationMsg).setContent(inLocationMsg.getLabel());
    }

    @Override
    protected WeixinOutMsg processInLinkMsg(WeixinInLinkMsg inLinkMsg) {
        return new WeixinOutTextMsg(inLinkMsg).setContent("您的链接为<a href=\""+inLinkMsg.getUrl()+"\">"+inLinkMsg.getTitle()+"</a>");
    }

    @Override
    protected WeixinOutMsg processInFollowEventMsg(WeixinInFollowEventMsg inFollowEventMsg){
        if(WeixinInFollowEventMsg.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEventMsg.getEvent())){
            return new WeixinOutTextMsg(inFollowEventMsg).setContent("感谢您的关注");
        }
        if(WeixinInFollowEventMsg.EVENT_INFOLLOW_UNSUBSCRIBE.equals(inFollowEventMsg.getEvent())){
            LogUtil.getLogger().info("您的粉丝{}取消关注了您", inFollowEventMsg.getFromUserName());
        }
        return new WeixinOutTextMsg(inFollowEventMsg).setContent(WeixinConstants.NOT_NEED_REPLY_FLAG);
    }

    @Override
    protected WeixinOutMsg processInQrcodeEventMsg(WeixinInQrcodeEventMsg inQrcodeEventMsg) {
        if(WeixinInQrcodeEventMsg.EVENT_INQRCODE_SUBSCRIBE.equals(inQrcodeEventMsg.getEvent())){
            return new WeixinOutTextMsg(inQrcodeEventMsg).setContent("感谢您的扫码并关注[" + inQrcodeEventMsg.getEventKey().substring(8) + "]");
        }
        if(WeixinInQrcodeEventMsg.EVENT_INQRCODE_SCAN.equals(inQrcodeEventMsg.getEvent())){
            return new WeixinOutTextMsg(inQrcodeEventMsg).setContent("感谢您的扫码[" + inQrcodeEventMsg.getEventKey() + "]");
        }
        return new WeixinOutTextMsg(inQrcodeEventMsg).setContent(WeixinConstants.NOT_NEED_REPLY_FLAG);
    }

    @Override
    protected WeixinOutMsg processInCustomServiceEventMsg(WeixinInCustomServiceEventMsg inCustomServiceEventMsg){
        if(WeixinInCustomServiceEventMsg.EVENT_INCUSTOMSERVICE_KF_CREATE_SESSION.equals(inCustomServiceEventMsg.getEvent())){
            LogUtil.getLogger().info("客服{}接入了会话", inCustomServiceEventMsg.getKfAccount());
            return new WeixinOutTextMsg(inCustomServiceEventMsg).setContent("客服" + inCustomServiceEventMsg.getKfAccount() + "接入了会话");
        }
        if(WeixinInCustomServiceEventMsg.EVENT_INCUSTOMSERVICE_KF_CLOSE_SESSION.equals(inCustomServiceEventMsg.getEvent())){
            LogUtil.getLogger().info("客服{}关闭了会话", inCustomServiceEventMsg.getKfAccount());
            return new WeixinOutTextMsg(inCustomServiceEventMsg).setContent("客服" + inCustomServiceEventMsg.getKfAccount() + "关闭了会话");
        }
        if(WeixinInCustomServiceEventMsg.EVENT_INCUSTOMSERVICE_KF_SWITCH_SESSION.equals(inCustomServiceEventMsg.getEvent())){
            LogUtil.getLogger().info("客服{}将会话转接给了客服{}", inCustomServiceEventMsg.getKfAccount(), inCustomServiceEventMsg.getToKfAccount());
            return new WeixinOutTextMsg(inCustomServiceEventMsg).setContent("客服" + inCustomServiceEventMsg.getKfAccount() + "将会话转接给了客服" + inCustomServiceEventMsg.getToKfAccount());
        }
        return new WeixinOutTextMsg(inCustomServiceEventMsg).setContent(WeixinConstants.NOT_NEED_REPLY_FLAG);
    }

    @Override
    protected WeixinOutMsg processInTemplateEventMsg(WeixinInTemplateEventMsg inTemplateEventMsg) {
        if(WeixinInTemplateEventMsg.EVENT_INTEMPLATE_TEMPLATEFANMSGREAD.equals(inTemplateEventMsg.getEvent())){
            LogUtil.getLogger().info("模板消息msgid={}阅读成功", inTemplateEventMsg.getMsgID());
        }
        if(WeixinInTemplateEventMsg.EVENT_INTEMPLATE_TEMPLATESENDJOBFINISH.equals(inTemplateEventMsg.getEvent())){
            if(WeixinInTemplateEventMsg.EVENT_INTEMPLATE_STATUS_SUCCESS.equals(inTemplateEventMsg.getStatus())){
                LogUtil.getLogger().info("模板消息msgid={}送达成功", inTemplateEventMsg.getMsgID());
            }
            if(WeixinInTemplateEventMsg.EVENT_INTEMPLATE_STATUS_BLOCK.equals(inTemplateEventMsg.getStatus())){
                LogUtil.getLogger().info("模板消息msgid={}由于{}而送达失败", inTemplateEventMsg.getMsgID(), "用户拒收（用户设置拒绝接收公众号消息）");
            }
            if(WeixinInTemplateEventMsg.EVENT_INTEMPLATE_STATUS_FAILED.equals(inTemplateEventMsg.getStatus())){
                LogUtil.getLogger().info("模板消息msgid={}由于{}而送达失败", inTemplateEventMsg.getMsgID(), "其它原因");
            }
        }
        return new WeixinOutTextMsg(inTemplateEventMsg).setContent(WeixinConstants.NOT_NEED_REPLY_FLAG);
    }

    @Override
    protected WeixinOutMsg processInLocationEventMsg(WeixinInLocationEventMsg inLocationEventMsg) {
        LogUtil.getLogger().info("收到粉絲=[{}]上報的地理位置纬度=[{}]，经度=[{}]，精度=[{}]", inLocationEventMsg.getFromUserName(), inLocationEventMsg.getLatitude(), inLocationEventMsg.getLongitude(), inLocationEventMsg.getPrecision());
        return new WeixinOutTextMsg(inLocationEventMsg).setContent(WeixinConstants.NOT_NEED_REPLY_FLAG);
    }
}