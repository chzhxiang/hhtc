package com.jadyer.seed.mpp.web.service.async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.OrderInfo;
import com.jadyer.seed.mpp.web.model.OrderInout;
import com.jadyer.seed.mpp.web.repository.OrderInoutRepository;
import com.jadyer.seed.mpp.web.service.GoodsService;
import com.jadyer.seed.mpp.web.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/24 17:40.
 */
//@Component
public class RocketmqMessageListener implements MessageListener {
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Value("${hhtc.portalUrl.center}")
    private String portalCenterUrl;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private OrderService orderService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderInoutRepository orderInoutRepository;

    /**
     * intime=2017-08-01 11:25:00
     * cph=渝BEJ623
     * type=1
     * cardno=1222
     */
    @Override
    public Action consume(Message message, ConsumeContext context) {
        LogUtil.getMQLogger().info("----------------------------------------------------------------------------------------");
        LogUtil.getMQLogger().info("收到消息-->" + ReflectionToStringBuilder.toString(message, ToStringStyle.MULTI_LINE_STYLE));
        String cardNo = "";
        try{
            String body = new String(message.getBody(), "GBK");
            Map<String, String> map = JSON.parseObject(body, new TypeReference<Map<String, String>>(){});
            LogUtil.getMQLogger().info("收到消息-->解码后" + JadyerUtil.buildStringFromMap(map));
            cardNo = map.get("cardno");
            OrderInout inout = orderInoutRepository.findByCardNo(cardNo);
            if(null == inout){
                LogUtil.getMQLogger().info("停车卡号["+cardNo+"]对应的订单记录不存在");
                return Action.CommitMessage;
            }
            //暂以车牌后六位比较（车牌识别仪暂时识别不出来第一个汉字）
            if(!map.get("cph").endsWith(inout.getCarNumber().substring(inout.getCarNumber().length()-6))){
                LogUtil.getMQLogger().info("停车卡号["+cardNo+"]通知的车牌号["+map.get("cph")+"]与数据库中的["+inout.getCarNumber()+"]不符");
                return Action.CommitMessage;
            }
            //入场
            if("1".equals(map.get("type"))){
                if(StringUtils.equals(map.get("intime"), DateFormatUtils.format(inout.getInTime(), "yyyy-MM-dd HH:mm:ss"))){
                    LogUtil.getMQLogger().info("停车卡号["+cardNo+"]的入场消息已通知过了，自动忽略");
                    return Action.CommitMessage;
                }
                Date inTime = DateUtils.parseDate(map.get("intime"), "yyyy-MM-dd HH:mm:ss");
                if(inTime.compareTo(hhtcHelper.convertToDate(inout.getFromDate(), inout.getFromTime())) == -1){
                    LogUtil.getMQLogger().info("停车卡号["+cardNo+"]的入场时间早于实际允许的最早入场时间");
                    return Action.CommitMessage;
                }
                inout.setInTime(inTime);
                inout.setOutTime(null);
            }
            //出场
            if("2".equals(map.get("type"))){
                if(StringUtils.equals(map.get("outtime"), DateFormatUtils.format(inout.getOutTime(), "yyyy-MM-dd HH:mm:ss"))){
                    LogUtil.getMQLogger().info("停车卡号["+cardNo+"]的出场消息已通知过了，自动忽略");
                    return Action.CommitMessage;
                }
                //订单生命周期已结束
//                OrderInfo order = orderService.getByOrderNo(inout.getOrderNo());
                OrderInfo order =null;
                if(inout.getCardNo().endsWith(JadyerUtil.leftPadUseZero(inout.getMaxIndex()+"", 3))){
                    order.setOrderStatus(99);
                    orderService.upsert(order);
                }
                inout.setOutTime(DateUtils.parseDate(map.get("outtime"), "yyyy-MM-dd HH:mm:ss"));
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
                dataItem.put("keyword1", new WeixinTemplateMsg.DItem(map.get("cph")));
                dataItem.put("keyword2", new WeixinTemplateMsg.DItem(map.get("outtime")));
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
            //inout.setInoutMsgid(message.getKey());
            orderInoutRepository.saveAndFlush(inout);
            return Action.CommitMessage;
        }catch(Exception e){
            LogUtil.getMQLogger().error("停车卡号["+cardNo+"]的通知消息消费失败，堆栈轨迹如下", e);
            LogUtil.logToTask().error("停车卡号["+cardNo+"]的通知消息消费失败，堆栈轨迹如下", e);
            return Action.ReconsumeLater;
        }
    }
}
