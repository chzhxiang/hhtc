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


    //TODO
    String openid = "ojZ6h1U3w-d-ueEdPv-UfttvdBcU";

    /**
     * TOKGO 获取粉丝自己的订单
     * @param type 1---预约订单  2----进行中的订单 3---所有
     */
    @GetMapping("/get/order")
    public CommonResult GetOrder(int type,HttpSession session){
        String appid = hhtcHelper.getWxAppidFromSession(session);
        //TODO
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(marketTransactionService.GetOrder(openid,type));
    }

    /**
     * TOKGO 取消订单
     */
    @PostMapping("/cancel")
    public CommonResult CancelOrder(String orderid,String type ,HttpSession session){
        String appid = hhtcHelper.getWxAppidFromSession(session);
        //TODO
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        marketTransactionService.CancelOrder(openid,orderid,type);
        return new CommonResult();
    }

    /**
     * TOKGO 预约下单
     */
    @PostMapping("/reservation")
    public CommonResult Reservation(String orderid,String CarNuber ,HttpSession session){
        String appid = hhtcHelper.getWxAppidFromSession(session);
        //TODO
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(marketTransactionService.Reservation(openid,orderid,CarNuber));
    }

    /**
     * TOKGO 取消预约
     */
    @PostMapping("/reservationLogout")
    public CommonResult ReservationLogout(String orderid,HttpSession session){
        String appid = hhtcHelper.getWxAppidFromSession(session);
        //TODO
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(marketTransactionService.ReservationLogout(openid,orderid));
    }

    /**
     * TOKGO 开始订单
     */
    @PostMapping("/startorder")
    public CommonResult StartOrder(String orderid,HttpSession session){
        String appid = hhtcHelper.getWxAppidFromSession(session);
        //TODO
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(marketTransactionService.StartOrder(openid,orderid));
    }


}
