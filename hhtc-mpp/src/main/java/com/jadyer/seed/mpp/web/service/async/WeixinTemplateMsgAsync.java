package com.jadyer.seed.mpp.web.service.async;


import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.constant.WxMsgEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.util.MoneyUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.MppUserInfoRepository;
import com.jadyer.seed.mpp.web.service.MppUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.jadyer.seed.comm.constant.Constants.*;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/2 16:04.
 */
@Async
@Component
public class WeixinTemplateMsgAsync {
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Value("${hhtc.portalUrl.center}")
    private String portalCenterUrl;
    @Resource
    private MppUserInfoRepository mppUserInfoRepository;


    /**
     * TOKGO 发送微信审核结果模板消息
     * */
    public void Send(String FirstData,String Key1Data,String Key2Data,String RemarkData,String openid,WxMsgEnum Template_id){
        MppUserInfo mppUserInfo = mppUserInfoRepository.findByType(1);
        String appid = mppUserInfo.getAppid();
        //TODO  测试时使用
        appid = "wx9777e4a1c1ee6ad8";
        try {
            WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
            dataItem.put("first", new WeixinTemplateMsg.DItem(FirstData));
            dataItem.put("keyword1", new WeixinTemplateMsg.DItem(Key1Data));
            dataItem.put("keyword2", new WeixinTemplateMsg.DItem(Key2Data));
            dataItem.put("remark", new WeixinTemplateMsg.DItem(RemarkData));
            WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
            templateMsg.setTemplate_id(Template_id.getID());
            String url = this.hhtcContextPath + this.portalCenterUrl;
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid
                    +"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+appid
                    +"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
            templateMsg.setUrl(url);
            templateMsg.setTouser(openid);
            templateMsg.setData(dataItem);
            WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(appid)
                    , templateMsg);
        }catch (Exception ex){
            new HHTCException(CodeEnum.SYSTEM_ERROR.getCode(),"微信模板消息错误");
        }
    }

    /**
     * TOKGO 发送审核结果消息
     * */
    public void SendAuditResult(FansInforAudit fansInforAudit,int status, String auditRemark){
        String FirstData = null, Key1Data = null, Key2Data, RemarkData ;
        if (fansInforAudit.getType() == Constants.AUDTI_TEPY_CARNUMBER){
            if(status == 1) {
                Send("尊敬的用户，您提交的车牌资料已通过物业审核。", fansInforAudit.getContent()
                        , new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                        , "登录吼吼停车微信公众号开始抢车位吧！", fansInforAudit.getOpenid(), WxMsgEnum.AUDIT_CARNUMBERPASS);
                return;
            }
        }
        RemarkData ="拒绝理由："+ auditRemark;
        Key1Data = "尾号"+fansInforAudit.getPhoneNo().substring(7,11);
        if(status == 1)
            Key2Data ="通过";
        else
            Key2Data ="未通过";
        //住房审核
        if (fansInforAudit.getType() == Constants.AUDTI_TEPY_COMMUNITY) {
            if (status == 1) {
                FirstData = "尊敬的用户，您提交的门牌资料已通过物业审核，确系本小区业主！";
                RemarkData ="吼吼停车为解决小区内业主停车难和地下停车场车位闲置的矛盾而生，所谓榨不出油钱的停车位不是好平台，感谢您的参与！";
            }
            else
                FirstData = "尊敬的用户，您提交的业主资料未通过物业审核。";
        }
        //车位审核
        else if (fansInforAudit.getType() == Constants.AUDTI_TEPY_CARPARK){
            if (status == 1) {
                FirstData = "尊敬的用户，您提交的车位资料（"+fansInforAudit.getContent().split(SPLITFLAG)[0]+"）已通过物业审核。";
                RemarkData ="分享停车位，赚钱您不累！您可将您的车位发布到车位列表中，动动手指就能赚钱！";
            }else {
                FirstData =  "尊敬的用户，您提交的车位资料（"+fansInforAudit.getContent().split(SPLITFLAG)[0]+"）未通过物业审核。";
            }
        }
        //车牌审核
        else{
            if (status != 1) {
                FirstData = "尊敬的用户，您提交的车牌资料（"+fansInforAudit.getContent()+"）未通过物业审核。";
            }
        }
        Send(FirstData,Key1Data,Key2Data,RemarkData,fansInforAudit.getOpenid(), WxMsgEnum.AUDIT_COMMON);
    }


    /**
     * 车主驶离时发送的模板消息
     */
    public void sendForCarownerOut(OrderInout inout, OrderInfo order){
        /*
        {{first.DATA}}
        车牌号：{{keyword1.DATA}}
        时间：{{keyword2.DATA}}
        {{remark.DATA}}

        您的车辆已经离开停车场
        车牌号：闽D12345
        时间：2017-07-10 18:00:00
        谢谢您的使用
        */
        WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
        dataItem.put("first", new WeixinTemplateMsg.DItem("您的车辆已经离开停车场"));
        dataItem.put("keyword1", new WeixinTemplateMsg.DItem(inout.getCarNumber()));
        dataItem.put("keyword2", new WeixinTemplateMsg.DItem(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")));
        dataItem.put("remark", new WeixinTemplateMsg.DItem("谢谢您的使用"));
        String url = this.hhtcContextPath + this.portalCenterUrl;
        url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+order.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+order.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
        WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
        templateMsg.setTemplate_id("RnM14iBa286CUU9_IrkzcDyGwoGq58jko9C4HGDVCYE");
        templateMsg.setUrl(url);
        templateMsg.setTouser(inout.getOpenid());
        templateMsg.setData(dataItem);
        WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(order.getAppid()), templateMsg);
    }





}