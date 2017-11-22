package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.service.GoodsPublishOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/15 16:29.
 */
@RestController
@RequestMapping("/wx/goods/publish/order")
public class WxGoodsPublishOrderController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private GoodsPublishOrderService goodsPublishOrderService;

    /**
     * 库存数量
     */
    @GetMapping("/inventory")
    public CommonResult inventory(long communityId){
        return new CommonResult(goodsPublishOrderService.inventory(communityId));
    }


    /**
     * 车位发布列表
     * @param page zero-based page index
     */
    @GetMapping("/listByGoodsId")
    public CommonResult listByGoodsId(long goodsId, String pageNo){
        return new CommonResult(goodsPublishOrderService.listByGoodsId(goodsId, pageNo));
    }


    /**
     * 车位主取消发布车位
     */
    @PostMapping("/cancel")
    public CommonResult cancel(long id, HttpSession session){
        goodsPublishOrderService.cancel(hhtcHelper.getWxOpenidFromSession(session), id);
        return new CommonResult();
    }
}