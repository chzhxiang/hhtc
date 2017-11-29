package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.BeanUtil;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.MoneyUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.GoodsPublishHistoryRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishOrderRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/19 18:32.
 */
@Service
public class GoodsPublishService {
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Value("${hhtc.wxtemplateUrl.needsucc}")
    private String templateUrlNeed;
    @Value("${hhtc.portalUrl.center}")
    private String portalCenterUrl;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderService orderService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private CommunityService communityService;
    @Resource
    private GoodsNeedService goodsNeedService;
    @Resource
    private OrderRentService orderRentService;
    @Resource
    private OrderInoutService orderInoutService;
    @Resource
    private GoodsPublishService goodsPublishService;
    @Resource
    private UserFundsFlowService userFundsFlowService;
    @Resource
    private GoodsPublishRepository goodsPublishRepository;
    @Resource
    private GoodsPublishOrderService goodsPublishOrderService;
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;
    @Resource
    private GoodsPublishHistoryRepository goodsPublishHistoryRepository;

    /**
     * 业务校验（车位主发布车位）
     */
    public GoodsInfor verifyBeforeAdd(String openid, long goodsId, int publishType, int publishFromTime, int publishEndTime, String publishFromDates){
        List<String> dateList = Arrays.asList(publishFromDates.split("-"));
        hhtcHelper.verifyOfTime(publishType, publishFromTime, publishEndTime, Integer.parseInt(dateList.get(0)), Integer.parseInt(dateList.get(dateList.size()-1)));
        //TODO
        //        //校验是否注册车位主
//        if(2 != fansService.getByOpenid(openid).getCarParkStatus()){
//            throw new HHTCException(CodeEnum.HHTC_UNREG_CAR_PARK);
//        }
        GoodsInfor goodsInfor = goodsService.get(goodsId);
        if(goodsInfor.getIsUsed() == 3){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "车位无效（已删除）");
        }
        try {
            Date endDate = DateUtils.parseDate(dateList.get(dateList.size()-1), "yyyyMMdd");
            if(publishType==3 || (2==publishType && publishEndTime<publishFromTime)){
                endDate = DateUtils.addDays(endDate, 1);
            }
//            if(Integer.parseInt(DateFormatUtils.format(endDate, "yyyyMMdd")) > goodsInfo.getCarUsefulEndDate()){
//                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "发布截止日不能超过车位有效期");
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //时间段不可重叠
        hhtcHelper.verifyOfTimeCross(dateList, goodsId, publishFromTime, publishEndTime);
        return goodsInfor;
    }


