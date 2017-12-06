package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayUnifiedorderRespData;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.FansAuditRepository;
import com.jadyer.seed.mpp.web.repository.MppUserInfoRepository;
import com.jadyer.seed.mpp.web.repository.UserFundsRepository;
import com.jadyer.seed.mpp.web.service.async.WeixinTemplateMsgAsync;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/27 18:34.
 */
@Service
public class UserFundsService {
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private CommunityService communityService;
    @Resource
    private UserFundsRepository userFundsRepository;
    @Resource
    private FansAuditRepository fansAuditRepository;
    @Resource
    private UserFundsFlowService userFundsFlowService;
    @Resource
    private WeixinTemplateMsgAsync weixinTemplateMsgAsync;
    @Resource
    private MppUserInfoRepository mppUserInfoRepository;
    /**
     * TOKGO 车主预约下单以及发布需求时，查询粉丝押金是否足够
     * @return true 押金足够
     */
    public boolean depositIsenough(String openid, long communityId) {
        BigDecimal moneyBase = this.get(openid).getMoneyBase();
        CommunityInfo communityInfo = communityService.get(communityId);
        return (communityInfo.getMoneyBase().compareTo(moneyBase) <= 0);
    }


    /**
     * TOKGO 检查余额是否足够
     * */
    public boolean BalanceIsenough(String openid, BigDecimal perice){
        BigDecimal moneyBase = this.get(openid).getMoneyBalance();
        return (perice.compareTo(moneyBase) <= 0);
    }


    /**
     * TOKGO 查询粉丝资金情况
     */
    public UserFunds get(String openid) {
        UserFunds funds = userFundsRepository.findByOpenid(openid);
        return funds;
    }

    /**
     * TOKGO 保存用户
     */
    public void save(UserFunds funds) {
        userFundsRepository.save(funds);
    }

