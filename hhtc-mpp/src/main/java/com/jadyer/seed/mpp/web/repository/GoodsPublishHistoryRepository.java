package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.GoodsPublishHistory;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/22 19:02.
 */
public interface GoodsPublishHistoryRepository extends BaseRepository<GoodsPublishHistory, Long> {
    GoodsPublishHistory findByGoodsPublishId(long goodsPublishId);
}