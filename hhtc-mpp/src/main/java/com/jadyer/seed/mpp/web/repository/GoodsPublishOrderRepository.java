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


    List<GoodsPublishOrder> findByCommunityId(long communityId);
//    List<GoodsPublishOrder> findByCommunityIdAndStatusAndPublishTypeAndPublishFromTimeGreaterThanAndPublishEndTimeGreaterThanEqual(long communityId, int status, int publishType, int publishFromTime, int publishEndTime);
//
    List<GoodsPublishOrder> findByOpenid(String openid);

    GoodsPublishOrder findByOrderID(String orderID);

    List<GoodsPublishOrder> findByGoodsIdAndOpenid(long goodsId, String openid);

    @Modifying
    @Transactional(timeout=10)
    @Query("DELETE FROM GoodsPublishOrder WHERE goodsId=?1")
    void deleteByGoodsId(long goodsId);

    @Modifying
    @Transactional(timeout=10)
    @Query("DELETE FROM GoodsPublishOrder WHERE orderID=?1")
    void deleteByOrderID(String orderID);



//    /**
//     * 定时任务：记录过期的发布信息到历史表
//     */
//    List<GoodsPublishOrder> findByPublishFromDatesEndingWith(String publishEndDate);
}