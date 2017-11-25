package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FansInforRepository extends BaseRepository<MppFansInfor, Long> {
    long countByPhoneNo(String phoneNo);
    MppFansInfor findByOpenid(String openid);
    MppFansInfor findByPhoneNo(String phoneNo);

    /**
     * 查询平台某用户的所有粉丝信息
     */
    List<MppFansInfor> findByUid(long uid);

    /**
     * 查询某个粉丝的信息
     */
    MppFansInfor findByUidAndOpenid(long uid, String openid);


    /**
     * 更新粉丝的关注状态
     */
    @Modifying
    @Transactional(timeout=10)
    @Query("UPDATE MppFansInfor SET subscribe=?1 WHERE uid=?2 AND openid=?3")
    int updateSubscribe(String subscribe, long uid, String openid);
}