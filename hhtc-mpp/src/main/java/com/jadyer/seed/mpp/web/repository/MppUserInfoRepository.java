package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MppUserInfoRepository extends BaseRepository<MppUserInfo, Long> {
    MppUserInfo findByUsernameAndPassword(String username, String password);

    MppUserInfo findByType(int type);

    List<MppUserInfo> findByBindStatus(int indStatus);

    MppUserInfo findByMptypeAndBindStatus(int mptype, int bindStatus);

    @Query("FROM MppUserInfo WHERE mptype=1 AND mpid=?1")
    MppUserInfo findByWxid(String mpid);

    @Query("FROM MppUserInfo WHERE mptype=2 AND mpid=?1")
    MppUserInfo findByQqid(String mpid);

    int countByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE MppUserInfo SET password=?2 WHERE id=?1")
    int updatePassword(long uid, String newPassword);
}