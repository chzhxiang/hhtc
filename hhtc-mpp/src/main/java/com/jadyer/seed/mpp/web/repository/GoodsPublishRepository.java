package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.GoodsPublishInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 10:42.
 */
public interface GoodsPublishRepository extends BaseRepository<GoodsPublishInfo, Long> {
    int countByStatus(int status);
    int countByGoodsId(long goodsId);
    List<GoodsPublishInfo> findByGoodsPublishOrderId(long goodsPublishOrderId);
    List<GoodsPublishInfo> findByGoodsIdAndPublishFromDateIn(long goodsId, List<Integer> publishFromDateList);
    List<GoodsPublishInfo> findByGoodsIdAndStatusIn(long goodsId, List<Integer> status);
    List<GoodsPublishInfo> findByCommunityIdAndStatus(long communityId, int status);

    /**
     * 匹配列表（单次停车）
     * goodsId=? AND status=? AND PublishFromDate>=待停车的起始日期 ORDER BY id ASC
     */
    List<GoodsPublishInfo> findByGoodsIdAndStatusAndPublishFromDateGreaterThanEqualOrderByIdAsc(long goodsId, int status, int publishFromDate);

    /**
     * 车主主动发布的需求的包裹匹配
     * communityId=? AND status=? AND price>=? AND publishFromDate in (?) ORDER BY price asc
     */
    List<GoodsPublishInfo> findByCommunityIdAndStatusAndPriceGreaterThanEqualAndPublishFromDateInOrderByPriceAsc(long communityId, int status, BigDecimal price, List<Integer> publishFromDateList);

    /**
     * 查询指定车位发布id的转租列表
     */
    List<GoodsPublishInfo> findByFromTypeAndFromIdsIn(int fromType, List<String> fromIds);

    ///**
    // * goodsId=? AND publishFromDate=? AND status=?
    // */
    //List<GoodsPublishInfo> getByGoodsIdAndPublishFromDateAndStatus(long goodsId, int publishFromDate, int status);

    ///**
    // * goodsId=? AND status=? AND publishFromDate<=? AND publishEndDate>=?
    // */
    //List<GoodsPublishInfo> getByGoodsIdAndStatusAndPublishFromDateLessThanEqualAndPublishEndDateGreaterThanEqual(long goodsId, int status, int publishFromDate, int publishEndDate);

    ///**
    // * communityId=? AND status=? AND publishFromDate<=? AND publishEndDate>=?
    // */
    //List<GoodsPublishInfo> getByCommunityIdAndStatusAndPublishFromDateLessThanEqualAndPublishEndDateGreaterThanEqual(long communityId, int status, int publishFromDate, int publishEndDate);

    ///**
    // * 精确匹配
    // * communityId=? AND status=? AND publishFromDate=? AND publishFromTime=? AND publishEndTime=?
    // */
    //List<GoodsPublishInfo> findByCommunityIdAndStatusAndPublishFromDateAndPublishFromTimeAndPublishEndTime(long communityId, int status, int publishFromDate, int publishFromTime, int publishEndTime);

    /**
     * 精确匹配 + 包裹匹配
     * communityId=? AND status=? AND publishFromDate=? AND publishFromTime<=? AND publishEndTime>=?
     */
    List<GoodsPublishInfo> findByCommunityIdAndStatusAndPublishFromDateAndPublishFromTimeLessThanEqualAndPublishEndTimeGreaterThanEqual(long communityId, int status, int publishFromDate, int publishFromTime, int publishEndTime);

    /**
     * 横跨匹配：同时间段类型（都是日间或夜间）：向后
     */
    @Query("FROM GoodsPublishInfo WHERE status=0 AND publishFromDate=?1 AND publishFromTime<=?2 and publishEndTime<=?3 AND publishEndTime>?2")
    List<GoodsPublishInfo> getCrossBackList(int publishFromDate, int publishFromTime, int publishEndTime);

    /**
     * 横跨匹配：同时间段类型（都是日间或夜间）：向前
     */
    @Query("FROM GoodsPublishInfo WHERE status=0 AND publishFromDate=?1 AND publishFromTime>=?2 and publishEndTime>=?3 AND publishFromTime<?3")
    List<GoodsPublishInfo> getCrossFrontList(int publishFromDate, int publishFromTime, int publishEndTime);

    ///**
    // * 库存列表
    // */
    //@Query(value="SELECT goods_id as goodsId, community_name as communityName, car_park_number as carParkNumber, car_park_img as carParkImg, price as price, publish_end_date as publishEndDate, publish_end_time as publishEndTime FROM t_goods_publish_info WHERE id in (SELECT max(id) FROM t_goods_publish_info WHERE publish_from_date=?1 AND publish_from_time<=?2 AND status=0 GROUP BY community_id)", nativeQuery=true)
    //List<GoodsPublishInfoSummary> listAll(int publishFromDate, int publishFromTime, int publishEndTime);

    @Modifying
    @Transactional(timeout=10)
    @Query("DELETE FROM GoodsPublishInfo WHERE goodsId=?1")
    void deleteByGoodsId(long goodsId);

    @Modifying
    @Transactional(timeout=10)
    @Query("DELETE FROM GoodsPublishInfo WHERE id IN (?1)")
    void deleteByIds(List<Long> idList);

    @Modifying
    @Transactional(timeout=10)
    @Query("DELETE FROM GoodsPublishInfo WHERE goodsPublishOrderId=?1")
    void deleteByGoodsPublishOrderId(long goodsPublishOrderId);

    /**
     * 更新发布的状态
     * <p>
     *     目前仅供车主发布的需求，系统自动匹配成功后，下单时更新的状态
     * </p>
     */
    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE GoodsPublishInfo SET status=?2 WHERE id=?1")
    int updateStatus(long id, int status);

    /**
     * 批量更新发布的状态
     */
    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE GoodsPublishInfo SET status=?2 WHERE id IN (?1) AND status=?3")
    int updateStatus(List<Long> idList, int status, int preStatus);

    /**
     * 解锁发布信息
     * @return 本次更新的记录数
     */
    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE GoodsPublishInfo SET status=0 WHERE id IN (?1) AND lockEndDate<now()")
    int unlockByIds(List<Long> idList);

    /**
     * 获取可解锁的发布列表
     */
    @Query("FROM GoodsPublishInfo WHERE status=1 AND lockEndDate<now()")
    List<GoodsPublishInfo> getUnlockList();
}