package com.jadyer.seed.mpp.web.service.async;

import com.jadyer.seed.mpp.web.model.CommunityDevice;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/2 11:44.
 */
public class CommunityDeviceCache {
    private static ConcurrentHashMap<Long, CommunityDevice> cacheMap = new ConcurrentHashMap<>();
    private CommunityDeviceCache(){}

    static void putAll(List<CommunityDevice> deviceList){
        for (CommunityDevice communityDevice : deviceList) {
            put(communityDevice);
        }
    }

    /**
     * @return 返回本次增加的对象
     */
    public static CommunityDevice put(CommunityDevice communityDevice){
        cacheMap.put(communityDevice.getId(), communityDevice);
        return cacheMap.get(communityDevice.getId());
    }

    public static CommunityDevice get(long deviceId){
        CommunityDevice device = cacheMap.get(deviceId);
        return null==device ? new CommunityDevice() : device;
    }
}