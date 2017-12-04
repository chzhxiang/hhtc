package com.jadyer.seed.mpp.web.service.async;


import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.WxMsgEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.model.OrderInfo;
import com.jadyer.seed.mpp.web.model.OrderInout;
import com.jadyer.seed.mpp.web.repository.MppUserInfoRepository;
import com.jadyer.seed.mpp.web.service.MppUserService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

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