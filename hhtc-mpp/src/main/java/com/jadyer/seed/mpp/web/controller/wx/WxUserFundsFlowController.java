package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.service.UserFundsFlowService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/27 12:01.
 */
@RestController
@RequestMapping("/wx/userfunds/flow")
public class WxUserFundsFlowController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private UserFundsFlowService userFundsFlowService;
    //TODO
    String openid = "ojZ6h1U3w-d-ueEdPv-UfttvdBcU";
    @RequestMapping("/list")
    public CommonResult list(String pageNo, HttpSession session){
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(userFundsFlowService.listViaPage(pageNo, openid));
    }
}