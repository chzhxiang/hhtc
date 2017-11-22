package com.jadyer.seed.mpp.web.service.async;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.web.HHTCHelper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Properties;

/**
 * https://help.aliyun.com/document_detail/29551.html
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/21 10:28.
 */
//@Async
//@Component
public class RocketmqAsync {
    private String onsAddr = "http://onsaddr-internet.aliyun.com/rocketmq/nsaddr4client-internet";
    private String accessKey = "LTAIJtuJQmRJEOe2";
    private String secretKey = "lAJsuyVpuCOT0o2ex31j9j2RfgKEvA";
    private OrderProducer orderProducer;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private RocketmqMessageListener rocketmqMessageListener;

    public OrderProducer getOrderProducer() {
        return orderProducer;
    }

    @PostConstruct
    public void initProducer(){
        try {
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.ONSAddr, this.onsAddr);
            properties.put(PropertyKeyConst.AccessKey, this.accessKey);
            properties.put(PropertyKeyConst.SecretKey, this.secretKey);
            properties.put(PropertyKeyConst.ProducerId, "PID_hhtc01");
            orderProducer = ONSFactory.createOrderProducer(properties);
            orderProducer.start();
            LogUtil.getMQLogger().info("Rocketmq Producer Started......");
        } catch (Exception e) {
            LogUtil.getMQLogger().error("致命异常：初始化Rocketmq--Producer时发生异常，堆栈轨迹如下", e);
            LogUtil.logToTask().error("致命异常：初始化Rocketmq--Producer时发生异常，堆栈轨迹如下", e);
            System.exit(1);
        }
    }


    @PostConstruct
    public void initConsumer(){
        try {
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.ONSAddr, this.onsAddr);
            properties.put(PropertyKeyConst.AccessKey, this.accessKey);
            properties.put(PropertyKeyConst.SecretKey, this.secretKey);
            properties.put(PropertyKeyConst.ConsumerId, "CID_hhtc_inout");
            Consumer consumer = ONSFactory.createConsumer(properties);
            consumer.subscribe("hhtc_topic", "inout", rocketmqMessageListener);
            consumer.start();
            LogUtil.getMQLogger().info("Rocketmq Consumer Started......");
        } catch (Exception e) {
            LogUtil.getMQLogger().error("致命异常：初始化Rocketmq--Consumer时发生异常，堆栈轨迹如下", e);
            LogUtil.logToTask().error("致命异常：初始化Rocketmq--Consumer时发生异常，堆栈轨迹如下", e);
            System.exit(1);
        }
    }


    public void send(){
        /*
        List<OrderMq> list = orderMqRepository.findAll();
        String currIndex = "1";
        int len = list.size();
        if(len == 0){
            return;
        }
        LogUtil.getQuartzLogger().info("定时任务：订单发卡-->查到记录[{}]条", len);
        for(OrderMq mq : list){
            currIndex = JadyerUtil.leftPadUseZero(currIndex, String.valueOf(len).length());
            LogUtil.getLogger().info("准备发送停车卡号={}的消息{}", mq.getCardNo(), ReflectionToStringBuilder.toString(mq));
            Map<String, String> dataMap = Maps.newHashMap();
            //卡号
            dataMap.put("cardNo", mq.getCardNo());
            //0--正常使用，1--已发挂失指令，2--已挂失，3--已发解挂指令，5--退卡
            dataMap.put("cardstate", "0");
            //发卡押金
            dataMap.put("cardyj", "0.00");
            //车场卡类（临时车、月租车、免费车、储值车）
            dataMap.put("carcardtype", "免费卡");
            //卡上余额
            dataMap.put("balance", "0.00");
            //车场发行日期
            //dataMap.put("carissuedate", "");
            //车场有效起日
            //dataMap.put("carvalidstartdate", "");
            //车场有效止日
            //dataMap.put("carvalidenddate", "");
            //车牌
            dataMap.put("cph", mq.getCarNumber());
            //有效开始时间yyyy-MM-dd HH:mm:ss
            dataMap.put("mjvalidstarttime", DateFormatUtils.format(hhtcHelper.convertToDate(mq.getDoFromDate(), mq.getDoFromTime()), "yyyy-MM-dd HH:mm:ss"));
            //有效结束时间yyyy-MM-dd HH:mm:ss
            dataMap.put("mjvalidendtime", DateFormatUtils.format(hhtcHelper.convertToDate(mq.getDoEndDate(), mq.getDoEndTime()), "yyyy-MM-dd HH:mm:ss"));
            String msgData = JSON.toJSONString(dataMap);
            String msgId = UUID.randomUUID().toString().replaceAll("-", "");
            Message msg = new Message();
            msg.setMsgID(msgId);
            msg.setKey(msgId);
            msg.setTag("card-" + mq.getCommunityId());
            msg.setTopic("hhtc_topic");
            try {
                msg.setBody(msgData.getBytes("UTF-8"));
                SendResult result = this.orderProducer.send(msg, mq.getCarNumber());
                LogUtil.getLogger().info("停车卡号={}的消息发送成功{}", mq.getCardNo(), result);
                OrderMqHistory history = new OrderMqHistory();
                BeanUtil.copyProperties(mq, history);
                history.setId(null);
                history.setOrderMqId(mq.getId());
                orderMqHistoryRepository.saveAndFlush(history);
                orderMqRepository.delete(mq);
            } catch (Throwable e) {
                LogUtil.getLogger().error("停车卡号="+mq.getCardNo()+"的消息发送失败，堆栈轨迹如下", e);
                LogUtil.logToTask().error("停车卡号="+mq.getCardNo()+"的消息发送失败，堆栈轨迹如下", e);
            }
            LogUtil.getQuartzLogger().info("定时任务：订单发卡-->处理完毕[{}-{}]条", len, currIndex);
            currIndex = String.valueOf(Integer.parseInt(currIndex) + 1);
        }
        */
    }
}