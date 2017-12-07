package com.jadyer.seed.mpp.web.controller;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.FansInforAudit;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.model.UserFunds;
import com.jadyer.seed.mpp.web.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Controller
@RequestMapping(value="/fans")
public class FansController{
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private AuditService auditService;
    @Resource
    private UserFundsService userFundsService;

    /**
     * TOKGO 分页查询待审核的车位列表
     * @param pageNo zero-based page index
     */
    @RequestMapping("/task/park/list")
    public String listTaskViaPage(String pageNo, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        request.setAttribute("page", fansService.TaskViaPagelist(userInfo, pageNo,Constants.AUDTI_TEPY_CARPARK));
        return "fans/task.park.list";
    }

    /**
     * TOKGO 分页查询待审核的车主列表
     * @param pageNo zero-based page index
     */
    @RequestMapping("/task/owner/list")
    public String listTaskOwnerViaPage(String pageNo, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        request.setAttribute("page", fansService.TaskViaPagelist(userInfo, pageNo, Constants.AUDTI_TEPY_CARNUMBER));
        return "fans/task.owner.list";
    }
    /**
     * TOKGO  用户资金管理
     * */
    @RequestMapping("/money/manage")
    public String moneymanage(HttpServletRequest request){
        return "fans/money.manage";
    }

    /**
     * TOKGO 分页查询待审核的地址列表
     * @param pageNo zero-based page index
     */
    @RequestMapping("/task/community/list")
    public String listTaskParkViaPage(String pageNo, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        request.setAttribute("page", fansService.TaskViaPagelist(userInfo, pageNo, Constants.AUDTI_TEPY_COMMUNITY));
        return "fans/task.community.list";
    }

    /**
     * TOKGO 审核粉丝地址、车牌和车位
     * @param status   审核状态：1--审核通过，2--审核拒绝
     * @param id     审核信息id号
     * */
    @ResponseBody
    @RequestMapping("/Audit")
    public CommonResult Audit(long id, int status, String auditRemark, HttpSession session){
        String appid = hhtcHelper.getWxAppidFromSession(session);
        //获取操作员的对象
        MppUserInfo userInfo = (MppUserInfo)session.getAttribute(Constants.WEB_SESSION_USER);
        FansInforAudit fansInforAudit = auditService.GetOne(id);
        fansService.Audit(userInfo, fansInforAudit,status, auditRemark, appid);
        return new CommonResult();
    }

    /**
     * TOKGO 分页查询粉丝信息
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

    /**
     * 获取用户资金信息
     * **/
    @GetMapping("/funds")
    public CommonResult Getfunds(String phoneNO, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以查看意见反馈");
        }
        HashMap hashMap = new HashMap();
        MppFansInfor mppFansInfor = fansService.Get(phoneNO);
        if (mppFansInfor ==null )
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "查询无果");
        UserFunds userFunds = userFundsService.get(mppFansInfor.getOpenid());
        if (mppFansInfor ==null )
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "查询无果");
        hashMap.put("phoneNO",mppFansInfor.getPhoneNo());
        hashMap.put("nickname",mppFansInfor.getNickname());
        hashMap.put("moneybase",userFunds.getMoneyBase());
        hashMap.put("moneybalance",userFunds.getMoneyBalance());
        return new CommonResult(hashMap);
    }
}