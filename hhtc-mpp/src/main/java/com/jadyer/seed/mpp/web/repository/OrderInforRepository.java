package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.OrderInfor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 16:08.
 */
public interface OrderInforRepository extends BaseRepository<OrderInfor, Long> {
//    long countByOpenidAndOrderTypeInAndOrderStatusIn(String openid, List<Integer> orderTypeList, List<Integer> orderStatusList);
//
//    long countByOrderStatusInAndOrderTypeLessThan(List<Integer> orderStatusList, int orderType);
//    long countByCommunityIdAndOrderStatusInAndOrderTypeLessThan(long communityId, List<Integer> orderStatusList, int orderType);
//
//    long countByGoodsIdAndOrderTypeInAndOrderStatusIn(long goodsId, List<Integer> orderTypeList, List<Integer> orderStatusList);
//
    OrderInfor findByOrderId(String orderid);

    List<OrderInfor> findByPostOpenid(String postopenid);
    List<OrderInfor> findByOwnersOpenid(String ownersopenid);

    List<OrderInfor> findByPostOpenidAndOrderStatus(String postopenid,int orderstatus);
    List<OrderInfor> findByOwnersOpenidAndOrderStatus(String ownersopenid,int orderstatus);
//
//    List<OrderInfo> findByGoodsPublishOrderIdsLike(String goodsPublishOrderIds);
//
//    List<OrderInfo> findByOrderStatusAndOpenFromDatesEndingWith(int orderStatus, String openFromDates);
//

//    @Modifying
//    @Transactional
//    @Query("UPDATE OrderInfor SET orderStatus=?1 WHERE orderId=?2")
//    int updateOrderState(int orderStatus, String orderid);

}