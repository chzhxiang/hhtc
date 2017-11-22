package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.OrderRent;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/24 20:05.
 */
public interface OrderRentRepository extends BaseRepository<OrderRent, Long> {
    List<OrderRent> findByOrderFromDateLessThanEqualAndOtherMoneyGreaterThan(int orderFromDate, BigDecimal otherMoney);
}