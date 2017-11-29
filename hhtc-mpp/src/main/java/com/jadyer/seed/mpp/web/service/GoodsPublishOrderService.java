package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
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
    private OrderInforService orderInforService;
    @Resource
    private UserFundsFlowService userFundsFlowService;
    @Resource
    private GoodsInforRepository goodsInforRepository;
    @Resource
    private GoodsPublishRepository goodsPublishRepository;
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;

    /**
     * TOKGO 获取用户车位
     * */
    @Transactional(rollbackFor=Exception.class)
    public List<GoodsInfor> GetPublishCarpark(String openid){
        return goodsInforRepository.findByOpenid(openid);
    }

    /**
     * TOKGO 用户发布订单
     * */
    @Transactional(rollbackFor=Exception.class)
    public void postOrder(String openid,long goodsId, BigDecimal price,String starttime,String endtime){
        GoodsInfor goodsInfor = goodsInforRepository.findByOpenidAndId(openid, goodsId);
        if (goodsInfor ==null)
            throw new HHTCException(CodeEnum.SYSTEM_NULL);
        //检测用户是否具有车主身份
        fansService.CheckOwners(fansService.getByOpenid(openid));
        //检查用户押金是否足够
        if(!userFundsService.depositIsenough(openid,goodsInfor.getCommunityId())){
            //押金不够
            throw new HHTCException(CodeEnum.HHTC_FUNDS_DEPOSIT_NO);
        }
        //时间检测
        if (OrderTimeCheck(starttime,endtime,openid,goodsId)) {
            //押金不够
            throw new HHTCException(CodeEnum.HHTC_ORDER_PORT_TIMEERROR);
        }
        //生成订单
        BuildOrder(goodsInfor,price,starttime,endtime);
    }


    /**
     * TOKGO 订单时间冲突检查
     * @return  true 含有时间冲突
     * */
    private boolean OrderTimeCheck(String stime,String etime,String openid,long goodsId){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            long starttime =  sdf.parse(stime).getTime()-Constants.S_ORDERINTERVAL;
            long endtime =  sdf.parse(etime).getTime()+Constants.S_ORDERINTERVAL;
            List<GoodsPublishOrder> goodsPublishOrders = goodsPublishOrderRepository.findByGoodsIdAndOpenid(goodsId,openid);
            for (GoodsPublishOrder goodsPublishOrder:goodsPublishOrders) {
                long orderstarttime = sdf.parse(goodsPublishOrder.getPublishFromTime()).getTime()-Constants.S_ORDERINTERVAL;
                long orderendtime = sdf.parse(goodsPublishOrder.getPublishEndTime()).getTime()+Constants.S_ORDERINTERVAL;
                if ((starttime>=orderstarttime&&starttime<orderendtime)
                        || (starttime<=orderstarttime&&orderendtime<=endtime)
                        || (endtime>=orderstarttime&&endtime<orderendtime))
                    return true;
            }
        } catch (ParseException e) {
            throw new HHTCException(CodeEnum.SYSTEM_ERROR);
        }
        return false;
    }




    /**
     * TOKGO 生成订单
     * */
    private void BuildOrder( GoodsInfor goodsInfor, BigDecimal price,String starttime,String endtime){
        GoodsPublishOrder goodsPublishOrder = new GoodsPublishOrder();
        goodsPublishOrder.setOrderID(HHTCHelper.buildOrderNo());
        goodsPublishOrder.setOpenid(goodsInfor.getOpenid());
        goodsPublishOrder.setCommunityId(goodsInfor.getCommunityId());
        goodsPublishOrder.setCommunityName(goodsInfor.getCommunityName());
        goodsPublishOrder.setGoodsId(goodsInfor.getId());
        goodsPublishOrder.setCarParkNumber(goodsInfor.getCarParkNumber());
        goodsPublishOrder.setCarParkImg(goodsInfor.getCarParkImg());
        goodsPublishOrder.setPrice(price);
        goodsPublishOrder.setPublishFromTime(starttime);
        goodsPublishOrder.setPublishEndTime(endtime);
        goodsPublishOrderRepository.save(goodsPublishOrder);
    }

    /**
     * TOKGO 计算价格
     * */

    public BigDecimal GetPrice(long communityId,String starttime,String endtime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        double free;
        long startdate,enddate;
        try {
            enddate= sdf.parse(starttime).getTime();
            startdate= sdf.parse(endtime).getTime();
        } catch (ParseException e) {
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_TIME_ERROR);
        }
        CommunityInfo communityInfo = communityService.get(communityId);
        //检测天数 并计算价格
        free = ((int)((enddate- startdate)/Constants.S_DATE_TIMES_DAY))
                *communityInfo.getMoneyRentFull().doubleValue();
        //计算飞非整天的
