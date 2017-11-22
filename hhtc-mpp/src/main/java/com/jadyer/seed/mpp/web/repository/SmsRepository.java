package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.SmsInfo;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 11:30.
 */
public interface SmsRepository extends BaseRepository<SmsInfo, Long> {
    List<SmsInfo> findByPhoneNoAndVerifyCodeOrderByIdDesc(String phoneNo, String verifyCode);
    SmsInfo findFirstByPhoneNoAndTypeOrderByIdDesc(String phoneNo, int type);
    SmsInfo findFirstByPhoneNoOrderByIdDesc(String phoneNo);
    List<SmsInfo> findFirst10ByPhoneNoOrderByIdDesc(String phoneNo);
}