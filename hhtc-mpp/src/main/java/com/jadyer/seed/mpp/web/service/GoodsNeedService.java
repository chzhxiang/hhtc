package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.BeanUtil;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.MoneyUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.GoodsNeedHistory;
import com.jadyer.seed.mpp.web.model.GoodsNeedInfo;
import com.jadyer.seed.mpp.web.model.GoodsPublishInfo;
import com.jadyer.seed.mpp.web.model.GoodsPublishOrder;
import com.jadyer.seed.mpp.web.model.OrderInfo;
import com.jadyer.seed.mpp.web.model.UserFunds;
import com.jadyer.seed.mpp.web.model.UserFundsFlow;
import com.jadyer.seed.mpp.web.repository.GoodsNeedHistoryRepository;
import com.jadyer.seed.mpp.web.repository.GoodsNeedRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishOrderRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishRepository;
import com.jadyer.seed.mpp.web.repository.MppUserInfoRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/26 14:42.
 */
@Service
public class GoodsNeedService {
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Value("${hhtc.publishTime.day}")
    private int timeDay;
    @Value("${hhtc.publishTime.night}")
    private int timeNight;
    @Value("${hhtc.wxtemplateUrl.needsucc}")
    private String templateUrlNeed;
    @Value("${hhtc.wxtemplateUrl.needfail}")
    private String templateUrlNeedFail;
    @Value("${hhtc.needExpire.minute}")
    private int needExpireMinute;
    @Value("${hhtc.portalUrl.center}")
    private String portalCenterUrl;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private OrderService orderService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private CommunityService communityService;
    @Resource
    private OrderRentService orderRentService;
    @Resource
    private OrderInoutService orderInoutService;
    @Resource
    private GoodsPublishService goodsPublishService;
    @Resource
    private GoodsNeedRepository goodsNeedRepository;
    @Resource
    private UserFundsFlowService userFundsFlowService;
    @Resource
    private MppUserInfoRepository mppUserInfoRepository;
    @Resource
    private GoodsPublishRepository goodsPublishRepository;
    @Resource
    private GoodsPublishOrderService goodsPublishOrderService;
    @Resource
    private GoodsNeedHistoryRepository goodsNeedHistoryRepository;
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;

    public GoodsNeedInfo get(long id){
        return goodsNeedRepository.findOne(id);
    }


    public Page<GoodsNeedInfo> listViaPage(String openid, String pageNo) {
        Sort sort = new Sort(Sort.Direction.DESC, "status");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        Condition<GoodsNeedInfo> spec = Condition.<GoodsNeedInfo>and().eq("openid", openid);
        return goodsNeedRepository.findAll(spec, pageable);
    }


    /**
     * 发布需求
     */
    @Transactional(rollbackFor=Exception.class)
    public GoodsNeedInfo add(String appid, String openid, long communityId, String carNumber, BigDecimal price, int needType, int needFromTime, int needEndTime, int needFromDate, int needEndDate){
        hhtcHelper.verifyOfTimeCross(needFromDate, communityId, carNumber, needFromTime, needEndTime);
        UserFunds funds = userFundsService.subtractMoneyBalanceForFans(openid, price);
        UserFundsFlow fundsFlow = new UserFundsFlow();
        fundsFlow.setFundsId(funds.getId());
        fundsFlow.setOpenid(openid);
        fundsFlow.setMoney(price);
        fundsFlow.setInOut("out");
        fundsFlow.setInOutDesc("发布需求时扣减余额");
        fundsFlow.setInOutType(4);
        fundsFlow.setBizDate(Integer.parseInt(DateUtil.getCurrentDate()));
        fundsFlow.setBizDateTime(new Date());
        userFundsFlowService.upsert(fundsFlow);
        GoodsNeedInfo need = new GoodsNeedInfo();
        need.setAppid(appid);
        need.setOpenid(openid);
        need.setCommunityId(communityId);
        need.setCarNumber(carNumber);
        need.setNeedType(needType);
        need.setNeedFromTime(needFromTime);
        need.setNeedEndTime(needEndTime);
        need.setNeedFromDate(needFromDate);
        need.setNeedEndDate(needEndDate);
        need.setStatus(1);
        need.setMoneyRent(price);
        return goodsNeedRepository.saveAndFlush(need);
    }


