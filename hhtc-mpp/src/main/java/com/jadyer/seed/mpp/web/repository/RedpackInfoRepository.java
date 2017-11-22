package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.RedpackInfo;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/5 16:33.
 */
public interface RedpackInfoRepository extends BaseRepository<RedpackInfo, Long> {
    List<RedpackInfo> findByStatus(int status);
    List<RedpackInfo> findByRefundApplyId(long refundApplyId);
}