package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.GoodsInfo;
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

    /**
     * 车位列表
     */
    @GetMapping("/listAllByOpenid")
    public CommonResult listAllByOpenid(HttpSession session){
        return new CommonResult(goodsService.listAllByOpenid(hhtcHelper.getWxOpenidFromSession(session)));
    }


    /**
     * 添加车位
     */
    @PostMapping("/add")
    public CommonResult add(GoodsInfo goodsInfo, HttpSession session){
        goodsInfo.setAppid(hhtcHelper.getWxAppidFromSession(session));
        goodsInfo.setOpenid(hhtcHelper.getWxOpenidFromSession(session));
        return new CommonResult(goodsService.add(goodsInfo));
    }


    /**
     * 删除车位（其会将该车位发布信息一并删除）
     */
    @PostMapping("/del")
    public CommonResult del(long id, HttpSession session){
        goodsService.del(null, id);
        return new CommonResult();
    }


    /**
     * 修改车位
     */
    @PostMapping("/update")
    public CommonResult update(long id, long communityId, String carParkNumber, String carEquityImg){
        return new CommonResult(goodsService.update(id, communityId, carParkNumber, carEquityImg));
    }
}