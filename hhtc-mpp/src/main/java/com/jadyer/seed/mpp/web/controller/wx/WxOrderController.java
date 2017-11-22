package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 15:50.
 */
@RestController
@RequestMapping("/wx/order")
public class WxOrderController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private OrderService orderService;

    @GetMapping("/get")
    public CommonResult get(long id){
        return new CommonResult(orderService.get(id));
    }


    /**
     * @param orderStatus 1—支付中，3—支付失败，7—已预约，8—停车中，9—超时中，10—已完成
     */
    @RequestMapping("/list")
    public CommonResult list(String orderStatus, HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(orderService.list(orderStatus, openid));
    }


    /**
     * 转租
     * @param id 订单id
     */
    @RequestMapping("/rent")
    public CommonResult rent(long id, HttpSession session){
        orderService.rent(id, hhtcHelper.getWxOpenidFromSession(session));
        return new CommonResult();
    }
}