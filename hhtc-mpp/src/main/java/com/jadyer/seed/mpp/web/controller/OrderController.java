package com.jadyer.seed.mpp.web.controller;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.OrderService;
import com.jadyer.seed.mpp.web.service.UserFundsFlowService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping(value="/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private UserFundsFlowService userFundsFlowService;

    @RequestMapping("/search")
    public String search(String phoneNo, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以搜索订单");
        }
        request.setAttribute("phoneNo", phoneNo);
        request.setAttribute("list", orderService.search(phoneNo));
        return "funds/order.allocate.list";
    }


//    @RequestMapping("/matchNewGoods")
//    public String matchNewGoods(String phoneNo, long orderId, HttpServletRequest request){
//        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
//        if(userInfo.getType() != 1){
//            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以匹配订单");
//        }
//        request.setAttribute("phoneNo", phoneNo);
//        request.setAttribute("orderId", orderId);
//        request.setAttribute("list", orderService.search(phoneNo));
//        request.setAttribute("matchList", orderService.matchNewGoods(orderId));
//        return "funds/order.allocate.match.list";
//    }


    @ResponseBody
    @PostMapping("/allocate")
    public CommonResult allocate(long orderId, String phoneNo, String ids, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以調配訂單");
        }
//        orderService.allocate(orderId, phoneNo, ids);
        return new CommonResult();
    }


    @ResponseBody
    @PostMapping("/chengfaCarparker")
    public CommonResult chengfaCarparker(long orderId, BigDecimal money, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以懲罰車位主");
        }
        userFundsFlowService.chengfaCarparker(orderId, money);
        return new CommonResult();
    }
}