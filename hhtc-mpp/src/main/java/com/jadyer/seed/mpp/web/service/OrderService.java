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
import com.jadyer.seed.mpp.web.model.MppFansInfo;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.model.OrderHistory;
import com.jadyer.seed.mpp.web.model.OrderInfo;
import com.jadyer.seed.mpp.web.model.OrderInout;
import com.jadyer.seed.mpp.web.model.UserFunds;
import com.jadyer.seed.mpp.web.model.UserFundsFlow;
import com.jadyer.seed.mpp.web.repository.FansInfoRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishOrderRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishRepository;
import com.jadyer.seed.mpp.web.repository.MppUserInfoRepository;
import com.jadyer.seed.mpp.web.repository.OrderHistoryRepository;
import com.jadyer.seed.mpp.web.repository.OrderInoutRepository;
import com.jadyer.seed.mpp.web.repository.OrderRepository;
import com.jadyer.seed.mpp.web.repository.UserFundsRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    private FansInfoRepository fansInfoRepository;
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
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;

    public OrderInfo getByOrderNo(String orderNo){
        return orderRepository.findByOutTradeNo(orderNo);
    }


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
                case "SUCCESS"   : orderInfo.setOrderStatus(2); userFundsFlowService.recharge(orderInfo); break;
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


    public long countByGoodsIdAndOrderTypeInAndOrderStatusIn(long goodsId, List<Integer> orderTypeList, List<Integer> orderStatusList){
        return orderRepository.countByGoodsIdAndOrderTypeInAndOrderStatusIn(goodsId, orderTypeList, orderStatusList);
    }


    public OrderInfo get(long id) {
        OrderInfo order = orderRepository.findOne(id);
        order.setSpbillCreateIp(MoneyUtil.fenToYuan(order.getTotalFee()+""));
        //order.setOrderStatus(hhtcHelper.convertOrderStatus(order.getOrderType(), order.getOrderStatus(), order.getSettleStatus(), order.getInTime(), order.getOutTime(), hhtcHelper.getEndDateFromOrder(order), order.getOpenEndTime()));
        order.setAllowRent(hhtcHelper.calcOrderAllowRent(order));
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
            order.setAllowRent(hhtcHelper.calcOrderAllowRent(order));
        }
        return list;
    }


    /**
     * 转租
     * @param id     订单id
     * @param openid 发起转租的人的openid（也即订单的openid）
     */
    @Transactional(rollbackFor=Exception.class)
    public void rent(long id, String openid) {
        OrderInfo order = orderRepository.findOne(id);
        if(order.getOrderType()!=1 && order.getOrderType()!=2){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "非车位订单不能转租");
        }
        //校验：订单状态必须是支付成功
        if(order.getOrderStatus()!=2 && order.getOrderStatus()!=9){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "原订单未支付成功");
        }
        //校验：只能转租一次
        if(order.getOrderStatus() == 9){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只能转租一次");
        }
        //校验：须提前一小时转租
        if(DateUtils.addHours(new Date(), 1).compareTo(hhtcHelper.convertToDate(hhtcHelper.calcOrderFromDate(order), order.getOpenFromTime())) == 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "转租至少提前一个小时发起");
        }
        //转租
        for(String obj : order.getGoodsPublishOrderIds().split("`")){
            GoodsPublishOrder po = goodsPublishOrderRepository.findOne(Long.parseLong(obj));
            GoodsPublishOrder newpo = new GoodsPublishOrder();
            BeanUtil.copyProperties(po, newpo);
            newpo.setId(null);
            newpo.setOpenid(openid);
            newpo.setFromType(2);
            newpo.setFromId(po.getId());
            newpo.setStatus(0);
            //newpo.setGoodsPublishIds();
            newpo = goodsPublishOrderRepository.saveAndFlush(newpo);
            String pubids = "";
            List<GoodsPublishInfo> list22 = goodsPublishRepository.findByGoodsPublishOrderId(po.getId());
            for(GoodsPublishInfo info : list22){
                GoodsPublishInfo newinfo = new GoodsPublishInfo();
                BeanUtil.copyProperties(info, newinfo);
                newinfo.setId(null);
                newinfo.setGoodsPublishOrderId(newpo.getId());
                newinfo.setOpenid(openid);
                newinfo.setFromType(2);
                newinfo.setFromIds(info.getId()+"");
                newinfo.setStatus(0);
                newinfo = goodsPublishRepository.saveAndFlush(newinfo);
                pubids = pubids + "`" + newinfo.getId();
            }
            newpo.setGoodsPublishIds(pubids.substring(1));
            goodsPublishOrderRepository.saveAndFlush(newpo);
        }
        //更新原订单信息
        order.setOrderStatus(9);
        orderRepository.saveAndFlush(order);
        orderInoutRepository.deleteByOrderNo(order.getOutTradeNo());
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
                    //更新车位的使用状态
                    if(this.countByGoodsIdAndOrderTypeInAndOrderStatusIn(obj.getGoodsId(), Arrays.asList(1, 2), Arrays.asList(2, 9)) == 0){
                        goodsService.updateStatus(obj.getGoodsId(), 1, 2);
                    }
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
            history.setOrderId(order.getId());
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
        MppFansInfo fansInfo = fansInfoRepository.findByPhoneNo(phoneNo);
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


    /**
     * 后台運營主動匹配訂單（注意匹配時的起始日期取的是當天）
     */
    public Object matchNewGoods(long orderId) {
        OrderInfo order = this.get(orderId);
        String publishFromDate = DateUtil.getCurrentDate();
        String publishEndDate = hhtcHelper.calcOrderEndDate(order) + "";
        String publishFromTime = order.getOpenFromTime() + "";
        String publishEndTime = order.getOpenEndTime() + "";
        List<Map<String, String>> list = goodsPublishService.matchlist(order.getCommunityId(), order.getOpenType(), publishFromDate, publishEndDate, publishFromTime, publishEndTime);
        CopyOnWriteArrayList<Map<String, String>> matchList = new CopyOnWriteArrayList<>(list);
        for(Map<String, String> obj : matchList){
            String[] fromDates = obj.get("publishFromDates").split("-");
            obj.put("dateStartEnd", fromDates[0] + " - " + fromDates[fromDates.length-1]);
            obj.put("timeStartEnd", DateFormatUtils.format(hhtcHelper.convertToDate(Integer.parseInt(DateUtil.getCurrentDate()), Integer.parseInt(obj.get("publishFromTime"))), "HH:mm") + " - " + DateFormatUtils.format(hhtcHelper.convertToDate(Integer.parseInt(DateUtil.getCurrentDate()), Integer.parseInt(obj.get("publishEndTime"))), "HH:mm"));
            //匹配到的價格是否比原訂單的多，若多則計算多出來多少
            BigDecimal price = new BigDecimal(obj.get("price"));
            BigDecimal oldPrice = new BigDecimal(MoneyUtil.fenToYuan(order.getTotalFee()+""));
            if(price.compareTo(oldPrice) == 1){
                obj.put("paymoney", price.subtract(oldPrice).intValue()+"");
            }else{
                obj.put("paymoney", "0");
            }
        }
        return matchList;
    }


    /**
     * 后台運營主動調配訂單
     */
    @Transactional(rollbackFor=Exception.class)
    public void allocate(long orderId, String phoneNo, String ids) {
        MppUserInfo mppUserInfo = mppUserInfoRepository.findByMptypeAndBindStatus(1, 1);
        if(null==mppUserInfo || mppUserInfo.getId()==0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "获取平台UID失败");
        }
        MppFansInfo fansInfo = fansInfoRepository.findByPhoneNo(phoneNo);
        if(null==fansInfo || null==fansInfo.getId() || fansInfo.getId()==0 || "0".equals(fansInfo.getSubscribe())){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无法识别粉丝或未关注公众号");
        }
        if(2 != fansInfo.getCarOwnerStatus()){
            throw new HHTCException(CodeEnum.HHTC_UNREG_CAR_OWNER);
        }
        //校驗原訂單
        OrderInfo order = this.get(orderId);
        if(order.getOrderType()!=1 && order.getOrderType()!=2){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "原訂單類型無效["+order.getOrderType()+"]");
        }
        if(order.getOrderStatus()==9 || order.getOrderStatus()==99){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "原訂單已轉租或已完成");
        }
        if(order.getOrderStatus()!=2){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "原訂單未支付成功");
        }
        //校验是否已入场
        OrderInout inout = orderInoutRepository.findByOrderNoAndInTimeNotNullAndOutTimeNull(order.getOutTradeNo());
        if(null==inout || null==inout.getInTime()){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "车主未入场");
        }
        //校验是否已被锁定
        BigDecimal price = new BigDecimal(0);
        List<GoodsPublishOrder> orderList = new ArrayList<>();
        for(String id : ids.split("`")){
            GoodsPublishOrder pubOrder = goodsPublishOrderRepository.findOne(Long.parseLong(id));
            if(pubOrder.getStatus() != 0){
                throw new HHTCException(CodeEnum.HHTC_GOODS_ORDER_FAIL);
            }
            orderList.add(pubOrder);
            price = price.add(pubOrder.getPrice());
        }
        //校验押金是否足够
        Map<String, String> dataMap = userFundsService.depositIsenough(fansInfo.getOpenid(), orderList.get(0).getCommunityId());
        if("0".equals(dataMap.get("isenough"))){
            throw new HHTCException(CodeEnum.HHTC_NEED_NO_MONEY.getCode(), "押金不足，缺少："+dataMap.get("money") + "元");
        }
        //计算publishFromDates
        List<String> publishFromDateList = new ArrayList<>();
        for(GoodsPublishOrder obj : orderList){
            for(String _fromDate : obj.getPublishFromDates().split("-")){
                if(!publishFromDateList.contains(_fromDate)){
                    publishFromDateList.add(_fromDate);
                }
            }
        }
        publishFromDateList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.parseInt(o1) - Integer.parseInt(o2);
            }
        });
        String publishFromDates = "";
        for(String obj : publishFromDateList){
            publishFromDates = publishFromDates + "-" + obj;
        }
        publishFromDates = publishFromDates.substring(1);
        //锁定发布信息
        goodsPublishOrderService.lock(orderList);
        //將原訂單置為已完成
        order.setOrderStatus(99);
        this.upsert(order);
        Date currInTime = new Date();
        orderInoutRepository.deleteByOrderNo(order.getOutTradeNo());
        //釋放原訂單的車位发布信息和車位信息
        goodsPublishOrderService.updateStatusToPublishing(orderList);
        //再重新下单（訂單金額仍為原訂單的金額）
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCommunityId(orderList.get(0).getCommunityId());
        orderInfo.setCommunityName(orderList.get(0).getCommunityName());
        orderInfo.setGoodsId(orderList.get(0).getGoodsId());
        orderInfo.setGoodsPublishOrderIds(ids);
        orderInfo.setCarParkNumber(orderList.get(0).getCarParkNumber());
        orderInfo.setCarParkImg(orderList.get(0).getCarParkImg());
        orderInfo.setCarNumber(order.getCarNumber());
        orderInfo.setOpenFromDates(publishFromDates);
        orderInfo.setOpenFromTime(orderList.get(0).getPublishFromTime());
        orderInfo.setOpenEndTime(orderList.get(orderList.size()-1).getPublishEndTime());
        orderInfo.setOutTradeNo(hhtcHelper.buildOrderNo(8));
        orderInfo.setTotalFee(Long.parseLong(MoneyUtil.yuanToFen(price.toString())));
        orderInfo.setDepositMoney(new BigDecimal(0));
        orderInfo.setCanRefundMoney(new BigDecimal(0));
        orderInfo.setOpenid(order.getOpenid());
        orderInfo.setAppid(mppUserInfo.getAppid());
        orderInfo.setOrderType(1);
        orderInfo.setOrderStatus(2);
        orderInfo = this.upsert(orderInfo);
        goodsPublishOrderService.updateStatusToUsed(orderList);
        orderInoutService.initInout(orderInfo, orderList.get(0).getOpenid(), inout.getInTime());
        //分润（分潤使用的金額為新訂單的发布信息總金額）
        BigDecimal oldPrice = new BigDecimal(MoneyUtil.fenToYuan(order.getTotalFee()+""));
        if(price.compareTo(oldPrice) == 1){
            //不需要扣减车主余额，因為車主在之前訂單已經支付過了，不過要看新訂單金額是否大于車主原訂單金額，大于則口平台資金
            BigDecimal lessPrice = price.subtract(oldPrice);
            UserFunds funds = userFundsRepository.findByUid(mppUserInfo.getId());
            if(null == funds){
                funds = new UserFunds();
                funds.setUid(mppUserInfo.getId());
                funds.setMoneyFreeze(new BigDecimal(0));
                funds.setMoneyBase(new BigDecimal(0));
                funds.setMoneyBalance(new BigDecimal(0));
            }
            funds.setMoneyBalance(funds.getMoneyBalance().subtract(lessPrice));
            funds = userFundsRepository.saveAndFlush(funds);
            UserFundsFlow fundsFlow = new UserFundsFlow();
            fundsFlow.setFundsId(funds.getId());
            fundsFlow.setUid(mppUserInfo.getId());
            fundsFlow.setMoney(lessPrice);
            fundsFlow.setInOut("out");
            fundsFlow.setInOutDesc("調配訂單時扣減平台余額");
            fundsFlow.setInOutType(4);
            fundsFlow.setBizDate(Integer.parseInt(DateUtil.getCurrentDate()));
            fundsFlow.setBizDateTime(new Date());
            userFundsFlowService.upsert(fundsFlow);
        }
        orderRentService.rent(99, orderList.get(0).getOpenid(), orderInfo);
    }
}