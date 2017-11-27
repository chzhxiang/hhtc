package com.jadyer.seed.mpp.web.controller;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.mpp.web.model.GoodsInfo;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 18:27.
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private GoodsService goodsService;

    @RequestMapping("/list")
    public String listViaPage(String pageNo, HttpServletRequest request){
        MppUserInfo userInfo = (MppUserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
//        request.setAttribute("regcount", goodsService.count(userInfo));
        request.setAttribute("page", goodsService.listViaPage(userInfo, pageNo));
        return "sys/goods.list";
    }


    @ResponseBody
    @GetMapping("/get/{id}")
    public CommonResult get(@PathVariable long id){
        return new CommonResult(goodsService.get(id));
    }


    /**
     * 审核通过或拒绝车位
     */
    @ResponseBody
    @PostMapping("/audit")
    public CommonResult audit(GoodsInfo goodsInfo, HttpSession session){
        MppUserInfo userInfo = (MppUserInfo)session.getAttribute(Constants.WEB_SESSION_USER);
        return new CommonResult(goodsService.audit(userInfo, goodsInfo));
    }
    @ResponseBody
    @PostMapping("/delete")
    public CommonResult delete(long id, HttpSession session){
        MppUserInfo userInfo = (MppUserInfo)session.getAttribute(Constants.WEB_SESSION_USER);
        //TODO
//        goodsService.del(userInfo, id);
        return new CommonResult();
    }
}