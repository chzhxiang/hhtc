package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.GoodsPublishOrderRepository;
import com.jadyer.seed.mpp.web.repository.OrderInforRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    @Resource
    private FansService fansService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderInforService orderInforService;
    @Resource
    private OrderInforRepository orderInforRepository;
    @Resource
    private GoodsPublishOrderService goodsPublishOrderService;
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;



    /**
     *TOKGO 获取订单
     * @param type 1---预约订单  2----进行中的订单 3---所有
     * */
    public List<HashMap> GetOrder(String openid,int type){
        List<HashMap> list = new ArrayList<>();
        List<OrderInfor> ownersOrderInfors;
        List<OrderInfor> PostOrderInfors;
        if (type == 3){
            ownersOrderInfors = orderInforRepository.findByOwnersOpenid(openid);
            PostOrderInfors = orderInforRepository.findByPostOpenid(openid);
        } else {
            ownersOrderInfors = orderInforRepository.findByOwnersOpenidAndOrderStatus(openid,type-1);
            PostOrderInfors = orderInforRepository.findByPostOpenidAndOrderStatus(openid,type-1);
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
            if (state == 1)
                hashMap.put("type","owners");
            else
                hashMap.put("type","carpark");
            hashMap.put("orderid",orderInfor.getOrderId());
            hashMap.put("communityName",orderInfor.getCommunityName());
            hashMap.put("carParkNumber",orderInfor.getCarParkNumber());
            hashMap.put("begintime",orderInfor.getTimeStart());
            hashMap.put("endtime",orderInfor.getTimeEnd());
            hashMap.put("price",orderInfor.getCashFee());
            ////TODO 电话先空着
            hashMap.put("phone","");
            list.add(hashMap);
        }

    }

    /**
     *TOKGO 用户取消下单
     * */
    public String CancelOrder(String openid,String orderid,String type){
        //TODO 各种条件的检测

        //查找订单
        OrderInfor orderInfor = orderInforRepository.findByOrderId(orderid);
        if (orderInfor == null)
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_NULL);
        if ("owners".equals(type)){
            if (!openid.equals(orderInfor.getOwnersOpenid()))
                throw new HHTCException(CodeEnum.SYSTEM_PARAM_ERROR);
            orderInforRepository.delete(orderInfor);
            goodsPublishOrderService.BackMarket(orderInfor);

        }else {
            if (!openid.equals(orderInfor.getPostOpenid()))
                throw new HHTCException(CodeEnum.SYSTEM_PARAM_ERROR);
            orderInforRepository.delete(orderInfor);
            goodsService.updateStatus(orderInfor.getGoodsId(),0,1);
        }
        //TODO 微信模板消息通知车主
        //TODO 暂时没有返回
        return "";
    }



    /**
     *TOKGO 用户预约下单
     * */
    public String Reservation(String openid,String orderid,String CarNumber){
        MppFansInfor fansInfor = fansService.getByOpenid(openid);
        if(fansInfor.getInfor_state().charAt(Constants.INFOR_STATE_CARNUMBE_BIT)!='1'){
            throw new HHTCException(CodeEnum.HHTC_INFOR_CARNUMBER_NO);
        }
        //TODO  车牌可以多次预定吗
        //进行订单预约
        GoodsPublishOrder goodsPublishOrder = goodsPublishOrderRepository.findByOrderID(orderid);
        OrderInfor orderInfor = orderInforService.AddOrder(fansInfor,goodsPublishOrder,CarNumber);
        //市场消除订单
        goodsPublishOrderRepository.delete(goodsPublishOrder.getId());
        //TODO 微信模板消息通知车主
        //TODO 暂时没有返回
        return "";
    }

    /**
     *TOKGO 取消预约
     * */
    public String ReservationLogout(String openid,String orderid){
        //获取订单

//        MppFansInfor fansInfor = fansService.getByOpenid(openid);
//        if(fansInfor.getInfor_state().charAt(Constants.INFOR_STATE_CARNUMBE_BIT)!='1'){
//            throw new HHTCException(CodeEnum.HHTC_INFOR_CARNUMBER_NO);
//        }
        //TODO  没有开始写业务逻辑
        //TODO 微信模板消息通知车主
//        //进行订单预约
//        GoodsPublishOrder goodsPublishOrder = goodsPublishOrderRepository.getOne(orderid);
//        OrderInfor orderInfor = orderInforService.AddOrder(fansInfor,goodsPublishOrder,CarNumber);
//        //市场消除订单
//        goodsPublishOrderRepository.delete(goodsPublishOrder.getId());
        return "";
    }

    /**
     *TOKGO 开始订单
     * */
    public String StartOrder(String openid,String orderid){
        //获取订单
        OrderInfor orderInfor = orderInforService.GetOrder(openid,orderid);
        if (orderInfor==null)
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_ERROR);
        if (!orderInforService.StartOrder(orderInfor)){
            throw new HHTCException(CodeEnum.SYSTEM_ERROR);
        }
        //TODO 微信模板消息通知车主
        return "";
    }




}
