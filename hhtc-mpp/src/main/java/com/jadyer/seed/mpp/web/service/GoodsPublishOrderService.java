package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.BeanUtil;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.GoodsPublishHistory;
import com.jadyer.seed.mpp.web.model.GoodsPublishInfo;
import com.jadyer.seed.mpp.web.model.GoodsPublishOrder;
import com.jadyer.seed.mpp.web.model.OrderInfo;
import com.jadyer.seed.mpp.web.repository.GoodsPublishHistoryRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishOrderRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishRepository;
import com.jadyer.seed.mpp.web.repository.GoodsRepository;
import com.jadyer.seed.mpp.web.repository.OrderRentRepository;
import com.jadyer.seed.mpp.web.repository.OrderRepository;
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
import java.text.ParseException;
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
    private GoodsRepository goodsRepository;
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
    private GoodsPublishRepository goodsPublishRepository;
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;
    @Resource
    private GoodsPublishHistoryRepository goodsPublishHistoryRepository;

    @Transactional(rollbackFor=Exception.class)
    public void lock(List<GoodsPublishOrder> orderList){
        Date lockFromDate = new Date();
        Date lockEndDate = DateUtils.addMinutes(lockFromDate, this.orderLockMinute+1);
        for(GoodsPublishOrder order : orderList){
            order.setStatus(1);
            order.setLockFromDate(lockFromDate);
            order.setLockEndDate(lockEndDate);
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
            order.setStatus(2);
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
            order.setStatus(1);
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
     * 库存数量
     */
    public Map<String, Long> inventory(long communityId){
        long dealCount;
        long remainCount;
        if(communityId > 0){
            dealCount = orderRepository.countByCommunityIdAndOrderStatusInAndOrderTypeLessThan(communityId, Arrays.asList(2, 9, 99), 10);
            remainCount = goodsPublishOrderRepository.countByCommunityIdAndStatus(communityId, 0);
        }else{
            dealCount = orderRepository.countByOrderStatusInAndOrderTypeLessThan(Arrays.asList(2, 9, 99), 10);
            remainCount = goodsPublishOrderRepository.countByStatus(0);
        }
        return new HashMap<String, Long>(){
            private static final long serialVersionUID = -3747740152447862442L;
            {
                put("dealCount", dealCount);
                put("remainCount", remainCount);
            }
        };
    }


    /**
     * 车位发布列表
     * @param page zero-based page index
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
    public void cancel(String openid, long id) {
        GoodsPublishOrder order = goodsPublishOrderRepository.findOne(id);
        //可以取消未锁定的
        if(order.getStatus() != 0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "该车位已被锁定或使用");
        }
        //只能提前一个小时
        int publishFromDate;
        if(order.getPublishFromDates().contains("-")){
            publishFromDate = Integer.parseInt(order.getPublishFromDates().substring(0, order.getPublishFromDates().indexOf("-")));
        }else{
            publishFromDate = Integer.parseInt(order.getPublishFromDates());
        }
        if(DateUtils.addHours(new Date(), 1).getTime() > hhtcHelper.convertToDate(publishFromDate, order.getPublishFromTime()).getTime()){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "至少提前一个小时取消");
        }
        goodsPublishOrderRepository.delete(id);
        goodsPublishRepository.deleteByGoodsPublishOrderId(id);
        //判断删除后是否发布订单就没有了，那就得把goods状态改为未使用
        List<GoodsPublishOrder> orderList = goodsPublishOrderRepository.findByGoodsIdAndOpenid(order.getGoodsId(), openid);
        if(orderList.isEmpty() || (orderList.size()==1 && orderList.get(0).getId()==id)){
            goodsRepository.updateStatus(order.getGoodsId(), 0);
        }
    }


    /**
     * 归档发布信息（记录过期的发布信息到历史表）
     */
    @Transactional(rollbackFor=Exception.class)
    public void history(){
        List<GoodsPublishOrder> pubOrderList = goodsPublishOrderRepository.findByPublishFromDatesEndingWith(DateUtil.getYestoday());
        List<GoodsPublishOrder> pubOrderList22 = goodsPublishOrderRepository.findByPublishFromDatesEndingWith(DateUtil.getCurrentDate());
        pubOrderList.addAll(pubOrderList22);
        String idx = "1";
        int len = pubOrderList.size();
        LogUtil.getQuartzLogger().info("定时任务：归档发布信息-->查到记录[{}]条", len);
        for(GoodsPublishOrder obj : pubOrderList){
            idx = JadyerUtil.leftPadUseZero(idx, String.valueOf(len).length());
            LogUtil.getQuartzLogger().info("定时任务：归档发布信息-->开始处理[{}-{}]条，id={}", len, idx, obj.getId());
            int publishEndDate;
            String[] fromDates = obj.getPublishFromDates().split("-");
            publishEndDate = Integer.parseInt(fromDates[fromDates.length-1]);
            if(3==obj.getPublishType() || (2==obj.getPublishType() && obj.getPublishEndTime()<obj.getPublishFromTime())){
                try {
                    publishEndDate = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(publishEndDate+"", "yyyyMMdd"), 1), "yyyyMMdd"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            //该发布信息是否被锁定中（正在下单支付）
            boolean isLocking = false;
            if(null!=obj.getLockEndDate() && new Date().compareTo(obj.getLockEndDate())<1){
                isLocking = true;
            }
            //标记该发布信息，是否已被下单并使用中，默认为否
            boolean isUsing = false;
            for(OrderInfo order : orderRepository.findByGoodsPublishOrderIdsLike("%"+obj.getId()+"%")){
                if(order.getOrderStatus()==1 || order.getOrderStatus()==2){
                    isUsing = true;
                }
            }
            Date endDate = hhtcHelper.convertToDate(publishEndDate, obj.getPublishEndTime());
            if(endDate.compareTo(new Date())>0 || isLocking || isUsing){
                LogUtil.getQuartzLogger().info("定时任务：归档发布信息-->正在处理[{}-{}]条，id={}的记录暂不需要归档", len, idx, obj.getId());
            }else{
                obj.setStatus(3);
                goodsPublishOrderRepository.saveAndFlush(obj);
                List<GoodsPublishInfo> pubList = goodsPublishRepository.findByGoodsPublishOrderId(obj.getId());
                for(GoodsPublishInfo pub : pubList){
                    GoodsPublishHistory history = new GoodsPublishHistory();
                    BeanUtil.copyProperties(pub, history);
                    history.setId(null);
                    history.setGoodsPublishId(pub.getId());
                    goodsPublishHistoryRepository.saveAndFlush(history);
                    goodsPublishRepository.delete(pub.getId());
                    //发布信息列表为空时，将其对应的车位置为待发布
                    if(0 == goodsPublishRepository.countByGoodsId(pub.getGoodsId())){
                        goodsRepository.updateStatus(pub.getGoodsId(), 0);
                    }
                }
            }
            LogUtil.getQuartzLogger().info("定时任务：归档发布信息-->处理完毕[{}-{}]条", len, idx);
            idx = String.valueOf(Integer.parseInt(idx) + 1);
        }
    }
}