package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.FansInforAudit;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FansAuditRepository extends BaseRepository<FansInforAudit,Long> {
    List<FansInforAudit> findByUidAndOpenidAndType(long uid, String openid,int type);
    List<FansInforAudit> findByOpenidAndType( String openid,int type);

//    /**
//     * TOKGO 更新查看状态
//     */
//    @Modifying
//    @Transactional
//    @Query("UPDATE FansInforAudit SET state= 0 WHERE id=?1")
//    int updateState (long id);
}
