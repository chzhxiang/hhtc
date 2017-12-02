package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.constant.WxMsgEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.service.async.OrderAsync;
import com.jadyer.seed.mpp.web.service.async.WeixinTemplateMsgAsync;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @描述
 * @创建人 TOKGO
 * @创建时间 2017/11/27
 * @修改人和其它信息
 */
@Service
public class MarketTransactionService {
    @Value("${hhtc.orderLock.lock}")
    private int locktime;
    @Resource
    private OrderAsync orderAsync;
    @Resource
    private FansService fansService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private OrderInforService orderInforService;
    @Resource
    private WeixinTemplateMsgAsync weixinTemplateMsgAsync;
    @Resource
    private GoodsPublishOrderService goodsPublishOrderService;



    /**
     *TOKGO 获取订单
     * @param type 1---预约订单  2----进行中的订单 3---所有
     * */
    public List<HashMap> GetOrder(String openid,int type){
        List<HashMap> list = new ArrayList<>();
        List<OrderInfor> ownersOrderInfors;
        List<OrderInfor> PostOrderInfors;
        if (type == 3){
            ownersOrderInfors = orderInforService.GetOwnersOrder(openid);
            PostOrderInfors = orderInforService.GetPostOrder(openid);
        } else {
            ownersOrderInfors = orderInforService.GetOwnersOrder(openid,type);
            PostOrderInfors = orderInforService.GetPostOrder(openid,type);
        }
        loaddata(ownersOrderInfors,list,1);
        loaddata(PostOrderInfors,list,2);
        return list;
    }

    /**
     * TOKGO 数据装填
     * **/
    private void loaddata(List<OrderInfor> orderInfors,List<HashMap> list,int state){
        HashMap hashMap;
        for (OrderInfor orderInfor:orderInfors){
            hashMap = new HashMap();
            if (state == 1) {
                hashMap.put("type", "owners");
                hashMap.put("phone",orderInfor.getPostPhoneNO());
            }
            else {
                hashMap.put("type", "carpark");
                hashMap.put("phone",orderInfor.getOwnersPhoneNO());
            }
            hashMap.put("orderid",orderInfor.getOrderId());
            hashMap.put("communityName",orderInfor.getCommunityName());
            hashMap.put("carParkNumber",orderInfor.getCarParkNumber());
            hashMap.put("begintime",orderInfor.getTimeStart());
            hashMap.put("endtime",orderInfor.getTimeEnd());
            hashMap.put("price",orderInfor.getTotalPrice());
            hashMap.put("Defaultprice", orderInfor.getOutPrice());
            list.add(hashMap);
        }
    }

    /**
     *TOKGO 用户取消下单
     * */
    public void CancelOrder(String openid,String orderid,String type){

        //获取订单
        OrderInfor orderInfor = orderInforService.GetOrder(orderid);
        //获取当前时间
        long timenow = new Date().getTime();
        //如果小于三个小时 不能取消预约 只能申诉退单
        if((orderInfor.getTimeEndCalculate()-timenow)<locktime* Constants.S_DATE_TIMES_HOUR){
            throw new HHTCException(CodeEnum.HHTC_ORDER_DISRESERVATION_FAIL);
        }
        //退钱给车主
        userFundsService.addMoneyBalanceForFans(orderInfor.getOwnersOpenid(),orderInfor.getTotalPrice());
        if ("owners".equals(type)){
            if (!openid.equals(orderInfor.getOwnersOpenid()))
                throw new HHTCException(CodeEnum.SYSTEM_PARAM_DATA_ERROR);
            orderInforService.Delelte(orderInfor.getId());
            goodsPublishOrderService.BackMarket(orderInfor);

        }else {
            if (!openid.equals(orderInfor.getPostOpenid()))
                throw new HHTCException(CodeEnum.SYSTEM_PARAM_DATA_ERROR);
            orderInforService.Delelte(orderInfor.getId());
        }
//        // TODO 微信发消息
//        weixinTemplateMsgAsync.Send("fistdata","ke1","ke2","remakg"
//                ,fansInfor.getAppid(),fansInfor.getOpenid(), WxMsgEnum.WX_TEST);
    }



    /**
     *TOKGO 用户预约下单
     * */
    public void Reservation(String openid,String orderid,String CarNumber,int type){
        MppFansInfor fansInfor = fansService.getByOpenid(openid);
        //检测用户是否具有使用资格
        fansService.CheckOwners(fansInfor);
        //获取订单
        GoodsPublishOrder goodsPublishOrder = goodsPublishOrderService.Get(orderid);
        if (goodsPublishOrder == null){
            //订单被抢
            throw new HHTCException(CodeEnum.HHTC_ORDER_RESERVATION_FAIL);
        }
        //检查用户押金是否足够
        if(!userFundsService.depositIsenough(openid,goodsPublishOrder.getCommunityId())){
            //押金不够
            throw new HHTCException(CodeEnum.HHTC_FUNDS_DEPOSIT_NO);
        }
        //检测用户余额是否足够
        if (!userFundsService.BalanceIsenough(openid,goodsPublishOrder.getPrice())){
            //余额不足
            throw new HHTCException(CodeEnum.HHTC_FUNDS_BALANCE_NO);
        }
        //订单时间冲突检查
        if (type == 1&& OrderTimeCheck(goodsPublishOrder.getPublishFromTime(),goodsPublishOrder.getPublishEndTime(),openid)){
            throw new HHTCException(CodeEnum.HHTC_ORDER_PORT_TIMEERROR);
        }
        //进行订单预约
        OrderInfor orderInfor = orderInforService.AddOrder(fansInfor,goodsPublishOrder,CarNumber);
        //扣除用户的余额
        userFundsService.subtractMoneyBalanceForFans(openid,goodsPublishOrder.getPrice());
        //市场消除订单
        goodsPublishOrderService.delete(goodsPublishOrder.getId());
        // TODO 微信发消息
        weixinTemplateMsgAsync.Send("fistdata","ke1","ke2","remakg"
                ,fansInfor.getAppid(),fansInfor.getOpenid(), WxMsgEnum.WX_TEST);
    }

    /**
     * TOKGO 订单时间冲突检查
     * @return  true 含有时间冲突
     * */
    public boolean OrderTimeCheck(String FromTime,String EndTime,String openid){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            long starttime =  sdf.parse(FromTime).getTime();
            long endtime =  sdf.parse(EndTime).getTime();
            List<OrderInfor> orderInfors = orderInforService.GetOwnersOrder(openid);
            for (OrderInfor orderInfor:orderInfors) {
                long orderstarttime = sdf.parse(orderInfor.getTimeStart()).getTime();
                long orderendtime = sdf.parse(orderInfor.getTimeEnd()).getTime();
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
     * TOKGO 订单结算
     * **/
    public void OrderGetMomey(String orderid,String openid){
        OrderInfor orderInfor = orderInforService.GetOrder(orderid);
        if (orderInfor == null ||!orderInfor.getPostOpenid().equals(openid) )
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_ERROR);
        if (orderInfor.getOutPrice().doubleValue()<1){
            throw new HHTCException(CodeEnum.HHTC_ORDER_MONEY_NO);
        }
        double toal = orderInfor.getTotalOutPrice().doubleValue() + orderInfor.getOutPrice().doubleValue();
        orderAsync.Penny(orderInfor.getOutPrice().doubleValue(),orderInfor);
        orderInfor.setTotalOutPrice(new BigDecimal(toal));
        orderInfor.setOutPrice(new BigDecimal(0));
        orderInforService.Save(orderInfor);
    }


}
