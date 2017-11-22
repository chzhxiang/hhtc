package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.CommunityInfo;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 11:02.
 */
public interface CommunityRepository extends BaseRepository<CommunityInfo, Long> {
    List<CommunityInfo> findByProvinceName(String provinceName);

    List<CommunityInfo> findByCityName(String cityName);

    List<CommunityInfo> findByUid(long uid);
}