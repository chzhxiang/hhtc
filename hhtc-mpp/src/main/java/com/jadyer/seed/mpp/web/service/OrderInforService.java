package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.constant.WxMsgEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.OrderHistoryRepository;
import com.jadyer.seed.mpp.web.repository.OrderInforRepository;
import com.jadyer.seed.mpp.web.repository.OrderInoutRepository;
import com.jadyer.seed.mpp.web.repository.TemporaryOutRepository;
import com.jadyer.seed.mpp.web.service.async.OrderAsync;
import com.jadyer.seed.mpp.web.service.async.WeixinTemplateMsgAsync;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.jadyer.seed.comm.constant.Constants.S_DATE_TIMES_HOUR;
import static com.jadyer.seed.comm.constant.Constants.S_DATE_TIMES_HOURHALF;
import static com.jadyer.seed.comm.constant.Constants.S_FINE;

/**
 * @描述
 * @创建人 TOKGO
 * @创建时间 2017/11/27
 * @修改人和其它信息
 */
@Service
public class OrderInforService {
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private OrderAsync orderAsync;
    @Resource
    private OrderInforRepository orderInforRepository;
    @Resource
    private OrderInoutRepository orderInoutRepository;
    @Resource
    private TemporaryOutRepository temporaryOutRepository;
    @Resource
    private OrderHistoryRepository orderHistoryRepository;
    @Resource
    private WeixinTemplateMsgAsync weixinTemplateMsgAsync;


    /**
     * TOKGO获取用户历史当担信息
     */
    public List<HashMap> Gethistory(String openid, int pageNo) {
        List<HashMap> hashMapList = new ArrayList<>();
        //排序 降序排序
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        //分页
        Pageable pageable = new PageRequest(pageNo, 10, sort);
        //条件
        Condition<OrderHistory> spec = Condition.<OrderHistory>or().eq("postOpenid", openid).eq("ownersOpenid", openid);
        //执行
        Page<OrderHistory> page = orderHistoryRepository.findAll(spec, pageable);
        List<OrderHistory> list = page.getContent();
        for (OrderHistory orderHistory : list) {
            loaddata(orderHistory, hashMapList, openid);
        }
        return hashMapList;
    }


    /**
     * TOKGO 数据装填
     **/
    private void loaddata(OrderHistory orderHistory, List<HashMap> list, String openid) {
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
        if (orderHistory.getFineFlag() == 0)
            hashMap.put("Defaultprice", 0);
        else
            hashMap.put("Defaultprice", orderHistory.getFinePrice());
        list.add(hashMap);
    }


    /**
     * TOKGO 保存订单
     **/
    public void Save(OrderInfor orderInfor) {
        orderInforRepository.save(orderInfor);
    }

    /**
     * TOKGO 添加新增订单(预约)
     */
    public OrderInfor AddOrder(MppFansInfor ownersInfor, GoodsPublishOrder goodsPublishOrder, String CarNumber) {
        OrderInfor orderInfor = new OrderInfor();
//        CommunityInfo communityInfo = communityService.get(communityId);
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
        orderInforRepository.save(orderInfor);
        return orderInfor;
    }

