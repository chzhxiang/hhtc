package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.SmsInfo;
import com.jadyer.seed.mpp.web.repository.SmsRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 11:36.
 */
@Service
public class SmsService {
    @Value("${aliyun.sms.validityMinute}")
    private int smsValidityMinute;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private SmsRepository smsRepository;

    /**
     * 新增短信记录
     */
    @Transactional(rollbackFor=Exception.class)
    public SmsInfo smsSend(String phoneNo, int type){
        String verifyCode = RandomStringUtils.randomNumeric(6);
        if(hhtcHelper.sendSms(phoneNo, verifyCode)){
            SmsInfo smsInfo = new SmsInfo();
            smsInfo.setIsUsed(0);
            smsInfo.setPhoneNo(phoneNo);
            smsInfo.setVerifyCode(verifyCode);
            smsInfo.setType(type);
            smsInfo.setTimeSend(new Date());
            smsInfo.setTimeExpire(DateUtils.addMinutes(new Date(), smsValidityMinute));
            return smsRepository.saveAndFlush(smsInfo);
        }
        throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "短信发送失败");
    }


    /**
     * 短信验证
     * @param type 短信类型：1—车主注册，2—车位主注册，3—车主提现，4—车位主提现
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean smsVerify(String phoneNo, String verifyCode, int type){
        Date currentDate = new Date();
        List<SmsInfo> smsInfoList = smsRepository.findByPhoneNoAndVerifyCodeOrderByIdDesc(phoneNo, verifyCode);
        for(SmsInfo obj : smsInfoList){
            if(obj.getType()==type && obj.getTimeExpire().getTime()>=currentDate.getTime()){
                obj.setIsUsed(1);
                obj.setUsedResult(1);
                obj.setUsedTime(currentDate);
                smsRepository.saveAndFlush(obj);
                return true;
            }
        }
        return false;
    }
}