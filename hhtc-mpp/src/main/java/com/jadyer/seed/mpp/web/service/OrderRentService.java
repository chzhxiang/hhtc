package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.MoneyUtil;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.CommunityInfo;
import com.jadyer.seed.mpp.web.model.OrderInfo;
import com.jadyer.seed.mpp.web.model.OrderRent;
import com.jadyer.seed.mpp.web.model.UserFunds;
import com.jadyer.seed.mpp.web.model.UserFundsFlow;
import com.jadyer.seed.mpp.web.repository.OrderRentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/24 20:06.
 */
@Service
public class OrderRentService {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private CommunityService communityService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private OrderRentRepository orderRentRepository;
    @Resource
    private UserFundsFlowService userFundsFlowService;

    /**
     * 分润给平台和车位主（訂單金額超過小區全天金額時，每月8、18、28号才分润）
     * <p>
     *     注意：車主余額的扣減，不在本方法中處理。需要調用本方法之前自行處理
     * </p>
     * @param type   1--车主预约下单，2--车主需求下单，99--運營調配訂單
     * @param openid 车位主openid（非车主的）
     * @return 返回車位主的資金收益，單位：元（對於超過24小時的，返回的金額為0）
     */
    @Transactional(rollbackFor=Exception.class)
    public BigDecimal rent(int type, String openid, OrderInfo order){
        if(type!=1 && type!=2 && type!=99){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无效的分润类型["+type+"]");
        }
        BigDecimal price = new BigDecimal(MoneyUtil.fenToYuan(order.getTotalFee()+""));
        CommunityInfo communityInfo = communityService.get(order.getCommunityId());
        if(price.compareTo(communityInfo.getMoneyRentFull()) == 1){
            OrderRent obj = new OrderRent();
            obj.setCommunityId(order.getCommunityId());
            obj.setOrderId(order.getId());
            obj.setOrderType(type);
            obj.setOrderFromDate(Integer.parseInt(order.getOpenFromDates().split("-")[0]));
            obj.setGoodsOpenid(openid);
            obj.setOrderMoney(price);
            obj.setPerMoney(price.divide(new BigDecimal(order.getOpenFromDates().split("-").length), 4, BigDecimal.ROUND_HALF_UP));
            obj.setOtherMoney(price);
            orderRentRepository.saveAndFlush(obj);
            return new BigDecimal(0);
        }
        return this.doRent(type, openid, price, communityInfo);
    }


    private BigDecimal doRent(int type, String openid, BigDecimal price, CommunityInfo communityInfo){
        BigDecimal moneyPlatform = price.multiply(new BigDecimal(communityInfo.getRentRatioPlatform()));
        BigDecimal moneyCarparker = price.multiply(new BigDecimal(communityInfo.getRentRatioCarparker()));
        moneyPlatform = moneyPlatform.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        moneyCarparker = moneyCarparker.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        UserFunds funds = userFundsService.addMoneyBalanceForFans(openid, moneyCarparker,"","");
        UserFundsFlow fundsFlow = new UserFundsFlow();
        fundsFlow.setOpenid(openid);
        fundsFlow.setMoney(moneyCarparker);
        fundsFlow.setInOutDesc((type==1 ? "车位被预约" : type==2 ? "车位被需求匹配后预约" : "车位被订单调配后下单") + "而获得租金");
        fundsFlow.setInOutType(5);
        userFundsFlowService.upsert(fundsFlow);

        fundsFlow = new UserFundsFlow();
        fundsFlow.setUid(funds.getUid());
        fundsFlow.setMoney(moneyPlatform);
        fundsFlow.setInOutDesc((type==1 ? "车位被预约" : type==2 ? "车位被需求匹配后预约" : "车位被订单调配后下单") + "而分润平台");
        fundsFlow.setInOutType(5);
        userFundsFlowService.upsert(fundsFlow);
        return moneyCarparker;
    }


    @Transactional(rollbackFor=Exception.class)
    public void rentSettle() {
        List<OrderRent> rentList = orderRentRepository.findByOrderFromDateLessThanEqualAndOtherMoneyGreaterThan(Integer.parseInt(DateUtil.getCurrentDate()), new BigDecimal(0));
        String currIndex = "1";
        int len = rentList.size();
        LogUtil.getQuartzLogger().info("定时任务：订单分润-->查到记录[{}]条", len);
        if(len == 0){
            return;
        }
        for(OrderRent obj : rentList) {
            currIndex = JadyerUtil.leftPadUseZero(currIndex, String.valueOf(len).length());
            LogUtil.getQuartzLogger().info("定时任务：订单分润-->开始处理[{}-{}]条， rentId={}", len, currIndex, obj.getId());
            //計算上次分潤到今天為止，需要扣款的金額（注意首次和尾次的情况）
            long dayCounts;
            if(obj.getOtherMoney().compareTo(obj.getOrderMoney()) == 0){
                dayCounts = DateUtil.getDistanceDay(hhtcHelper.convertToDate(obj.getOrderFromDate(), 700), new Date());
            }else{
                dayCounts = DateUtil.getDistanceDay(obj.getUpdateTime(), new Date());
            }
            BigDecimal money = obj.getPerMoney().multiply(new BigDecimal(dayCounts));
            if(money.compareTo(obj.getOtherMoney()) == 1){
                money = obj.getOtherMoney();
            }
            LogUtil.getQuartzLogger().info("定时任务：订单分润-->开始处理[{}-{}]条，待分润金额=[{}]", len, currIndex, money.toString());
            //分潤
            this.doRent(obj.getOrderType(), obj.getGoodsOpenid(), money, communityService.get(obj.getCommunityId()));
            //更新剩餘金額
            obj.setOtherMoney(obj.getOtherMoney().subtract(money));
            orderRentRepository.saveAndFlush(obj);
            LogUtil.getQuartzLogger().info("定时任务：订单分润-->处理完毕[{}-{}]条", len, currIndex);
            currIndex = String.valueOf(Integer.parseInt(currIndex) + 1);
        }
    }
}