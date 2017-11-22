package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.GoodsInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 15:01.
 */
public interface GoodsRepository extends BaseRepository<GoodsInfo, Long> {
    long countByOpenidAndIsUsedIn(String openid, List<Integer> isUsedList);

    long countByCarAuditStatus(int carAuditStatus);

    long countByCommunityId(long communityId);

    List<GoodsInfo> findByCarParkNumber(String carParkNumber);

    long countByCommunityIdAndCarAuditStatus(long communityId, int carAuditStatus);

    GoodsInfo findByOpenidAndCarParkNumber(String openid, String carParkNumber);

    List<GoodsInfo> findByOpenid(String openid);

    List<GoodsInfo> findByCommunityIdAndCarAuditStatus(long communityId, int carAuditStatus);

    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE GoodsInfo SET isUsed=?2 WHERE id=?1")
    int updateStatus(long id, int isUsed);

    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE GoodsInfo SET isRepetition=?2 WHERE car_park_number=?1")
    int updateStatus(String carParkNumber, int isRepetition);

    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE GoodsInfo SET isUsed=?2 WHERE id=?1 AND isUsed=?3")
    int updateStatus(long id, int isUsed, int preIsUsed);
}