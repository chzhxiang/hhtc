package com.jadyer.seed.mpp.web.controller;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.model.OrderHistory;
import com.jadyer.seed.mpp.web.model.OrderInfor;
import com.jadyer.seed.mpp.web.repository.OrderHistoryRepository;
import com.jadyer.seed.mpp.web.service.OrderInforService;
import com.jadyer.seed.mpp.web.service.OrderService;
import com.jadyer.seed.mpp.web.service.UserFundsFlowService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;

@Controller
@RequestMapping(value="/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private OrderInforService orderInforService;
    @Resource
    private UserFundsFlowService userFundsFlowService;
    @Resource
    private OrderHistoryRepository orderHistoryRepository;

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

    @RequestMapping("/search/orderid")
    public CommonResult searchorderid(String orderid, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以搜索订单");
        }
        HashMap hashMap = new HashMap();
        OrderInfor orderInfor = orderInforService.GetOrder(orderid);
        OrderHistory orderHistory =orderHistoryRepository.findByOrderId(orderid);
        if (orderInfor==null && orderHistory ==null)
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "没有找到该订单");
        hashMap.put("orderId",orderid);
        if (orderInfor!=null){
            hashMap.put("postPhoneNO",orderInfor.getPostPhoneNO());
            hashMap.put("ownersPhoneNO",orderInfor.getOwnersPhoneNO());
            hashMap.put("orderStatus",orderInfor.getOrderStatus());
            hashMap.put("totalPrice",orderInfor.getTotalPrice());
            hashMap.put("timeStart",orderInfor.getTimeStart());
            hashMap.put("timeEnd",orderInfor.getTimeEnd());
        }else{
            hashMap.put("postPhoneNO",orderHistory.getPostPhoneNO());
            hashMap.put("ownersPhoneNO",orderHistory.getOwnersPhoneNO());
            hashMap.put("orderStatus",3);
            hashMap.put("totalPrice",orderHistory.getTotalprice());
            hashMap.put("timeStart",orderHistory.getFromTime());
            hashMap.put("timeEnd",orderHistory.getEndTime());
        }
        return new CommonResult(hashMap);
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