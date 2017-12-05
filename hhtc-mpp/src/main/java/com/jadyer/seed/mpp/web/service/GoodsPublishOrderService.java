package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.constant.WxMsgEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.BeanUtil;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.*;
import com.jadyer.seed.mpp.web.service.async.WeixinTemplateMsgAsync;
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
    @Value("${hhtc.publishTime.day}")
    private int timeDay;
    @Value("${hhtc.publishTime.night}")
    private int timeNight;
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
    private GoodsPublishRepository goodsPublishRepository;
    @Resource
    private WeixinTemplateMsgAsync weixinTemplateMsgAsync;
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;


    /**
     * TOKGO 获取用户的发布的订单
     * */
    public List<GoodsPublishOrder> Get(long goodsId, String openid){
        List<GoodsPublishOrder> goodsPublishOrders = goodsPublishOrderRepository.findByGoodsIdAndOpenid(goodsId,openid);
        //检测订单是否已经过期了
        CheckOverTime(goodsPublishOrders);
        return goodsPublishOrders;
    }

    /**
     * TOKGO 获取用户的发布的订单
     * */
    public GoodsPublishOrder Get(String orderid){
        GoodsPublishOrder goodsPublishOrder = goodsPublishOrderRepository.findByOrderID(orderid);
        //检测订单是否已经过期了
        CheckOverTime(goodsPublishOrder);
        return goodsPublishOrder;
    }

    /**
     * TOKGO 用户发布订单
     * */
    @Transactional(rollbackFor=Exception.class)
    public void postOrder(String openid,long goodsId, BigDecimal price,String starttime,String endtime){
        GoodsInfor goodsInfor = goodsService.get(openid, goodsId);
        if (goodsInfor ==null)
            throw new HHTCException(CodeEnum.SYSTEM_NULL);
        //检测用户是否具有车主身份
        fansService.CheckCarpark(fansService.getByOpenid(openid));
        //检查用户押金是否足够
        if(!userFundsService.depositIsenough(openid,goodsInfor.getCommunityId())){
            //押金不够
            throw new HHTCException(CodeEnum.HHTC_FUNDS_DEPOSIT_NO);
        }
        //时间检测
        if (OrderTimeCheck(starttime,endtime,openid,goodsId)) {
            throw new HHTCException(CodeEnum.HHTC_ORDER_PORT_TIMEERROR);
        }
        //生成订单
        BuildOrder(goodsInfor,price,starttime,endtime);
        // TODO 微信发消息 给车位主 订单发布成功
        weixinTemplateMsgAsync.Send("fistdata","ke1","ke2","remakg"
                ,openid, WxMsgEnum.WX_TEST);
    }


    /**
     * TOKGO 订单时间冲突检查
     * @return  true 含有时间冲突
     * */
    public boolean OrderTimeCheck(String stime,String etime,String openid,long goodsId){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            GoodsInfor goodsInfor = goodsService.get(goodsId);
            if (sdf.parse(etime).getTime()>sdf.parse(goodsInfor.getCarUsefulEndDate()).getTime())
                throw new HHTCException(CodeEnum.SYSTEM_ERROR);
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
        goodsPublishOrder.setPhoneNO(fansService.getByOpenid(goodsInfor.getOpenid()).getPhoneNo());
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
     * //TODO 考虑线程同步问题 防止多用户使用产生计算出错
     * */

    public BigDecimal GetPrice(long communityId,String starttime,String endtime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        double free ,temp=0;
        long startdate,enddate;
        try {
            startdate= sdf.parse(starttime).getTime();
            enddate= sdf.parse(endtime).getTime();
        } catch (ParseException e) {
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_TIME_ERROR);
        }
        //如果开始时间小于结束时间  这不符合逻辑
        if(enddate<=startdate)
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_DATA_ERROR);
        CommunityInfo communityInfo = communityService.get(communityId);
        if (communityInfo == null)
            throw  new HHTCException(CodeEnum.SYSTEM_NULL);
        //检测天数 并计算价格
        free = ((int)((enddate- startdate)/Constants.S_DATE_TIMES_DAY))
                *communityInfo.getMoneyRentFull().doubleValue();
        double payday = communityInfo.getMoneyRentDay().doubleValue();
        double paynight= communityInfo.getMoneyRentNight().doubleValue();
        long starttime24 = startdate/Constants.S_DATE_TIMES_HOUR;
        long endtime24 = enddate/Constants.S_DATE_TIMES_HOUR;
        //计算 半点时间
        if ((startdate%Constants.S_DATE_TIMES_DAY !=0)){
            //跨夜晚和晚上的时间分隔 ----- 以价格高的为准
            double ts = ((double) startdate)/Constants.S_DATE_TIMES_HOUR;
            double te = ((double) enddate)/Constants.S_DATE_TIMES_HOUR;
            //16---s--17
            if ((timeNight-ts)<1&&(timeNight-ts)>0)
                temp += Math.abs(paynight-payday);
            //17---e--18
            if ((te-timeNight)<1&&(te-timeNight)>0)
                temp += Math.abs(paynight-payday);

        }else{
            //如果开始整点 结束半点自动加一
            if (enddate%Constants.S_DATE_TIMES_HOUR !=0)
                endtime24++;
        }
        //计算飞非整天的
        // 9---s----e--17
        if (starttime24 >=timeDay && endtime24<=timeNight)
            temp += (endtime24 -starttime24)*payday;
        //9-----s--17--e
        else if (starttime24 >=timeDay &&starttime24 <timeNight && endtime24>timeNight)
            temp += (timeDay-starttime24)*payday +(endtime24-timeNight)*paynight;
        //9----s---17-----0---e--9
        else if (starttime24 >=timeDay &&starttime24 <timeNight && endtime24<=timeDay)
            temp += (timeDay-starttime24)*payday +endtime24*paynight;
        //17---s---e--0     0---s---e--9
        else if ((starttime24 >=timeNight &&starttime24>timeNight)||(starttime24<=timeDay && endtime24<=timeDay))
            temp += (endtime24 -starttime24)*paynight;
        //17---s---0----e---9
        else if (starttime24 >=timeNight && endtime24<=timeDay)
            temp += (starttime24-timeNight+endtime24)*paynight;
        //17---s---0----9---e--17
        else if (starttime24 >=timeNight && endtime24>timeDay && endtime24<=timeNight)
            temp += (starttime24-timeNight+timeDay)*paynight +(endtime24-timeDay)*payday;
        ///0---s---9--e--17
        else
            temp += starttime24*paynight +(endtime24-timeDay)*payday;
        //非整天有最大额度
        if (temp >= communityInfo.getMoneyRentFull().doubleValue())
            temp= communityInfo.getMoneyRentFull().doubleValue();
        free+=temp;