    /**
     * 时间段连接：三合一
     * <p>
     *     注意：该方法返回的是t_goods_publish_info，而非t_goods_publish_order
     * </p>
     * @return 返回合并后的信息（如果存在三合一，则返回的信息中deleteId属性会记录需要删除的信息）
     */
    private GoodsPublishInfo threeToOne(long goodsId, int publishType, int publishFromTime, int publishEndTime, int publishFromDate, int publishEndDate){
        GoodsPublishInfo info = new GoodsPublishInfo();
        //标记是否已合并了头部和尾部
        boolean matchHeader = false;
        boolean matchFooter = false;
        //连接到数据库中的头部
        Condition<GoodsPublishInfo> spec01 = Condition.and();
        spec01.eq("goodsId", goodsId);
        spec01.eq("status", 0);
        spec01.eq("publishType", publishType);
        spec01.eq("publishFromDate", publishEndDate);
        spec01.eq("publishFromTime", publishEndTime);
        List<GoodsPublishInfo> list01 = goodsPublishRepository.findAll(spec01);
        if(!list01.isEmpty()){
            matchHeader = true;
            BeanUtil.copyProperties(list01.get(0), info);
            info.setId(list01.get(0).getId());
            info.setPublishFromDate(publishFromDate);
            info.setPublishFromTime(publishFromTime);
        }
        //连接到数据库中的尾部
        Condition<GoodsPublishInfo> spec02 = Condition.and();
        spec02.eq("goodsId", goodsId);
        spec02.eq("status", 0);
        spec02.eq("publishType", publishType);
        spec02.eq("publishEndDate", publishFromDate);
        spec02.eq("publishEndTime", publishFromTime);
        List<GoodsPublishInfo> list02 = goodsPublishRepository.findAll(spec02);
        if(!list02.isEmpty()){
            matchFooter = true;
            BeanUtil.copyProperties(list02.get(0), info);
            info.setId(list02.get(0).getId());
            if(matchHeader){
                info.setPublishEndDate(list01.get(0).getPublishEndDate());
                info.setPublishEndTime(list01.get(0).getPublishEndTime());
            }else{
                info.setPublishEndDate(publishEndDate);
                info.setPublishEndTime(publishEndTime);
            }
        }
        if(matchHeader && matchFooter){
            info.setDeleteId(list01.get(0).getId());
        }
        //最后判断info所属的order是否是大于24小时发布的那种
        if(null!=info.getId() && info.getId()>0){
//            GoodsPublishOrder order = goodsPublishOrderRepository.findByGoodsPublishIdsLike("%"+info.getId()+"%");
//            if(null!=order && order.getId()>0 && order.getPublishFromDates().contains("-")){
//                return new GoodsPublishInfo();
//            }
        }
        return info;
    }


