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

    //TODO
    String openid = "ojZ6h1QmJysqUUpDb9I9v5seu_Dw";


    /**
     * TOKGO 获取历史数据 每次返回10条数据
     * */
    @GetMapping("/gethistory")
    public CommonResult get(int pageNo, HttpSession session){
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(orderInforService.Gethistory(openid,pageNo));
    }

    /**
     * TOKGO 超时补款
     * */
    @PostMapping("/overtime/repayments")
    public CommonResult repayments(String orderid, HttpSession session){
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        orderInforService.OvertimeRepayment(openid,orderid);
        return new CommonResult();
    }

    /**
     * TODO 测试接口
     * */
    @RequestMapping("/test")
    public void repayments( HttpSession session){
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        orderInforService.test(openid);
    }


//    /**
//     * @param orderStatus 1—支付中，3—支付失败，7—已预约，8—停车中，9—超时中，10—已完成
//     */
//    @RequestMapping("/list")
//    public CommonResult list(String orderStatus, HttpSession session){
//        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
//        return new CommonResult(orderService.list(orderStatus, openid));
//    }

}