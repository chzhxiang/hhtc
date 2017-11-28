package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.BeanUtil;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.*;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/15 16:29.
 */
@Service
public class GoodsPublishOrderService {
    @Value("${hhtc.orderLock.minute}")
    private int orderLockMinute;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private OrderService orderService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderRepository orderRepository;
    @Resource
    private CommunityService communityService;
    @Resource
    private UserFundsService userFundsService;

    @Resource
    private OrderRentRepository orderRentRepository;
    @Resource
    private UserFundsFlowService userFundsFlowService;
    @Resource
    private GoodsInforRepository goodsInforRepository;
    @Resource
    private GoodsPublishRepository goodsPublishRepository;
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;
    @Resource
    private GoodsPublishHistoryRepository goodsPublishHistoryRepository;



    /**
     * TOKGO 获取用户发布车位
     * */
    @Transactional(rollbackFor=Exception.class)
    public List<GoodsInfor> GetPublishCarpark(String openid){
        return goodsInforRepository.findByOpenidAndIsUsed(openid,0);
    }


    /**
     * TOKGO 用户发布订单
     * */
    @Transactional(rollbackFor=Exception.class)
    public void postOrder(String openid,long goodsId, String starttime,String endtime){
        GoodsInfor goodsInfor = goodsInforRepository.findByOpenidAndId(openid, goodsId);
        if (goodsInfor ==null)
            throw new HHTCException(CodeEnum.SYSTEM_NULL);
        //检查用户押金是否足够
        if(!userFundsService.depositIsenough(openid,goodsInfor.getCommunityId())){
            //押金不够
            throw new HHTCException(CodeEnum.HHTC_FUNDS_DEPOSIT_NO);
        }
        GoodsPublishOrder goodsPublishOrder = new GoodsPublishOrder();
        goodsPublishOrder.setOrderID(HHTCHelper.buildOrderNo());
        goodsPublishOrder.setOpenid(openid);
        goodsPublishOrder.setCommunityId(goodsInfor.getCommunityId());
        goodsPublishOrder.setCommunityName(goodsInfor.getCommunityName());
        goodsPublishOrder.setGoodsId(goodsInfor.getId());
        goodsPublishOrder.setCarParkNumber(goodsInfor.getCarParkNumber());
        goodsPublishOrder.setCarParkImg(goodsInfor.getCarParkImg());
        goodsPublishOrder.setPrice(GetPrice(starttime,endtime));
        goodsPublishOrder.setPublishFromTime(starttime);
        goodsPublishOrder.setPublishEndTime(endtime);
        //更改车位当前状态
        goodsService.updateStatus(goodsId,1,goodsInfor.getIsUsed());
        goodsPublishOrderRepository.save(goodsPublishOrder);
    }

    /**
     * TOKGO 计算价格
     * */