    /**
     * 发布的车位是否可合并
     */
    public boolean addCanMerge(String openid, long goodsId, int publishType, int publishFromTime, int publishEndTime, String publishFromDates){
//        GoodsInfo goodsInfo = this.verifyBeforeAdd(openid, goodsId, publishType, publishFromTime, publishEndTime, publishFromDates);
        //全天或超过24小时的发布订单，都不合并
        if(publishFromDates.contains("-") || publishType==3){
            return false;
        }
        //计算起止日期（注意夜间跨天）
        int publishFromDate = Integer.parseInt(publishFromDates);
        int publishEndDate = Integer.parseInt(publishFromDates);
        if(2==publishType && publishEndTime<publishFromTime){
            try {
                publishEndDate = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(publishEndDate+"", "yyyyMMdd"), 1), "yyyyMMdd"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        GoodsPublishInfo info = this.threeToOne(goodsId, publishType, publishFromTime, publishEndTime, publishFromDate, publishEndDate);
        return null!=info.getId() && info.getId()>0;
    }




    public GoodsPublishInfo get(long id){
        return goodsPublishRepository.findOne(id);
    }


    @Transactional(rollbackFor=Exception.class)
    public GoodsPublishInfo upsert(GoodsPublishInfo publish) {
        return goodsPublishRepository.saveAndFlush(publish);
    }


    /**
     * 更新发布状态
     * @param goodsPublishId 对应OrderInfo#goodsPublishId属性
     */
    @Transactional(rollbackFor=Exception.class)
    public void updateStatus(String goodsPublishId, int status, int preStatus){
        String[] goodsPublishIds = goodsPublishId.split("`");
        List<Long> idList = new ArrayList<>();
        for(String obj : goodsPublishIds){
            idList.add(Long.parseLong(obj));
        }
        goodsPublishRepository.updateStatus(idList, status, preStatus);
    }


    /**
     * 预约下单
     */
    @Transactional(rollbackFor=Exception.class)
    public Object order(String appid, String openid, String carNumber, BigDecimal price, String ids, String publishFromDates, List<GoodsPublishOrder> orderList, MppFansInfor fansInfo){
        //锁定发布信息
        goodsPublishOrderService.lock(orderList);
        //下单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCommunityId(orderList.get(0).getCommunityId());
        orderInfo.setCommunityName(orderList.get(0).getCommunityName());
        orderInfo.setGoodsId(orderList.get(0).getGoodsId());
        orderInfo.setGoodsPublishOrderIds(ids);
        orderInfo.setCarParkNumber(orderList.get(0).getCarParkNumber());
        orderInfo.setCarParkImg(orderList.get(0).getCarParkImg());
        orderInfo.setCarNumber(carNumber);
//        orderInfo.setOpenType(orderList.get(0).getPublishType());
//        orderInfo.setOpenFromDates(publishFromDates);
//        orderInfo.setOpenFromTime(orderList.get(0).getPublishFromTime());
//        orderInfo.setOpenEndTime(orderList.get(orderList.size()-1).getPublishEndTime());
//        orderInfo.setOutTradeNo(hhtcHelper.buildOrderNo(8));
        orderInfo.setTotalFee(Long.parseLong(MoneyUtil.yuanToFen(price.toString())));
        orderInfo.setDepositMoney(new BigDecimal(0));
        orderInfo.setCanRefundMoney(new BigDecimal(0));
        orderInfo.setOpenid(openid);
        orderInfo.setAppid(appid);
        orderInfo.setOrderType(1);
        orderInfo.setOrderStatus(2);
        orderInfo = orderService.upsert(orderInfo);
        goodsPublishOrderService.updateStatusToUsed(orderList);
        orderInoutService.initInout(orderInfo, orderList.get(0).getOpenid(), null);
        //分润（先要扣减车主余额，然后再分）
        UserFunds funds = userFundsService.subtractMoneyBalanceForFans(openid, price);
        UserFundsFlow fundsFlow = new UserFundsFlow();
        fundsFlow.setFundsId(funds.getId());
        fundsFlow.setOpenid(openid);
        fundsFlow.setMoney(price);
        fundsFlow.setInOut("out");
        fundsFlow.setInOutDesc("预约下单时扣减余额");
        fundsFlow.setInOutType(4);
        fundsFlow.setBizDate(Integer.parseInt(DateUtil.getCurrentDate()));
        fundsFlow.setBizDateTime(new Date());
        userFundsFlowService.upsert(fundsFlow);
        BigDecimal moneyCarparker = orderRentService.rent(1, orderList.get(0).getOpenid(), orderInfo);
        //模版CODE: SMS_86510100（車主）
        //模版内容: 尊敬的手机尾号为${phone}的用户：您已成功预约[${community}+${carpark}]车位，本次停车金额${money}，请您及时入场停车
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", fansInfo.getPhoneNo().substring(7, 11));
        paramMap.put("community", orderInfo.getCommunityName());
        paramMap.put("carpark", orderInfo.getCarParkNumber());
        paramMap.put("money", price.toString() + "元");
        hhtcHelper.sendSms(fansInfo.getPhoneNo(), "SMS_86510100", paramMap);
        //TODO 长期车位订单金额超过小区全天金额时，会在8、18、28才结算给车位主，这种情况的提示方式待修改
        //模版CODE: SMS_86745123（車位主）
        //模版内容: 尊敬的手机尾号为${phone}的用户：您的${carpark}车位已于${time}交易成功，${money}停车收益已到账。
        String phone = fansService.getByOpenid(orderList.get(0).getOpenid()).getPhoneNo();
        paramMap = new HashMap<>();
        paramMap.put("phone", phone.substring(7, 11));
        paramMap.put("carpark", orderInfo.getCarParkNumber());
        paramMap.put("time", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        paramMap.put("money", moneyCarparker.toString() + "元");
        hhtcHelper.sendSms(phone, "SMS_86745123", paramMap);
        /*
        {{first.DATA}}
        订单编号：{{keyword1.DATA}}
        停车位：{{keyword2.DATA}}
        车牌号：{{keyword3.DATA}}
        预计到达时间：{{keyword4.DATA}}
        预计离开时间：{{keyword5.DATA}}
        {{remark.DATA}}

        尊敬的客户，您已成功预约珠江帝景A00停车位
        订单编号：1000387777
        停车位：珠江帝景A00
        车牌号：粤A0397Z
        预计到达时间：2016年5月19日 18:00
        预计离开时间：2016年5月19日 20:00
        优泊停车提醒您，请注意帐号防盗 如有疑问，请拨打咨询热线020-38383888。
        */
        WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
        dataItem.put("first", new WeixinTemplateMsg.DItem("尊敬的车主，您已成功预约车位："+orderInfo.getCommunityName()+"-" +orderInfo.getCarParkNumber()));
        dataItem.put("keyword1", new WeixinTemplateMsg.DItem(orderInfo.getOutTradeNo()));
        dataItem.put("keyword2", new WeixinTemplateMsg.DItem(orderInfo.getCarParkNumber()));
        dataItem.put("keyword3", new WeixinTemplateMsg.DItem(orderInfo.getCarNumber()));
        dataItem.put("keyword4", new WeixinTemplateMsg.DItem(DateFormatUtils.format(hhtcHelper.convertToDate(hhtcHelper.calcOrderFromDate(orderInfo), orderInfo.getOpenFromTime()), "yyyy-MM-dd HH:mm")));
        dataItem.put("keyword5", new WeixinTemplateMsg.DItem(DateFormatUtils.format(hhtcHelper.convertToDate(hhtcHelper.calcOrderEndDate(orderInfo), orderInfo.getOpenEndTime()), "yyyy-MM-dd HH:mm")));
        dataItem.put("remark", new WeixinTemplateMsg.DItem("吼吼共享停车提醒您：请注意停车时间，避免错过。"));
        String url = this.hhtcContextPath + this.templateUrlNeed.replace("{orderId}", orderInfo.getId()+"");
        url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+orderInfo.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+orderInfo.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
        WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
        templateMsg.setTemplate_id("vkWecSSZW6qJQ4qXXX1iH7QaRvV1HJrcmky208AKx88");
        templateMsg.setUrl(url);
        templateMsg.setTouser(orderInfo.getOpenid());
        templateMsg.setData(dataItem);
        WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(orderInfo.getAppid()), templateMsg);
        dataItem = new WeixinTemplateMsg.DataItem();
        dataItem.put("first", new WeixinTemplateMsg.DItem("尊敬的车位主，您的车位已被预约："+orderInfo.getCommunityName()+"-" +orderInfo.getCarParkNumber()));
        dataItem.put("keyword1", new WeixinTemplateMsg.DItem(orderInfo.getOutTradeNo()));
        dataItem.put("keyword2", new WeixinTemplateMsg.DItem(orderInfo.getCarParkNumber()));
        dataItem.put("keyword3", new WeixinTemplateMsg.DItem(orderInfo.getCarNumber()));
        dataItem.put("keyword4", new WeixinTemplateMsg.DItem(DateFormatUtils.format(hhtcHelper.convertToDate(hhtcHelper.calcOrderFromDate(orderInfo), orderInfo.getOpenFromTime()), "yyyy-MM-dd HH:mm")));
        dataItem.put("keyword5", new WeixinTemplateMsg.DItem(DateFormatUtils.format(hhtcHelper.convertToDate(hhtcHelper.calcOrderEndDate(orderInfo), orderInfo.getOpenEndTime()), "yyyy-MM-dd HH:mm")));
        dataItem.put("remark", new WeixinTemplateMsg.DItem("吼吼共享停车提醒您：本次的车位预约所获得租金已打到您的余额中。"));
        url = this.hhtcContextPath + this.portalCenterUrl;
        url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+orderInfo.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+orderInfo.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
        templateMsg = new WeixinTemplateMsg();
        templateMsg.setTemplate_id("vkWecSSZW6qJQ4qXXX1iH7QaRvV1HJrcmky208AKx88");
        templateMsg.setUrl(url);
        templateMsg.setTouser(orderList.get(0).getOpenid());
        templateMsg.setData(dataItem);
        WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(orderInfo.getAppid()), templateMsg);
        return orderInfo;
    }



}