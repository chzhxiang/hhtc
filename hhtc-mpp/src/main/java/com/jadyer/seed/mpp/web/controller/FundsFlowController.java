package com.jadyer.seed.mpp.web.controller;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.UserFundsFlowService;
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
}