    private BigDecimal GetPrice(String starttime,String endtime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        long timestart,timeend;
        try {
            timestart= sdf.parse(starttime).getTime();
            timeend= sdf.parse(endtime).getTime();
        } catch (ParseException e) {
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_TIME_ERROR);
        }
        //TODO  计算订单价格
        return  new BigDecimal(100.00);
    }

    /**
     * TOKGO 库存数量 获取市场的车位
     */
    @Transactional(rollbackFor=Exception.class)
    public List<GoodsPublishOrder> inventory(long communityId,String starttime){
        List<GoodsPublishOrder> goodsPublishOrderSends = new ArrayList<>();
        List<GoodsPublishOrder> goodsPublishOrders =goodsPublishOrderRepository.findByCommunityId(communityId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long timestart;
        try {
            timestart= new SimpleDateFormat("yyyy-MM-dd").parse(starttime).getTime();
            //进行时间筛选
            for (GoodsPublishOrder goodsPublishOrder:goodsPublishOrders){
                if (sdf.parse(goodsPublishOrder.getPublishFromTime()).getTime()>=timestart){
                    goodsPublishOrderSends.add(goodsPublishOrder);
                }
            }
        } catch (ParseException e) {
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_TIME_ERROR);
        }
        return goodsPublishOrderSends;
    }

    /**
     * TOKGO 获取发布订单
     * */
    public List<GoodsPublishOrder> Getfansorder(String openid){
        return goodsPublishOrderRepository.findByOpenid(openid);
    }


    /**
     * TOKGO 订单取消 回归市场
     * */
    public void BackMarket( OrderInfor orderInfor){
        //TODO 订单回归市场 操作
    }






    @Transactional(rollbackFor=Exception.class)
    public void lock(List<GoodsPublishOrder> orderList){
        Date lockFromDate = new Date();
        Date lockEndDate = DateUtils.addMinutes(lockFromDate, this.orderLockMinute+1);
        for(GoodsPublishOrder order : orderList){
//            order.setStatus(1);
//            order.setLockFromDate(lockFromDate);
//            order.setLockEndDate(lockEndDate);
            goodsPublishOrderRepository.saveAndFlush(order);
            List<GoodsPublishInfo> publishList = goodsPublishRepository.findByGoodsPublishOrderId(order.getId());
            for(GoodsPublishInfo obj : publishList){
                obj.setStatus(1);
                obj.setLockFromDate(lockFromDate);
                obj.setLockEndDate(lockEndDate);
                goodsPublishRepository.saveAndFlush(obj);
            }
        }
    }


    /**
     * 车主预约下单成功时，更新车位发布状态为已使用
     */
    @Transactional(rollbackFor=Exception.class)
    public void updateStatusToUsed(List<GoodsPublishOrder> orderList){
        for(GoodsPublishOrder order : orderList){
//            order.setStatus(2);
            goodsPublishOrderRepository.saveAndFlush(order);
            List<GoodsPublishInfo> publishList = goodsPublishRepository.findByGoodsPublishOrderId(order.getId());
            for(GoodsPublishInfo obj : publishList){
                obj.setStatus(2);
                goodsPublishRepository.saveAndFlush(obj);
            }
        }
        goodsService.updateStatus(orderList.get(0).getGoodsId(), 2, 1);
    }


    /**
     * 车主预约下单成功后，去停車发現不能停，聯繫客服后客服調配車位時，釋放原車位发布信息
     */
    @Transactional(rollbackFor=Exception.class)
    public void updateStatusToPublishing(List<GoodsPublishOrder> orderList){
        for(GoodsPublishOrder order : orderList){
//            order.setStatus(1);
            goodsPublishOrderRepository.saveAndFlush(order);
            List<GoodsPublishInfo> publishList = goodsPublishRepository.findByGoodsPublishOrderId(order.getId());
            for(GoodsPublishInfo obj : publishList){
                obj.setStatus(1);
                goodsPublishRepository.saveAndFlush(obj);
            }
        }
        if(orderService.countByGoodsIdAndOrderTypeInAndOrderStatusIn(orderList.get(0).getGoodsId(), Arrays.asList(1, 2), Arrays.asList(2, 9)) == 0){
            goodsService.updateStatus(orderList.get(0).getGoodsId(), 1, 2);
        }
    }

    /**
     * 车位发布列表
     * @param goodsId zero-based page index
     */
    public Page<GoodsPublishOrder> listByGoodsId(long goodsId, String pageNo) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        //预约后被转租的，不给车位主看
        Condition<GoodsPublishOrder> spec = Condition.<GoodsPublishOrder>and().eq("goodsId", goodsId).ne("fromType", 2);
        //被切割的原发布，也不给车位主看
        Set<Long> idSet = new HashSet<>();
        for(GoodsPublishOrder obj : goodsPublishOrderRepository.findAll()){
            if(obj.getFromType() == 3){
                idSet.add(obj.getFromId());
            }
        }
        if(!idSet.isEmpty()){
            spec.notIn("id", new ArrayList<>(idSet));
        }
        return goodsPublishOrderRepository.findAll(spec, pageable);
    }


    /**
     * 车位主取消发布车位
     */
    @Transactional(rollbackFor=Exception.class)
    public void cancel(String openid,String orderid) {
        GoodsPublishOrder order = goodsPublishOrderRepository.findByOrderID(orderid);
        // TODO 只能提前一个小时 没有写
//        int publishFromDate;
//        if(order.getPublishFromDates().contains("-")){
//            publishFromDate = Integer.parseInt(order.getPublishFromDates().substring(0, order.getPublishFromDates().indexOf("-")));
//        }else{
//            publishFromDate = Integer.parseInt(order.getPublishFromDates());
//        }
////        if(DateUtils.addHours(new Date(), 1).getTime() > hhtcHelper.convertToDate(publishFromDate, order.getPublishFromTime()).getTime()){
////            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "至少提前一个小时取消");
////        }
        // 那就得把goods状态改为未使用
        goodsService.updateStatus(order.getGoodsId(),0,1);
        //删除市场中的订单
        goodsPublishOrderRepository.delete(order);
    }


    /**
     * 归档发布信息（记录过期的发布信息到历史表）
     */
    @Transactional(rollbackFor=Exception.class)
    public void history(){
//        List<GoodsPublishOrder> pubOrderList = goodsPublishOrderRepository.findByPublishFromDatesEndingWith(DateUtil.getYestoday());
//        List<GoodsPublishOrder> pubOrderList22 = goodsPublishOrderRepository.findByPublishFromDatesEndingWith(DateUtil.getCurrentDate());
//        pubOrderList.addAll(pubOrderList22);
//        String idx = "1";
//        int len = pubOrderList.size();
//        LogUtil.getQuartzLogger().info("定时任务：归档发布信息-->查到记录[{}]条", len);
//        for(GoodsPublishOrder obj : pubOrderList){
//            idx = JadyerUtil.leftPadUseZero(idx, String.valueOf(len).length());
//            LogUtil.getQuartzLogger().info("定时任务：归档发布信息-->开始处理[{}-{}]条，id={}", len, idx, obj.getId());
//            int publishEndDate;
//            String[] fromDates = obj.getPublishFromDates().split("-");
//            publishEndDate = Integer.parseInt(fromDates[fromDates.length-1]);
//            if(3==obj.getPublishType() || (2==obj.getPublishType() && obj.getPublishEndTime()<obj.getPublishFromTime())){
//                try {
//                    publishEndDate = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(publishEndDate+"", "yyyyMMdd"), 1), "yyyyMMdd"));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//            //该发布信息是否被锁定中（正在下单支付）
//            boolean isLocking = false;
//            if(null!=obj.getLockEndDate() && new Date().compareTo(obj.getLockEndDate())<1){
//                isLocking = true;
//            }
            //标记该发布信息，是否已被下单并使用中，默认为否
//            boolean isUsing = false;
//            for(OrderInfo order : orderRepository.findByGoodsPublishOrderIdsLike("%"+obj.getId()+"%")){
//                if(order.getOrderStatus()==1 || order.getOrderStatus()==2){
//                    isUsing = true;
//                }
//            }
//            Date endDate = hhtcHelper.convertToDate(publishEndDate, 0);
//            if(endDate.compareTo(new Date())>0  || isUsing){
//                LogUtil.getQuartzLogger().info("定时任务：归档发布信息-->正在处理[{}-{}]条，id={}的记录暂不需要归档", len, idx, obj.getId());
//            }else{
////                obj.setStatus(3);
//                goodsPublishOrderRepository.saveAndFlush(obj);
//                List<GoodsPublishInfo> pubList = goodsPublishRepository.findByGoodsPublishOrderId(obj.getId());
//                for(GoodsPublishInfo pub : pubList){
//                    GoodsPublishHistory history = new GoodsPublishHistory();
//                    BeanUtil.copyProperties(pub, history);
//                    history.setId(null);
//                    history.setGoodsPublishId(pub.getId());
//                    goodsPublishHistoryRepository.saveAndFlush(history);
//                    goodsPublishRepository.delete(pub.getId());
//                    //发布信息列表为空时，将其对应的车位置为待发布
//                    if(0 == goodsPublishRepository.countByGoodsId(pub.getGoodsId())){
//                        goodsRepository.updateStatus(pub.getGoodsId(), 0);
//                    }
//                }
//            }
//            LogUtil.getQuartzLogger().info("定时任务：归档发布信息-->处理完毕[{}-{}]条", len, idx);
//            idx = String.valueOf(Integer.parseInt(idx) + 1);
//        }
    }
}