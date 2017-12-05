package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.AdviceInfor;
import com.jadyer.seed.mpp.web.model.TemporaryOut;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 10:31.
 */
public interface TemporaryOutRepository extends BaseRepository<TemporaryOut, Long> {
    TemporaryOut findByCommunityIdAndCarNumber(long communityid, String carnumber);
}