//        (24-((startdate%Constants.S_DATE_TIMES_DAY)/Constants.S_DATE_TIMES_HOUR))
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
                //进行订单超时检查，如果订单超时 删除
                if(CheckOverTime(goodsPublishOrder))
                    continue;
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
        List<GoodsPublishOrder> goodsPublishOrders = goodsPublishOrderRepository.findByOpenid(openid);
        for (GoodsPublishOrder goodsPublishOrder :goodsPublishOrders){
            //删除过期订单
            if (CheckOverTime(goodsPublishOrder))
                goodsPublishOrders.remove(goodsPublishOrder);
        }
        return goodsPublishOrders;
    }


    /**
     * TOKGO 订单取消 回归市场
     * */
    public void BackMarket( OrderInfor orderInfor){
        GoodsPublishOrder goodsPublishOrder = new GoodsPublishOrder();
        goodsPublishOrder.setOrderID(orderInfor.getOrderId());
        goodsPublishOrder.setOpenid(orderInfor.getPostOpenid());
        goodsPublishOrder.setCommunityId(orderInfor.getCommunityId());
        goodsPublishOrder.setCommunityName(orderInfor.getCommunityName());
        goodsPublishOrder.setGoodsId(orderInfor.getGoodsId());
        goodsPublishOrder.setCarParkNumber(orderInfor.getCarParkNumber());
        goodsPublishOrder.setCarParkImg(orderInfor.getCarParkImg());
        goodsPublishOrder.setPrice(orderInfor.getPrice());
        goodsPublishOrder.setPublishFromTime(orderInfor.getTimeStart());
        goodsPublishOrder.setPublishEndTime(orderInfor.getTimeEnd());
        goodsPublishOrderRepository.save(goodsPublishOrder);
    }



    /**
     * TOKGO 订单到点后删除 所有订单
     * */
    public void CheckOverTime(){
        CheckOverTime(goodsPublishOrderRepository.findAll());
    }
    /**
     * TOKGO 订单到点后删除 当前订单
     * */
    public boolean CheckOverTime(GoodsPublishOrder goodsPublishOrder){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        //获取当前时间
        long timenow = new Date().getTime();
        long timetemp;
        try {
            timetemp= sdf.parse(goodsPublishOrder.getPublishFromTime()).getTime();
            if (timetemp>timenow) {
                //TODO 发送微信模板消息
                goodsPublishOrderRepository.delete(goodsPublishOrder);
                return true;
            }
        } catch (ParseException e) {
        }
        return false;
    }

    /**
     * TOKGO 订单到点后删除 订单数组
     * */
    public void CheckOverTime(List<GoodsPublishOrder> goodsPublishOrders){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        //获取当前时间
        long timenow = new Date().getTime();
        long timetemp;
        for(GoodsPublishOrder goodsPublishOrder : goodsPublishOrders){
            try {
                timetemp= sdf.parse(goodsPublishOrder.getPublishFromTime()).getTime();
                if (timetemp>timenow) {
                    //TODO 发送微信模板消息
                    goodsPublishOrderRepository.delete(goodsPublishOrder);
                }
            } catch (ParseException e) {
            }
        }
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

    }


    /**
     * TOKGO 车位主取消发布车位
     */
    @Transactional(rollbackFor=Exception.class)
    public void cancel(String openid,String orderid) {
        //删除市场中的订单
        goodsPublishOrderRepository.delete(goodsPublishOrderRepository.findByOpenidAndOrderID(openid,orderid));
    }

}