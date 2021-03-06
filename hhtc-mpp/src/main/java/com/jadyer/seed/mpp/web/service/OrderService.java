package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.BeanUtil;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.MoneyUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayOrderqueryReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayOrderqueryRespData;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.GoodsPublishInfo;
import com.jadyer.seed.mpp.web.model.GoodsPublishOrder;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.model.OrderHistory;
import com.jadyer.seed.mpp.web.model.OrderInfo;
import com.jadyer.seed.mpp.web.model.OrderInout;
import com.jadyer.seed.mpp.web.model.UserFunds;
import com.jadyer.seed.mpp.web.model.UserFundsFlow;
import com.jadyer.seed.mpp.web.repository.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 16:11.
 */
@Service
public class OrderService {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderRepository orderRepository;
    @Resource
    private CommunityService communityService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private OrderRentService orderRentService;
    @Resource
    private OrderInoutService orderInoutService;
    @Resource
    private FansInforRepository fansInforRepository;
    @Resource
    private UserFundsRepository userFundsRepository;
    @Resource
    private GoodsPublishService goodsPublishService;
    @Resource
    private OrderInoutRepository orderInoutRepository;
    @Resource
    private UserFundsFlowService userFundsFlowService;
    @Resource
    private MppUserInfoRepository mppUserInfoRepository;
    @Resource
    private OrderHistoryRepository orderHistoryRepository;
    @Resource
    private GoodsPublishRepository goodsPublishRepository;
    @Resource
    private GoodsPublishOrderService goodsPublishOrderService;
    @Resource
    private GoodsPublishHistoryService goodsPublishHistoryService;

    /**
     * 获取待支付和支付中的订单列表
     */
    public List<OrderInfo> getPayingList(){
        return orderRepository.findByOrderStatusIn(Arrays.asList(0, 1));
    }


    /**
     * update or insert
     */
    @Transactional(rollbackFor=Exception.class)
    public OrderInfo upsert(OrderInfo orderInfo){
        return orderRepository.saveAndFlush(orderInfo);
    }


    /**
     * 主动查询微信支付状态并更新到数据库
     * @param fromType 0--微信后台通知，1--支付补单
     */
    @Transactional(rollbackFor=Exception.class)
    public void query(OrderInfo orderInfo, int fromType){
        if(fromType == 1){
            orderInfo = orderRepository.findByOutTradeNo(orderInfo.getOutTradeNo());
            if(orderInfo.getIsNotify() == 1){
                LogUtil.getLogger().info("orderInfoId={}已后台通知，无需再次查询腾讯的订单状态");
                return;
            }
            //微信支付后台通知频率为15/15/30/180/1800/1800/1800/1800/3600，单位：秒
            if(new Date().compareTo(DateUtils.addMinutes(orderInfo.getCreateTime(), 66)) < 1){
                LogUtil.getLogger().info("orderInfoId={}未后台通知，那么就等订单创建后的第66分钟再补单");
                return;
            }
        }
        try {
            WeixinPayOrderqueryReqData reqData = new WeixinPayOrderqueryReqData();
            reqData.setAppid(orderInfo.getAppid());
            reqData.setMch_id(WeixinTokenHolder.getWeixinMchid(orderInfo.getAppid()));
            reqData.setNonce_str(RandomStringUtils.randomNumeric(30));
            reqData.setSign_type("MD5");
            if(StringUtils.isBlank(orderInfo.getTransactionId())){
                reqData.setOut_trade_no(orderInfo.getOutTradeNo());
            }else{
                reqData.setTransaction_id(orderInfo.getTransactionId());
            }
            WeixinPayOrderqueryRespData respData = WeixinHelper.payOrderquery(reqData);
            if("SUCCESS".equals(respData.getTrade_state()) ||  "REFUND".equals(respData.getTrade_state())){
                if(!StringUtils.equals(orderInfo.getTotalFee()+"", respData.getTotal_fee())){
                    throw new IllegalArgumentException("微信公众号支付主动查询返回的订单总金额与商户订单金额不符");
                }
            }
            switch(respData.getTrade_state()){
                //TODO  与订单充值相关
//                case "SUCCESS"   : orderInfo.setOrderStatus(2); userFundsFlowService.recharge(orderInfo); break;
                case "REFUND"    : orderInfo.setOrderStatus(5); break;
                case "NOTPAY"    : orderInfo.setOrderStatus(0); break;
                case "CLOSED"    : orderInfo.setOrderStatus(4); break;
                case "REVOKED"   : orderInfo.setOrderStatus(6); break;
                case "USERPAYING": orderInfo.setOrderStatus(1); break;
                case "PAYERROR"  : orderInfo.setOrderStatus(3); break;
                default: throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无法识别的["+orderInfo.getOrderType()+"]支付状态["+respData.getTrade_state()+"]");
            }
            orderInfo.setTradeStateDesc(respData.getTrade_state_desc());
            orderRepository.saveAndFlush(orderInfo);
        } catch (Exception e) {
            LogUtil.getLogger().error("查询微信订单状态时发生异常，堆栈轨迹如下", e);
        }
    }

