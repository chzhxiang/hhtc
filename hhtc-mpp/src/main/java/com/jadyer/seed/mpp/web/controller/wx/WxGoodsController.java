package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.service.GoodsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/18 16:35.
 */
@RestController
@RequestMapping("/wx/goods")
public class WxGoodsController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private GoodsService goodsService;

    //TODO
    String openid = "ojZ6h1U3w-d-ueEdPv-UfttvdBcU";

    /**
     * TOKGO 查询粉丝车位信息信息
     */
    @GetMapping("/get/CarPark")
    public CommonResult getCarPark(HttpSession session){
        //TODO
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(goodsService.getFansCarParkInfor(openid));
    }


    /**
     * 车位绑定
     */
    @PostMapping("/infor/carParkBind")
    public CommonResult BindCarPark(String carParkNumber, String carEquityImg,String carUsefulEndDate, HttpSession session){
        //TODO
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(goodsService.regCarPark(openid
                , carParkNumber, carEquityImg,carUsefulEndDate));
    }


    /**
     * TOKGO 车位注销
     */
    @PostMapping("/infor/carParkLogout")
    public CommonResult LogoutFanscarPark(long id,String state , HttpSession session){
        //TODO
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(goodsService.carParkLogout(openid, id,state));
    }

}