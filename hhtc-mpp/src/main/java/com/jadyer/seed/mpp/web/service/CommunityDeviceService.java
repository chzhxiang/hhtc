package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.mpp.web.model.CommunityDevice;
import com.jadyer.seed.mpp.web.repository.CommunityDeviceRepository;
import com.jadyer.seed.mpp.web.service.async.CommunityDeviceCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Comment by 玄玉<http://jadyer.cn/> on 2017/9/2 12:18.
 */
@Service
public class CommunityDeviceService {
    @Resource
    private CommunityService communityService;
    @Resource
    private CommunityDeviceRepository communityDeviceRepository;

    public Page<CommunityDevice> listViaPage(String pageNo, String communityName){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        Condition<CommunityDevice> spec = null;
        if(StringUtils.isNotBlank(communityName)){
            spec = Condition.<CommunityDevice>and().like("name", communityName);
        }
        return communityDeviceRepository.findAll(spec, pageable);
    }


    public CommunityDevice get(long id){
        return communityDeviceRepository.findOne(id);
    }


    @Transactional(rollbackFor=Exception.class)
    public CommunityDevice upsert(CommunityDevice communityDevice) {
        communityDevice.setCommunityName(communityService.get(communityDevice.getCommunityId()).getName());
        return CommunityDeviceCache.put(communityDeviceRepository.saveAndFlush(communityDevice));
    }


    @Transactional(rollbackFor=Exception.class)
    public void del(long id) {
        communityDeviceRepository.delete(id);
    }
}