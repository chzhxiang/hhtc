package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.mpp.web.model.GoodsPublishOrder;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.model.OrderHistory;
import com.jadyer.seed.mpp.web.model.OrderInfor;
import com.jadyer.seed.mpp.web.repository.OrderHistoryRepository;
import com.jadyer.seed.mpp.web.repository.OrderInforRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
public class OrderInforService {
    @Resource
    private OrderInforRepository orderInforRepository;
    @Resource
    private OrderHistoryRepository orderHistoryRepository;


    /**
     *TOKGO获取用户历史当担信息
     * */
    public List<HashMap> Gethistory(String openid, int pageNo){
        List<HashMap> hashMapList = new ArrayList<>();
        //排序 降序排序
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        //分页
        Pageable pageable = new PageRequest(pageNo, 10, sort);
        //条件
        Condition<OrderHistory> spec = Condition.<OrderHistory>or().eq("postOpenid", openid).eq("ownersOpenid",openid);
        //执行
        Page<OrderHistory> page = orderHistoryRepository.findAll(spec, pageable);
        List<OrderHistory> list = page.getContent();
        for (OrderHistory orderHistory:list){
            loaddata(orderHistory,hashMapList,openid);
        }
        return hashMapList;
    }


    /**
     * TOKGO 数据装填
     * **/
    private void loaddata(OrderHistory orderHistory,List<HashMap> list,String openid) {
        HashMap hashMap = new HashMap();
        if (openid.equals(orderHistory.getOwnersOpenid())) {
            hashMap.put("type", "owners");
            hashMap.put("phone", orderHistory.getPostPhoneNO());
        } else {
            hashMap.put("type", "carpark");
            hashMap.put("phone", orderHistory.getOwnersPhoneNO());
        }
        hashMap.put("orderid", orderHistory.getOrderId());
        hashMap.put("communityName", orderHistory.getCommunityName());
        hashMap.put("carParkNumber", orderHistory.getCarParkNumber());
        hashMap.put("begintime", orderHistory.getFromTime());
        hashMap.put("endtime", orderHistory.getEndTime());
        hashMap.put("price", orderHistory.getTotalprice());
        if (orderHistory.getFineFlag()==0)
            hashMap.put("Defaultprice", 0);
        else
            hashMap.put("Defaultprice", orderHistory.getFinePrice());
        list.add(hashMap);
    }



    /**
     * TOKGO 添加新增订单(预约)
     * */
    public OrderInfor AddOrder( MppFansInfor ownersInfor, GoodsPublishOrder goodsPublishOrder,String CarNumber){
        OrderInfor orderInfor = new OrderInfor();
        //状态为预约
        orderInfor.setOrderStatus(0);
        orderInfor.setOrderId(goodsPublishOrder.getOrderID());
        orderInfor.setPostPhoneNO(goodsPublishOrder.getPhoneNO());
        orderInfor.setPostOpenid(goodsPublishOrder.getOpenid());
        orderInfor.setOwnersOpenid(ownersInfor.getOpenid());
        orderInfor.setOwnersPhoneNO(ownersInfor.getPhoneNo());
        orderInfor.setCommunityId(goodsPublishOrder.getCommunityId());
        orderInfor.setCommunityName(goodsPublishOrder.getCommunityName());
        orderInfor.setGoodsId(goodsPublishOrder.getGoodsId());
        orderInfor.setCarParkNumber(goodsPublishOrder.getCarParkNumber());
        orderInfor.setCarParkImg(goodsPublishOrder.getCarParkImg());
        orderInfor.setCarNumber(CarNumber);
        orderInfor.setTotalPrice(goodsPublishOrder.getPrice());
        orderInfor.setTimeStart(goodsPublishOrder.getPublishFromTime());
        orderInfor.setTimeEnd(goodsPublishOrder.getPublishEndTime());
        orderInforRepository.saveAndFlush(orderInfor);
        return orderInfor;
    }


    /**
     * TOKGO 车主获取订单
     * */
    public List<OrderInfor> GetOwnersOrder(String ownersopenid){
        List<OrderInfor> orderInfors =orderInforRepository.findByOwnersOpenid(ownersopenid);
        CheckStartOrEndOrder(orderInfors);
        return orderInfors;
    }

    /**
     * TOKGO 车位主获取订单
     * */
    public List<OrderInfor> GetPostOrder(String postopenid){
        List<OrderInfor> orderInfors =orderInforRepository.findByPostOpenid(postopenid);
        CheckStartOrEndOrder(orderInfors);
        return orderInfors;
    }

    /**
     * TOKGO 车位主获取订单
     * @param type 1---预约订单  2----进行中的订单
     * */
    public List<OrderInfor> GetPostOrder(String postopenid,int type){
        List<OrderInfor> orderInfors =orderInforRepository.findByPostOpenidAndOrderStatus(postopenid,type-1);
        CheckStartOrEndOrder(orderInfors);
        return orderInfors;
    }


    /**
     * TOKGO 车主获取订单
     * @param type 1---预约订单  2----进行中的订单
     * */
    public List<OrderInfor> GetOwnersOrder(String ownersopenid,int type){
        List<OrderInfor> orderInfors =orderInforRepository.findByOwnersOpenidAndOrderStatus(ownersopenid,type-1);
        CheckStartOrEndOrder(orderInfors);
        return orderInfors;
    }

    /**
     * TOKGO 获取订单订单
     * */
    public OrderInfor GetOrder(String orderid){
        OrderInfor orderInfor = orderInforRepository.findByOrderId(orderid);
        CheckStartOrEndOrder(orderInfor);
        return orderInfor;
    }


    /**
     * TOKGO 删除订单
     * */
    public void Delelte(long id){
        orderInforRepository.delete(id);
    }


    /**
     * TOKGO 检测订单是否开始 或者结束 （订单开始由系统完成）
     * */
    public boolean CheckStartOrEndOrder(OrderInfor orderInfor){
        //获取当前时间
        long timenow = new Date().getTime();
        if (orderInfor.getTimeStartCalculate()>=timenow && orderInfor.getOrderStatus()==0) {
            //订单开始
            orderInforRepository.updateOrderState(1,orderInfor.getOrderId());
            //TODO 发送微信模板消息
            return true;
        }
        if (orderInfor.getTimeEndCalculate()>=timenow) {
            //订单结束
            //TODO 做订单结束的操作
            //TODO 发送微信模板消息
            return true;
        }
        //计算可提取金额
        if ((timenow-orderInfor.getOutPriceTime())>=Constants.S_DATE_TIMES_MONTH){
            //TODO 这样计算有问题
        }
        return false;
    }

    /**
     * TOKGO 检测订单是否开始 （订单开始由系统完成）
     * */
    public void CheckStartOrEndOrder(List<OrderInfor> orderInfors){
        //获取当前时间
        long timenow = new Date().getTime();
        for(OrderInfor orderInfor : orderInfors){
            if (orderInfor.getTimeStartCalculate()>=timenow && orderInfor.getOrderStatus()==0) {
                //订单开始
                orderInforRepository.updateOrderState(1,orderInfor.getOrderId());
                //TODO 发送微信模板消息
        }
            if (orderInfor.getTimeEndCalculate()>=timenow) {
                //订单结束
                //TODO 做订单结束的操作
                //TODO 发送微信模板消息
            }
        }
    }


}
