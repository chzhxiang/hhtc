package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.service.FansService;
import com.jadyer.seed.mpp.web.service.UserFundsFlowService;
import com.jadyer.seed.mpp.web.service.UserFundsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/28 17:51.
 */
@RestController
@RequestMapping("/wx/userfunds")
public class WxUserFundsController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private UserFundsFlowService userFundsFlowService;



    /**
     * TOKGO 查询粉丝资金情况
     */
    @GetMapping("/get")
    public CommonResult get(HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(userFundsService.get(openid));
    }

    /***
     * TOKGO 获取余额资金流水
     * */
    @GetMapping("/flow/list/balance")
    public CommonResult listbalance(int pageNo, HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(userFundsFlowService.listViaPageBalace(pageNo, openid));
    }


    /***
     * TOKGO 获取押金资金流水
     * */
    @GetMapping("/flow/list/base")
    public CommonResult listbase(int pageNo, HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(userFundsFlowService.listViaPageBase(pageNo, openid));
    }
    /**
     * TOKGO 提现请求
     *@param  type 4---余额提现 5---押金提现
     * */
    @PostMapping("/withdraw")
    public CommonResult Wthdraw(double amount, int type , HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        userFundsService.WthdrawApplication(openid,amount,type);
        return new CommonResult();
    }

    /**
     * 充值
     * @param type "base" 押金  “balance”余额
     * @param money     实际充值金额，单位：元
     */
    @PostMapping("/recharge")
    public CommonResult recharge(String type,BigDecimal money, HttpServletRequest request){
        return new CommonResult(userFundsService.recharge(type,money, request));
    }
}