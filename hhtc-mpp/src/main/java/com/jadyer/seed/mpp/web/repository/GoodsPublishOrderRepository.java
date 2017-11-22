package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.GoodsPublishOrder;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 10:42.
 */
public interface GoodsPublishOrderRepository extends BaseRepository<GoodsPublishOrder, Long> {
    long countByStatus(int status);
    long countByGoodsIdAndStatusIn(long goodsId, List<Integer> statusList);
    long countByCommunityIdAndStatus(long communityId, int status);

    List<GoodsPublishOrder> findByCommunityIdAndStatusAndPublishFromTimeLessThanEqual(long communityId, int status, int publishFromTime);
    List<GoodsPublishOrder> findByCommunityIdAndStatusAndPublishTypeAndPublishFromTimeGreaterThanAndPublishEndTimeGreaterThanEqual(long communityId, int status, int publishType, int publishFromTime, int publishEndTime);

    GoodsPublishOrder findByGoodsPublishIdsLike(String GoodsPublishIds);

    List<GoodsPublishOrder> findByGoodsIdAndOpenid(long goodsId, String openid);

    @Modifying
    @Transactional(timeout=10)
    @Query("DELETE FROM GoodsPublishOrder WHERE goodsId=?1")
    void deleteByGoodsId(long goodsId);

    @Modifying
    @Transactional(timeout=10)
    @Query("DELETE FROM GoodsPublishOrder WHERE goodsPublishIds=?1")
    void deleteByGoodsPublishId(long goodPublishId);


    /**
     * 更新发布的状态
     */
    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE GoodsPublishOrder SET status=?2 WHERE id=?1")
    int updateStatus(long id, int status);


    /**
     * 定时任务：记录过期的发布信息到历史表
     */
    List<GoodsPublishOrder> findByPublishFromDatesEndingWith(String publishEndDate);
}