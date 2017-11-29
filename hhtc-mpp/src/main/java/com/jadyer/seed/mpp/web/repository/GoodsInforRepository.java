package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.GoodsInfor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 15:01.
 */
public interface GoodsInforRepository extends BaseRepository<GoodsInfor, Long> {
    long countByOpenidAndIsUsedIn(String openid, List<Integer> isUsedList);


    long countByCommunityId(long communityId);

    List<GoodsInfor> findByCarParkNumber(String carParkNumber);

    List<GoodsInfor> findByOpenidAndIsUsed(String openid,int isUsed);

    GoodsInfor findByOpenidAndCarParkNumber(String openid, String carParkNumber);
    GoodsInfor findByOpenidAndId(String openid,long id);
    List<GoodsInfor> findByOpenid(String openid);


    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE GoodsInfor SET isUsed=?2 WHERE id=?1")
    int updateStatus(long id, int isUsed);

    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE GoodsInfor SET isRepetition=?2 WHERE car_park_number=?1")
    int updateStatus(String carParkNumber, int isRepetition);

    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE GoodsInfor SET isUsed=?2 WHERE id=?1 AND isUsed=?3")
    int updateStatus(long id, int isUsed, int preIsUsed);
}