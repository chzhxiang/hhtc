package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.CommunityDeviceFlow;

import java.util.Date;
import java.util.List;

/**
 * 小区设备扫描流水
 * Generated from seed-simcoder by 玄玉<http://jadyer.cn/> on 2017/09/24 10:47.
 */
public interface CommunityDeviceFlowRepository extends BaseRepository<CommunityDeviceFlow, Long> {
    List<CommunityDeviceFlow> findByCommunityIdAndUpdateTimeGreaterThanEqualAndUpdateTimeLessThanEqual(long communityId, Date fromDate, Date endDate);
    List<CommunityDeviceFlow> findByCommunityIdAndScanCarNumberAndUpdateTimeGreaterThanEqualAndUpdateTimeLessThanEqual(long communityId, String scanCarNumber, Date fromDate, Date endDate);
}