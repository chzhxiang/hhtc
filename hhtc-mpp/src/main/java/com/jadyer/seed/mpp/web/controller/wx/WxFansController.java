package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.mpp.web.service.FansService;
import com.jadyer.seed.mpp.web.service.OwnersInforServic;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
/**
 * 微信中 个人信息的相关操作
 *
 * */
@RestController
@RequestMapping(value="/wx/fans")
public class WxFansController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private OwnersInforServic ownersInforServic;



    /**
     * TOKGO 查询粉丝信息
     */
    @GetMapping("/get")
    public CommonResult get(HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(fansService.getByOpenid(openid));
    }

    /**
     * TOKGO 查询粉丝车牌信息信息
     */
    @GetMapping("/get/CarNuber")
    public CommonResult getCarNumber(HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(fansService.getFansCarNumberInfor(openid));
    }

    /***
     * TOKGO 查询粉丝当前地址id
     * */
    @GetMapping("/get/communityid")
    public CommonResult getCommunityid(HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(fansService.GetCommunityidNow(openid));
    }

    /*
    * TOKGO 用户授权查询 查询该用户是否授权
    * */
    @GetMapping("/infor/accredit/get")
    public CommonResult GetAccredit(HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(fansService.GetInforState(2, openid));
    }

    /**
     * TOKGO用户同意授权
     * */
    @PostMapping("/infor/accredit/allow")
    public CommonResult FansAccredit(HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        if (fansService.UpdatedataInforSate(0,'1',2,openid)){
            return GetAccredit(session);
        }
        return new CommonResult(CodeEnum.HHTC_INFOR_ACCREDIT_ERROR);
    }


    /**
     * TOKGO用户电话绑定
     * */
    @PostMapping("/infor/phoneNOBind")
    public CommonResult BindFansPhoneNO(String phoneNO ,String verifyCod, HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult( fansService.PhoneNOCheck(phoneNO,verifyCod,openid));
    }

    /**
     * TOKGO用户电话注销
     * */
    @PostMapping("/infor/phoneNOLogout")
    public CommonResult LogoutFansPhoneNO(String phoneNO ,String verifyCod, HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult( fansService.PhoneNODelete(phoneNO,verifyCod,openid));
    }

    /**
     * TOKGO 地址绑定
     * */
    @PostMapping("/infor/communityBind")
    public CommonResult BindFansCommunity(long communityID ,String houseNumber, HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult( fansService.CommunityCheck(communityID,houseNumber,openid));
    }


    /**
     * TOKGO 查询车牌是否存在
     * */
    @GetMapping("/infor/carNumbercheck")
    public CommonResult CheckCarNumber(String carNumber,HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(ownersInforServic.IsExist(carNumber));
    }


    /**
     * TOKGO 车牌号绑定
     */
    @PostMapping("/infor/carNumberBind")
    public CommonResult BindFansCarNumber(String carNumber, String carNumberImg, HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        //把所有的小写字母转化为大写字母
        carNumber = carNumber.toUpperCase();
        //判断该车牌是否允许注册
        return new CommonResult(fansService.CarNumber(openid, carNumber,carNumberImg));
    }


    /**
     * TOKGO 车牌号注销
     */
    @PostMapping("/infor/carNumberLogout")
    public CommonResult LogoutFansCarNumber(long id,String state , HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(fansService.CarNumberLogout(openid, id,state));
    }



}