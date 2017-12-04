package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.service.GoodsPublishOrderService;
import com.jadyer.seed.mpp.web.service.GoodsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

/**
 * @描述
 * @创建人 TOKGO
 * @创建时间 2017/11/27
 * @修改人和其它信息
 */

@RestController
@RequestMapping("/wx/market/post")
public class WxMarketPostController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private GoodsService goodsService;
    @Resource
    private GoodsPublishOrderService goodsPublishOrderService;





    /**
     * TOKGO 库存数量 获取市场的车位
     * */
    @GetMapping("/inventory")
    public CommonResult inventory(long communityId,String starttime){
        return new CommonResult(goodsPublishOrderService.inventory(communityId,starttime));

    }

    /**
     * TOKGO 获取粉丝的车位信息
     * */
    @GetMapping("/getcarparks")
    public CommonResult cancel(HttpSession session) {

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(goodsService.GetPublishCarpark(openid));
    }


    /**
     *  TOKGO 车位发布
     * @param goodsId zero-based page index
     */
    @PostMapping("/postCarpark")
    public CommonResult post(long goodsId, BigDecimal price,String starttime,String endtime, HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        goodsPublishOrderService.postOrder(openid,goodsId,price,starttime,endtime);
        return new CommonResult();
    }


    /**
     *  TOKGO 检测用户订单时间冲突
     * @param goodsId zero-based page index
     */
    @GetMapping("/check/ordertime")
    public CommonResult CheckOrderTime(String starttime,String endtime,long goodsId,HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(goodsPublishOrderService.OrderTimeCheck(starttime,endtime,openid,goodsId));
    }


    /***
     * TOKGO 车位主获取发布的订单
     * */
    @GetMapping("/getorder")
    public CommonResult getorder(HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(goodsPublishOrderService.Getfansorder(openid));
    }


    /**
     * TOKGO 车位主取消发布车位
     */
    @PostMapping("/cancel")
    public CommonResult cancel(String orderid, HttpSession session){

        String openid = hhtcHelper.getWxOpenidFromSession(session);
        goodsPublishOrderService.cancel(openid, orderid);
        return new CommonResult();
    }


    /***
     * TOKGO 获得订单价格
     * */
    @GetMapping("/getorderprice")
    public CommonResult GetOrderPrice(long communityId, String starttime,String endtime){
        return new CommonResult(goodsPublishOrderService.GetPrice(communityId,starttime,endtime));
    }


}
