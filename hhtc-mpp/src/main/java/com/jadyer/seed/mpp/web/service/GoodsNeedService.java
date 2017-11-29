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
//        order01.setGoodsPublishIds("");
//        order01.setPrice(hhtcHelper.calcPrice(order.getCommunityId(), type));
//        order01.setPublishType(type);
//        order01.setPublishFromDates(newFromDate+"");
//        order01.setPublishFromTime(newFromTime);
//        order01.setPublishEndTime(newEndTime);
//        order01.setFromType(3);
//        order01.setFromId(order.getId());
//        order01.setStatus(0);
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
//        order01.setGoodsPublishIds(p1.getId()+"");
        order01 = goodsPublishOrderRepository.saveAndFlush(order01);
        LogUtil.getQuartzLogger().info("定时任务：车位需求匹配：匹配处理-->开始处理[{}-{}]：needId={}：切割出新发布orderId={}", len, currIndex, needId, order01.getId());
    }

}