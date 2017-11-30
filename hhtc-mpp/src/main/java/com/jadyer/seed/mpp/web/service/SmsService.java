package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.SmsInfor;
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
     * TOKGO 新增短信记录
     * @param type 短信类型：0--通用，1--电话号码验证，2--车位主注册，3--车主提现，4--车位主提现
     */
    @Transactional(rollbackFor=Exception.class)
    public SmsInfor smsSend(String phoneNo, int type){
        CheckSMS();
        try {
            String verifyCode = RandomStringUtils.randomNumeric(6);
            if(hhtcHelper.sendSms(phoneNo, verifyCode)){
                SmsInfor smsInfor = new SmsInfor();
                smsInfor.setPhoneNo(phoneNo);
                smsInfor.setVerifyCode(verifyCode);
                smsInfor.setType(type);
                smsInfor.setTimeExpire(new Date().getTime());
                return smsRepository.saveAndFlush(smsInfor);
            }
        }catch (Exception ex) {
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "短信发送失败");
        }
        return null;
    }


    /**
     * TOKGO 短信验证
     * @param type 短信类型：1—电话号码验证，2—电话号码注销，3—车主提现，4—车位主提现
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean smsVerify(String phoneNo, String verifyCode, int type){
        CheckSMS();
        List<SmsInfor> smsInforList = smsRepository.findByPhoneNoAndVerifyCode(phoneNo,verifyCode);
        for(SmsInfor obj : smsInforList){
            if(obj.getType()==type ){
                //TODO 测试阶段 不删除验证码
//                smsRepository.delete(obj.getId());
                return true;
            }
        }
        return false;
    }

    /**
     * TOKGO 检查历史验证是否过期
     * */
    private void CheckSMS(){
        long currentDate = new Date().getTime();
        List<SmsInfor> smsInforList = smsRepository.findAll();
        for(SmsInfor obj : smsInforList){
            if((currentDate - obj.getTimeExpire())>= Constants.S_SMSFILEMAX){
                //TODO 测试阶段 不删除验证码
//                smsRepository.delete(obj.getId());
                ;
            }
        }
        smsRepository.flush();
    }


}