package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.mpp.web.model.CommunityDeviceFlow;
import com.jadyer.seed.mpp.web.repository.CommunityDeviceFlowRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 小区设备扫描流水
 * Generated from seed-simcoder by 玄玉<http://jadyer.cn/> on 2017/09/24 10:47.
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class CommunityDeviceFlowService {
    @Resource
    private CommunityDeviceFlowRepository communityDeviceFlowRepository;

    @Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
    public List<CommunityDeviceFlow> listSearch(long communityId, String _fromDate, String _endDate, String carNumber){
        Date fromDate = null;
        Date endDate = null;
        try {
            fromDate = DateUtils.parseDate(_fromDate + "000000", "yyyyMMddHHmmss");
            endDate = DateUtils.parseDate(_endDate + "235959", "yyyyMMddHHmmss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(StringUtils.isBlank(carNumber)){
            return communityDeviceFlowRepository.findByCommunityIdAndUpdateTimeGreaterThanEqualAndUpdateTimeLessThanEqual(communityId, fromDate, endDate);
        }
        return communityDeviceFlowRepository.findByCommunityIdAndScanCarNumberAndUpdateTimeGreaterThanEqualAndUpdateTimeLessThanEqual(communityId, carNumber, fromDate, endDate);
    }


    /**
     * 分页查询
     * @param pageNo 页码，起始值为0，未传此值则默认取0
     */
    @Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
    public Page<CommunityDeviceFlow> list(String pageNo){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        //Condition<CommunityDeviceFlow> spec = Condition.and();
        //spec.eq("id", 2);
        //return communityDeviceFlowRepository.findAll(spec, pageable);
        return communityDeviceFlowRepository.findAll(pageable);
    }


    @Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
    public CommunityDeviceFlow get(long id){
        return communityDeviceFlowRepository.findOne(id);
    }


    public void delete(long id){
        communityDeviceFlowRepository.delete(id);
    }


    public CommunityDeviceFlow upsert(CommunityDeviceFlow communityDeviceFlow){
        return communityDeviceFlowRepository.saveAndFlush(communityDeviceFlow);
    }
}