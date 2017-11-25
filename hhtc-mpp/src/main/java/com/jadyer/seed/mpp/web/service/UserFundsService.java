package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.util.IPUtil;
import com.jadyer.seed.comm.util.MoneyUtil;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayUnifiedorderRespData;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.CommunityInfo;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.model.OrderInfo;
import com.jadyer.seed.mpp.web.model.UserFunds;
import com.jadyer.seed.mpp.web.repository.GoodsNeedRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishOrderRepository;
import com.jadyer.seed.mpp.web.repository.MppUserInfoRepository;
import com.jadyer.seed.mpp.web.repository.OrderRepository;
import com.jadyer.seed.mpp.web.repository.UserFundsFlowRepository;
import com.jadyer.seed.mpp.web.repository.UserFundsRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/27 18:34.
 */
@Service
public class UserFundsService {
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Value("${hhtc.orderLock.minute}")
    private int orderLockMinute;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private OrderService orderService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderRepository orderRepository;
    @Resource
    private CommunityService communityService;
    @Resource
    private GoodsNeedRepository goodsNeedRepository;
    @Resource
    private UserFundsRepository userFundsRepository;
    @Resource
    private MppUserInfoRepository mppUserInfoRepository;
    @Resource
    private UserFundsFlowRepository userFundsFlowRepository;
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;

    /**
     * 车主预约下单以及发布需求时，查询粉丝押金是否足够
     */
    public Map<String, String> depositIsenough(String openid, long communityId) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("isenough", "1");
        dataMap.put("money", new BigDecimal(0).toString());
        BigDecimal moneyBase = this.get(openid).getMoneyBase();
        CommunityInfo communityInfo = communityService.get(communityId);
        if(communityInfo.getMoneyBase().compareTo(moneyBase) > 0){
            dataMap.put("isenough", "0");
            dataMap.put("money", (communityInfo.getMoneyBase().subtract(moneyBase)).toString());
        }
        return dataMap;
    }


    /**
     * 查询粉丝资金情况
     */
    public UserFunds get(String openid) {
        UserFunds funds = userFundsRepository.findByOpenid(openid);
        if(null == funds){
            funds = new UserFunds();
            funds.setOpenid(openid);
            funds.setMoneyBase(new BigDecimal(0));
            funds.setMoneyFreeze(new BigDecimal(0));
            funds.setMoneyBalance(new BigDecimal(0));
        }
        return funds;
    }


    /**
     * 增加金额（流水需单独增加）
     * <p>
     *     如果传入的金额小于等于0，则什么都不干
     * </p>
     * @return 返回该粉丝的资金总览信息
     */
    @Transactional(rollbackFor=Exception.class)
    public UserFunds addMoneyBalanceForFans(String openid, BigDecimal money){
        UserFunds funds = userFundsRepository.findByOpenid(openid);
        if(null == funds){
            funds = new UserFunds();
            funds.setOpenid(openid);
            funds.setMoneyFreeze(new BigDecimal(0));
            funds.setMoneyBase(new BigDecimal(0));
            funds.setMoneyBalance(money);
        }else{
            funds.setMoneyBalance(money.add(funds.getMoneyBalance()));
        }
        return userFundsRepository.saveAndFlush(funds);
    }
    @Transactional(rollbackFor=Exception.class)
    public UserFunds addMoneyBalanceForPlatform(BigDecimal money){
        MppUserInfo mppUserInfo = mppUserInfoRepository.findByMptypeAndBindStatus(1, 1);
        if(null==mppUserInfo || mppUserInfo.getId()==0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "获取平台UID失败");
        }
        UserFunds funds = userFundsRepository.findByUid(mppUserInfo.getId());
        if(null == funds){
            funds = new UserFunds();
            funds.setUid(mppUserInfo.getId());
            funds.setMoneyFreeze(new BigDecimal(0));
            funds.setMoneyBase(new BigDecimal(0));
            funds.setMoneyBalance(money);
        }else{
            funds.setMoneyBalance(money.add(funds.getMoneyBalance()));
        }
        return userFundsRepository.saveAndFlush(funds);
    }
    @Transactional(rollbackFor=Exception.class)
    public UserFunds addMoneyBaseForFans(String openid, BigDecimal money){
        UserFunds funds = userFundsRepository.findByOpenid(openid);
        if(null == funds){
            funds = new UserFunds();
            funds.setOpenid(openid);
            funds.setMoneyFreeze(new BigDecimal(0));
            funds.setMoneyBalance(new BigDecimal(0));
            funds.setMoneyBase(money);
        }else{
            funds.setMoneyBase(money.add(funds.getMoneyBase()));
        }
        return userFundsRepository.saveAndFlush(funds);
    }


    /**
     * 扣除余额（流水需单独增加）
     * <p>
     *     如果传入的金额大于现有余额，则抛异常
     * </p>
     * @return 返回该粉丝的资金总览信息
     */
    @Transactional(rollbackFor=Exception.class)
    public UserFunds subtractMoneyBalanceForFans(String openid, BigDecimal money){
        UserFunds funds = userFundsRepository.findByOpenid(openid);
        if(money.compareTo(funds.getMoneyBalance()) == 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "余额不足");
        }
        funds.setMoneyBalance(funds.getMoneyBalance().subtract(money));
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
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "现有押金不足");
        }
        funds.setMoneyBase(funds.getMoneyBase().subtract(money));
        return userFundsRepository.saveAndFlush(funds);
    }


    /**
     * 充值（返回弹出微信支付所需的数据）
     * @param type      充值类型：10--个人中心充值，11--车位主发布车位充值，12--车主预约下单充值，13--车主发布需求充值
     * @param goodsId   车位ID
     * @param moneyBase 押金，单位：元
     * @param moneyRent 租金，单位：元
     */
    @Transactional(rollbackFor=Exception.class)
    public WeixinPayUnifiedorderRespData recharge(int type, String goodsId, String moneyBase, String moneyRent, BigDecimal money, HttpServletRequest request) {
        String appid = hhtcHelper.getWxAppidFromSession(request.getSession());
        String openid = hhtcHelper.getWxOpenidFromSession(request.getSession());
        //计算该增加押金和余额
        BigDecimal _moneyBase = new BigDecimal(0);
        BigDecimal _moneyRent = new BigDecimal(0);
        switch(type){
            case 10 :
                //个人中心充值的时候，可以选择充值到押金还是余额。选择充值到押金时，moneyBase和money都要传充值的金额（此时moneyRent可不传），同理选择充值到余额时moneyRent和money也都要传充值的金额（此时moneyBase可不传）
                //换句话说：money必传且传实际充值的金额，而moneyBase和moneyRent看粉丝选择的充值目标，选哪个，就传哪个值，就行了
                if(StringUtils.isBlank(moneyBase) && StringUtils.isBlank(moneyRent)){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "押金或余额至少传一");
                }
                if(StringUtils.isNotBlank(moneyBase)){
                    _moneyBase = new BigDecimal(moneyBase);
                }
                if(StringUtils.isNotBlank(moneyRent)){
                    _moneyRent = new BigDecimal(moneyRent);
                }
                break;
            case 11 :
                if(StringUtils.isBlank(moneyBase)){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "租金必传["+type+"]");
                }
                _moneyBase = new BigDecimal(moneyBase);
                if(money.compareTo(_moneyBase) >= 0){
                    _moneyRent = money.subtract(_moneyBase);
                }else{
                    _moneyBase = money;
                }
                break;
            case 12 :
            case 13 :
                if(StringUtils.isBlank(moneyBase) && StringUtils.isBlank(moneyRent)){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "租金或押金至少传一个["+type+"]");
                }
                if(StringUtils.isNotBlank(moneyBase)){
                    _moneyBase = new BigDecimal(moneyBase);
                }
                if(money.compareTo(_moneyBase) >= 0){
                    _moneyRent = money.subtract(_moneyBase);
                }else{
                    _moneyBase = money;
                    _moneyRent = new BigDecimal(0);
                }
                break;
            default: throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无效的充值类型["+type+"]");
        }
        //记录一笔订单
        MppFansInfor fansInfo = fansService.getByOpenid(openid);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setTotalFee(Long.parseLong(MoneyUtil.yuanToFen(_moneyBase.add(_moneyRent).toString())));
        orderInfo.setDepositMoney(_moneyBase);
        orderInfo.setCanRefundMoney(new BigDecimal(MoneyUtil.fenToYuan(orderInfo.getTotalFee()+"")));
        //TODO
