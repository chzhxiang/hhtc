package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.mpp.web.service.CommunityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 13:09.
 */
@RestController
@RequestMapping("/wx/community")
public class WxCommunityController {
    @Resource
    private CommunityService communityService;

    /**
     * 获取当前支持的（即业务已开展到的）省份列表
     */
    @GetMapping("/province/list")
    public CommonResult getProvinceList(){
        return new CommonResult(new HashMap<String, Set<String>>(){
            private static final long serialVersionUID = -3620272837726265621L;
            {
                put("provinceNameList", communityService.getProvinceSet());
            }
        });
    }


    /**
     * 根据省份名称获取城市列表
     */
    @GetMapping("/city/list")
    public CommonResult getCityList(String provinceName){
        return new CommonResult(new HashMap<String, Set<String>>(){
            private static final long serialVersionUID = -3620272837726265621L;
            {
                put("cityNameList", communityService.getCitySet(provinceName));
            }
        });
    }


    /**
     * TOKGO 根据城市名称获取小区名称列表
     */
    @GetMapping("/listByCityName")
    public CommonResult listByCityName(String cityName){
        return new CommonResult(communityService.listByCityName(cityName));
    }


    @GetMapping("/get")
    public CommonResult get(long id){
        return new CommonResult(communityService.get(id));
    }
}