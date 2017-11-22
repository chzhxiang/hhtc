package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.OrderInout;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/21 20:55.
 */
public interface OrderInoutRepository extends BaseRepository<OrderInout, Long> {
    List<OrderInout> findByEndDateAndInTimeNotNullAndOutTimeNull(int endDate);
    OrderInout findByCardNo(String cardNo);
    OrderInout findByOrderNoAndMaxIndex(String orderNo, int maxIndex);
    OrderInout findByOrderNoAndInTimeNotNullAndOutTimeNull(String orderNo);
    List<OrderInout> findByCommunityIdAndCarNumberAndFromDateInOrderByFromDateAscFromTimeAsc(long communityId, String carNumber, List<Integer> fromDateList);
    List<OrderInout> findByCommunityIdAndCarNumberAndEndDateInOrderByFromDateAscFromTimeAsc(long communityId, String carNumber, List<Integer> endDateList);
    OrderInout findFirstByOrderNoOrderByMaxIndexDesc(String orderNo);
    @Modifying
    @Transactional(timeout=10)
    @Query("DELETE FROM OrderInout WHERE orderNo=?1")
    void deleteByOrderNo(String orderNo);
}