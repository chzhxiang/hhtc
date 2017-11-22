package com.jadyer.seed.mpp.sdk.weixin.controller;

import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.msg.WeixinInMsgParser;
import com.jadyer.seed.mpp.sdk.weixin.msg.WeixinOutMsgXmlBuilder;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInImageMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInLinkMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInLocationMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInTextMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInCustomServiceEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInFollowEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInLocationEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInMenuEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInQrcodeEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInTemplateEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutMsg;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public abstract class WeixinMsgController {
    @RequestMapping(value="/{uuid}")
    public void index(@PathVariable String uuid, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(Constants.MPP_CHARSET_UTF8);
        PrintWriter out = response.getWriter();
        String reqBodyMsg = JadyerUtil.extractHttpServletRequestBodyMessage(request);
        LogUtil.getLogger().info("收到微信服务器消息如下\n{}", JadyerUtil.extractHttpServletRequestHeaderMessage(request)+"\n"+reqBodyMsg);
        if(!this.verifySignature(DigestUtils.md5Hex(uuid+"http://jadyer.cn/"), request)){
            out.write("verify signature failed");
            out.flush();
            out.close();
            return;
        }
        if("GET".equalsIgnoreCase(request.getMethod())){
            out.write(request.getParameter("echostr"));
            out.flush();
            out.close();
            return;
        }
        WeixinInMsg inMsg = WeixinInMsgParser.parse(reqBodyMsg);
        WeixinOutMsg outMsg = new WeixinOutMsg();
        if(inMsg instanceof WeixinInTextMsg){
            outMsg = this.processInTextMsg((WeixinInTextMsg)inMsg);
        }
        if(inMsg instanceof WeixinInImageMsg){
            outMsg = this.processInImageMsg((WeixinInImageMsg)inMsg);
        }
        if(inMsg instanceof WeixinInLocationMsg){
            outMsg = this.processInLocationMsg((WeixinInLocationMsg)inMsg);
        }
        if(inMsg instanceof WeixinInLinkMsg){
            outMsg = this.processInLinkMsg((WeixinInLinkMsg)inMsg);
        }
        if(inMsg instanceof WeixinInFollowEventMsg){
            outMsg = this.processInFollowEventMsg((WeixinInFollowEventMsg)inMsg);
        }
        if(inMsg instanceof WeixinInQrcodeEventMsg){
            outMsg = this.processInQrcodeEventMsg((WeixinInQrcodeEventMsg)inMsg);
        }
        if(inMsg instanceof WeixinInMenuEventMsg){
            outMsg = this.processInMenuEventMsg((WeixinInMenuEventMsg)inMsg);
        }
        if(inMsg instanceof WeixinInCustomServiceEventMsg){
            outMsg = this.processInCustomServiceEventMsg((WeixinInCustomServiceEventMsg)inMsg);
        }
        if(inMsg instanceof WeixinInTemplateEventMsg){
            outMsg = this.processInTemplateEventMsg((WeixinInTemplateEventMsg)inMsg);
        }
        if(inMsg instanceof WeixinInLocationEventMsg){
            outMsg = this.processInLocationEventMsg((WeixinInLocationEventMsg)inMsg);
        }
        String outMsgXml = WeixinOutMsgXmlBuilder.build(outMsg);
        out.write(outMsgXml);
        out.flush();
        out.close();
        LogUtil.getLogger().info("应答微信服务器消息-->{}", outMsgXml);
    }


    private boolean verifySignature(String token, HttpServletRequest request){
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        if(StringUtils.isBlank(signature) || StringUtils.isBlank(timestamp) || StringUtils.isBlank(nonce)){
            return false;
        }
        String[] signPlains = new String[]{token, nonce, timestamp};
        Arrays.sort(signPlains);
        return signature.equals(DigestUtils.sha1Hex(signPlains[0] + signPlains[1] + signPlains[2]));
    }


    protected abstract WeixinOutMsg processInTextMsg(WeixinInTextMsg inTextMsg);
    protected abstract WeixinOutMsg processInImageMsg(WeixinInImageMsg inImageMsg);
    protected abstract WeixinOutMsg processInLocationMsg(WeixinInLocationMsg inLocationMsg);
    protected abstract WeixinOutMsg processInLinkMsg(WeixinInLinkMsg inLinkMsg);
    protected abstract WeixinOutMsg processInFollowEventMsg(WeixinInFollowEventMsg inFollowEventMsg);
    protected abstract WeixinOutMsg processInQrcodeEventMsg(WeixinInQrcodeEventMsg inQrcodeEventMsg);
    protected abstract WeixinOutMsg processInMenuEventMsg(WeixinInMenuEventMsg inMenuEventMsg);
    protected abstract WeixinOutMsg processInCustomServiceEventMsg(WeixinInCustomServiceEventMsg inCustomServiceEventMsg);
    protected abstract WeixinOutMsg processInTemplateEventMsg(WeixinInTemplateEventMsg inTemplateEventMsg);
    protected abstract WeixinOutMsg processInLocationEventMsg(WeixinInLocationEventMsg inLocationEventMsg);
}