package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.mpp.web.model.OrderInfo;
import com.jadyer.seed.mpp.web.model.UserFunds;
import com.jadyer.seed.mpp.web.model.UserFundsFlow;
import com.jadyer.seed.mpp.web.repository.UserFundsFlowRepository;
import com.jadyer.seed.mpp.web.repository.UserFundsRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/27 12:00.
 */
@Service
public class UserFundsFlowService {
    //惩罚车位主时，补贴给平台的比例：50%
    private static final BigDecimal rentRatioPlatform = new BigDecimal(0.5);
    //惩罚车位主时，补贴给车主的比例：50%
    private static final BigDecimal rentRatioCarowner = new BigDecimal(0.5);
    @Resource
    private OrderService orderService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private UserFundsRepository userFundsRepository;
    @Resource
    private UserFundsFlowRepository userFundsFlowRepository;

    @Transactional(rollbackFor=Exception.class)
    public UserFundsFlow upsert(UserFundsFlow userFundsFlow){
        return userFundsFlowRepository.saveAndFlush(userFundsFlow);
    }


    public Page<UserFundsFlow> listViaPage(String pageNo, String openid) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        Condition<UserFundsFlow> spec = Condition.<UserFundsFlow>and().eq("openid", openid);
        return userFundsFlowRepository.findAll(spec, pageable);
    }


    public Page<UserFundsFlow> listByPlatformViaPage(String pageNo, long uid) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        Condition<UserFundsFlow> spec = Condition.<UserFundsFlow>and().eq("uid", uid);
        return userFundsFlowRepository.findAll(spec, pageable);
    }


    /**
     * 懲罰車位主（車主下單後來停車，发現車位上有車，無法停車，聯繫客服後，客服在后台主動懲罰車位主）
     */
    @Transactional(rollbackFor=Exception.class)
    public void chengfaCarparker(long orderId, BigDecimal money){
        if(money.compareTo(new BigDecimal(0)) == 0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "懲罰金額為0");
        }
        OrderInfo order = orderService.get(orderId);
//        GoodsInfo goods = goodsService.get(order.getGoodsId());
//        UserFunds funds = userFundsService.subtractMoneyBaseForFans(goods.getOpenid(), money);
        UserFundsFlow fundsFlow = new UserFundsFlow();
//        fundsFlow.setFundsId(funds.getId());
//        fundsFlow.setOpenid(funds.getOpenid());
        fundsFlow.setMoney(money);
        fundsFlow.setInOut("out");
        fundsFlow.setInOutDesc("车主无法停车时扣除车位主押金");
        fundsFlow.setInOutType(3);
        fundsFlow.setBizDate(Integer.parseInt(DateUtil.getCurrentDate()));
        fundsFlow.setBizDateTime(new Date());
        userFundsFlowRepository.saveAndFlush(fundsFlow);
        //补贴给平台和车主
        BigDecimal moneyPlatform = money.multiply(rentRatioPlatform);
        BigDecimal moneyCarowner = money.multiply(rentRatioCarowner);
//        funds = userFundsService.addMoneyBalanceForFans(order.getOpenid(), moneyCarowner);
//        fundsFlow = new UserFundsFlow();
//        fundsFlow.setFundsId(funds.getId());
//        fundsFlow.setOpenid(order.getOpenid());
//        fundsFlow.setMoney(moneyCarowner);
//        fundsFlow.setInOut("in");
//        fundsFlow.setInOutDesc("车主无法停车时补贴给车主的金额");
//        fundsFlow.setInOutType(9);
//        fundsFlow.setBizDate(Integer.parseInt(DateUtil.getCurrentDate()));
//        fundsFlow.setBizDateTime(new Date());
//        this.upsert(fundsFlow);
//        funds = userFundsService.addMoneyBalanceForPlatform(moneyPlatform);
//        fundsFlow = new UserFundsFlow();
//        fundsFlow.setFundsId(funds.getId());
//        fundsFlow.setUid(funds.getUid());
        fundsFlow.setMoney(moneyPlatform);
        fundsFlow.setInOut("in");
        fundsFlow.setInOutDesc("车主无法停车时补贴给平台的金额");
        fundsFlow.setInOutType(9);
        fundsFlow.setBizDate(Integer.parseInt(DateUtil.getCurrentDate()));
        fundsFlow.setBizDateTime(new Date());
        this.upsert(fundsFlow);
    }


    /**
     * 微信支付--充值交易--成功时，增加资金流水
     */
    private void addFlowForRecharge(long fundsId, String openid, BigDecimal money, int orderType, int inOutType){
        if(inOutType!=1 && inOutType!=2){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无效的流水类型["+inOutType+"]");
        }
        String typedesc = orderType==10 ? "个人中心充值" : orderType==11 ? "车位主发布车位充值" : orderType==12 ? "车主预约下单充值" : orderType==13 ? "车主发布需求充值" : "hhtc-unknown";
        UserFundsFlow fundsFlow = new UserFundsFlow();
        fundsFlow.setFundsId(fundsId);
        fundsFlow.setOpenid(openid);
        fundsFlow.setMoney(money);
        fundsFlow.setInOut("in");
        fundsFlow.setInOutDesc(typedesc + (inOutType==1 ? "押金" : "余额"));
        fundsFlow.setInOutType(inOutType);
        fundsFlow.setBizDate(Integer.parseInt(DateUtil.getCurrentDate()));
        fundsFlow.setBizDateTime(new Date());
        userFundsFlowRepository.saveAndFlush(fundsFlow);
    }


    /**
     * 微信支付--充值交易--成功后，更新资金信息
     * <p>
     *     充值类型：10--个人中心充值，11--车位主发布车位充值，12--车主预约下单充值，13--车主发布需求充值
     * </p>
     */
    @Transactional(rollbackFor=Exception.class)
    public void recharge(OrderInfo orderInfo){
        UserFunds funds = userFundsRepository.findByOpenid(orderInfo.getOpenid());
        if(null == funds){
            funds = new UserFunds();
            funds.setOpenid(orderInfo.getOpenid());
            funds.setMoneyBase(new BigDecimal(0));
            funds.setMoneyFreeze(new BigDecimal(0));
            funds.setMoneyBalance(new BigDecimal(0));
        }
        if(orderInfo.getOrderType()==10 || orderInfo.getOrderType()==12 || orderInfo.getOrderType()==13){
            BigDecimal moneyRent = orderInfo.getCanRefundMoney().subtract(orderInfo.getDepositMoney());
            funds.setMoneyBalance(funds.getMoneyBalance().add(moneyRent));
            funds.setMoneyBase(funds.getMoneyBase().add(orderInfo.getDepositMoney()));
            funds = userFundsRepository.saveAndFlush(funds);
            if(orderInfo.getDepositMoney().compareTo(new BigDecimal(0)) == 1){
                this.addFlowForRecharge(funds.getId(), orderInfo.getOpenid(), orderInfo.getDepositMoney(), orderInfo.getOrderType(), 1);
            }
            if(moneyRent.compareTo(new BigDecimal(0)) == 1){
                this.addFlowForRecharge(funds.getId(), orderInfo.getOpenid(), moneyRent, orderInfo.getOrderType(), 2);
            }
        }
        if(orderInfo.getOrderType() == 11){
            funds.setMoneyBase(funds.getMoneyBase().add(orderInfo.getCanRefundMoney()));
            funds = userFundsRepository.saveAndFlush(funds);
            this.addFlowForRecharge(funds.getId(), orderInfo.getOpenid(), orderInfo.getCanRefundMoney(), orderInfo.getOrderType(), 1);
        }
    }
}