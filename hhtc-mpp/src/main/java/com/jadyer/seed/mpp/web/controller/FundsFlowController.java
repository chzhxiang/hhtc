package com.jadyer.seed.mpp.web.controller;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.model.UserFunds;
import com.jadyer.seed.mpp.web.service.FansService;
import com.jadyer.seed.mpp.web.service.UserFundsFlowService;
import com.jadyer.seed.mpp.web.service.UserFundsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Comment by 玄玉<http://jadyer.cn/> on 2017/8/31 15:06.
 */
@Controller
@RequestMapping("/funds/flow")
public class FundsFlowController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private UserFundsFlowService userFundsFlowService;

    @RequestMapping("/listByPlatform")
    public String listByPlatform(String pageNo, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以查看平台收益");
        }
        request.setAttribute("page", userFundsFlowService.listByPlatformViaPage(pageNo, userInfo.getId()));
        return "funds/flow.list";
    }

    @RequestMapping("/change")
    public CommonResult change(String phoneNO, double moneybase, double moneybalance, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以操作用户押金");
        }
        MppFansInfor mppFansInfor = fansService.Get(phoneNO);
        if (mppFansInfor ==null)
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "没有找到该用户");
        UserFunds userFunds = userFundsService.get(mppFansInfor.getOpenid());
        if (mppFansInfor ==null )
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "查询无果");
       if (userFunds.getMoneyBase().doubleValue()+moneybase<0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "押金操作错误");
        }
        if (moneybalance<0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "不能扣除用户余额");
        }
        if (moneybalance == 0 && moneybase == 0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无操作");
        }
        userFundsService.AdminChange(moneybase,moneybalance,userFunds);
        return new CommonResult();
    }


}