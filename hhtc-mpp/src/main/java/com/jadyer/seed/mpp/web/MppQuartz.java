package com.jadyer.seed.mpp.web;

import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.web.model.OrderInfo;
import com.jadyer.seed.mpp.web.model.RedpackInfo;
import com.jadyer.seed.mpp.web.model.RefundApply;
import com.jadyer.seed.mpp.web.model.RefundInfo;
import com.jadyer.seed.mpp.web.service.GoodsNeedService;
import com.jadyer.seed.mpp.web.service.GoodsPublishOrderService;
import com.jadyer.seed.mpp.web.service.GoodsPublishService;
import com.jadyer.seed.mpp.web.service.OrderInoutService;
import com.jadyer.seed.mpp.web.service.OrderRentService;
import com.jadyer.seed.mpp.web.service.OrderService;
import com.jadyer.seed.mpp.web.service.RedpackService;
import com.jadyer.seed.mpp.web.service.RefundApplyService;
import com.jadyer.seed.mpp.web.service.RefundService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/23 11:06.
 */
@Component
public class MppQuartz {
    @Resource
    private OrderService orderService;
    @Resource
    private RefundService refundService;
    @Resource
    private RedpackService redpackService;
    @Resource
    private GoodsNeedService goodsNeedService;
    @Resource
    private OrderRentService orderRentService;
    @Resource
    private OrderInoutService orderInoutService;
    @Resource
    private RefundApplyService refundApplyService;
    @Resource
    private GoodsPublishService goodsPublishService;
    @Resource
    private GoodsPublishOrderService goodsPublishOrderService;

    /**
     * 微信支付自动补单（50秒一次）
     */
    @Scheduled(cron="50 * * * * ?")
    void orderQuery(){
        List<OrderInfo> orderInfoList = orderService.getPayingList();
        String currIndex = "1";
        int len = orderInfoList.size();
        if(len == 0){
            return;
        }
        LogUtil.getQuartzLogger().info("定时任务：微信支付自动补单-->查到记录[{}]条", len);
        for(OrderInfo obj : orderInfoList){
            currIndex = JadyerUtil.leftPadUseZero(currIndex, String.valueOf(len).length());
            LogUtil.getQuartzLogger().info("定时任务：微信支付自动补单-->开始处理[{}-{}]条， orderInfoId={}", len, currIndex, obj.getId());
            orderService.query(obj, 1);
            LogUtil.getQuartzLogger().info("定时任务：微信支付自动补单-->处理完毕[{}-{}]条", len, currIndex);
            currIndex = String.valueOf(Integer.parseInt(currIndex) + 1);
        }
    }


    /**
     * 微信退款和提现补单（每分钟一次）
     */
    @Scheduled(cron="0 0/1 * * * ?")
    void refundQuery(){
        List<RedpackInfo> redpackList = redpackService.getPayingList();
        String currIndex = "1";
        int len = redpackList.size();
        if(len > 0){
            LogUtil.getQuartzLogger().info("定时任务：微信红包补单-->查到记录[{}]条", len);
            for(RedpackInfo obj : redpackList){
                currIndex = JadyerUtil.leftPadUseZero(currIndex, String.valueOf(len).length());
                LogUtil.getQuartzLogger().info("定时任务：微信红包补单-->开始处理[{}-{}]条，redpackInfoId={}", len, currIndex, obj.getId());
                redpackService.query(obj);
                LogUtil.getQuartzLogger().info("定时任务：微信红包补单-->处理完毕[{}-{}]条", len, currIndex);
                currIndex = String.valueOf(Integer.parseInt(currIndex) + 1);
            }
        }
        List<RefundInfo> refundList = refundService.getPayingList();
        currIndex = "1";
        len = refundList.size();
        if(len == 0){
            return;
        }
        LogUtil.getQuartzLogger().info("定时任务：微信退款补单-->查到记录[{}]条", len);
        for(RefundInfo obj : refundList){
            currIndex = JadyerUtil.leftPadUseZero(currIndex, String.valueOf(len).length());
            LogUtil.getQuartzLogger().info("定时任务：微信退款补单-->开始处理[{}-{}]条，refundInfoId={}", len, currIndex, obj.getId());
            refundService.query(obj);
            LogUtil.getQuartzLogger().info("定时任务：微信退款补单-->处理完毕[{}-{}]条", len, currIndex);
            currIndex = String.valueOf(Integer.parseInt(currIndex) + 1);
        }
    }


    /**
     * 微信退款和提现（2分钟一次）
     */
    @Scheduled(cron="0 0/2 * * * ?")
    void refund(){
        List<RefundApply> refundList = refundApplyService.getTaskList(0);
        String currIndex = "1";
        int len = refundList.size();
        if(len > 0){
            LogUtil.getQuartzLogger().info("定时任务：微信退款和提现-->查到记录[{}]条", len);
            for(RefundApply obj : refundList){
                currIndex = JadyerUtil.leftPadUseZero(currIndex, String.valueOf(len).length());
                LogUtil.getQuartzLogger().info("定时任务：微信退款和提现-->开始处理[{}-{}]条，refundApplyId={}", len, currIndex, obj.getId());
                refundApplyService.refund(obj);
                LogUtil.getQuartzLogger().info("定时任务：微信退款和提现-->处理完毕[{}-{}]条", len, currIndex);
                currIndex = String.valueOf(Integer.parseInt(currIndex) + 1);
            }
        }
        //更新支付状态
        refundList = refundApplyService.getTaskList(1);
        currIndex = "1";
        len = refundList.size();
        if(len == 0){
            return;
        }
        LogUtil.getQuartzLogger().info("定时任务：微信退款和提现：支付状态更新-->查到记录[{}]条", len);
        for(RefundApply obj : refundList){
            currIndex = JadyerUtil.leftPadUseZero(currIndex, String.valueOf(len).length());
            LogUtil.getQuartzLogger().info("定时任务：微信退款和提现：支付状态更新-->开始处理[{}-{}]条，refundApplyIdId={}", len, currIndex, obj.getId());
            refundApplyService.refundUpdatePayStatus(obj);
            LogUtil.getQuartzLogger().info("定时任务：微信退款和提现：支付状态更新-->处理完毕[{}-{}]条", len, currIndex);
            currIndex = String.valueOf(Integer.parseInt(currIndex) + 1);
        }
    }


    /**
     * 订单归档（4分钟一次）
     * <p>
     *     一般是处理那种在充值页面，弹出来微信支付密码框之后，粉丝没有输入密码去支付，的订单
     * </p>
     */
    @Scheduled(cron="0 0/4 * * * ?")
    void orderHistory(){
        orderService.history();
    }


    /**
     * 订单分潤（8/18/28號淩晨00:08分執行）
     */
    @Scheduled(cron="0 8 0 8,18,28 * ?")
    void orderRentSettle(){
        orderRentService.rentSettle();
    }


    /**
     * 订单超时（5分钟一次）
     */
    @Scheduled(cron="0 0/5 * * * ?")
    void orderSettle(){
        orderInoutService.settle();
    }
}