//        orderInfo.setCommunityId(0!=fansInfo.getCarOwnerCommunityId() ? fansInfo.getCarOwnerCommunityId() : fansInfo.getCarParkCommunityId());
//        orderInfo.setCommunityName(0!=fansInfo.getCarOwnerCommunityId() ? fansInfo.getCarOwnerCommunityName() : fansInfo.getCarParkCommunityName());
        orderInfo.setGoodsId(StringUtils.isNotBlank(goodsId) ? Long.parseLong(goodsId) : 0);
        orderInfo.setAppid(appid);
        orderInfo.setBody("吼吼共享车位 - 充值 - " + orderInfo.getCanRefundMoney() + " 元");
        orderInfo.setOutTradeNo(hhtcHelper.buildOrderNo());
        orderInfo.setAttach(orderInfo.getOutTradeNo());
        orderInfo.setSpbillCreateIp(IPUtil.getClientIP(request));
        orderInfo.setTimeStart(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        orderInfo.setTimeExpire(DateFormatUtils.format(DateUtils.addMinutes(new Date(), this.orderLockMinute), "yyyyMMddHHmmss"));
        orderInfo.setNotifyUrl(hhtcContextPath + "/weixin/helper/pay/notify");
        orderInfo.setTradeType("JSAPI");
        orderInfo.setOpenid(openid);
        orderInfo.setIsNotify(0);
        orderInfo.setOrderType(type);
        orderInfo.setOrderStatus(1);
        orderRepository.saveAndFlush(orderInfo);
        return hhtcHelper.payUnifiedorder(orderInfo);
    }
}