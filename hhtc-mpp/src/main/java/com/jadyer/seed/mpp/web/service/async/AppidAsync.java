package com.jadyer.seed.mpp.web.service.async;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.repository.CommunityDeviceRepository;
import com.jadyer.seed.mpp.web.service.MppUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/7 19:46.
 */
@Async
@Component
public class AppidAsync {
    @Resource
    private MppUserService mppUserService;
    @Resource
    private CommunityDeviceRepository communityDeviceRepository;

    public void signup(){
        try {
            List<MppUserInfo> userinfoList = mppUserService.getHasBindStatus();
            if(userinfoList.isEmpty()){
                LogUtil.getLogger().info("未查询到需要登记的appid");
            }
            for(MppUserInfo obj : userinfoList){
                if(1 == obj.getMptype()){
                    WeixinTokenHolder.setWeixinAppidAppsecret(obj.getAppid(), obj.getAppsecret());
                    LogUtil.getLogger().info("登记微信appid=[{}]，appsecret=[{}]完毕", obj.getAppid(), WeixinTokenHolder.getWeixinAppsecret(obj.getAppid()));
                    if(StringUtils.isNotBlank(obj.getMchid())){
                        WeixinTokenHolder.setWeixinAppidMch(obj.getAppid(), obj.getMchid(), obj.getMchkey());
                        LogUtil.getLogger().info("登记微信appid=[{}]，mchid=[{}]完毕", obj.getAppid(), WeixinTokenHolder.getWeixinMchid(obj.getAppid()));
                    }
                }
                //if(2 == obj.getMptype()){
                //    QQTokenHolder.setQQAppidAppsecret(obj.getAppid(), obj.getAppsecret());
                //    LogUtil.getLogger().info("登记QQappid=[{}]，appsecret=[{}]完毕", obj.getAppid(), QQTokenHolder.getQQAppsecret(obj.getAppid()));
                //}
            }
            CommunityDeviceCache.putAll(communityDeviceRepository.findAll());
            LogUtil.getLogger().info("缓存小区设备信息完毕");
        } catch (Exception e) {
            LogUtil.getLogger().error("致命异常：登记appid时发生异常，堆栈轨迹如下", e);
            LogUtil.logToTask().error("致命异常：登记appid时发生异常，堆栈轨迹如下", e);
            System.exit(1);
        }
    }
}