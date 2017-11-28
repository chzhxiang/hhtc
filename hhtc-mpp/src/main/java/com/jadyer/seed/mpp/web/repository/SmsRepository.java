package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.SmsInfor;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 11:30.
 */
public interface SmsRepository extends BaseRepository<SmsInfor, Long> {
    List<SmsInfor> findByPhoneNoAndVerifyCodeOrderByIdDesc(String phoneNo, String verifyCode);
}