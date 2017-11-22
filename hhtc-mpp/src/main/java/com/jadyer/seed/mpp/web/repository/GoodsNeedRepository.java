package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.GoodsNeedInfo;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 10:37.
 */
public interface GoodsNeedRepository extends BaseRepository<GoodsNeedInfo, Long> {
    long countByOpenidAndStatus(String openid, int status);

    List<GoodsNeedInfo> findByOpenidAndCommunityIdAndStatus(String openid, long communityId, int status);

    /**
     * 查询即将过期的需求列表
     * status=? AND needFromDate=? AND needFromTime<=?
     */
    List<GoodsNeedInfo> findByStatusAndNeedFromDateAndNeedFromTimeLessThanEqual(int status, int needFromDate, int needFromTime);

    /**
     * 查询待匹配列表
     */
    List<GoodsNeedInfo> findByNeedFromDateGreaterThanEqualAndStatus(int needFromDate, int status);

    /**
     * 用于校验发布的需求信息是否与现有的重复
     */
    List<GoodsNeedInfo> findByCommunityIdAndCarNumberAndStatusInAndNeedFromDateIn(long communityId, String carNumber, List<Integer> statusList, List<Integer> needFromDateList);
}