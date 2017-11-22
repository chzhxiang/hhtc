package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.OrderInfo;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 16:08.
 */
public interface OrderRepository extends BaseRepository<OrderInfo, Long> {
    long countByOpenidAndOrderTypeInAndOrderStatusIn(String openid, List<Integer> orderTypeList, List<Integer> orderStatusList);

    long countByOrderStatusInAndOrderTypeLessThan(List<Integer> orderStatusList, int orderType);
    long countByCommunityIdAndOrderStatusInAndOrderTypeLessThan(long communityId, List<Integer> orderStatusList, int orderType);

    long countByGoodsIdAndOrderTypeInAndOrderStatusIn(long goodsId, List<Integer> orderTypeList, List<Integer> orderStatusList);

    OrderInfo findByOutTradeNo(String outTradeNo);

    List<OrderInfo> findByOrderStatusIn(List<Integer> orderStatus);

    List<OrderInfo> findByGoodsPublishOrderIdsLike(String goodsPublishOrderIds);

    List<OrderInfo> findByOrderStatusAndOpenFromDatesEndingWith(int orderStatus, String openFromDates);

    /**
     * 归档：查找待支付且已过期的订单
     */
    List<OrderInfo> findByOrderStatusAndTimeExpireLessThanEqual(int orderStatus, String timeExpire);
}