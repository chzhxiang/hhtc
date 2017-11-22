package com.jadyer.seed.mpp.web.controller;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.RefundApplyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/5 17:58.
 */
@Controller
@RequestMapping("/refund/apply")
public class RefundApplyController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private RefundApplyService refundApplyService;

    /**
     * 退款列表
     */
    @RequestMapping("/list")
    public String list(String pageNo, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        request.setAttribute("page", refundApplyService.listViaPage(userInfo, pageNo));
        return "funds/refund.apply.list";
    }


    /**
     * 退款审核列表
     */
    @RequestMapping("/task/list")
    public String taskList(String pageNo, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        request.setAttribute("page", refundApplyService.listTaskViaPage(userInfo, pageNo));
        return "funds/refund.apply.task.list";
    }


    /**
     * 退款或提现审核
     */
    @ResponseBody
    @PostMapping("/audit")
    public CommonResult audit(long id, int auditStatus){
        refundApplyService.audit(id, auditStatus);
        return new CommonResult();
    }
}