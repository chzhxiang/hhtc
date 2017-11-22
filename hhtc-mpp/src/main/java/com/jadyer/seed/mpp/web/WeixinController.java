package com.jadyer.seed.mpp.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.util.HttpUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinConstants;
import com.jadyer.seed.mpp.sdk.weixin.controller.WeixinMsgControllerCustomServiceAdapter;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInTextMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInFollowEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInMenuEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutCustomServiceMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutTextMsg;
import com.jadyer.seed.mpp.web.model.MppReplyInfo;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.FansService;
import com.jadyer.seed.mpp.web.service.MppReplyService;
import com.jadyer.seed.mpp.web.service.MppUserService;
import com.jadyer.seed.mpp.web.service.async.FansSaveAsync;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping(value="/weixin")
public class WeixinController extends WeixinMsgControllerCustomServiceAdapter {
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Resource
    private FansService fansService;
    @Resource
    private FansSaveAsync fansSaveAsync;
    @Resource
    private MppUserService mppUserService;
    @Resource
    private MppReplyService mppReplyService;

    @Override
    protected WeixinOutMsg processInTextMsg(WeixinInTextMsg inTextMsg) {
        MppUserInfo mppUserInfo = mppUserService.findByWxid(inTextMsg.getToUserName());
        if(null == mppUserInfo){
            return new WeixinOutTextMsg(inTextMsg).setContent("该公众号未绑定");
        }
        if(0== mppUserInfo.getBindStatus() && !Constants.MPP_BIND_TEXT.equals(inTextMsg.getContent())){
            return new WeixinOutTextMsg(inTextMsg).setContent("账户未绑定\r请发送\"" + Constants.MPP_BIND_TEXT + "\"绑定");
        }
        if(0== mppUserInfo.getBindStatus() && Constants.MPP_BIND_TEXT.equals(inTextMsg.getContent())){
            mppUserInfo.setBindStatus(1);
            mppUserInfo.setBindTime(new Date());
            mppUserService.upsert(mppUserInfo);
            return new WeixinOutTextMsg(inTextMsg).setContent("绑定完毕，升级成功！");
        }
        if(Constants.SECURITY_SUSPEND_MSG.replace("x", "0").equals(inTextMsg.getContent())){
            LogUtil.setSuspend("0");
            return new WeixinOutTextMsg(inTextMsg).setContent("服务已挂起");
        }
        if(Constants.SECURITY_SUSPEND_MSG.replace("x", "1").equals(inTextMsg.getContent())){
            LogUtil.setSuspend("1");
            return new WeixinOutTextMsg(inTextMsg).setContent("服务已恢复");
        }
        String timeFlag = DateFormatUtils.format(new Date(), "HHmm").substring(0, 3) + "0";
        if(Constants.SECURITY_SHUTDOWN_MSG.replace("MMd0", timeFlag).equals(inTextMsg.getContent())){
            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
            String jsonData = HttpUtil.post(hhtcContextPath + "/shutdown", null, null);
            Map<String, String> map = JSON.parseObject(jsonData, new TypeReference<Map<String, String>>(){});
            return new WeixinOutTextMsg(inTextMsg).setContent(map.get("message"));
        }
        MppReplyInfo mppReplyInfo = mppReplyService.getByUidAndKeyword(mppUserInfo.getId(), inTextMsg.getContent());
        if(0 == mppReplyInfo.getType()){
            return new WeixinOutTextMsg(inTextMsg).setContent(mppReplyInfo.getContent());
        }
        mppReplyInfo = mppReplyService.getByUidAndCategory(mppUserInfo.getId(), 0);
        if(4 == mppReplyInfo.getType()){
            return new WeixinOutCustomServiceMsg(inTextMsg);
        }
        return new WeixinOutTextMsg(inTextMsg).setContent(inTextMsg.getContent());
    }


    @Override
    protected WeixinOutMsg processInMenuEventMsg(WeixinInMenuEventMsg inMenuEventMsg) {
        MppUserInfo mppUserInfo = mppUserService.findByWxid(inMenuEventMsg.getToUserName());
        if(null == mppUserInfo){
            return new WeixinOutTextMsg(inMenuEventMsg).setContent("该公众号未绑定");
        }
        if(WeixinInMenuEventMsg.EVENT_INMENU_CLICK.equals(inMenuEventMsg.getEvent())){
            MppReplyInfo mppReplyInfo = mppReplyService.getByUidAndKeyword(mppUserInfo.getId(), inMenuEventMsg.getEventKey());
            if(StringUtils.isBlank(mppReplyInfo.getKeyword())){
                return new WeixinOutCustomServiceMsg(inMenuEventMsg);
            }else{
                return new WeixinOutTextMsg(inMenuEventMsg).setContent(mppReplyInfo.getContent());
            }
        }
        return new WeixinOutTextMsg(inMenuEventMsg).setContent(WeixinConstants.NOT_NEED_REPLY_FLAG);
    }


    @Override
    protected WeixinOutMsg processInFollowEventMsg(WeixinInFollowEventMsg inFollowEventMsg) {
        MppUserInfo mppUserInfo = mppUserService.findByWxid(inFollowEventMsg.getToUserName());
        if(null == mppUserInfo){
            return new WeixinOutTextMsg(inFollowEventMsg).setContent("该公众号未绑定");
        }
        if(WeixinInFollowEventMsg.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEventMsg.getEvent())){
            fansSaveAsync.save(mppUserInfo, inFollowEventMsg.getFromUserName());
            MppReplyInfo mppReplyInfo = mppReplyService.getByUidAndCategory(mppUserInfo.getId(), 1);
            if(StringUtils.isBlank(mppReplyInfo.getContent())){
                return new WeixinOutTextMsg(inFollowEventMsg).setContent("感谢您的关注");
            }else{
                return new WeixinOutTextMsg(inFollowEventMsg).setContent(mppReplyInfo.getContent());
            }
        }
        if(WeixinInFollowEventMsg.EVENT_INFOLLOW_UNSUBSCRIBE.equals(inFollowEventMsg.getEvent())){
            ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if(null != attributes){
                HttpSession session = attributes.getRequest().getSession();
                session.removeAttribute(Constants.WEB_SESSION_WX_OPENID);
            }
            fansService.unSubscribe(mppUserInfo.getId(), inFollowEventMsg.getFromUserName());
            LogUtil.getLogger().info("您的粉丝" + inFollowEventMsg.getFromUserName() + "取消关注了您");
        }
        return new WeixinOutTextMsg(inFollowEventMsg).setContent("您的粉丝" + inFollowEventMsg.getFromUserName() + "取消关注了您");
    }
}