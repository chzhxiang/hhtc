package com.jadyer.seed.mpp.web.controller;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.mpp.web.model.CommunityDeviceFlow;
import com.jadyer.seed.mpp.web.service.CommunityDeviceFlowService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 小区设备扫描流水
 * Generated from seed-simcoder by 玄玉<http://jadyer.cn/> on 2017/09/24 10:47.
 */
@Controller
@RequestMapping("/communitydeviceflow")
public class CommunityDeviceFlowController {
    @Resource
    private CommunityDeviceFlowService communityDeviceFlowService;

    @RequestMapping("/list/search")
    public String listSearch(long communityId, String fromDate, String endDate, String carNumber, HttpServletRequest request){
        request.setAttribute("communityId", communityId);
        request.setAttribute("fromDate", fromDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("carNumber", carNumber);
        request.setAttribute("flowList", communityDeviceFlowService.listSearch(communityId, fromDate, endDate, carNumber));
        return "sys/community.device.flow.list.search";
    }


    /**
     * 分页查询
     * @param pageNo 页码，起始值为0，未传此值则默认取0
     */
    @RequestMapping("/list")
    public String list(String pageNo, HttpServletRequest request){
        request.setAttribute("page", communityDeviceFlowService.list(pageNo));
        return "sys/community.device.flow.list";
    }


    @ResponseBody
    @GetMapping("/get/{id}")
    public CommonResult get(@PathVariable long id){
        return new CommonResult(communityDeviceFlowService.get(id));
    }


    @ResponseBody
    @PostMapping("delete/{id}")
    public CommonResult delete(@PathVariable long id){
        communityDeviceFlowService.delete(id);
        return new CommonResult();
    }


    @ResponseBody
    @PostMapping("/upsert")
    public CommonResult upsert(CommunityDeviceFlow communityDeviceFlow){
        return new CommonResult(communityDeviceFlowService.upsert(communityDeviceFlow));
    }
}