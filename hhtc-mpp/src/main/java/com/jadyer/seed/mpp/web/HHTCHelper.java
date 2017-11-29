package com.jadyer.seed.mpp.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.common.collect.Maps;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.HttpUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayUnifiedorderReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayUnifiedorderRespData;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.model.SmsInfor;
import com.jadyer.seed.mpp.web.repository.GoodsNeedRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishRepository;
import com.jadyer.seed.mpp.web.repository.MppUserInfoRepository;
import com.jadyer.seed.mpp.web.repository.OrderRepository;
import com.jadyer.seed.mpp.web.repository.SmsRepository;
import com.jadyer.seed.mpp.web.service.CommunityService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/14 9:22.
 */
@Component
public class HHTCHelper {
    private IAcsClient acsClient;
    @Value("${hhtc.picsavepath}")
    private String pictureSavePath;
    @Value("${aliyun.sms.signName}")
    private String smsSignName;
    @Value("${aliyun.sms.accessKeyId}")
    private String smsAccessKeyId;
    @Value("${aliyun.sms.accessKeySecret}")
    private String smsAccessKeySecret;
    @Value("${hhtc.publishTime.day}")
    private int timeDay;
    @Value("${hhtc.publishTime.night}")
    private int timeNight;
    @Resource
    private SmsRepository smsRepository;
    @Resource
    private OrderRepository orderRepository;
    @Resource
    private CommunityService communityService;
    @Resource
    private GoodsNeedRepository goodsNeedRepository;
    @Resource
    private MppUserInfoRepository mppUserInfoRepository;
    @Resource
    private GoodsPublishRepository goodsPublishRepository;

