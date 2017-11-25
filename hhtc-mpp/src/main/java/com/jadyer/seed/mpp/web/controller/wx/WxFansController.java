package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.OperationEnum;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.mpp.web.service.FansService;
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

    //TODO
    String openid = "ojZ6h1dw3Pd6gEosXuZx7A1QeUsY";
    /**
     * TOKGO 查询粉丝信息
     */
    @GetMapping("/get")
    public CommonResult get(HttpSession session){
        //TODO
        //String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(fansService.getByOpenid(openid));
    }

    /*
    * TOKGO用户授权查询 查询该用户是否授权
    * */
    @GetMapping("/infor/accredit/get")
    public CommonResult GetAccredit(HttpSession session){
        //TODO
        //String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(OperationEnum.FANS_INFOR,fansService.GetInforState(2, openid));
    }

    /**
     * TOKGO用户同意授权
     * */
    @PostMapping("/infor/accredit/allow")
    public CommonResult FansAccredit(HttpSession session){
        //TODO
        //String openid = hhtcHelper.getWxOpenidFromSession(session);
        if (fansService.UpdatedataInforSate(0,'1',2,openid)){
            return GetAccredit(session);
        }
        return new CommonResult(OperationEnum.FANS_INFOR,CodeEnum.SYSTEM_ERROR);
    }

    /**
     * TOKGO用户电话绑定
     * */
    @PostMapping("/infor/phoneNOBind")
    public CommonResult BindFansPhoneNO(String phoneNO ,String verifyCod, HttpSession session){
        //TODO
        //String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(OperationEnum.FANS_INFOR, fansService.PhoneNOCheck(phoneNO,verifyCod,openid));
    }

    /**
     * TOKGO 地址绑定
     * */
    @PostMapping("/infor/communityBind")
    public CommonResult BindFansCommunity(long CommunityID ,String houseNumber, HttpSession session){
        //TODO
        //String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(OperationEnum.FANS_INFOR, fansService.CommunityCheck(CommunityID,houseNumber,openid));
    }


    /**
     * TOKGO 车牌号绑定
     */
    @PostMapping("/infor/carNumberBind")
    public CommonResult BindFansCarNumber(String carNumber, String carNumberImg, HttpSession session){
        //TODO
        //String openid = hhtcHelper.getWxOpenidFromSession(session);
        //把所有的小写字母转化为大写字母
        carNumber = carNumber.toUpperCase();
        //判断该车牌是否允许注册
        return new CommonResult(OperationEnum.FANS_INFOR,fansService.CarNumber(openid, carNumber,carNumberImg));
    }


    /**
     * 车位主绑定
     */
    @PostMapping("/infor/carParkBind")
    public CommonResult BindCarPark(String carParkNumber, String carEquityImg,Integer carUsefulEndDate, HttpSession session){
        //TODO
        //String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(OperationEnum.FANS_INFOR,fansService.regCarPark(openid
                , carParkNumber, carEquityImg,carUsefulEndDate));
    }
}