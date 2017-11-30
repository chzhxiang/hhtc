package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.service.AdviceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/25 15:35.
 */
@RestController
@RequestMapping("/wx/advice")
public class WxAdviceController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private AdviceService adviceService;

    //TODO
    String openid = "ojZ6h1f1NBoUBWuSf3bTDna5xNVc";

    /**
     * TOKGO 新增意见反馈
     */
    @PostMapping("/add")
    public CommonResult add(String content, String img,HttpSession session){
        //TODO
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        adviceService.add(openid, content,img);
        return new CommonResult();
    }

    /**
     * TOKGO 查询历史投诉记录
     * */

    @GetMapping("/get")
    public CommonResult get(HttpSession session){
        //TODO
        if(Constants.ISWEIXIN)
            openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(adviceService.Get(openid));
    }


}