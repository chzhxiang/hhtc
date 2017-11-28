package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.mpp.web.model.GoodsPublishOrder;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.model.OrderInfor;
import com.jadyer.seed.mpp.web.repository.OrderInforRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @描述
 * @创建人 TOKGO
 * @创建时间 2017/11/27
 * @修改人和其它信息
 */
@Service
public class OrderInforService {
    @Resource
    private OrderInforRepository orderInforRepository;

    /**
     * TOKGO 添加新增订单(预约)
     * */
    public OrderInfor AddOrder( MppFansInfor ownersInfor, GoodsPublishOrder goodsPublishOrder,String CarNumber){
        OrderInfor orderInfor = new OrderInfor();
        orderInfor.setOrderId(goodsPublishOrder.getOrderID());
        orderInfor.setOrderStatus(Constants.ORDER_STATE_RESERVATION);
        orderInfor.setPostOpenid(goodsPublishOrder.getOpenid());
        orderInfor.setOwnersOpenid(ownersInfor.getOpenid());
        orderInfor.setCommunityId(goodsPublishOrder.getCommunityId());
        orderInfor.setCommunityName(goodsPublishOrder.getCommunityName());
        orderInfor.setGoodsId(goodsPublishOrder.getGoodsId());
        orderInfor.setCarParkNumber(goodsPublishOrder.getCarParkNumber());
        orderInfor.setCarParkImg(goodsPublishOrder.getCarParkImg());
        orderInfor.setCarNumber(CarNumber);
        orderInfor.setReservationFromTime(DateUtil.getStringDate());
        //TODO 很多相关参数没有设置
        orderInforRepository.save(orderInfor);
        return orderInfor;
    }


    /**
     * TOKGO 车主获取订单
     * */
    public List<OrderInfor> GetOwnersOrder(String ownersopenid){
        return orderInforRepository.findByOwnersOpenid(ownersopenid);
    }

    /**
     * TOKGO 订单
     * */
    public OrderInfor GetOrder(String orderid){
        return orderInforRepository.findByOrderId(orderid);
    }


    /**
     * TOKGO 开始订单
     * */
    public boolean StartOrder(OrderInfor orderInfor){
        if (orderInfor.getOrderStatus()==0)
        {
//            orderInforRepository.updateOrderState(1,orderInfor.getOrderId());
            return true;
        }
        return false;
    }



}
