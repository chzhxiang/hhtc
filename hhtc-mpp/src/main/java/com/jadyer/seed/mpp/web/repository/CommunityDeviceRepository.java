package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.CommunityDevice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Comment by 玄玉<http://jadyer.cn/> on 2017/9/2 11:43.
 */
public interface CommunityDeviceRepository extends BaseRepository<CommunityDevice, Long> {
    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE CommunityDevice Set communityName=?2 WHERE communityId=?1")
    void updateCommunityName(long communityId, String communityName);
}