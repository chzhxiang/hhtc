package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.service.AdviceService;
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

    /**
     * 新增意见反馈
     */
    @PostMapping("/add")
    public CommonResult add(String content, HttpSession session){
        adviceService.add(hhtcHelper.getWxOpenidFromSession(session), content);
        return new CommonResult();
    }
}