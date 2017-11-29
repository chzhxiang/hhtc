package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.GoodsPublishOrderRepository;
import com.jadyer.seed.mpp.web.repository.OrderInforRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private UserFundsService userFundsService;
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
            hashMap.put("price",orderInfor.getPrice());
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
    public String Reservation(String openid,String orderid,String CarNumber,int type){
        MppFansInfor fansInfor = fansService.getByOpenid(openid);
        //检测用户是否具有使用资格
        fansService.CheckOwners(fansInfor);
        ////TODO 订单 超时删除
        //获取订单
        GoodsPublishOrder goodsPublishOrder = goodsPublishOrderRepository.findByOrderID(orderid);
        //检查用户押金是否足够
        if(!userFundsService.depositIsenough(openid,goodsPublishOrder.getCommunityId())){
            //押金不够
            throw new HHTCException(CodeEnum.HHTC_FUNDS_DEPOSIT_NO);
        }



        //订单时间冲突检查
        if (type == 1&& OrderTimeCheck(goodsPublishOrder,openid)){
            throw new HHTCException(CodeEnum.HHTC_ORDER_PORT_TIMEERROR);
        }
        //进行订单预约
        OrderInfor orderInfor = orderInforService.AddOrder(fansInfor,goodsPublishOrder,CarNumber);
        //市场消除订单
        goodsPublishOrderRepository.delete(goodsPublishOrder.getId());
        //TODO 微信模板消息通知车主
        //TODO 暂时没有返回
        return "";
    }

    /****/




    /**
     * TOKGO 订单时间冲突检查
     * @return  true 含有时间冲突
     * */
    private boolean OrderTimeCheck(GoodsPublishOrder goodsPublishOrder,String openid){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            long starttime =  sdf.parse(goodsPublishOrder.getPublishFromTime()).getTime();
            long endtime =  sdf.parse(goodsPublishOrder.getPublishEndTime()).getTime();
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
        OrderInfor orderInfor = orderInforService.GetOrder(orderid);
        if (orderInfor==null)
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_ERROR);
        if (!orderInforService.StartOrder(orderInfor)){
            throw new HHTCException(CodeEnum.SYSTEM_ERROR);
        }
        //TODO 微信模板消息通知车主
        return "";
    }



}
