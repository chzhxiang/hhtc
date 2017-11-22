package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.RefundApply;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/5 16:37.
 */
public interface RefundApplyRepository extends BaseRepository<RefundApply, Long> {
    List<RefundApply> findByOpenidAndPayStatusIn(String openid, List<Integer> payStatus);
    List<RefundApply> findByAuditStatusAndPayStatus(int auditStatus, int payStatus);

    /**
     * 更新支付状态
     * @param payStatus 支付状态：2--支付成功，3--支付部分失败，4--支付全部失败
     */
    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE RefundApply SET payStatus=?2 WHERE id=?1")
    void updatePayStatus(long id, int payStatus);
}