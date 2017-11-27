package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.AdviceInfor;
import com.jadyer.seed.mpp.web.model.MppFansInfor;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 10:31.
 */
public interface AdviceRepository extends BaseRepository<AdviceInfor, Long> {
    /**
     * 查询某个粉丝的投诉记录
     */
    List<AdviceInfor> findByOpenid(String openid);

}