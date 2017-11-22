package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.UserFunds;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 11:05.
 */
public interface UserFundsRepository extends BaseRepository<UserFunds, Long> {
    UserFunds findByUid(long uid);

    UserFunds findByOpenid(String openid);
}