    private void history(){
        int currDate = Integer.parseInt(DateUtil.getCurrentDate());
        int expireTime = Integer.parseInt(DateFormatUtils.format(DateUtils.addMinutes(new Date(), this.needExpireMinute), "HHmm"));
        List<GoodsNeedInfo> needExpireList = goodsNeedRepository.findByStatusAndNeedFromDateAndNeedFromTimeLessThanEqual(1, currDate, expireTime);
        String currIndex = "1";
        int len = needExpireList.size();
        if(len == 0){
            return;
        }
        LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：归档处理-->查到待处理记录[{}]条", len);
        for(GoodsNeedInfo need : needExpireList){
            currIndex = JadyerUtil.leftPadUseZero(currIndex, String.valueOf(len).length());
            GoodsNeedHistory history = new GoodsNeedHistory();
            BeanUtil.copyProperties(need, history);
            history.setId(null);
            history.setNeedId(need.getId());
            goodsNeedHistoryRepository.saveAndFlush(history);
            goodsNeedRepository.delete(need.getId());
            UserFunds funds = userFundsService.addMoneyBalanceForFans(need.getOpenid(), need.getMoneyRent());
            UserFundsFlow fundsFlow = new UserFundsFlow();
            fundsFlow.setFundsId(funds.getId());
            fundsFlow.setOpenid(need.getOpenid());
            fundsFlow.setMoney(need.getMoneyRent());
            fundsFlow.setInOut("in");
            fundsFlow.setInOutDesc("车主需求未匹配合适车位时返还余额");
            fundsFlow.setInOutType(6);
            fundsFlow.setBizDate(Integer.parseInt(DateUtil.getCurrentDate()));
            fundsFlow.setBizDateTime(new Date());
            userFundsFlowService.upsert(fundsFlow);
            /*
            {{first.DATA}}
            预约内容：{{keyword1.DATA}}
            预约时间：{{keyword2.DATA}}
            {{remark.DATA}}

            抱歉，您预约已超过名额。
            预约内容：车检
            预约时间：2017年7月02日 15:36
            请及时与客服联系！感谢你的使用。
            */
            WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
            dataItem.put("first", new WeixinTemplateMsg.DItem("尊敬的车主，系统没有找到合适您的车位"));
            dataItem.put("keyword1", new WeixinTemplateMsg.DItem("主动停车需求"));
            dataItem.put("keyword2", new WeixinTemplateMsg.DItem(DateFormatUtils.format(hhtcHelper.convertToDate(need.getNeedFromDate(), need.getNeedFromTime()), "yyyy-MM-dd HH:mm")));
            dataItem.put("remark", new WeixinTemplateMsg.DItem("可点击详情查看系统推荐车位，感谢您的使用！"));
            String url = this.hhtcContextPath + this.templateUrlNeedFail.replace("{type}", need.getNeedType()+"").replace("{beginDay}", need.getNeedFromDate()+"").replace("{endDay}", need.getNeedEndDate()+"").replace("{beginTime}", need.getNeedFromTime()+"").replace("{endTime}", need.getNeedEndTime()+"");
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+need.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+need.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
            WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
            templateMsg.setTemplate_id("eiBoQwb-2OHcg_pLqCWdeVBd08KDVY57VdOgGxQAOp4");
            templateMsg.setUrl(url);
            templateMsg.setTouser(need.getOpenid());
            templateMsg.setData(dataItem);
            WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(need.getAppid()), templateMsg);
            LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：归档处理-->处理完毕[{}-{}]条", len, currIndex);
            currIndex = String.valueOf(Integer.parseInt(currIndex) + 1);
        }
    }


    private void newPublishOrder(int type, GoodsPublishInfo source, GoodsPublishOrder order, int newFromTime, int newEndTime, int newFromDate, int newEndDate, int len, String currIndex, long needId){
        GoodsPublishOrder order01 = new GoodsPublishOrder();
        //每段至少一个小时
        if(DateUtils.addHours(hhtcHelper.convertToDate(newFromDate, newFromTime), 1).getTime() > hhtcHelper.convertToDate(newEndDate, newEndTime).getTime()){
            return;
        }
        BeanUtil.copyProperties(order, order01);
        order01.setId(null);
        order01.setGoodsPublishIds("");
        order01.setPrice(hhtcHelper.calcPrice(order.getCommunityId(), type));
        order01.setPublishType(type);
        order01.setPublishFromDates(newFromDate+"");
        order01.setPublishFromTime(newFromTime);
        order01.setPublishEndTime(newEndTime);
        order01.setFromType(3);
        order01.setFromId(order.getId());
        order01.setStatus(0);
        order01 = goodsPublishOrderRepository.saveAndFlush(order01);
        GoodsPublishInfo p1 = new GoodsPublishInfo();
        BeanUtil.copyProperties(source, p1);
        p1.setId(null);
        p1.setGoodsPublishOrderId(order01.getId());
        p1.setPrice(order01.getPrice());
        p1.setPublishType(type);
        p1.setPublishFromDate(newFromDate);
        p1.setPublishEndDate(newEndDate);
        p1.setPublishFromTime(newFromTime);
        p1.setPublishEndTime(newEndTime);
        p1.setFromType(3);
        p1.setFromIds(source.getId()+"");
        p1.setStatus(0);
        p1 = goodsPublishService.upsert(p1);
        order01.setGoodsPublishIds(p1.getId()+"");
        order01 = goodsPublishOrderRepository.saveAndFlush(order01);
        LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：匹配处理-->开始处理[{}-{}]：needId={}：切割出新发布orderId={}", len, currIndex, needId, order01.getId());
    }


    /**
     * 定时任务：车位需求匹配（一个小区的一个车位的严格匹配）
     */
    @Transactional(rollbackFor=Exception.class)
    public void needMatch() {
        //先处理预过期需求
        this.history();
        //再进行匹配
        List<GoodsNeedInfo> needList = goodsNeedRepository.findByNeedFromDateGreaterThanEqualAndStatus(Integer.parseInt(DateUtil.getCurrentDate()), 1);
        String currIndex = "1";
        int len = needList.size();
        if(len == 0){
            return;
        }
        LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：匹配处理-->查到待匹配记录[{}]条", len);
        for(GoodsNeedInfo need : needList){
            currIndex = JadyerUtil.leftPadUseZero(currIndex, String.valueOf(len).length());
            LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：匹配处理-->开始处理[{}-{}]：needId={}：开始精确匹配", len, currIndex, need.getId());
            //匹配类型：0--未匹配，1--精确匹配成功，2--包裹匹配成功
            int matchType = 0;
            //匹配到的发布信息
            GoodsPublishInfo publish = new GoodsPublishInfo();
            //精确匹配
            Pageable pageable = new PageRequest(0, 1, new Sort(Sort.Direction.ASC, "id"));
            Condition<GoodsPublishInfo> spec = Condition.and();
            spec.eq("communityId", need.getCommunityId());
            spec.eq("status", 0);
            spec.eq("price", need.getMoneyRent());
            spec.eq("publishFromDate", need.getNeedFromDate());
            spec.eq("publishFromTime", need.getNeedFromTime());
            spec.eq("publishEndTime", need.getNeedEndTime());
            Page<GoodsPublishInfo> page = goodsPublishRepository.findAll(spec, pageable);
            if(page.getTotalElements() == 1){
                matchType = 1;
                BeanUtil.copyProperties(page.getContent().get(0), publish);
                publish.setId(page.getContent().get(0).getId());
                LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：匹配处理-->开始处理[{}-{}]：needId={}：精确匹配成功：publishId={}", len, currIndex, need.getId(), publish.getId());
            }
            //包裹匹配
            if(matchType == 0){
                LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：匹配处理-->开始处理[{}-{}]：needId={}：开始包裹匹配", len, currIndex, need.getId());
                int yestoday = 0;
                try {
                    yestoday = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(need.getNeedFromDate()+"", "yyyyMMdd"), -1), "yyyyMMdd"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //把当天发布和昨天发布的都查出来（昨天的可能包含今天0--900的时间）（注意：价格最便宜的会最先被匹配到或切割）
                List<GoodsPublishInfo> list = goodsPublishRepository.findByCommunityIdAndStatusAndPriceGreaterThanEqualAndPublishFromDateInOrderByPriceAsc(need.getCommunityId(), 0, need.getMoneyRent(), Arrays.asList(need.getNeedFromDate(), yestoday));
                for(GoodsPublishInfo obj : list){
                    Date objStartDate = hhtcHelper.convertToDate(obj.getPublishFromDate(), obj.getPublishFromTime());
                    Date objEndDate = hhtcHelper.convertToDate(obj.getPublishEndDate(), obj.getPublishEndTime());
                    Date needStartDate = hhtcHelper.convertToDate(need.getNeedFromDate(), need.getNeedFromTime());
                    Date needEndDate = hhtcHelper.convertToDate(need.getNeedEndDate(), need.getNeedEndTime());
                    if(objStartDate.compareTo(needStartDate)<1 && objEndDate.compareTo(needEndDate)>=0){
                        matchType = 2;
                        BeanUtil.copyProperties(obj, publish);
                        publish.setId(obj.getId());
                        LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：匹配处理-->开始处理[{}-{}]：needId={}：包裹匹配成功", len, currIndex, need.getId(), publish.getId());
                        break;
                    }
                }
            }
            if(matchType > 0){
                //计算GoodsPublishOrderIds（防止下单成功后再转租出现的那个问题）
                String goodsPublishOrderIds = publish.getGoodsPublishOrderId()+"";
                GoodsPublishOrder order = goodsPublishOrderRepository.findOne(publish.getGoodsPublishOrderId());
                if(matchType==2 || order.getGoodsPublishIds().contains("`")){
                    GoodsPublishOrder po01 = new GoodsPublishOrder();
                    BeanUtil.copyProperties(order, po01);
                    po01.setGoodsPublishIds("");
                    po01.setPrice(new BigDecimal(0));
                    po01.setPublishFromDates(need.getNeedFromDate()+"");
                    po01.setPublishFromTime(need.getNeedFromTime());
                    po01.setPublishEndTime(need.getNeedEndTime());
                    po01.setFromType(3);
                    po01.setFromId(order.getId());
                    po01.setStatus(2);
                    po01 = goodsPublishOrderRepository.saveAndFlush(po01);
                    goodsPublishOrderIds = po01.getId()+"";
                    GoodsPublishInfo p01 = new GoodsPublishInfo();
                    BeanUtil.copyProperties(order, p01);
                    p01.setId(null);
                    p01.setPrice(hhtcHelper.calcPrice(need.getCommunityId(), need.getNeedType()));
                    p01.setGoodsPublishOrderId(po01.getId());
                    p01.setPublishFromTime(need.getNeedFromTime());
                    p01.setPublishEndTime(need.getNeedEndTime());
                    p01.setPublishFromDate(need.getNeedFromDate());
                    if(need.getNeedType()==3 || (2==need.getNeedType() && need.getNeedEndTime()<need.getNeedFromTime())){
                        try {
                            p01.setPublishEndDate(Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(need.getNeedFromDate()+"", "yyyyMMdd"), 1), "yyyyMMdd")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        p01.setPublishEndDate(need.getNeedFromDate());
                    }
                    p01.setFromType(3);
                    p01.setFromIds(publish.getId()+"");
                    p01.setStatus(2);
                    p01 = goodsPublishRepository.saveAndFlush(p01);
                    po01.setGoodsPublishIds(p01.getId()+"");
                    po01.setPrice(p01.getPrice());
                    goodsPublishOrderRepository.saveAndFlush(po01);
                }
                //新增订单、修改车位信息、更新需求、分润
                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setCommunityId(need.getCommunityId());
                orderInfo.setCommunityName(communityService.get(need.getCommunityId()).getName());
                orderInfo.setGoodsId(publish.getGoodsId());
                orderInfo.setGoodsPublishOrderIds(goodsPublishOrderIds);
                orderInfo.setGoodsNeedId(need.getId());
                orderInfo.setCarParkNumber(publish.getCarParkNumber());
                orderInfo.setCarParkImg(publish.getCarParkImg());
                orderInfo.setCarNumber(need.getCarNumber());
                orderInfo.setOpenType(need.getNeedType());
                orderInfo.setOpenFromDates(need.getNeedFromDate()+"");
                orderInfo.setOpenFromTime(need.getNeedFromTime());
                orderInfo.setOpenEndTime(need.getNeedEndTime());
                orderInfo.setAppid(need.getAppid());
                orderInfo.setOutTradeNo(hhtcHelper.buildOrderNo(9));
                orderInfo.setTotalFee(Long.parseLong(MoneyUtil.yuanToFen(need.getMoneyRent().toString())));
                orderInfo.setDepositMoney(new BigDecimal(0));
                orderInfo.setCanRefundMoney(new BigDecimal(0));
                orderInfo.setOpenid(need.getOpenid());
                orderInfo.setOrderType(2);
                orderInfo.setOrderStatus(2);
                orderInfo = orderService.upsert(orderInfo);
                goodsService.updateStatus(orderInfo.getGoodsId(), 2, 1);
                goodsPublishRepository.updateStatus(publish.getId(), 2);
                goodsPublishOrderRepository.updateStatus(publish.getGoodsPublishOrderId(), 2);
                orderInoutService.initInout(orderInfo, publish.getOpenid(), null);
                need.setStatus(2);
                need.setGoodsPublishOrderId(Long.parseLong(goodsPublishOrderIds));
                goodsNeedRepository.saveAndFlush(need);
                BigDecimal moneyCarparker = orderRentService.rent(2, publish.getOpenid(), orderInfo);
                /*
                 * 切割原发布信息
                 */
                String[] pubIds = order.getGoodsPublishIds().split("`");
                if(pubIds.length > 1){
                    StringBuilder fromDates01 = new StringBuilder();
                    StringBuilder fromDates02 = new StringBuilder();
                    BigDecimal price01 = new BigDecimal(0);
                    BigDecimal price02 = new BigDecimal(0);
                    List<GoodsPublishInfo> pubList01 = new ArrayList<>();
                    List<GoodsPublishInfo> pubList02 = new ArrayList<>();
                    for(String obj : pubIds){
                        if(obj.equals(publish.getId()+"")){
                            break;
                        }
                        GoodsPublishInfo pub01 = goodsPublishService.get(Long.parseLong(obj));
                        fromDates01.append("-").append(pub01.getPublishFromDate());
                        price01 = price01.add(pub01.getPrice());
                        pubList01.add(pub01);
                    }
                    for(String obj : pubIds){
                        if(Long.parseLong(obj) > publish.getId()){
                            GoodsPublishInfo pub02 = goodsPublishService.get(Long.parseLong(obj));
                            fromDates02.append("-").append(pub02.getPublishFromDate());
                            price02 = price02.add(pub02.getPrice());
                            pubList02.add(pub02);
                        }
                    }
                    if(StringUtils.isNotBlank(fromDates01.toString())){
                        GoodsPublishOrder order01 = new GoodsPublishOrder();
                        BeanUtil.copyProperties(order, order01);
                        order01.setGoodsPublishIds("");
                        order01.setPrice(price01);
                        order01.setPublishFromDates(fromDates01.toString().substring(1));
                        order01.setFromType(3);
                        order01.setFromId(order.getId());
                        order01.setStatus(0);
                        order01 = goodsPublishOrderRepository.saveAndFlush(order01);
                        StringBuilder p1ids = new StringBuilder();
                        for(GoodsPublishInfo obj01 : pubList01){
                            GoodsPublishInfo p1 = new GoodsPublishInfo();
                            BeanUtil.copyProperties(obj01, p1);
                            p1.setId(null);
                            p1.setGoodsPublishOrderId(order01.getId());
                            p1.setFromType(3);
                            p1.setFromIds(obj01.getId()+"");
                            p1.setStatus(0);
                            p1 = goodsPublishService.upsert(p1);
                            p1ids.append("`").append(p1.getId());
                            obj01.setStatus(2);
                            goodsPublishService.upsert(obj01);
                        }
                        order01.setGoodsPublishIds(p1ids.toString().substring(1));
                        order01 = goodsPublishOrderRepository.saveAndFlush(order01);
                        LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：匹配处理-->开始处理[{}-{}]：needId={}：切割出-新发布orderId={}", len, currIndex, need.getId(), order01.getId());
                    }
                    if(StringUtils.isNotBlank(fromDates02.toString())){
                        GoodsPublishOrder order02 = new GoodsPublishOrder();
                        BeanUtil.copyProperties(order, order02);
                        order02.setGoodsPublishIds("");
                        order02.setPrice(price02);
                        order02.setPublishFromDates(fromDates02.toString().substring(1));
                        order02.setFromType(3);
                        order02.setFromId(order.getId());
                        order02.setStatus(0);
                        order02 = goodsPublishOrderRepository.saveAndFlush(order02);
                        StringBuilder p2ids = new StringBuilder();
                        for(GoodsPublishInfo obj02 : pubList02){
                            GoodsPublishInfo p2 = new GoodsPublishInfo();
                            BeanUtil.copyProperties(obj02, p2);
                            p2.setId(null);
                            p2.setGoodsPublishOrderId(order02.getId());
                            p2.setFromType(3);
                            p2.setFromIds(obj02.getId()+"");
                            p2.setStatus(0);
                            p2 = goodsPublishService.upsert(p2);
                            p2ids.append("`").append(p2.getId());
                            obj02.setStatus(2);
                            goodsPublishService.upsert(obj02);
                        }
                        order02.setGoodsPublishIds(p2ids.toString().substring(1));
                        order02 = goodsPublishOrderRepository.saveAndFlush(order02);
                        LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：匹配处理-->开始处理[{}-{}]：needId={}：切割出-新发布orderId={}", len, currIndex, need.getId(), order02.getId());
                    }
                }
                Date objStartDate = hhtcHelper.convertToDate(publish.getPublishFromDate(), publish.getPublishFromTime());
                Date objEndDate = hhtcHelper.convertToDate(publish.getPublishEndDate(), publish.getPublishEndTime());
                Date needStartDate = hhtcHelper.convertToDate(need.getNeedFromDate(), need.getNeedFromTime());
                Date needEndDate = hhtcHelper.convertToDate(need.getNeedEndDate(), need.getNeedEndTime());
                if(objStartDate.compareTo(needStartDate) < 0){
                    int type = hhtcHelper.calcPublishTypeExt(publish.getPublishFromTime(), need.getNeedFromTime());
                    switch(type){
                        case 1 :
                        case 2 :
                        case 3 :
                            this.newPublishOrder(type, publish, order, publish.getPublishFromTime(), need.getNeedFromTime(), publish.getPublishFromDate(), need.getNeedFromDate(), len, currIndex, need.getId());
                            break;
                        case 4 :
                            //横跨（日间）
                            this.newPublishOrder(2, publish, order, publish.getPublishFromTime(), this.timeDay, publish.getPublishFromDate(), need.getNeedFromDate(), len, currIndex, need.getId());
                            this.newPublishOrder(1, publish, order, this.timeDay, need.getNeedFromTime(), publish.getPublishFromDate(), need.getNeedFromDate(), len, currIndex, need.getId());
                            break;
                        case 5 :
                            //横跨（夜间）
                            this.newPublishOrder(1, publish, order, publish.getPublishFromTime(), this.timeNight, publish.getPublishFromDate(), need.getNeedFromDate(), len, currIndex, need.getId());
                            this.newPublishOrder(2, publish, order, this.timeNight, need.getNeedFromTime(), publish.getPublishFromDate(), need.getNeedFromDate(), len, currIndex, need.getId());
                            break;
                        case 6 :
                            LogUtil.logToTask().error("致命异常：定时任务：车位需求匹配：匹配处理-->正在处理[{}-{}]：需求[{}]匹配到publishId=[{}]：计算切割后的发布类型，得值：[{}]", len, currIndex, need.getId(), publish.getId(), type);
                            break;
                    }
                }
                if(objEndDate.compareTo(needEndDate) > 0){
                    int type = hhtcHelper.calcPublishTypeExt(need.getNeedEndTime(), publish.getPublishEndTime());
                    switch(type){
                        case 1 :
                        case 2 :
                        case 3 :
                            this.newPublishOrder(type, publish, order, need.getNeedEndTime(), publish.getPublishEndTime(), need.getNeedEndDate(), publish.getPublishEndDate(), len, currIndex, need.getId());
                            break;
                        case 4 :
                            //横跨（日间）
                            this.newPublishOrder(2, publish, order, need.getNeedEndTime(), this.timeDay, need.getNeedEndDate(), publish.getPublishEndDate(), len, currIndex, need.getId());
                            this.newPublishOrder(1, publish, order, this.timeDay, publish.getPublishEndTime(), need.getNeedEndDate(), publish.getPublishEndDate(), len, currIndex, need.getId());
                            break;
                        case 5 :
                            //横跨（夜间）
                            this.newPublishOrder(1, publish, order, need.getNeedEndTime(), this.timeNight, need.getNeedEndDate(), publish.getPublishEndDate(), len, currIndex, need.getId());
                            this.newPublishOrder(2, publish, order, this.timeNight, publish.getPublishEndTime(), need.getNeedEndDate(), publish.getPublishEndDate(), len, currIndex, need.getId());
                            break;
                        case 6 :
                            LogUtil.logToTask().error("致命异常：定时任务：车位需求匹配：匹配处理-->正在处理[{}-{}]：需求[{}]匹配到publishId=[{}]：计算切割后的发布类型，得值：[{}]", len, currIndex, need.getId(), publish.getId(), type);
                            break;
                    }
                }
                //模版CODE: SMS_86540104（車主）
                //模版内容: 尊敬的手机尾号为${phone}的用户：根据您的停车需求，系统已成功为您匹配到与您需求相符的车位，车位信息：${carpark}
                //模版CODE: SMS_86510101（車位主）
                //模版内容: 尊敬的手机尾号为${phone}的用户：您的${carpark}车位已成功与车主需求匹配，并自动达成交易，${money}停车收益已到账。
                String phone = fansService.getByOpenid(need.getOpenid()).getPhoneNo();
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("phone", phone.substring(7, 11));
                paramMap.put("carpark", order.getCarParkNumber());
                hhtcHelper.sendSms(phone, "SMS_86540104", paramMap);
                phone = fansService.getByOpenid(publish.getOpenid()).getPhoneNo();
                paramMap = new HashMap<>();
                paramMap.put("phone", phone.substring(7, 11));
                paramMap.put("carpark", order.getCarParkNumber());
                paramMap.put("money", moneyCarparker.toString() + "元");
                hhtcHelper.sendSms(phone, "SMS_86510101", paramMap);
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
                url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+need.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+need.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
                WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
                templateMsg.setTemplate_id("vkWecSSZW6qJQ4qXXX1iH7QaRvV1HJrcmky208AKx88");
                templateMsg.setUrl(url);
                templateMsg.setTouser(need.getOpenid());
                templateMsg.setData(dataItem);
                WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(need.getAppid()), templateMsg);
                dataItem = new WeixinTemplateMsg.DataItem();
                dataItem.put("first", new WeixinTemplateMsg.DItem("尊敬的车位主，您的车位已被预约："+orderInfo.getCommunityName()+"-" +orderInfo.getCarParkNumber()));
                dataItem.put("keyword1", new WeixinTemplateMsg.DItem(orderInfo.getOutTradeNo()));
                dataItem.put("keyword2", new WeixinTemplateMsg.DItem(orderInfo.getCarParkNumber()));
                dataItem.put("keyword3", new WeixinTemplateMsg.DItem(orderInfo.getCarNumber()));
                dataItem.put("keyword4", new WeixinTemplateMsg.DItem(DateFormatUtils.format(hhtcHelper.convertToDate(hhtcHelper.calcOrderFromDate(orderInfo), orderInfo.getOpenFromTime()), "yyyy-MM-dd HH:mm")));
                dataItem.put("keyword5", new WeixinTemplateMsg.DItem(DateFormatUtils.format(hhtcHelper.convertToDate(hhtcHelper.calcOrderEndDate(orderInfo), orderInfo.getOpenEndTime()), "yyyy-MM-dd HH:mm")));
                dataItem.put("remark", new WeixinTemplateMsg.DItem("吼吼共享停车提醒您：本次的车位预约所获得租金已打到您的余额中。"));
                url = this.hhtcContextPath + this.portalCenterUrl;
                url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+need.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+need.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
                templateMsg = new WeixinTemplateMsg();
                templateMsg.setTemplate_id("vkWecSSZW6qJQ4qXXX1iH7QaRvV1HJrcmky208AKx88");
                templateMsg.setUrl(url);
                templateMsg.setTouser(publish.getOpenid());
                templateMsg.setData(dataItem);
                WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(need.getAppid()), templateMsg);
                LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：匹配处理-->处理完毕[{}-{}]：需求[{}]匹配到[{}]", len, currIndex, need.getId(), publish.getId());
            }else{
                LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：匹配处理-->正在处理[{}-{}]：未匹配到发布信息", len, currIndex);
            }
            LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：匹配处理-->处理完毕[{}-{}]条", len, currIndex);
            currIndex = String.valueOf(Integer.parseInt(currIndex) + 1);
        }
    }
}