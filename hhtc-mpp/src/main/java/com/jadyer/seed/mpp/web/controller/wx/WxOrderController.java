package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.service.OrderInforService;
import com.jadyer.seed.mpp.web.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    private OrderInforService orderInforService;





    /**
     * TOKGO 获取历史数据 每次返回10条数据
     * */
    @GetMapping("/gethistory")
    public CommonResult get(int pageNo, HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(orderInforService.Gethistory(openid,pageNo));
    }

    /**
     * TOKGO 超时补款
     * */
    @PostMapping("/overtime/repayments")
    public CommonResult repayments(String orderid,double fineprice ,HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        orderInforService.OvertimeRepayment(openid,orderid,fineprice);
        return new CommonResult();
    }

    /**
     * TOKGO 超时补款 价格计算
     * */
    @GetMapping("/overtime/calculat")
    public CommonResult CalculateFine(String orderid ,HttpSession session){
        return new CommonResult(orderInforService.CalculateFine(orderid));
    }

    /**
     * TODO 测试接口
     * */
    @RequestMapping("/test")
    public void repayments( HttpSession session){
        String openid ="";
        orderInforService.test(openid);
    }


//    /**
//     * @param orderStatus 1—支付中，3—支付失败，7—已预约，8—停车中，9—超时中，10—已完成
//     */
//    @RequestMapping("/list")
//    public CommonResult list(String orderStatus, HttpSession session){
//        String openid = hhtcHelper.getWxOpenidFromSession(session);
//        return new CommonResult(orderService.list(orderStatus, openid));
//    }

}