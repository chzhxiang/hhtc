package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.service.MarketTransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @描述
 * @创建人 TOKGO
 * @创建时间 2017/11/27
 * @修改人和其它信息
 */
@RestController
@RequestMapping("/wx/market/transaction")
public class WxMarketTransactionController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private MarketTransactionService marketTransactionService;

    /**
     * TOKGO 获取粉丝自己的订单
     * @param type 1---预约订单  2----进行中的订单 3---所有
     */
    @GetMapping("/get/order")
    public CommonResult GetOrder(int type,HttpSession session){
        String appid = hhtcHelper.getWxAppidFromSession(session);

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(marketTransactionService.GetOrder(openid,type));
    }

    /**
     * TOKGO 取消订单
     * @param type "owners"代表是车主取消  其他：车位主取消
     */
    @PostMapping("/cancel")
    public CommonResult CancelOrder(String orderid,String type ,HttpSession session){
        String appid = hhtcHelper.getWxAppidFromSession(session);

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        marketTransactionService.CancelOrder(openid,orderid,type);
        return new CommonResult();
    }

    /**
     * TOKGO 检测时间冲突
     * */
    @GetMapping("/checkOrdertime")
    public CommonResult CheckOrderTime(String FromTime,String EndTime,HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(marketTransactionService.OrderTimeCheck(FromTime,EndTime,openid));
    }

    /**
     * TOKGO 预约下单
     * @param type  1----第一次请求  2----第二次请求
     */
    @PostMapping("/reservation")
    public CommonResult Reservation(String orderid,String CarNuber,int type,HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        marketTransactionService.Reservation(openid,orderid,CarNuber,type);
        return new CommonResult();
    }

    /**
     * TOKGO 订单结算
     */
    @PostMapping("/ordersettlement")
    public CommonResult orderSettlement(String orderid,HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        marketTransactionService.OrderGetMomey(orderid,openid);
        return new CommonResult();
    }

    /**
     * TOKGO 订单结算 价格计算
     */
    @GetMapping("/orderCalculate")
    public CommonResult orderSettlementCalculate(String orderid,HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(marketTransactionService.OrderGetMomeyCalculate(orderid,openid));
    }

}
