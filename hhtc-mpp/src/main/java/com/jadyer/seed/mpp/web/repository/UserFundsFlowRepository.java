package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.UserFundsFlow;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 11:10.
 */
public interface UserFundsFlowRepository extends BaseRepository<UserFundsFlow, Long> {
    List<UserFundsFlow> findByOpenidAndInOutType(String openid, int inOutType);
}