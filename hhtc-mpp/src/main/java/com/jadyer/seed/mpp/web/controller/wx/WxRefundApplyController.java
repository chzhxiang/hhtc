package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.service.RefundApplyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("/wx/refund/apply")
public class WxRefundApplyController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private RefundApplyService refundApplyService;

    /**
     * 退款申请
     */
    @PostMapping("/refund")
    public CommonResult refund(HttpServletRequest request){
        refundApplyService.apply(1, new BigDecimal(0), request);
        return new CommonResult();
    }


    /**
     * 提现申请
     */
    @PostMapping("/withdraw")
    public CommonResult withdraw(BigDecimal money, HttpServletRequest request){
        refundApplyService.apply(2, money, request);
        return new CommonResult();
    }
}