    /**
     * TOKGO 用户提现申请
     *@param  type 4---余额提现 5---押金提现
     * */
    public void WthdrawApplication(String openid,double amount, int type){
        if (!(type== Constants.AUDTI_TEPY_BALANCE||type== Constants.AUDTI_TEPY_DEPOSIT))
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_DATA_ERROR);
        UserFunds funds = userFundsRepository.findByOpenid(openid);
        if (type== Constants.AUDTI_TEPY_BALANCE && funds.getMoneyBalance().doubleValue()<amount)
            throw new HHTCException(CodeEnum.HHTC_FUNDS_BALANCE_NO);
        if (type== Constants.AUDTI_TEPY_DEPOSIT && funds.getMoneyBase().doubleValue()<amount)
            throw new HHTCException(CodeEnum.HHTC_FUNDS_DEPOSIT_NO);
        MppFansInfor mppFansInfor = fansService.getByOpenid(openid);
        FansInforAudit audit = new FansInforAudit();
        audit.setUid(mppFansInfor.getUid());
        audit.setType(type);
        audit.setOpenid(openid);
        audit.setCommunityId(mppFansInfor.getCommunityId());
        audit.setCommunityName(mppFansInfor.getCommunityName());
        audit.setContent(""+amount);
        fansAuditRepository.save(audit);
    }



    /**
     * TOKGO 增加金额（流水需单独增加）
     * <p>
     *     如果传入的金额小于等于0，则什么都不干
     * </p>
     * @return 返回该粉丝的资金总览信息
     */
    @Transactional(rollbackFor=Exception.class)
    public UserFunds addMoneyBalanceForFans(String openid, BigDecimal money,String orderid,String remak){
        UserFunds funds = userFundsRepository.findByOpenid(openid);
        if(null == funds){
            funds = new UserFunds();
            funds.setOpenid(openid);
            funds.setMoneyBase(new BigDecimal(0));
            funds.setMoneyBalance(money);
        }else{
            funds.setMoneyBalance(money.add(funds.getMoneyBalance()));
        }
        userFundsFlowService.AddFlowForRecharge(openid,orderid,money,Constants.FUNDS_TEPY_BALANCE_IN,remak);
        return userFundsRepository.saveAndFlush(funds);
    }

    /**
     * TOKGO 给平台打钱
     * */
    @Transactional(rollbackFor=Exception.class)
    public UserFunds addMoneyBalanceForPlatform(BigDecimal money,String orderid,String remak){
        MppUserInfo mppUserInfo = mppUserInfoRepository.findByMptypeAndBindStatus(1, 1);
        if(null==mppUserInfo || mppUserInfo.getId()==0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "获取平台UID失败");
        }
        UserFunds funds = userFundsRepository.findByUid(mppUserInfo.getId());
        if(null == funds){
            funds = new UserFunds();
            funds.setUid(mppUserInfo.getId());
            funds.setMoneyBase(new BigDecimal(0));
            funds.setMoneyBalance(money);
        }else{
            funds.setMoneyBalance(money.add(funds.getMoneyBalance()));
        }
        //平台流水记录
        userFundsFlowService.AddFlowForRecharge("",orderid,money,Constants.FUNDS_TEPY_BALANCE_IN,remak);
        return userFundsRepository.saveAndFlush(funds);
    }

    @Transactional(rollbackFor=Exception.class)
    public UserFunds addMoneyBaseForFans(String openid, BigDecimal money){
        UserFunds funds = userFundsRepository.findByOpenid(openid);
        if(null == funds){
            funds = new UserFunds();
            funds.setOpenid(openid);
            funds.setMoneyBalance(new BigDecimal(0));
            funds.setMoneyBase(money);
        }else{
            funds.setMoneyBase(money.add(funds.getMoneyBase()));
        }
        return userFundsRepository.saveAndFlush(funds);
    }


    /**
     * TOKGO 扣除余额（流水需单独增加）
     * <p>
     *     如果传入的金额大于现有余额，则抛异常
     * </p>
     * @return 返回该粉丝的资金总览信息
     */
    @Transactional(rollbackFor=Exception.class)
    public UserFunds subtractMoneyBalanceForFans(String openid, BigDecimal money,String orderid,String remak){
        UserFunds funds = userFundsRepository.findByOpenid(openid);
        if(money.compareTo(funds.getMoneyBalance()) == 1){
            throw new HHTCException(CodeEnum.HHTC_FUNDS_BALANCE_NO);
        }
        funds.setMoneyBalance(funds.getMoneyBalance().subtract(money));
        userFundsFlowService.AddFlowForRecharge(openid,orderid,money,Constants.FUNDS_TEPY_BALANCE_OUT,remak);
        return userFundsRepository.saveAndFlush(funds);
    }


    /**
     * 为粉丝扣除押金（流水需单独增加）
     * <p>
     *     如果传入的金额大于现有押金，则抛异常
     * </p>
     * @return 返回该粉丝的资金总览信息
     */
    @Transactional(rollbackFor=Exception.class)
    public UserFunds subtractMoneyBaseForFans(String openid, BigDecimal money){
        UserFunds funds = userFundsRepository.findByOpenid(openid);
        if(money.compareTo(funds.getMoneyBase()) == 1){
            throw new HHTCException(CodeEnum.HHTC_FUNDS_DEPOSIT_NO);
        }
        funds.setMoneyBase(funds.getMoneyBase().subtract(money));
        return userFundsRepository.saveAndFlush(funds);
    }


    /**
     * TOKGO 后台控制用户资金
     * */
    public void AdminChange(double moneybase, double moneybalance,UserFunds userFunds){
        //TODO  资金流水没有做
        if (moneybalance!=0){

            userFunds.setMoneyBalance(userFunds.getMoneyBalance().add(BigDecimal.valueOf(moneybalance)));
            userFundsRepository.save(userFunds);
        }
        if (moneybase >0){
            userFunds.setMoneyBase(userFunds.getMoneyBase().add(BigDecimal.valueOf(moneybase)));
            userFundsRepository.save(userFunds);
        }
       else {
            userFunds.setMoneyBase(userFunds.getMoneyBase().add(BigDecimal.valueOf(moneybase)));
            userFundsRepository.save(userFunds);
        }
    }



    /**
     * 充值（返回弹出微信支付所需的数据）
     */
    @Transactional(rollbackFor=Exception.class)
    public WeixinPayUnifiedorderRespData recharge(String type,BigDecimal money, HttpServletRequest request) {
        String appid = hhtcHelper.getWxAppidFromSession(request.getSession());
        String openid = hhtcHelper.getWxOpenidFromSession(request.getSession());
        return hhtcHelper.payUnifiedorder(appid,openid,money,request);
    }
}