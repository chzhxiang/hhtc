package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.RefundInfo;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 11:03.
 */
public interface RefundRepository extends BaseRepository<RefundInfo, Long> {
    List<RefundInfo> findByRefundStatus(int refundStatus);
    List<RefundInfo> findByRefundApplyId(long refundApplyId);
}