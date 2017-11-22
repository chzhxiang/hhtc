package com.jadyer.seed.mpp.web.controller;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.CommunityDevice;
import com.jadyer.seed.mpp.web.model.CommunityDeviceFlow;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.CommunityDeviceService;
import com.jadyer.seed.mpp.web.service.async.CommunityDeviceCache;
import com.jadyer.seed.mpp.web.service.async.CommunityDeviceFlowAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * Comment by 玄玉<http://jadyer.cn/> on 2017/9/2 12:17.
 */
@Controller
@RequestMapping("/community/device")
public class CommunityDeviceController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private CommunityDeviceService communityDeviceService;
    @Resource
    private CommunityDeviceFlowAsync communityDeviceFlowAsync;

    @RequestMapping("/list")
    public String list(String pageNo, String communityName, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以查看小区设备");
        }
        request.setAttribute("page", communityDeviceService.listViaPage(pageNo, communityName));
        return "sys/community.device.list";
    }


    @ResponseBody
    @GetMapping("/get/{id}")
    public CommonResult get(@PathVariable long id, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以查看小区设备");
        }
        return new CommonResult(communityDeviceService.get(id));
    }


    @ResponseBody
    @PostMapping("/upsert")
    public CommonResult upsert(CommunityDevice communityDevice, HttpSession session){
        MppUserInfo userInfo = (MppUserInfo)session.getAttribute(Constants.WEB_SESSION_USER);
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以编辑小区设备");
        }
        return new CommonResult(communityDeviceService.upsert(communityDevice));
    }


    @ResponseBody
    @PostMapping("/delete")
    public CommonResult delete(long id, HttpSession session){
        MppUserInfo userInfo = (MppUserInfo)session.getAttribute(Constants.WEB_SESSION_USER);
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以编辑小区设备");
        }
        communityDeviceService.del(id);
        return new CommonResult();
    }


    /**
     * @param serialno 继电器序列号
     */
    @ResponseBody
    @PostMapping("/openDoor")
    public CommonResult openDoor(long deviceId, String serialno, String doorid, HttpSession session){
        MppUserInfo userInfo = (MppUserInfo)session.getAttribute(Constants.WEB_SESSION_USER);
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以手动开闸");
        }
        boolean openResult = hhtcHelper.openDoor(serialno, doorid);
        CommunityDevice device = CommunityDeviceCache.get(deviceId);
        CommunityDeviceFlow communityDeviceFlow = new CommunityDeviceFlow();
        communityDeviceFlow.setDeviceId(device.getId());
        communityDeviceFlow.setCommunityId(device.getCommunityId());
        communityDeviceFlow.setCommunityName(device.getCommunityName());
        communityDeviceFlow.setScanAllowOpen(1);
        communityDeviceFlow.setOpenResult(openResult ? 1 : 0);
        communityDeviceFlow.setOpenTime(new Date());
        communityDeviceFlow.setOpenRemark("平台运营手动开闸");
        communityDeviceFlowAsync.add(communityDeviceFlow);
        return new CommonResult(openResult);
    }
}