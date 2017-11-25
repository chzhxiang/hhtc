package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.FansInforAudit;

import java.util.List;

public interface FansAuditRepository extends BaseRepository<FansInforAudit,Long> {
    List<FansInforAudit> findByUidAndOpenidAndType(long uid, String openid,int type);
    //FansInforAudit findbyOpenid()
}