//        free = (free *communityInfo.getRentRatioCarparker()/100.0);
        return  new BigDecimal(free);
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
        CheckOverTime(goodsPublishOrders);
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
        goodsPublishOrder.setPrice(orderInfor.getTotalPrice());
        goodsPublishOrder.setPublishFromTime(orderInfor.getTimeStart());
        goodsPublishOrder.setPublishEndTime(orderInfor.getTimeEnd());
        goodsPublishOrderRepository.save(goodsPublishOrder);
    }



    /**
     * TOKGO 订单到点后删除 所有订单
     * */
    public void CheckOverTime(){
        LogUtil.getQuartzLogger().info("定时任务：订单发布超时 未接单检测");
        CheckOverTime(goodsPublishOrderRepository.findAll());
    }
    /**
     * TOKGO 订单到点后删除 当前订单
     * */
    public boolean CheckOverTime(GoodsPublishOrder goodsPublishOrder){
        //获取当前时间
        return CheckOverTime(goodsPublishOrder,new Date().getTime());
    }

    /**
     * TOKGO 操作
     * */
    private boolean CheckOverTime(GoodsPublishOrder goodsPublishOrder, long timenow) {
        if (goodsPublishOrder.getFromdateCalculate()<timenow){
            String openid = goodsPublishOrder.getOpenid();
            goodsPublishOrderRepository.delete(goodsPublishOrder);
            // TODO 微信发消息 给车位主 订单超时 未被预约 订单取消取消
            weixinTemplateMsgAsync.Send(" 订单超时 未被预约 订单取消取消","ke1","ke2","remakg"
                ,openid, WxMsgEnum.WX_TEST);
            return true;
        }
        return false;
    }

    /**
     * TOKGO 订单到点后删除 订单数组
     * */
    public void CheckOverTime(List<GoodsPublishOrder> goodsPublishOrders){
        //获取当前时间
        long timenow = new Date().getTime();
        for(GoodsPublishOrder goodsPublishOrder : goodsPublishOrders){
            CheckOverTime(goodsPublishOrder,timenow);
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
     * TOKGO 删除订单
     */
    @Transactional(rollbackFor=Exception.class)
    public void delete(long id) {
        //删除市场中的订单
        goodsPublishOrderRepository.delete(id);
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