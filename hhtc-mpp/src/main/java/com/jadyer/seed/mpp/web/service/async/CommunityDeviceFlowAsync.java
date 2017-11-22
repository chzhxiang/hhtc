package com.jadyer.seed.mpp.web.service.async;

import com.jadyer.seed.mpp.web.model.CommunityDeviceFlow;
import com.jadyer.seed.mpp.web.service.CommunityDeviceFlowService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Async
@Component
public class CommunityDeviceFlowAsync {
    @Resource
    private CommunityDeviceFlowService communityDeviceFlowService;

    public void add(CommunityDeviceFlow communityDeviceFlow){
        communityDeviceFlowService.upsert(communityDeviceFlow);
    }
}