    public OrderInfo get(long id) {
        OrderInfo order = orderRepository.findOne(id);
        order.setSpbillCreateIp(MoneyUtil.fenToYuan(order.getTotalFee()+""));
        //order.setOrderStatus(hhtcHelper.convertOrderStatus(order.getOrderType(), order.getOrderStatus(), order.getSettleStatus(), order.getInTime(), order.getOutTime(), hhtcHelper.getEndDateFromOrder(order), order.getOpenEndTime()));
//        order.setAllowRent(hhtcHelper.calcOrderAllowRent(order));
        return order;
    }


    public List<OrderInfo> list(String orderStatus, String openid) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Condition<OrderInfo> spec = Condition.<OrderInfo>and().eq("openid", openid).in("orderType", Arrays.asList(1, 2));
        if(StringUtils.isNotBlank(orderStatus)){
            spec.eq("orderStatus", orderStatus);
        }
        List<OrderInfo> orderList = orderRepository.findAll(spec, sort);
        final CopyOnWriteArrayList<OrderInfo> list = new CopyOnWriteArrayList<>(orderList);
        for(OrderInfo order : list){
            order.setSpbillCreateIp(MoneyUtil.fenToYuan(order.getTotalFee()+""));
            //order.setOrderStatus(hhtcHelper.convertOrderStatus(order.getOrderType(), order.getOrderStatus(), order.getSettleStatus(), order.getInTime(), order.getOutTime(), hhtcHelper.getEndDateFromOrder(order), order.getOpenEndTime()));
//            order.setAllowRent(hhtcHelper.calcOrderAllowRent(order));
        }
        return list;
    }


    /**
     * 归档订单
     */
    @Transactional(rollbackFor=Exception.class)
    public void history() {
        //先处理已出场的（看是否应改为已完成）
        String yestoday = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyyMMdd");
        List<OrderInfo> orderList = orderRepository.findByOrderStatusAndOpenFromDatesEndingWith(2, yestoday);
        orderList.addAll(orderRepository.findByOrderStatusAndOpenFromDatesEndingWith(2, DateUtil.getCurrentDate()));
        for(OrderInfo obj : orderList){
            Date orderEndDate = hhtcHelper.convertToDate(hhtcHelper.calcOrderEndDate(obj), obj.getOpenEndTime());
            if(orderEndDate.compareTo(new Date()) < 1){
                OrderInout inout = orderInoutRepository.findFirstByOrderNoOrderByMaxIndexDesc(obj.getOutTradeNo());
                //已出场，或者压根就没入过场
                if(null!=inout.getOutTime() || (null==inout.getInTime()&&null==inout.getOutTime())){
                    obj.setOrderStatus(99);
                    this.upsert(obj);
                }
            }
        }
        //再处理没支付的
        String timeExpire = DateFormatUtils.format(DateUtils.addMinutes(new Date(), 3), "yyyyMMddHHmmss");
        orderList = orderRepository.findByOrderStatusAndTimeExpireLessThanEqual(0, timeExpire);
        String currIndex = "1";
        int len = orderList.size();
        if(len == 0){
            return;
        }
        LogUtil.getQuartzLogger().info("定时任务：归档订单-->查到记录[{}]条", len);
        for(OrderInfo order : orderList){
            currIndex = JadyerUtil.leftPadUseZero(currIndex, String.valueOf(len).length());
            OrderHistory history = new OrderHistory();
            BeanUtil.copyProperties(order, history);
            history.setId(null);
//            history.setOrderId(order.getId());
            orderHistoryRepository.saveAndFlush(history);
            orderRepository.delete(order.getId());
            LogUtil.getQuartzLogger().info("定时任务：归档订单-->处理完毕[{}-{}]条", len, currIndex);
            currIndex = String.valueOf(Integer.parseInt(currIndex) + 1);
        }
    }


    /**
     * 后台运营主动搜索订单
     */
    public List<OrderInfo> search(String phoneNo) {
        MppFansInfor fansInfo = fansInforRepository.findByPhoneNo(phoneNo);
        if(null==fansInfo || null==fansInfo.getId() || fansInfo.getId()==0){
            return new ArrayList<>();
        }
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Condition<OrderInfo> spec = Condition.<OrderInfo>and().eq("openid", fansInfo.getOpenid()).in("orderType", Arrays.asList(1, 2));
        List<OrderInfo> orderList = orderRepository.findAll(spec, sort);
        final CopyOnWriteArrayList<OrderInfo> list = new CopyOnWriteArrayList<>(orderList);
        for(OrderInfo order : list){
            order.setSpbillCreateIp(MoneyUtil.fenToYuan(order.getTotalFee()+""));
            order.setOpenFromDates(hhtcHelper.calcOrderFromDate(order) + "");
            order.setTimeStart(DateFormatUtils.format(hhtcHelper.convertToDate(Integer.parseInt(order.getOpenFromDates()), order.getOpenFromTime()), "yyyy-MM-dd HH:mm"));
        }
        return list;
    }
}