    /**
     * TOKGO 超时补款
     */
    public void OvertimeRepayment(String openid, String orderid,double fineprice) {
        //查找订单
        OrderHistory orderHistory = orderHistoryRepository.findByOrderId(orderid);
        //查找用户资金
        UserFunds userFunds = userFundsService.get(openid);
        if (orderHistory==null ||userFunds ==null)
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_ERROR);
        BigDecimal price =BigDecimal.valueOf(fineprice);
        if (!userFundsService.BalanceIsenough(openid,price ))
            throw new HHTCException(CodeEnum.HHTC_FUNDS_BALANCE_NO);
        //扣款
        userFundsService.subtractMoneyBalanceForFans(openid,price,orderid,"订单超时扣款");
        //分润
        orderAsync.Penny(price.doubleValue(),orderHistory);
        //添加临时开门权限
        TemporaryOut temporaryOut = new TemporaryOut();
        temporaryOut.setCarNumber(orderHistory.getCarNumber());
        temporaryOut.setCommunityId(orderHistory.getCommunityId());
        temporaryOut.setTimeEnd(new Date().getTime()+Constants.S_TEMPORARYOUT_TIME);
        temporaryOutRepository.save(temporaryOut);
        orderHistory.setFinePrice(price);
        orderHistory.setFineFlag(1);
        orderHistoryRepository.save(orderHistory);
    }

    /**
     * TOKGO 计算超时罚款
     * */
    public double CalculateFine(String orderid){
        long nowtime = new Date().getTime();
        double fineprice =0;
        OrderHistory orderHistory = orderHistoryRepository.findByOrderId(orderid);
        if (orderHistory==null)
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_ERROR);
        //已经缴纳过罚金了
        if (orderHistory.getFineFlag()==1)
            throw new HHTCException(CodeEnum.HHTC_ORDER_FINE_ED);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            long endtime =sdf.parse(orderHistory.getEndTime()).getTime();
            //计算罚金
            if (nowtime - endtime<=S_DATE_TIMES_HOUR && nowtime -endtime>0)
                fineprice = S_FINE;
            if (nowtime -endtime>S_DATE_TIMES_HOUR){
                long t = (nowtime-endtime-S_DATE_TIMES_HOUR);
                if (t%S_DATE_TIMES_HOURHALF==0)
                    fineprice += (t/S_DATE_TIMES_HOURHALF)*S_FINE;
                else
                    fineprice += ((t/S_DATE_TIMES_HOURHALF)+1)*S_FINE;
            }
        } catch (ParseException e) {
            throw new HHTCException(CodeEnum.SYSTEM_ERROR);
        }
        return fineprice;
    }


    /**
     * //TODO 测试接口
     **/
    public void test(String openid) {
        weixinTemplateMsgAsync.Send("111111111", "2222222222", "3333333333"
                , "2222222222", "oyExF0yWol_vLIJDN_WlWREBshTE", WxMsgEnum.WX_TEST);

    }

    /**
     * TOKGO 车主获取订单
     */
    public List<OrderInfor> GetOwnersOrder(String ownersopenid) {
        List<OrderInfor> orderInfors = orderInforRepository.findByOwnersOpenid(ownersopenid);
        CheckStartOrEndOrder(orderInfors);
        return orderInfors;
    }

    /**
     * TOKGO 车位主获取订单
     */
    public List<OrderInfor> GetPostOrder(String postopenid) {
        List<OrderInfor> orderInfors = orderInforRepository.findByPostOpenid(postopenid);
        CheckStartOrEndOrder(orderInfors);
        return orderInfors;
    }

    /**
     * TOKGO 车位主获取订单
     *
     * @param type 1---预约订单  2----进行中的订单
     */
    public List<OrderInfor> GetPostOrder(String postopenid, int type) {
        List<OrderInfor> orderInfors = orderInforRepository.findByPostOpenidAndOrderStatus(postopenid, type - 1);
        CheckStartOrEndOrder(orderInfors);
        return orderInfors;
    }


    /**
     * TOKGO 车主获取订单
     *
     * @param type 1---预约订单  2----进行中的订单
     */
    public List<OrderInfor> GetOwnersOrder(String ownersopenid, int type) {
        List<OrderInfor> orderInfors = orderInforRepository.findByOwnersOpenidAndOrderStatus(ownersopenid, type - 1);
        CheckStartOrEndOrder(orderInfors);
        return orderInfors;
    }

    /**
     * TOKGO 获取订单订单
     */
    public OrderInfor GetOrder(String orderid) {
        OrderInfor orderInfor = orderInforRepository.findByOrderId(orderid);
        CheckStartOrEndOrder(orderInfor);
        return orderInfor;
    }


    /**
     * TOKGO 删除订单
     */
    public void Delelte(long id) {
        orderInforRepository.delete(id);
    }

    /**
     * TOKGO 检测订单是否开始结束 （订单开始结束由系统完成）
     */
    public void CheckStartOrEndOrder() {
        CheckStartOrEndOrder(orderInforRepository.findAll());
    }


    /**
     * TOKGO 检测订单是否开始 或者结束 （订单开始结束由系统完成）
     */
    public void CheckStartOrEndOrder(OrderInfor orderInfor) {
        //获取当前时间
        CheckStartOrEndOrder(orderInfor, new Date().getTime());
    }

    /**
     * TOKGO 检测的操作
     */
    private boolean CheckStartOrEndOrder(OrderInfor orderInfor, long timenow) {
        if (orderInfor == null)
            return false;
        try {
            if (orderInfor.getTimeStartCalculate() < timenow && orderInfor.getOrderStatus() == 0) {
                //订单开始
                orderInforRepository.updateOrderState(1, orderInfor.getOrderId());
                return true;
            }
            if (orderInfor.getTimeEndCalculate() < timenow) {
                //订单结束
                orderChange(orderInfor);
                //分钱 车位主和平台
                orderAsync.Penny(orderInfor.getTotalPrice().subtract(orderInfor.getTotalOutPrice())
                        .doubleValue(), orderInfor);
                orderInforRepository.delete(orderInfor);
                // TODO 微信发消息 给车位主 订单完成
                weixinTemplateMsgAsync.Send("订单完成", "ke1", "ke2", "remakg"
                        , orderInfor.getPostOpenid(), WxMsgEnum.WX_TEST);
                // TODO 微信发消息 给车主 订单完成
                weixinTemplateMsgAsync.Send("订单完成", "ke1", "ke2", "remakg"
                        , orderInfor.getOwnersOpenid(), WxMsgEnum.WX_TEST);
                return true;
            }
            //计算可提取金额
            if ((timenow - orderInfor.getOutPriceTime()) > Constants.S_DATE_TIMES_MONTH) {
                orderAsync.CalculateMonth(orderInfor,timenow);
            }
        } catch (Exception e) {
        }
        return false;
    }


    /**
     * TOKGO 检测订单是否开始结束 （订单开始结束由系统完成）
     */
    public void CheckStartOrEndOrder(List<OrderInfor> orderInfors) {
        //获取当前时间
        long timenow = new Date().getTime();
        for (OrderInfor orderInfor : orderInfors) {
            CheckStartOrEndOrder(orderInfor, timenow);
        }
    }

    /**
     * TOKGO 订单由进行中转为已完成
     */
    private void orderChange(OrderInfor orderInfor) {
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrderId(orderInfor.getOrderId());
        orderHistory.setPostOpenid(orderInfor.getPostOpenid());
        orderHistory.setPostPhoneNO(orderInfor.getPostPhoneNO());
        orderHistory.setOwnersOpenid(orderInfor.getOwnersOpenid());
        orderHistory.setOwnersPhoneNO(orderInfor.getOwnersPhoneNO());
        orderHistory.setCommunityId(orderInfor.getCommunityId());
        orderHistory.setCommunityName(orderInfor.getCommunityName());
        orderHistory.setGoodsId(orderInfor.getGoodsId());
        orderHistory.setCarParkNumber(orderInfor.getCarParkNumber());
        orderHistory.setCarParkImg(orderInfor.getCarParkImg());
        orderHistory.setCarNumber(orderInfor.getCarNumber());
        orderHistory.setFromTime(orderInfor.getTimeStart());
        orderHistory.setEndTime(orderInfor.getTimeEnd());
        orderHistory.setTotalprice(orderInfor.getTotalPrice());
        //罚金设置 由后续程序进行
        orderHistoryRepository.saveAndFlush(orderHistory);
    }


    /**
     * TOKGO  通过车牌获取订单
     */
    public List<OrderInfor> Get(String carnumber,long communityid){
        List<OrderInfor> orderInfors = orderInforRepository.findByCarNumberAndCommunityId(carnumber,communityid);
        CheckStartOrEndOrder(orderInfors);
        return orderInfors;
    }

    /**
     * TOKGO 更新订单 车的位置
     *   0--车在外面  1--车在里面
     * */
    public void UpdateInoutState(int state,String orderid){
        orderInforRepository.updateinoutState(state,orderid);
    }
}
