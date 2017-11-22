package com.jadyer.seed.mpp.web.controller;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.AdviceService;
import com.jadyer.seed.mpp.web.service.FansService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value="/fans")
public class FansController{
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private AdviceService adviceService;

    /**
     * 分页查询待审核的车主列表
     * @param pageNo zero-based page index
     */
    @RequestMapping("/task/owner/list")
    public String listTaskOwnerViaPage(String pageNo, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        request.setAttribute("page", fansService.listTaskViaPage(userInfo, pageNo, 1));
        return "fans/task.owner.list";
    }


    /**
     * 分页查询待审核的车位主列表
     * @param pageNo zero-based page index
     */
    @RequestMapping("/task/park/list")
    public String listTaskParkViaPage(String pageNo, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        request.setAttribute("page", fansService.listTaskViaPage(userInfo, pageNo, 2));
        return "fans/task.park.list";
    }


    /**
     * 审核车主或车位主
     * @param id     粉丝ID
     * @param status 审核状态：2--审核通过，3--审核拒绝
     * @param type   类型：1--车主，2--车位主
     */
    @ResponseBody
    @RequestMapping("/carAudit")
    public CommonResult carAudit(long id, int status, int type, String auditRemark, HttpSession session){
          String appid = hhtcHelper.getWxAppidFromSession(session);
        MppUserInfo userInfo = (MppUserInfo)session.getAttribute(Constants.WEB_SESSION_USER);
        fansService.carAudit(userInfo, id, status, type, auditRemark, appid);
        return new CommonResult();
    }


    /**
     * 分页查询粉丝信息
     * @param pageNo zero-based page index
     */
    @RequestMapping("/list")
    public String listViaPage(String pageNo, HttpServletRequest request){
        final long uid = ((MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER)).getId();
        request.setAttribute("page", fansService.listViaPage(uid, pageNo));
        return "fans/list";
    }


    @RequestMapping("/advice/list")
    public String listAdviceViaPage(String pageNo, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以查看意见反馈");
        }
        request.setAttribute("page", fansService.listAdviceViaPage(pageNo));
        return "fans/advice.list";
    }
}