    @PostConstruct
    public void loadSmsData(){
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsAccessKeyId, smsAccessKeySecret);
        try{
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
        }catch(ClientException e){
            LogUtil.getLogger().error("阿里云短信服务初始化异常，堆栈轨迹如下", e);
        }
        acsClient = new DefaultAcsClient(profile);
    }


    /**
     * 开闸
     * @param serialno 继电器序列号
     */
    public boolean openDoor(String serialno, String doorid){
        //Map<String, String> params = new HashMap<>();
        //params.put("phone", "18996191825");
        //params.put("seria", serialno);
        //String respData = HttpUtil.post("http://115.28.3.3/door/api/code", params);
        //入口：doorId:14419FFC4F864FB4929B0D2F6CE351F3
        //出口：doorId:F6352261ADA04F79BA6B9D331CCFF2DE
        String respData = HttpUtil.post("http://web.123667.com/door/door/open?doorId=" + doorid, null);
        if(StringUtils.isBlank(respData)){
            return false;
        }
        //{"success":true,"message":"OK","code":200,"data":null}
        //{"success":true,"message":"开门成功","code":200,"data":null}
        //{"success":false,"message":"无权操作该门禁,请联系所在物管授权操作","code":403,"data":null}
        Map<String,Object> map = JSON.parseObject(respData, new TypeReference<Map<String,Object>>(){});
        return StringUtils.equals(map.get("success").toString(), "true");
    }


    /**
     * 通过阿里云接口发送短信
     * @param phoneNo      接收短信的用户手机号
     * @param templateCode 模板code
     * @param paramMap     模板参数（key为模板消息中的占位符）
     */
    public boolean sendSms(String phoneNo, String templateCode, Map<String, String> paramMap){
        SendSmsRequest smsRequest = new SendSmsRequest();
        smsRequest.setPhoneNumbers(phoneNo);
        smsRequest.setSignName(smsSignName);
        smsRequest.setTemplateCode(templateCode);
        smsRequest.setTemplateParam(JSON.toJSONString(paramMap));
        try {
            LogUtil.getLogger().info("开始发送短信phoneNo={}，入参为{}", phoneNo, ReflectionToStringBuilder.toString(smsRequest));
            SendSmsResponse response = acsClient.getAcsResponse(smsRequest);
            LogUtil.getLogger().info("短信phoneNo={}发送完毕，出参为{}", phoneNo, ReflectionToStringBuilder.toString(response));
            return true;
        } catch (ClientException e) {
            LogUtil.getLogger().error("短信发送失败，堆栈轨迹如下", e);
            return false;
        }
    }

    /**
     * TOKGO 发送验证码
     * */
    public boolean sendSms(String phoneNo, String verifyCode){
        SendSmsRequest smsRequest = new SendSmsRequest();
        smsRequest.setPhoneNumbers(phoneNo);
        smsRequest.setSignName(smsSignName);
        smsRequest.setTemplateCode("SMS_78835017");
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("code", verifyCode);
        smsRequest.setTemplateParam(JSON.toJSONString(paramMap));
        try {
            LogUtil.getLogger().info("准备发送短信phoneNo={}，入参为{}", phoneNo, ReflectionToStringBuilder.toString(smsRequest));
            SendSmsResponse response = acsClient.getAcsResponse(smsRequest);
            LogUtil.getLogger().info("短信phoneNo={}发送完毕，出参为{}", phoneNo, ReflectionToStringBuilder.toString(response));
            return true;
        } catch (ClientException e) {
            LogUtil.getLogger().error("短信发送失败，堆栈轨迹如下", e);
            return false;
        }
    }


    String getFilePath(){
        return pictureSavePath.endsWith("/") ? pictureSavePath : pictureSavePath+"/";
    }


    String buildUploadFilePath(String originalFilename){
        String fileExtension = FilenameUtils.getExtension(originalFilename);
        String fileName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS") + RandomStringUtils.randomNumeric(6);
        String filePath = this.getFilePath() + fileName + "." + fileExtension;
        LogUtil.getLogger().info("originalFilename=[{}]，filePath=[{}]", originalFilename, filePath);
        return filePath;
    }


    public String getWxOpenidFromSession(HttpSession session){
        String openid = (String)session.getAttribute(Constants.WEB_SESSION_WX_OPENID);
        LogUtil.getLogger().info("Session get openid=[{}]", openid);
        if(StringUtils.isBlank(openid)){
            throw new HHTCException(CodeEnum.SYSTEM_UNLOGIN);
        }
        return openid;
    }


    public String getWxAppidFromSession(HttpSession session){
        String appid = (String)session.getAttribute(Constants.WEB_SESSION_WX_APPID);
        if(StringUtils.isBlank(appid)){
            MppUserInfo mppUserInfo = mppUserInfoRepository.findByMptypeAndBindStatus(1, 1);
            if(null==mppUserInfo || mppUserInfo.getId()==0){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "session读取appid失败");
            }
            session.setAttribute(Constants.WEB_SESSION_WX_APPID, mppUserInfo.getAppid());
            appid = mppUserInfo.getAppid();
        }
        return appid;
    }


    /**
     * TOKGO 生成下单或退款的订单号（固长25）
     */
    public static String buildOrderNo(){
        return DateFormatUtils.format(new Date(), "yyMMddHHmmssSSS") + RandomStringUtils.randomNumeric(10);
    }



    /**
     * 转换整型的日期和时间为java.util.Date
     */
    public Date convertToDate(int fromDate, int fromTime){
        try {
            return DateUtils.parseDate(fromDate + ((fromTime+"").length()==3?"0":(fromTime+"").length()==2?"00":"") + (fromTime==0?"0000":fromTime), "yyyyMMddHHmm");
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * 計算此筆訂單的起始日期
     */
    public int calcOrderFromDate(OrderInfo order){
        int fromDate = 0;
        if(order.getOrderType()==1 || order.getOrderType()==2){
            if(order.getOpenFromDates().contains("-")){
                fromDate = Integer.parseInt(order.getOpenFromDates().substring(0, order.getOpenFromDates().indexOf("-")));
            }else{
                fromDate = Integer.parseInt(order.getOpenFromDates());
            }
        }
        return fromDate;
    }


    /**
     * 計算此筆訂單的結束日期
     */
    public int calcOrderEndDate(OrderInfo order){
        int endDate = 0;
        if(order.getOrderType()==1 || order.getOrderType()==2){
            String[] fromDates = order.getOpenFromDates().split("-");
            endDate = Integer.parseInt(fromDates[fromDates.length-1]);
            if(3==order.getOpenType() || (2==order.getOpenType() && order.getOpenEndTime()<order.getOpenFromTime())){
                try {
                    endDate = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(endDate+"", "yyyyMMdd"), 1), "yyyyMMdd"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return endDate;
    }


    /**
     * 计算此笔订单是否允许转租
     */
    public boolean calcOrderAllowRent(OrderInfo order){
        boolean allowRent = false;
        if((order.getOrderType()==1 || order.getOrderType()==2) && order.getOrderStatus()==2 && order.getOrderStatus()!=9 && order.getOrderStatus()!=99){
            if(DateUtils.addHours(new Date(), 1).compareTo(this.convertToDate(this.calcOrderFromDate(order), order.getOpenFromTime())) == -1){
                allowRent = true;
            }
        }
        return allowRent;
    }


    /**
     * 计算车位发布时的价格
     * <ul>
     *     <li>示例如下</li>
     *     <li>09:00--17:00：6元/次</li>
     *     <li>17:00--09:00：15元/次</li>
     *     <li>全天：20元/次</li>
     * </ul>
     * @param publishType 车位发布类型：1--日间，2--夜间，3--全天
     * @return 价格，单位：元
     */
    public BigDecimal calcPrice(long communityId, int publishType){
        CommunityInfo communityInfo = communityService.get(communityId);
        BigDecimal price = new BigDecimal(0);
        switch(publishType){
            case 1: price = price.add(communityInfo.getMoneyRentDay());   break;
            case 2: price = price.add(communityInfo.getMoneyRentNight()); break;
            case 3: price = price.add(communityInfo.getMoneyRentFull());  break;
            default: throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无法识别的车位发布类型");
        }
        price = price.divide(new BigDecimal(1), 2, BigDecimal.ROUND_HALF_UP);
        return price;
    }



    /**
     * 时间段的校验（车主发布需求或者车位主发布车位信息时）
     */
    public void verifyOfTime(int publishType, int publishFromTime, int publishEndTime, int publishFromDate, int publishEndDate){
        try {
            if(publishEndDate > Integer.parseInt(DateFormatUtils.format(DateUtil.getIncreaseDate(DateUtils.parseDate(publishFromDate+"", "yyyyMMdd"), 90), "yyyyMMdd"))){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "车位时长最长3个月");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(publishFromDate==Integer.parseInt(DateUtil.getCurrentDate()) && publishFromTime<=(Integer.parseInt(DateFormatUtils.format(new Date(), "HHmm"))+100)){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无效的当天发布起始时间或至少提前一小时发布");
        }
        if(1 == publishType){
            if(publishFromTime<this.timeDay || publishEndTime>this.timeNight){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无效的日间时间间隔");
            }
            if(this.convertToDate(publishFromDate, publishEndTime).getTime() < DateUtils.addHours(this.convertToDate(publishFromDate, publishFromTime), 1).getTime()){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "发布时长最短一小时");
            }
        }
        if(2 == publishType){
            //夜间：跨天的（截止时间在后半夜的）
            if(publishEndTime < publishFromTime){
                if(publishFromTime < this.timeNight){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无效的夜间起始时间点");
                }
                if(publishEndTime > this.timeDay){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无效的夜间截止时间点");
                }
                int endDate = 0;
                try {
                    endDate = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(publishFromDate+"", "yyyyMMdd"), 1), "yyyyMMdd"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(this.convertToDate(endDate, publishEndTime).getTime() < DateUtils.addHours(this.convertToDate(publishFromDate, publishFromTime), 1).getTime()){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "发布时长最短一小时");
                }
            }
            //夜间：都在前半夜
            if(publishEndTime > this.timeNight){
                if(publishFromTime>=0 && publishFromTime<this.timeNight){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无效的夜间-起始时间点");
                }
                if(this.convertToDate(publishFromDate, publishEndTime).getTime() < DateUtils.addHours(this.convertToDate(publishFromDate, publishFromTime), 1).getTime()){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "发布时长最短一小时");
                }
            }
            //夜间：都在后半夜
            if(publishFromTime>=0 && publishFromTime<this.timeDay){
                if(publishEndTime > this.timeDay){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无效的夜间-截止时间点");
                }
                if(this.convertToDate(publishFromDate, publishEndTime).getTime() < DateUtils.addHours(this.convertToDate(publishFromDate, publishFromTime), 1).getTime()){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "发布时长最短一小时");
                }
            }
        }
        if(3 == publishType){
            if(publishFromTime != publishEndTime){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无效的-全天时间间隔");
            }
            if(publishFromTime!=this.timeDay && publishFromTime!=this.timeNight){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无效的全天时间间隔");
            }
        }
    }



    /**
     * 计算发布类型
     * @return 1--日间，2--夜间，3--全天，4--横跨（日间），5--横跨（夜间），6--横跨（日夜间）
     */
    public int calcPublishTypeExt(int fromTime, int endTime){
        //全天
        if(fromTime==endTime && (fromTime==this.timeDay || fromTime==this.timeNight)){
            return 3;
        }
        //日间
        if(endTime>fromTime && fromTime>=this.timeDay && endTime<=this.timeNight){
            return 1;
        }
        //夜间：前半夜
        if(endTime>fromTime && fromTime>=this.timeNight && endTime<=2400){
            return 2;
        }
        //夜间：后半夜
        if(endTime>fromTime && endTime<=this.timeDay){
            return 2;
        }
        //夜间：跨天
        if(endTime<fromTime && fromTime>=this.timeNight && fromTime<2400 && endTime>0 && endTime<=this.timeDay){
            return 2;
        }
        //横跨：日间
        if((fromTime>=this.timeNight || (fromTime>=0 && fromTime<this.timeDay)) && endTime>this.timeDay && endTime<=this.timeNight){
            return 4;
        }
        //横跨：夜间
        if(fromTime>=this.timeDay && fromTime<this.timeNight && (endTime>this.timeNight || (endTime>=0 && endTime<this.timeDay))){
            return 5;
        }
        //横跨：日夜间
        return 6;
    }


    /**
     * 车位主发布车位时，校验发布的信息，是否与数据库中重复
     * @param dateList 待发布的起始日期集合
     */
    public void verifyOfTimeCross(List<String> dateList, long goodsId, int publishFromTime, int publishEndTime){
        for(String date : dateList){
            int yestoday = 0;
            try {
                yestoday = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(date, "yyyyMMdd"), -1), "yyyyMMdd"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //把当天发布和昨天发布的都查出来（昨天的可能包含今天0--900的时间）
            List<GoodsPublishInfo> list = goodsPublishRepository.findByGoodsIdAndPublishFromDateIn(goodsId, Arrays.asList(Integer.parseInt(date), yestoday));
            for(GoodsPublishInfo obj : list){
                Date objStartDate = this.convertToDate(obj.getPublishFromDate(), obj.getPublishFromTime());
                Date objEndDate = this.convertToDate(obj.getPublishEndDate(), obj.getPublishEndTime());
                Date pubStartDate = this.convertToDate(Integer.parseInt(date), publishFromTime);
                Date pubEndDate = this.convertToDate(Integer.parseInt(date), publishEndTime);
                if(publishFromTime >= publishEndTime){
                    int endDate = 0;
                    try {
                        endDate = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(Integer.parseInt(date)+"", "yyyyMMdd"), 1), "yyyyMMdd"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    pubEndDate = this.convertToDate(endDate, publishEndTime);
                }
                String repeatDate = DateFormatUtils.format(this.convertToDate(obj.getPublishFromDate(), obj.getPublishFromTime()), "yyyy年MM月dd日HH点");
                if(objStartDate.compareTo(pubStartDate)<1 && objEndDate.compareTo(pubEndDate)>=0){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "发布-时间重叠-" + repeatDate);
                }
                if(objStartDate.compareTo(pubStartDate)>=0 && objStartDate.compareTo(pubEndDate)==-1){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "发布时间-重叠-" + repeatDate);
                }
                if(objEndDate.compareTo(pubStartDate)==1 && objEndDate.compareTo(pubEndDate)<1){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "发布时间重叠-" + repeatDate);
                }
            }
        }
    }


    /**
     * 车主发布需求时，校验发布的信息，是否与数据库中重复
     */
    public void verifyOfTimeCross(int needFromDate, long communityId, String carNumber, int needFromTime, int needEndTime){
        int yestoday = 0;
        try {
            yestoday = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(needFromDate+"", "yyyyMMdd"), -1), "yyyyMMdd"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //把当天发布和昨天发布的都查出来（昨天的可能包含今天0--900的时间）
        List<GoodsNeedInfo> list = goodsNeedRepository.findByCommunityIdAndCarNumberAndStatusInAndNeedFromDateIn(communityId, carNumber, Arrays.asList(1, 2), Arrays.asList(needFromDate, yestoday));
        for(GoodsNeedInfo obj : list){
            Date objStartDate = this.convertToDate(obj.getNeedFromDate(), obj.getNeedFromTime());
            Date objEndDate = this.convertToDate(obj.getNeedEndDate(), obj.getNeedEndTime());
            Date pubStartDate = this.convertToDate(needFromDate, needFromTime);
            Date pubEndDate = this.convertToDate(needFromDate, needEndTime);
            if(needFromTime >= needEndTime){
                int endDate = 0;
                try {
                    endDate = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(needFromDate+"", "yyyyMMdd"), 1), "yyyyMMdd"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                pubEndDate = this.convertToDate(endDate, needEndTime);
            }
            String repeatDate = DateFormatUtils.format(this.convertToDate(obj.getNeedFromDate(), obj.getNeedFromTime()), "yyyy年MM月dd日HH点");
            if(objStartDate.compareTo(pubStartDate)<1 && objEndDate.compareTo(pubEndDate)>=0){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "需求-时间重叠-" + repeatDate);
            }
            if(objStartDate.compareTo(pubStartDate)>=0 && objStartDate.compareTo(pubEndDate)==-1){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "需求时间-重叠-" + repeatDate);
            }
            if(objEndDate.compareTo(pubStartDate)==1 && objEndDate.compareTo(pubEndDate)<1){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "需求时间重叠-" + repeatDate);
            }
        }
    }


    ///**
    // * 預約下單時（車主主動或者后台運營調配訂單），校验发布的信息，是否与数据库中重复
    // */
    //public void verifyOfTimeCrossForOrder(List<String> publishFromDateList, long communityId, int fromTime, int endTime){
    //    for(String date : publishFromDateList){
    //        String yestoday = "";
    //        try {
    //            yestoday = DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(date, "yyyyMMdd"), -1), "yyyyMMdd");
    //        } catch (ParseException e) {
    //            e.printStackTrace();
    //        }
    //        //把当天发布和昨天发布的都查出来（昨天的可能包含今天0--900的时间）
    //        Condition<OrderInfo> spec = Condition.<OrderInfo>and().eq("communityId", communityId).like("open_from_dates", Arrays.asList(date, yestoday));
    //        List<OrderInfo> list = orderRepository.findAll(spec);
    //        for(OrderInfo obj : list){
    //
    //            Date objStartDate = this.convertToDate(obj.getPublishFromDate(), obj.getPublishFromTime());
    //            Date objEndDate = this.convertToDate(obj.getPublishEndDate(), obj.getPublishEndTime());
    //            Date pubStartDate = this.convertToDate(Integer.parseInt(date), publishFromTime);
    //            Date pubEndDate = this.convertToDate(Integer.parseInt(date), publishEndTime);
    //            if(publishFromTime >= publishEndTime){
    //                int endDate = 0;
    //                try {
    //                    endDate = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(Integer.parseInt(date)+"", "yyyyMMdd"), 1), "yyyyMMdd"));
    //                } catch (ParseException e) {
    //                    e.printStackTrace();
    //                }
    //                pubEndDate = this.convertToDate(endDate, publishEndTime);
    //            }
    //            String repeatDate = DateFormatUtils.format(this.convertToDate(obj.getPublishFromDate(), obj.getPublishFromTime()), "yyyy年MM月dd日HH点");
    //            if(objStartDate.compareTo(pubStartDate)<1 && objEndDate.compareTo(pubEndDate)>=0){
    //                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "发布-时间重叠-" + repeatDate);
    //            }
    //            if(objStartDate.compareTo(pubStartDate)>=0 && objStartDate.compareTo(pubEndDate)==-1){
    //                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "发布时间-重叠-" + repeatDate);
    //            }
    //            if(objEndDate.compareTo(pubStartDate)==1 && objEndDate.compareTo(pubEndDate)<1){
    //                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "发布时间重叠-" + repeatDate);
    //            }
    //        }
    //    }
    //}


    /**
     * 微信下单
     */
    public WeixinPayUnifiedorderRespData payUnifiedorder(OrderInfo orderInfo){
        WeixinPayUnifiedorderReqData reqData = new WeixinPayUnifiedorderReqData();
        reqData.setAppid(orderInfo.getAppid());
        reqData.setMch_id(WeixinTokenHolder.getWeixinMchid(reqData.getAppid()));
        reqData.setNonce_str(RandomStringUtils.randomNumeric(30));
        reqData.setSign_type("MD5");
        reqData.setDevice_info("WEB");
        reqData.setBody(orderInfo.getBody());
        reqData.setAttach(orderInfo.getAttach());
        reqData.setOut_trade_no(orderInfo.getOutTradeNo());
        reqData.setTotal_fee(orderInfo.getTotalFee()+"");
        reqData.setSpbill_create_ip(orderInfo.getSpbillCreateIp());
        reqData.setTime_start(orderInfo.getTimeStart());
        reqData.setTime_expire(orderInfo.getTimeExpire());
        reqData.setNotify_url(orderInfo.getNotifyUrl());
        reqData.setTrade_type("JSAPI");
        reqData.setOpenid(orderInfo.getOpenid());
        return WeixinHelper.payUnifiedorder(reqData);
    }
}

/*
package com.hhcms.service.impl;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.hhcms.service.SmsSendService;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by konghang on 2017/6/28.
 /
@Service
public class SmsSendServiceImpl implements SmsSendService {
    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";
    static final String signName = "吼吼健身";

    //此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String accessKeyId = "LTAIPyEjB430xKd9";
    static final String accessKeySecret = "N2JKONywbLL275BG7UKu2Z1NUFmRCW";
    static IAcsClient acsClient = null;
    static {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        acsClient = new DefaultAcsClient(profile);
    }

    @Override
    public Boolean sendSms(String phone, String validCode) {
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode("SMS_75920036");
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        Map<String, String> map = new HashMap<>();
        map.put("number", validCode);
        request.setTemplateParam(JSON.toJSONString(map));
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId("yourOutId");
        //hint 此处可能会抛出异常，注意catch
        Boolean success = Boolean.TRUE;
        try {
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
            success = Boolean.FALSE;
        }
        return success;
    }
    public static void main(String[] args) {
        SmsSendServiceImpl smsSendService = new SmsSendServiceImpl();
        smsSendService.sendSms("13340248649", "123456");
    }
}
*/