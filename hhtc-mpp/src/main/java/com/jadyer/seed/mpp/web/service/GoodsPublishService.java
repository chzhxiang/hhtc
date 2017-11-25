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
import com.jadyer.seed.mpp.web.model.GoodsInfo;
import com.jadyer.seed.mpp.web.model.GoodsPublishInfo;
import com.jadyer.seed.mpp.web.model.GoodsPublishOrder;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.model.OrderInfo;
import com.jadyer.seed.mpp.web.model.UserFunds;
import com.jadyer.seed.mpp.web.model.UserFundsFlow;
import com.jadyer.seed.mpp.web.repository.GoodsPublishHistoryRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishOrderRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishRepository;
import com.jadyer.seed.mpp.web.repository.GoodsRepository;
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
    private GoodsRepository goodsRepository;
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
    public GoodsInfo verifyBeforeAdd(String openid, long goodsId, int publishType, int publishFromTime, int publishEndTime, String publishFromDates){
        List<String> dateList = Arrays.asList(publishFromDates.split("-"));
        hhtcHelper.verifyOfTime(publishType, publishFromTime, publishEndTime, Integer.parseInt(dateList.get(0)), Integer.parseInt(dateList.get(dateList.size()-1)));
        //TODO
        //        //校验是否注册车位主
//        if(2 != fansService.getByOpenid(openid).getCarParkStatus()){
//            throw new HHTCException(CodeEnum.HHTC_UNREG_CAR_PARK);
//        }
        GoodsInfo goodsInfo = goodsService.get(goodsId);
        if(goodsInfo.getIsUsed() == 3){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "车位无效（已删除）");
        }
        try {
            Date endDate = DateUtils.parseDate(dateList.get(dateList.size()-1), "yyyyMMdd");
            if(publishType==3 || (2==publishType && publishEndTime<publishFromTime)){
                endDate = DateUtils.addDays(endDate, 1);
            }
            if(Integer.parseInt(DateFormatUtils.format(endDate, "yyyyMMdd")) > goodsInfo.getCarUsefulEndDate()){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "发布截止日不能超过车位有效期");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //时间段不可重叠
        hhtcHelper.verifyOfTimeCross(dateList, goodsId, publishFromTime, publishEndTime);
        return goodsInfo;
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
            GoodsPublishOrder order = goodsPublishOrderRepository.findByGoodsPublishIdsLike("%"+info.getId()+"%");
            if(null!=order && order.getId()>0 && order.getPublishFromDates().contains("-")){
                return new GoodsPublishInfo();
            }
        }
        return info;
    }


    /**
     * 发布的车位是否可合并
     */
    public boolean addCanMerge(String openid, long goodsId, int publishType, int publishFromTime, int publishEndTime, String publishFromDates){
        GoodsInfo goodsInfo = this.verifyBeforeAdd(openid, goodsId, publishType, publishFromTime, publishEndTime, publishFromDates);
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


    /**
     * 车位主发布车位
     * @return 本次发布的列表明细
     */
    @Transactional(rollbackFor=Exception.class)
    public GoodsPublishOrder add(String openid, int publishType, int publishFromTime, int publishEndTime, String publishFromDates, GoodsInfo goodsInfo){
        GoodsPublishOrder order = new GoodsPublishOrder();
        //先处理不需要合并的（全天或超过24小时的发布订单）
        if(publishFromDates.contains("-") || publishType==3){
            order.setGoodsPublishIds("");
            order.setPrice(new BigDecimal(0));
            order.setOpenid(openid);
            order.setCommunityId(goodsInfo.getCommunityId());
            order.setCommunityName(goodsInfo.getCommunityName());
            order.setGoodsId(goodsInfo.getId());
            order.setCarParkNumber(goodsInfo.getCarParkNumber());
            order.setCarParkImg(goodsInfo.getCarParkImg());
            order.setPublishType(publishType);
            order.setPublishFromDates(publishFromDates);
            order.setPublishFromTime(publishFromTime);
            order.setPublishEndTime(publishEndTime);
            order.setFromType(1);
            order.setFromId(0);
            order.setStatus(0);
            order = goodsPublishOrderRepository.saveAndFlush(order);
            String ids = "";
            BigDecimal price = new BigDecimal(0);
            for(String date : publishFromDates.split("-")){
                int publishFromDate = Integer.parseInt(date);
                int publishEndDate = Integer.parseInt(date);
                if(3==publishType || (2==publishType && publishEndTime<publishFromTime)){
                    try {
                        publishEndDate = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(publishEndDate+"", "yyyyMMdd"), 1), "yyyyMMdd"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                GoodsPublishInfo info = new GoodsPublishInfo();
                info.setId(null);
                info.setGoodsPublishOrderId(order.getId());
                info.setOpenid(openid);
                info.setCommunityId(goodsInfo.getCommunityId());
                info.setCommunityName(goodsInfo.getCommunityName());
                info.setGoodsId(goodsInfo.getId());
                info.setCarParkNumber(goodsInfo.getCarParkNumber());
                info.setCarParkImg(goodsInfo.getCarParkImg());
                info.setPrice(hhtcHelper.calcPrice(goodsInfo.getCommunityId(), publishType));
                info.setPublishType(publishType);
                info.setPublishFromDate(publishFromDate);
                info.setPublishEndDate(publishEndDate);
                info.setPublishFromTime(publishFromTime);
                info.setPublishEndTime(publishEndTime);
                info.setFromType(1);
                info.setFromIds("0");
                info.setStatus(0);
                info = goodsPublishRepository.saveAndFlush(info);
                ids = ids + "`" + info.getId();
                price = price.add(info.getPrice());
            }
            order.setGoodsPublishIds(ids.substring(1));
            order.setPrice(price);
            order = goodsPublishOrderRepository.saveAndFlush(order);
        }else{
            //下面再处理可能会合并的（注意夜间跨天时的起止日期的计算）
            int publishFromDate = Integer.parseInt(publishFromDates);
            int publishEndDate = Integer.parseInt(publishFromDates);
            if(2==publishType && publishEndTime<publishFromTime){
                try {
                    publishEndDate = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(publishEndDate+"", "yyyyMMdd"), 1), "yyyyMMdd"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            GoodsPublishInfo info = this.threeToOne(goodsInfo.getId(), publishType, publishFromTime, publishEndTime, publishFromDate, publishEndDate);
            //不存在合并的：新增publishOrder和publishInfo
            if(null==info.getId() || info.getId()==0){
                order.setGoodsPublishIds("");
                order.setPrice(new BigDecimal(0));
                order.setOpenid(openid);
                order.setCommunityId(goodsInfo.getCommunityId());
                order.setCommunityName(goodsInfo.getCommunityName());
                order.setGoodsId(goodsInfo.getId());
                order.setCarParkNumber(goodsInfo.getCarParkNumber());
                order.setCarParkImg(goodsInfo.getCarParkImg());
                order.setPublishType(publishType);
                order.setPublishFromDates(publishFromDates);
                order.setPublishFromTime(publishFromTime);
                order.setPublishEndTime(publishEndTime);
                order.setFromType(1);
                order.setFromId(0);
                order.setStatus(0);
                order = goodsPublishOrderRepository.saveAndFlush(order);
                BeanUtil.copyProperties(order, info);
                info.setId(null);
                info.setPrice(hhtcHelper.calcPrice(goodsInfo.getCommunityId(), publishType));
                info.setGoodsPublishOrderId(order.getId());
                info.setPublishFromDate(publishFromDate);
                info.setPublishEndDate(publishEndDate);
                info.setFromIds("0");
                info.setStatus(0);
                info = goodsPublishRepository.saveAndFlush(info);
                order.setGoodsPublishIds(info.getId()+"");
                order.setPrice(info.getPrice());
                order = goodsPublishOrderRepository.saveAndFlush(order);
            }else{
                //存在合并的（有三合一的则要删除一个，否则更新publishOrder和publishInfo，更新时要特别注意order的时间变化）
                if(info.getDeleteId() > 0){
                    goodsPublishRepository.delete(info.getDeleteId());
                    goodsPublishOrderRepository.deleteByGoodsPublishId(info.getDeleteId());
                }
                info = goodsPublishRepository.saveAndFlush(info);
                order = goodsPublishOrderRepository.findOne(info.getGoodsPublishOrderId());
                order.setPublishFromDates(info.getPublishFromDate()+"");
                order.setPublishFromTime(info.getPublishFromTime());
                order.setPublishEndTime(info.getPublishEndTime());
                order = goodsPublishOrderRepository.saveAndFlush(order);
            }
        }
        goodsService.updateStatus(goodsInfo.getId(), 1, 0);
        return order;
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
        orderInfo.setOpenType(orderList.get(0).getPublishType());
        orderInfo.setOpenFromDates(publishFromDates);
        orderInfo.setOpenFromTime(orderList.get(0).getPublishFromTime());
        orderInfo.setOpenEndTime(orderList.get(orderList.size()-1).getPublishEndTime());
        orderInfo.setOutTradeNo(hhtcHelper.buildOrderNo(8));
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


    /**
     * 匹配列表
     * @param communityId     小区ID
     * @param publishFromDate 发布日期（入场日期）
     * @param publishEndDate  发布日期（出场日期）
     * @param publishFromTime 发布时间（入场时间）
     * @param publishEndTime  发布时间（出场时间）
     */
    public List<Map<String, String>> matchlist(long communityId, int publishType, String publishFromDate, String publishEndDate, String publishFromTime, String publishEndTime){
        List<Map<String, String>> respData = new ArrayList<>();
        //标记该匹配结果是否为交叉匹配
        boolean matchCross = false;
        //标记是否匹配成功
        boolean matchSuccess = false;
        List<List<GoodsPublishInfo>> matchPublishList = new ArrayList<>();
        if(StringUtils.isNotBlank(publishFromDate) && StringUtils.isBlank(publishEndDate)){
            publishEndDate = publishFromDate;
        }
        if(StringUtils.isNotBlank(publishFromDate) && StringUtils.isNotBlank(publishFromTime) && StringUtils.isNotBlank(publishEndTime)){
            if(publishType==3 || (2==publishType && Integer.parseInt(publishEndTime)<Integer.parseInt(publishFromTime))){
                try {
                    publishEndDate = DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(publishFromDate, "yyyyMMdd"), 1), "yyyyMMdd");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        if(StringUtils.isNotBlank(publishFromDate) && StringUtils.isNotBlank(publishEndDate) && StringUtils.isNotBlank(publishFromTime) && StringUtils.isNotBlank(publishEndTime)){
            int yestoday = 0;
            try {
                yestoday = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(publishFromDate, "yyyyMMdd"), -1), "yyyyMMdd"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int fromDate = Integer.parseInt(publishFromDate);
            int endDate = Integer.parseInt(publishEndDate);
            int fromTime = Integer.parseInt(publishFromTime);
            int endTime = Integer.parseInt(publishEndTime);
            Date s_date = hhtcHelper.convertToDate(fromDate, fromTime);
            Date e_date = hhtcHelper.convertToDate(endDate, endTime);
            //重新计算parkType（24小时以内算连续停车，超过24小时为周期停车）
            int parkType = 2;
            if(DateUtils.addHours(s_date, 24).getTime() < e_date.getTime()){
                parkType = 1;
            }
            //该小区所有的发布车位列表
            List<GoodsInfo> goodsList = goodsRepository.findByCommunityIdAndCarAuditStatus(communityId, 2);
            for(GoodsInfo goods : goodsList){
                //车位有效期判断
                if(goods.getCarUsefulEndDate() <= endDate){
                    continue;
                }
                //该车位的所有可用发布信息（并过滤掉非包含待发布起始日期的发布信息后break）
                List<GoodsPublishInfo> list = goodsPublishRepository.findByGoodsIdAndStatusAndPublishFromDateGreaterThanEqualOrderByIdAsc(goods.getId(), 0, yestoday);
                //用户可能在同一天先发布15点的，再发布13点的，所有手工排序一下
                list.sort(new Comparator<GoodsPublishInfo>() {
                    @Override
                    public int compare(GoodsPublishInfo o1, GoodsPublishInfo o2) {
                        if(o1.getPublishFromDate() != o2.getPublishFromDate()){
                            return o1.getPublishFromDate() - o2.getPublishFromDate();
                        }else{
                            return o1.getPublishFromTime() - o2.getPublishFromTime();
                        }
                    }
                });
                CopyOnWriteArrayList<GoodsPublishInfo> pubList = new CopyOnWriteArrayList<>();
                if(!list.isEmpty()){
                    pubList = new CopyOnWriteArrayList<>(list);
                    while(!pubList.isEmpty()){
                        Date pubSDate = hhtcHelper.convertToDate(pubList.get(0).getPublishFromDate(), pubList.get(0).getPublishFromTime());
                        Date pubEDate = hhtcHelper.convertToDate(pubList.get(0).getPublishEndDate(), pubList.get(0).getPublishEndTime());
                        if(s_date.compareTo(pubSDate)>=0 && s_date.compareTo(pubEDate)==-1){
                            break;
                        }else{
                            pubList.remove(0);
                        }
                    }
                }
                /*
                 * 连续停车
                 * http://127.0.0.1/wx/goods/publish/matchlist?communityId=1&parkType=2&publishFromDate=20170831&publishEndDate=20170902&publishFromTime=1200&publishEndTime=300
                 */
                if(parkType==2 && !pubList.isEmpty()){
                    //开始迭代，看看是不是现有发布信息的时间都是连续的（上一条的结束时间等于下一条的起始时间）
                    List<GoodsPublishInfo> dataList = new ArrayList<>();
                    for(int i=0; i<pubList.size(); i++){
                        Date currEndDate = hhtcHelper.convertToDate(pubList.get(i).getPublishEndDate(), pubList.get(i).getPublishEndTime());
                        if(e_date.compareTo(currEndDate) < 1){
                            dataList.add(pubList.get(i));
                            matchSuccess = true;
                            break;
                        }
                        if(i+1 < pubList.size()){
                            Date nextStartDate = hhtcHelper.convertToDate(pubList.get(i+1).getPublishFromDate(), pubList.get(i+1).getPublishFromTime());
                            if(currEndDate.getTime() != nextStartDate.getTime()){
                                break;
                            }
                            dataList.add(pubList.get(i));
                        }
                    }
                    if(matchSuccess && !dataList.isEmpty()){
                        matchPublishList.add(dataList);
                    }
                }
                /*
                 * 周期停车
                 * http://127.0.0.1/wx/goods/publish/matchlist?communityId=1&parkType=1&publishFromDate=20170831&publishEndDate=20170902&publishFromTime=1200&publishEndTime=300
                 */
                if(parkType==1 && !pubList.isEmpty()){
                    List<GoodsPublishInfo> dataList = new ArrayList<>();
                    //由于是周期停车，所以先初始化周期的首次起止日期
                    boolean periodNext = false;
                    Date periodStartDate = hhtcHelper.convertToDate(fromDate, fromTime);
                    Date periodEndDate = hhtcHelper.convertToDate(fromDate, endTime);
                    if(fromTime >= endTime){
                        periodEndDate = DateUtils.addDays(periodEndDate, 1);
                    }
                    //开始迭代，注意周期时间的变化
                    for(int i=0; i<pubList.size(); i++){
                        //每个周期开始，要重新计算起止日期（但截止日期不能超过用户输入的截止日期）
                        if(periodNext){
                            periodStartDate = DateUtils.addDays(periodStartDate, 1);
                            periodEndDate = DateUtils.addDays(periodEndDate, 1);
                            if(periodEndDate.getTime() > e_date.getTime()){
                                break;
                            }
                        }
                        //校验时间范围是否包含（尤其是新的周期循环时）
                        Date currStartDate = hhtcHelper.convertToDate(pubList.get(i).getPublishFromDate(), pubList.get(i).getPublishFromTime());
                        Date currEndDate = hhtcHelper.convertToDate(pubList.get(i).getPublishEndDate(), pubList.get(i).getPublishEndTime());
                        if(periodNext){
                            if(periodStartDate.compareTo(currEndDate)>=0 || periodEndDate.compareTo(currStartDate)<=0){
                                periodNext = false;
                                continue;
                            }
                        }
                        //判断是否本周期该结束了
                        if(periodEndDate.compareTo(currEndDate) < 1){
                            dataList.add(pubList.get(i));
                            //全生命周期是否该结束了
                            if(e_date.compareTo(currEndDate) < 1){
                                matchSuccess = true;
                                break;
                            }
                            //本周期迭代结束
                            periodNext = true;
                            continue;
                        }
                        //未完的周期事业，下个循环再续
                        if(i+1 < pubList.size()){
                            Date nextStartDate = hhtcHelper.convertToDate(pubList.get(i+1).getPublishFromDate(), pubList.get(i+1).getPublishFromTime());
                            if(currEndDate.getTime() != nextStartDate.getTime()){
                                break;
                            }
                            periodNext = false;
                            dataList.add(pubList.get(i));
                        }
                    }
                    if(matchSuccess && !dataList.isEmpty()){
                        matchPublishList.add(dataList);
                    }
                }
            }
            //交叉匹配
            if(!matchSuccess){
                for(GoodsInfo goods : goodsList){
                    if(goods.getCarUsefulEndDate() <= endDate){
                        continue;
                    }
                    List<GoodsPublishInfo> list = goodsPublishRepository.findByGoodsIdAndStatusAndPublishFromDateGreaterThanEqualOrderByIdAsc(goods.getId(), 0, yestoday);
                    list.sort(new Comparator<GoodsPublishInfo>() {
                        @Override
                        public int compare(GoodsPublishInfo o1, GoodsPublishInfo o2) {
                            if(o1.getPublishFromDate() != o2.getPublishFromDate()){
                                return o1.getPublishFromDate() - o2.getPublishFromDate();
                            }else{
                                return o1.getPublishFromTime() - o2.getPublishFromTime();
                            }
                        }
                    });
                    CopyOnWriteArrayList<GoodsPublishInfo> pubList = new CopyOnWriteArrayList<>();
                    if(!list.isEmpty()){
                        pubList = new CopyOnWriteArrayList<>(list);
                        while(!pubList.isEmpty()){
                            Date pubSDate = hhtcHelper.convertToDate(pubList.get(0).getPublishFromDate(), pubList.get(0).getPublishFromTime());
                            Date pubEDate = hhtcHelper.convertToDate(pubList.get(0).getPublishEndDate(), pubList.get(0).getPublishEndTime());
                            if(s_date.compareTo(pubSDate)>=0 && s_date.compareTo(pubEDate)==-1){
                                break;
                            }else{
                                pubList.remove(0);
                            }
                        }
                    }
                    List<GoodsPublishInfo> dataList = new ArrayList<>();
                    for(GoodsPublishInfo pub : pubList){
                        dataList.add(pub);
                    }
                    if(!dataList.isEmpty()){
                        matchCross = true;
                        matchPublishList.add(dataList);
                    }
                }
            }
        }else if(StringUtils.isNotBlank(publishFromTime) && StringUtils.isNotBlank(publishEndTime)){
            List<GoodsPublishOrder> puborderList = goodsPublishOrderRepository.findByCommunityIdAndStatusAndPublishFromTimeLessThanEqual(communityId, 0, Integer.parseInt(publishFromTime));
            List<GoodsPublishOrder> puborderList22 = goodsPublishOrderRepository.findByCommunityIdAndStatusAndPublishTypeAndPublishFromTimeGreaterThanAndPublishEndTimeGreaterThanEqual(communityId, 0, 3, Integer.parseInt(publishFromTime), Integer.parseInt(publishEndTime));
            puborderList.addAll(puborderList22);
            List<GoodsPublishOrder> poList = new ArrayList<>();
            for(GoodsPublishOrder obj : puborderList){
                Date seaEdate = hhtcHelper.convertToDate(Integer.parseInt(DateUtil.getCurrentDate()), Integer.parseInt(publishEndTime));
                Date objEdate = hhtcHelper.convertToDate(Integer.parseInt(DateUtil.getCurrentDate()), obj.getPublishEndTime());
                if(obj.getPublishType()==3 || (obj.getPublishType()==2 && obj.getPublishEndTime()<obj.getPublishFromTime())){
                    objEdate = DateUtils.addDays(objEdate, 1);
                }
                if(seaEdate.compareTo(objEdate) == 1){
                    //精确匹配或包裹匹配外则continue
                    continue;
                }
                poList.add(obj);
            }
            for(GoodsPublishOrder obj : poList){
                Map<String, String> map = new HashMap<>();
                map.put("matchSuccess", "true");
                map.put("ids", obj.getId()+"");
                map.put("price", obj.getPrice().toString());
                map.put("carParkImg", obj.getCarParkImg());
                map.put("carParkNumber", obj.getCarParkNumber());
                map.put("publishType", obj.getPublishType()+"");
                map.put("publishFromDates", obj.getPublishFromDates());
                map.put("publishFromTime", obj.getPublishFromTime()+"");
                map.put("publishEndTime", obj.getPublishEndTime()+"");
                respData.add(map);
            }
        }
        /*
         * 都没匹配到，或者搜索条件至少一个是空的，那么返回最新的10条记录
         */
        //List<GoodsPublishOrder> orderList = new ArrayList<>();
        //List<Map<String, String>> respData = new ArrayList<>();
        //if(!matchSuccess){
        //    Pageable pageable = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id"));
        //    Condition<GoodsPublishOrder> spec = Condition.<GoodsPublishOrder>and().eq("communityId", communityId).eq("status", 0);
        //    orderList = goodsPublishOrderRepository.findAll(spec, pageable).getContent();
        //}else{
        //    for(List<GoodsPublishInfo> list : matchPublishList){
        //        orderList.add(goodsPublishOrderRepository.findOne(list.get(0).getGoodsPublishOrderId()));
        //    }
        //    orderList.sort(new Comparator<GoodsPublishOrder>() {
        //        @Override
        //        public int compare(GoodsPublishOrder o1, GoodsPublishOrder o2) {
        //            if (o1.getPrice().compareTo(o2.getPrice()) != 0) {
        //                return o1.getPrice().compareTo(o2.getPrice());
        //            } else {
        //                return o1.getId().intValue() - o2.getId().intValue();
        //            }
        //        }
        //    });
        //}
        //for(GoodsPublishOrder obj : orderList){
        //    Map<String, String> map = new HashMap<>();
        //    map.put("matchSuccess", matchSuccess+"");
        //    map.put("id", obj.getId()+"");
        //    map.put("price", obj.getPrice().toString());
        //    map.put("carParkImg", obj.getCarParkImg());
        //    map.put("carParkNumber", obj.getCarParkNumber());
        //    map.put("publishFromDates", obj.getPublishFromDates());
        //    map.put("publishFromTime", obj.getPublishFromTime()+"");
        //    map.put("publishEndTime", obj.getPublishEndTime()+"");
        //    respData.add(map);
        //}
        //上面注释是因为它没有处理：客户搜索的车位时间需要两个order拼接的情况
        if(respData.isEmpty()){
            if(matchSuccess || matchCross){
                for(List<GoodsPublishInfo> list : matchPublishList){
                    String ids = "";
                    BigDecimal price = new BigDecimal(0);
                    String publishFromDates = "";
                    List<String> publishFromDateList = new ArrayList<>();
                    for(GoodsPublishInfo obj : list){
                        if(!ids.contains(obj.getGoodsPublishOrderId()+"")){
                            GoodsPublishOrder order = goodsPublishOrderRepository.findOne(obj.getGoodsPublishOrderId());
                            ids = ids + "`" + order.getId();
                            price = price.add(order.getPrice());
                            for(String _fromDate : order.getPublishFromDates().split("-")){
                                if(!publishFromDateList.contains(_fromDate)){
                                    publishFromDateList.add(_fromDate);
                                }
                            }
                        }
                    }
                    publishFromDateList.sort(new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return Integer.parseInt(o1) - Integer.parseInt(o2);
                        }
                    });
                    for(String obj : publishFromDateList){
                        publishFromDates = publishFromDates + "-" + obj;
                    }
                    Map<String, String> map = new HashMap<>();
                    map.put("matchSuccess", matchCross ? "false" : matchSuccess+"");
                    map.put("ids", ids.substring(1));
                    map.put("price", price.toString());
                    map.put("carParkImg", list.get(0).getCarParkImg());
                    map.put("carParkNumber", list.get(0).getCarParkNumber());
                    map.put("publishType", list.get(0).getPublishType()+"");
                    map.put("publishFromDates", publishFromDates.substring(1));
                    map.put("publishFromTime", list.get(0).getPublishFromTime()+"");
                    map.put("publishEndTime", list.get(list.size()-1).getPublishEndTime()+"");
                    respData.add(map);
                }
            }else{
                //都没查到，则根据起止日期查询或者查询最新的10条
                Pageable pageable = new PageRequest(0, 60, new Sort(Sort.Direction.DESC, "id"));
                Condition<GoodsPublishOrder> spec = Condition.<GoodsPublishOrder>and().eq("status", 0);
                if(communityId > 0){
                    spec.eq("communityId", communityId);
                }
                if(publishType > 0){
                    spec.eq("publishType", publishType);
                }
                if(StringUtils.isNotBlank(publishFromDate) && StringUtils.isNotBlank(publishEndDate)){
                    List<String> crossDayList = new ArrayList<>();
                    for(Integer day : DateUtil.getCrossDayList(publishFromDate, publishEndDate)){
                        crossDayList.add(day+"");
                    }
                    crossDayList.add(publishFromDate);
                    crossDayList.add(publishEndDate);
                    spec.like("publishFromDates", crossDayList);
                }
                List<GoodsPublishOrder> orderList = goodsPublishOrderRepository.findAll(spec, pageable).getContent();
                if(orderList.isEmpty()){
                    spec = Condition.<GoodsPublishOrder>and().eq("status", 0);
                    if(communityId > 0){
                        spec.eq("communityId", communityId);
                    }
                    if(publishType > 0){
                        spec.eq("publishType", publishType);
                    }
                    orderList = goodsPublishOrderRepository.findAll(spec, pageable).getContent();
                }else{
                    matchSuccess = true;
                }
                for(GoodsPublishOrder obj : orderList){
                    Map<String, String> map = new HashMap<>();
                    map.put("matchSuccess", matchSuccess+"");
                    map.put("ids", obj.getId()+"");
                    map.put("price", obj.getPrice().toString());
                    map.put("carParkImg", obj.getCarParkImg());
                    map.put("carParkNumber", obj.getCarParkNumber());
                    map.put("publishType", obj.getPublishType()+"");
                    map.put("publishFromDates", obj.getPublishFromDates());
                    map.put("publishFromTime", obj.getPublishFromTime()+"");
                    map.put("publishEndTime", obj.getPublishEndTime()+"");
                    respData.add(map);
                }
            }
        }
        //返回结果排序
        respData.sort(new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                if(!o1.get("publishFromDates").substring(0, 8).equals(o2.get("publishFromDates").substring(0, 8))){
                    return Integer.parseInt(o1.get("publishFromDates").substring(0, 8)) - Integer.parseInt(o2.get("publishFromDates").substring(0, 8));
                }else{
                    return new BigDecimal(o1.get("price")).compareTo(new BigDecimal(o2.get("price")));
                }
            }
        });
        return respData;
    }


    ///**
    // * 匹配列表
    // * @param communityId     小区ID
    // * @param publishFromDate 发布日期（入场日期）
    // * @param publishEndDate  发布日期（出场日期）
    // * @param publishFromTime 发布时间（入场时间）
    // * @param publishEndTime  发布时间（出场时间）
    // */
    //public List<Map<String, String>> matchlist(long communityId, int publishType, String publishFromDate, String publishEndDate, String publishFromTime, String publishEndTime){
    //    //标记该匹配结果是否为交叉匹配
    //    boolean matchCross = false;
    //    //标记是否匹配成功
    //    boolean matchSuccess = false;
    //    List<List<GoodsPublishInfo>> matchPublishList = new ArrayList<>();
    //    if(StringUtils.isNotBlank(publishFromDate) && StringUtils.isNotBlank(publishFromTime) && StringUtils.isNotBlank(publishEndTime)){
    //        if(publishType==3 || (2==publishType && Integer.parseInt(publishEndTime)<Integer.parseInt(publishFromTime))){
    //            try {
    //                publishEndDate = DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(publishFromDate, "yyyyMMdd"), 1), "yyyyMMdd");
    //            } catch (ParseException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    }
    //    if(StringUtils.isNotBlank(publishFromDate) && StringUtils.isNotBlank(publishEndDate) && StringUtils.isNotBlank(publishFromTime) && StringUtils.isNotBlank(publishEndTime)){
    //        int yestoday = 0;
    //        try {
    //            yestoday = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(publishFromDate, "yyyyMMdd"), -1), "yyyyMMdd"));
    //        } catch (ParseException e) {
    //            e.printStackTrace();
    //        }
    //        int fromDate = Integer.parseInt(publishFromDate);
    //        int endDate = Integer.parseInt(publishEndDate);
    //        int fromTime = Integer.parseInt(publishFromTime);
    //        int endTime = Integer.parseInt(publishEndTime);
    //        Date s_date = hhtcHelper.convertToDate(fromDate, fromTime);
    //        Date e_date = hhtcHelper.convertToDate(endDate, endTime);
    //        //重新计算parkType（24小时以内算连续停车，超过24小时为周期停车）
    //        int parkType = 2;
    //        if(DateUtils.addHours(s_date, 24).getTime() < e_date.getTime()){
    //            parkType = 1;
    //        }
    //        //该小区所有的发布车位列表
    //        List<GoodsInfo> goodsList = goodsRepository.findByCommunityIdAndCarAuditStatus(communityId, 2);
    //        for(GoodsInfo goods : goodsList){
    //            //车位有效期判断
    //            if(goods.getCarUsefulEndDate() <= endDate){
    //                continue;
    //            }
    //            //该车位的所有可用发布信息（并过滤掉非包含待发布起始日期的发布信息后break）
    //            List<GoodsPublishInfo> list = goodsPublishRepository.findByGoodsIdAndStatusAndPublishFromDateGreaterThanEqualOrderByIdAsc(goods.getId(), 0, yestoday);
    //            //用户可能在同一天先发布15点的，再发布13点的，所有手工排序一下
    //            list.sort(new Comparator<GoodsPublishInfo>() {
    //                @Override
    //                public int compare(GoodsPublishInfo o1, GoodsPublishInfo o2) {
    //                    if(o1.getPublishFromDate() != o2.getPublishFromDate()){
    //                        return o1.getPublishFromDate() - o2.getPublishFromDate();
    //                    }else{
    //                        return o1.getPublishFromTime() - o2.getPublishFromTime();
    //                    }
    //                }
    //            });
    //            CopyOnWriteArrayList<GoodsPublishInfo> pubList = new CopyOnWriteArrayList<>();
    //            if(!list.isEmpty()){
    //                pubList = new CopyOnWriteArrayList<>(list);
    //                while(!pubList.isEmpty()){
    //                    Date pubSDate = hhtcHelper.convertToDate(pubList.get(0).getPublishFromDate(), pubList.get(0).getPublishFromTime());
    //                    Date pubEDate = hhtcHelper.convertToDate(pubList.get(0).getPublishEndDate(), pubList.get(0).getPublishEndTime());
    //                    if(s_date.compareTo(pubSDate)>=0 && s_date.compareTo(pubEDate)==-1){
    //                        break;
    //                    }else{
    //                        pubList.remove(0);
    //                    }
    //                }
    //            }
    //            /*
    //             * 连续停车
    //             * http://127.0.0.1/wx/goods/publish/matchlist?communityId=1&parkType=2&publishFromDate=20170831&publishEndDate=20170902&publishFromTime=1200&publishEndTime=300
    //             */
    //            if(parkType==2 && !pubList.isEmpty()){
    //                //开始迭代，看看是不是现有发布信息的时间都是连续的（上一条的结束时间等于下一条的起始时间）
    //                List<GoodsPublishInfo> dataList = new ArrayList<>();
    //                for(int i=0; i<pubList.size(); i++){
    //                    Date currEndDate = hhtcHelper.convertToDate(pubList.get(i).getPublishEndDate(), pubList.get(i).getPublishEndTime());
    //                    if(e_date.compareTo(currEndDate) < 1){
    //                        dataList.add(pubList.get(i));
    //                        matchSuccess = true;
    //                        break;
    //                    }
    //                    if(i+1 < pubList.size()){
    //                        Date nextStartDate = hhtcHelper.convertToDate(pubList.get(i+1).getPublishFromDate(), pubList.get(i+1).getPublishFromTime());
    //                        if(currEndDate.getTime() != nextStartDate.getTime()){
    //                            break;
    //                        }
    //                        dataList.add(pubList.get(i));
    //                    }
    //                }
    //                if(matchSuccess && !dataList.isEmpty()){
    //                    matchPublishList.add(dataList);
    //                }
    //            }
    //            /*
    //             * 周期停车
    //             * http://127.0.0.1/wx/goods/publish/matchlist?communityId=1&parkType=1&publishFromDate=20170831&publishEndDate=20170902&publishFromTime=1200&publishEndTime=300
    //             */
    //            if(parkType==1 && !pubList.isEmpty()){
    //                List<GoodsPublishInfo> dataList = new ArrayList<>();
    //                //由于是周期停车，所以先初始化周期的首次起止日期
    //                boolean periodNext = false;
    //                Date periodStartDate = hhtcHelper.convertToDate(fromDate, fromTime);
    //                Date periodEndDate = hhtcHelper.convertToDate(fromDate, endTime);
    //                if(fromTime >= endTime){
    //                    periodEndDate = DateUtils.addDays(periodEndDate, 1);
    //                }
    //                //开始迭代，注意周期时间的变化
    //                for(int i=0; i<pubList.size(); i++){
    //                    //每个周期开始，要重新计算起止日期（但截止日期不能超过用户输入的截止日期）
    //                    if(periodNext){
    //                        periodStartDate = DateUtils.addDays(periodStartDate, 1);
    //                        periodEndDate = DateUtils.addDays(periodEndDate, 1);
    //                        if(periodEndDate.getTime() > e_date.getTime()){
    //                            break;
    //                        }
    //                    }
    //                    //校验时间范围是否包含（尤其是新的周期循环时）
    //                    Date currStartDate = hhtcHelper.convertToDate(pubList.get(i).getPublishFromDate(), pubList.get(i).getPublishFromTime());
    //                    Date currEndDate = hhtcHelper.convertToDate(pubList.get(i).getPublishEndDate(), pubList.get(i).getPublishEndTime());
    //                    if(periodNext){
    //                        if(periodStartDate.compareTo(currEndDate)>=0 || periodEndDate.compareTo(currStartDate)<=0){
    //                            periodNext = false;
    //                            continue;
    //                        }
    //                    }
    //                    //判断是否本周期该结束了
    //                    if(periodEndDate.compareTo(currEndDate) < 1){
    //                        dataList.add(pubList.get(i));
    //                        //全生命周期是否该结束了
    //                        if(e_date.compareTo(currEndDate) < 1){
    //                            matchSuccess = true;
    //                            break;
    //                        }
    //                        //本周期迭代结束
    //                        periodNext = true;
    //                        continue;
    //                    }
    //                    //未完的周期事业，下个循环再续
    //                    if(i+1 < pubList.size()){
    //                        Date nextStartDate = hhtcHelper.convertToDate(pubList.get(i+1).getPublishFromDate(), pubList.get(i+1).getPublishFromTime());
    //                        if(currEndDate.getTime() != nextStartDate.getTime()){
    //                            break;
    //                        }
    //                        periodNext = false;
    //                        dataList.add(pubList.get(i));
    //                    }
    //                }
    //                if(matchSuccess && !dataList.isEmpty()){
    //                    matchPublishList.add(dataList);
    //                }
    //            }
    //        }
    //        //交叉匹配
    //        if(!matchSuccess){
    //            for(GoodsInfo goods : goodsList){
    //                if(goods.getCarUsefulEndDate() <= endDate){
    //                    continue;
    //                }
    //                List<GoodsPublishInfo> list = goodsPublishRepository.findByGoodsIdAndStatusAndPublishFromDateGreaterThanEqualOrderByIdAsc(goods.getId(), 0, yestoday);
    //                list.sort(new Comparator<GoodsPublishInfo>() {
    //                    @Override
    //                    public int compare(GoodsPublishInfo o1, GoodsPublishInfo o2) {
    //                        if(o1.getPublishFromDate() != o2.getPublishFromDate()){
    //                            return o1.getPublishFromDate() - o2.getPublishFromDate();
    //                        }else{
    //                            return o1.getPublishFromTime() - o2.getPublishFromTime();
    //                        }
    //                    }
    //                });
    //                CopyOnWriteArrayList<GoodsPublishInfo> pubList = new CopyOnWriteArrayList<>();
    //                if(!list.isEmpty()){
    //                    pubList = new CopyOnWriteArrayList<>(list);
    //                    while(!pubList.isEmpty()){
    //                        Date pubSDate = hhtcHelper.convertToDate(pubList.get(0).getPublishFromDate(), pubList.get(0).getPublishFromTime());
    //                        Date pubEDate = hhtcHelper.convertToDate(pubList.get(0).getPublishEndDate(), pubList.get(0).getPublishEndTime());
    //                        if(s_date.compareTo(pubSDate)>=0 && s_date.compareTo(pubEDate)==-1){
    //                            break;
    //                        }else{
    //                            pubList.remove(0);
    //                        }
    //                    }
    //                }
    //                List<GoodsPublishInfo> dataList = new ArrayList<>();
    //                for(GoodsPublishInfo pub : pubList){
    //                    dataList.add(pub);
    //                }
    //                if(!dataList.isEmpty()){
    //                    matchCross = true;
    //                    matchPublishList.add(dataList);
    //                }
    //            }
    //        }
    //    }
    //    /*
    //     * 都没匹配到，或者搜索条件至少一个是空的，那么返回最新的10条记录
    //     */
    //    //List<GoodsPublishOrder> orderList = new ArrayList<>();
    //    //List<Map<String, String>> respData = new ArrayList<>();
    //    //if(!matchSuccess){
    //    //    Pageable pageable = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id"));
    //    //    Condition<GoodsPublishOrder> spec = Condition.<GoodsPublishOrder>and().eq("communityId", communityId).eq("status", 0);
    //    //    orderList = goodsPublishOrderRepository.findAll(spec, pageable).getContent();
    //    //}else{
    //    //    for(List<GoodsPublishInfo> list : matchPublishList){
    //    //        orderList.add(goodsPublishOrderRepository.findOne(list.get(0).getGoodsPublishOrderId()));
    //    //    }
    //    //    orderList.sort(new Comparator<GoodsPublishOrder>() {
    //    //        @Override
    //    //        public int compare(GoodsPublishOrder o1, GoodsPublishOrder o2) {
    //    //            if (o1.getPrice().compareTo(o2.getPrice()) != 0) {
    //    //                return o1.getPrice().compareTo(o2.getPrice());
    //    //            } else {
    //    //                return o1.getId().intValue() - o2.getId().intValue();
    //    //            }
    //    //        }
    //    //    });
    //    //}
    //    //for(GoodsPublishOrder obj : orderList){
    //    //    Map<String, String> map = new HashMap<>();
    //    //    map.put("matchSuccess", matchSuccess+"");
    //    //    map.put("id", obj.getId()+"");
    //    //    map.put("price", obj.getPrice().toString());
    //    //    map.put("carParkImg", obj.getCarParkImg());
    //    //    map.put("carParkNumber", obj.getCarParkNumber());
    //    //    map.put("publishFromDates", obj.getPublishFromDates());
    //    //    map.put("publishFromTime", obj.getPublishFromTime()+"");
    //    //    map.put("publishEndTime", obj.getPublishEndTime()+"");
    //    //    respData.add(map);
    //    //}
    //    //上面注释是因为它没有处理：客户搜索的车位时间需要两个order拼接的情况
    //    List<Map<String, String>> respData = new ArrayList<>();
    //    if(matchCross || matchSuccess){
    //        for(List<GoodsPublishInfo> list : matchPublishList){
    //            String ids = "";
    //            BigDecimal price = new BigDecimal(0);
    //            String publishFromDates = "";
    //            List<String> publishFromDateList = new ArrayList<>();
    //            for(GoodsPublishInfo obj : list){
    //                if(!ids.contains(obj.getGoodsPublishOrderId()+"")){
    //                    GoodsPublishOrder order = goodsPublishOrderRepository.findOne(obj.getGoodsPublishOrderId());
    //                    ids = ids + "`" + order.getId();
    //                    price = price.add(order.getPrice());
    //                    for(String _fromDate : order.getPublishFromDates().split("-")){
    //                        if(!publishFromDateList.contains(_fromDate)){
    //                            publishFromDateList.add(_fromDate);
    //                        }
    //                    }
    //                }
    //            }
    //            publishFromDateList.sort(new Comparator<String>() {
    //                @Override
    //                public int compare(String o1, String o2) {
    //                    return Integer.parseInt(o1) - Integer.parseInt(o2);
    //                }
    //            });
    //            for(String obj : publishFromDateList){
    //                publishFromDates = publishFromDates + "-" + obj;
    //            }
    //            Map<String, String> map = new HashMap<>();
    //            map.put("matchCross", matchCross+"");
    //            map.put("matchSuccess", matchCross ? "false" : "true");
    //            map.put("ids", ids.substring(1));
    //            map.put("price", price.toString());
    //            map.put("carParkImg", list.get(0).getCarParkImg());
    //            map.put("carParkNumber", list.get(0).getCarParkNumber());
    //            map.put("publishType", list.get(0).getPublishType()+"");
    //            map.put("publishFromDates", publishFromDates.substring(1));
    //            map.put("publishFromTime", list.get(0).getPublishFromTime()+"");
    //            map.put("publishEndTime", list.get(list.size()-1).getPublishEndTime()+"");
    //            respData.add(map);
    //        }
    //    }else{
    //        //List<GoodsPublishOrder> orderList = new ArrayList<>();
    //        //if(StringUtils.isNotBlank(publishFromDate) && StringUtils.isNotBlank(publishEndDate)){
    //        //    if(communityId>0 && publishType>0){
    //        //        matchSuccess = true;
    //        //        orderList = goodsPublishOrderRepository.queryForMatchList01(communityId, publishType, publishFromDate, publishEndDate);
    //        //    }
    //        //    if(communityId>0 && publishType==0){
    //        //        matchSuccess = true;
    //        //        orderList = goodsPublishOrderRepository.queryForMatchList02(communityId, publishFromDate, publishEndDate);
    //        //    }
    //        //    if(communityId==0 && publishType>0){
    //        //        matchSuccess = true;
    //        //        orderList = goodsPublishOrderRepository.queryForMatchList03(publishType, publishFromDate, publishEndDate);
    //        //    }
    //        //    if(communityId==0 && publishType==0){
    //        //        matchSuccess = true;
    //        //        orderList = goodsPublishOrderRepository.queryForMatchList04(publishFromDate, publishEndDate);
    //        //    }
    //        //}else{
    //        Pageable pageable = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id"));
    //        Condition<GoodsPublishOrder> spec = Condition.<GoodsPublishOrder>and().eq("status", 0);
    //        if(communityId > 0){
    //            matchSuccess = true;
    //            spec.eq("communityId", communityId);
    //        }
    //        if(publishType > 0){
    //            matchSuccess = true;
    //            spec.eq("publishType", publishType);
    //        }
    //        if(StringUtils.isNotBlank(publishFromDate) && StringUtils.isNotBlank(publishEndDate)){
    //            matchSuccess = true;
    //            spec.like("publishFromDates", Arrays.asList(publishFromDate, publishEndDate));
    //        }
    //        if(StringUtils.isNotBlank(publishFromTime) && StringUtils.isNotBlank(publishEndTime)){
    //            matchSuccess = true;
    //            spec.eq("publishFromTime", Integer.parseInt(publishFromTime));
    //            spec.eq("publishEndTime", Integer.parseInt(publishEndTime));
    //        }
    //        List<GoodsPublishOrder> orderList = goodsPublishOrderRepository.findAll(spec, pageable).getContent();
    //        //}
    //        for(GoodsPublishOrder obj : orderList){
    //            Map<String, String> map = new HashMap<>();
    //            map.put("matchSuccess", matchSuccess+"");
    //            map.put("ids", obj.getId()+"");
    //            map.put("price", obj.getPrice().toString());
    //            map.put("carParkImg", obj.getCarParkImg());
    //            map.put("carParkNumber", obj.getCarParkNumber());
    //            map.put("publishType", obj.getPublishType()+"");
    //            map.put("publishFromDates", obj.getPublishFromDates());
    //            map.put("publishFromTime", obj.getPublishFromTime()+"");
    //            map.put("publishEndTime", obj.getPublishEndTime()+"");
    //            respData.add(map);
    //        }
    //    }
    //    //返回结果排序
    //    respData.sort(new Comparator<Map<String, String>>() {
    //        @Override
    //        public int compare(Map<String, String> o1, Map<String, String> o2) {
    //            if(!o1.get("publishFromDates").substring(0, 8).equals(o2.get("publishFromDates").substring(0, 8))){
    //                return Integer.parseInt(o1.get("publishFromDates").substring(0, 8)) - Integer.parseInt(o2.get("publishFromDates").substring(0, 8));
    //            }else{
    //                return new BigDecimal(o1.get("price")).compareTo(new BigDecimal(o2.get("price")));
    //            }
    //        }
    //    });
    //    return respData;
    //}
}