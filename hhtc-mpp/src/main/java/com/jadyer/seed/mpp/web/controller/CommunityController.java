package com.jadyer.seed.mpp.web.controller;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.mpp.web.model.CommunityInfo;
import com.jadyer.seed.mpp.web.model.FansInforAudit;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.CommunityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 11:16.
 */
@Controller
@RequestMapping("/community")
public class CommunityController {
    @Resource
    private CommunityService communityService;

    @RequestMapping("/list")
    public String list(String pageNo, HttpServletRequest request,String name){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        request.setAttribute("page", communityService.listViaPage(userInfo, pageNo,name));
        request.setAttribute("name",name);
        return "sys/community.list";
    }


    @RequestMapping("/list/mgr")
    public String listMgr(String pageNo, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        Page<MppUserInfo> page = communityService.listMgrViaPage(userInfo, pageNo);
        Map<Long, String> communityNameMap = new HashMap<>();
        for(MppUserInfo obj : page.getContent()){
            StringBuilder sb = new StringBuilder();
            for(CommunityInfo community : communityService.getByUid(obj.getId())){
                sb.append("&").append(community.getName());
            }
            communityNameMap.put(obj.getId(), StringUtils.isNotBlank(sb.toString())?sb.toString().substring(1):"");
        }
        request.setAttribute("communityNameMap", communityNameMap);
        request.setAttribute("page", page);
        return "sys/community.mgr.list";
    }


    @ResponseBody
    @RequestMapping("/listAll")
    public CommonResult listAll(){
        return new CommonResult(communityService.listAll());
    }


    @ResponseBody
    @GetMapping("/get/{id}")
    public CommonResult get(@PathVariable long id){
        return new CommonResult(communityService.get(id));
    }


    @ResponseBody
    @PostMapping("/upsert")
    public CommonResult upsert(CommunityInfo communityInfo, HttpSession session){
        MppUserInfo userInfo = (MppUserInfo)session.getAttribute(Constants.WEB_SESSION_USER);
        return new CommonResult(communityService.upsert(userInfo, communityInfo));
    }


    /**
     * 平台运营重置物管密码
     */
    @ResponseBody
    @PostMapping("/resetPassword")
    public CommonResult resetPassword(long uid, String newPassword, HttpSession session){
        MppUserInfo userInfo = (MppUserInfo)session.getAttribute(Constants.WEB_SESSION_USER);
        return new CommonResult(communityService.resetPassword(userInfo, uid, newPassword));
    }
}