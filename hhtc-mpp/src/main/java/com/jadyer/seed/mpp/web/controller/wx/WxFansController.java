package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.mpp.web.service.FansService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value="/wx/fans")
public class WxFansController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;

    /**
     * 查询粉丝信息
     */
    @GetMapping("/get")
    public CommonResult get(HttpSession session){
        return new CommonResult(fansService.getByOpenid(hhtcHelper.getWxOpenidFromSession(session)));
    }


    /**
     * 添加车牌号
     */
    @PostMapping("/carNumber/add")
    public CommonResult carNumberAdd(String carNumber, HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        carNumber = carNumber.toUpperCase();
        return new CommonResult(fansService.carNumberAdd(openid, carNumber));
    }


    /*
    * 用户授权查询 查询该用户是否授权
    * */
    @PostMapping("/accredit/get")
    public CommonResult IsAccredit(HttpSession session){
        int ResultCode = 0;

        return new CommonResult(ResultCode);
    }

    /**
     * 用户同意授权
     * */
    @PostMapping("/accredit/allow")
    public CommonResult UserAccredit(HttpSession session){
        //TODO 功能实现  现在没有实现
        return new CommonResult(fansService.getByOpenid(hhtcHelper.getWxOpenidFromSession(session)));
    }

    /**
     * 车主注册
     */
    @PostMapping("/reg/carOwner")
    public CommonResult regCarOwner(String phoneNo, String verifyCode, long carOwnerCommunityId, String carNumber, String houseNumber, HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        //把所有的小写字母转化为大写字母
        carNumber = carNumber.toUpperCase();
        //判断该车牌是否允许注册
        return new CommonResult(fansService.regCarOwner(openid, phoneNo, verifyCode, carOwnerCommunityId, carNumber, houseNumber));
    }


    /**
     * 车位主注册
     */
    @PostMapping("/reg/carPark")
    public CommonResult regCarPark(String phoneNo, String verifyCode, long carParkCommunityId,
                                   String carParkNumber, String carEquityImg, HttpSession session,
                                    Integer carUsefulFromDate, Integer carUsefulEndDate){
        String appid = hhtcHelper.getWxAppidFromSession(session);
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(fansService.regCarPark(appid, openid, phoneNo, verifyCode, carParkCommunityId, carParkNumber, carEquityImg, carUsefulFromDate,carUsefulEndDate));
    }
}