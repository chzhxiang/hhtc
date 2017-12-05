package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.OrderInoutRepository;
import com.jadyer.seed.mpp.web.repository.UserFundsRepository;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/21 20:55.
 */
@Service
public class OrderInoutService {
    //罚金金额：10.00元
    private static BigDecimal moneyfine = new BigDecimal(10);
    //罚金补贴给平台的比例：40%
    private static final BigDecimal rentRatioPlatform = new BigDecimal(0.4);
    //罚金补贴给车位主的比例：60%
    private static final BigDecimal rentRatioCarparker = new BigDecimal(0.6);
    @Value("${hhtc.publishTime.day}")
    private int timeDay;
    @Value("${hhtc.publishTime.night}")
    private int timeNight;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private OrderInforService orderInforService;
    @Resource
    private CommunityService communityService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private UserFundsRepository userFundsRepository;
    @Resource
    private UserFundsFlowService userFundsFlowService;
    @Resource
    private OrderInoutRepository orderInoutRepository;


    /**
     *TOKGO  车牌车辆进入检测
     * @param device 1--进场，2--出场
     * */
    public String CheckCarNumber(String carNumber,CommunityDevice device){
        long nowdate = new Date().getTime();
        //查找 该车牌订单
        List<OrderInfor> orderInfors  = orderInforService.Get(carNumber,device.getCommunityId());
        //TODO  超时补款的临时开门权限 检测
        if (orderInfors.size() == 0)
            return "进出无效通知：非平台下单车主";
        OrderInfor order =null;
        for (OrderInfor orderInfor :orderInfors){
            if (nowdate>=orderInfor.getTimeStartCalculate() && nowdate <= orderInfor.getTimeEndCalculate()) {
                order = orderInfor;
                break;
            }
        }
        //检测车辆进出状态  0--车在外面  1--车在里面
        if(device.getType() == 1){
            if (order.getInoutStatus() == 1)
                return  "入场重复通知：车主已经入场了";
            else
                return "";

        }else{
            if (order.getInoutStatus() == 0)
                return  "出场重复通知：车主已经出场了";
            else
                return "";
        }

//        if(type == 1){
//            //后半夜入场及当天
//            String yestoday = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyyMMdd");
//            List<Integer> fromDateList = Arrays.asList(Integer.parseInt(yestoday), Integer.parseInt(DateUtil.getCurrentDate()));
//            List<OrderInout> inoutList = orderInoutRepository.findByCommunityIdAndCarNumberAndFromDateInOrderByFromDateAscFromTimeAsc(device.getCommunityId(), carNumber, fromDateList);
//            if(null==inoutList || inoutList.isEmpty()) {
//                respMsg = "入场无效通知：非平台下单车主";
//            }else{
//                OrderInout inout = this.findMatchInout(inoutList, 1);
//                if(null == inout){
//                    respMsg = "不能入场：今日订单均已完成";
//                }else if(new Date().compareTo(hhtcHelper.convertToDate(inout.getFromDate(), inout.getFromTime())) < 0){
//                    respMsg = "不能入场：订单起始时间未到";
//                }else if(new Date().compareTo(inout.getAllowLatestOutDate()) >= 0){
//                    respMsg = "不能入场：超过订单当日截止日";
//                }else if(null!=inout.getInTime() && null==inout.getOutTime() && inout.getInTime().getTime()!=dtime.getTime()){
//                    respMsg = "入场重复通知：车主已经入场了";
//                    //}else if(null!=inout.getInTime() && null!=inout.getOutTime()){
//                    //    //支持多次入场出场，故注释
//                    //    respMsg = "无效通知：车主已离场";
////                }else if(userFundsService.depositIsenough(inout.getOpenid(), device.getCommunityId()).get("isenough").equals("0")){
////                    respMsg = "不能入场：车主押金不足";
//                }else{
//                    communityDeviceFlow.setScanAllowOpen(1);
//                    communityDeviceFlow.setOpenTime(new Date());
//                    if(hhtcHelper.openDoor(device.getSerialnoRelays(), device.getRelaysDoorid())){
//                        respMsg = "in success";
//                        communityDeviceFlow.setOpenResult(1);
//                        inout.setInTime(new Date());
//                        inout.setOutTime(null);
//                        orderInoutRepository.saveAndFlush(inout);
//                    }else{
//                        respMsg = "开闸失败（入口）";
//                        //TODO 开闸失败需要通知运营
//                    }
//                }
//            }
//        }else{
//            //前半夜出场及当天
//            String tomorrow = DateFormatUtils.format(DateUtils.addDays(new Date(), 1), "yyyyMMdd");
//            List<Integer> endDateList = Arrays.asList(Integer.parseInt(tomorrow), Integer.parseInt(DateUtil.getCurrentDate()));
//            List<OrderInout> inoutList = orderInoutRepository.findByCommunityIdAndCarNumberAndEndDateInOrderByFromDateAscFromTimeAsc(device.getCommunityId(), carNumber, endDateList);
//            if(null==inoutList || inoutList.isEmpty()) {
//                respMsg = "出场无效通知：非平台下单车主";
//            }else{
//                OrderInout inout = this.findMatchInout(inoutList, 2);
//                if(null == inout) {
//                    respMsg = "不能出场：今日订单均已完成";
//                }else if(null==inout.getInTime()){
//                    respMsg = "出场无效通知：车主未入场";
//                }else if(null!=inout.getInTime() && null!=inout.getOutTime()){
//                    respMsg = "出场重复通知：车主已经出场了";
//                }else if(new Date().compareTo(inout.getAllowLatestOutDate()) > 0){
//                    respMsg = "不能出场：超过本次允许离场时间";
//                }else{
//                    communityDeviceFlow.setScanAllowOpen(1);
//                    communityDeviceFlow.setOpenTime(new Date());
//                    if(hhtcHelper.openDoor(device.getSerialnoRelays(), device.getRelaysDoorid())){
//                        respMsg = "out success";
//                        communityDeviceFlow.setOpenResult(1);
//                        //订单生命周期已结束
////                        OrderInfo order = orderService.getByOrderNo(inout.getOrderNo());
////                        Date orderEndDate = hhtcHelper.convertToDate(hhtcHelper.calcOrderEndDate(order), order.getOpenEndTime());
////                        if(inout.getCardNo().endsWith(JadyerUtil.leftPadUseZero(inout.getMaxIndex()+"", 3)) && orderEndDate.compareTo(new Date())<1){
////                            order.setOrderStatus(99);
////                            orderService.upsert(order);
////                            //更新车位的使用状态
////                            if(orderService.countByGoodsIdAndOrderTypeInAndOrderStatusIn(order.getGoodsId(), Arrays.asList(1, 2), Arrays.asList(2, 9)) == 0){
////                                goodsService.updateStatus(order.getGoodsId(), 1, 2);
////                            }
////                        }
////                        inout.setOutTime(new Date());
////                        orderInoutRepository.saveAndFlush(inout);
////                        weixinTemplateMsgAsync.sendForCarownerOut(inout, order);
//                    }else{
//                        respMsg = "开闸失败（出口）";
//                        //TODO 开闸失败需要通知运营
//                    }
//                }
//            }
//        }
    }




    /**
     * 初始化订单出入明细及停车卡
     * @param goodsOpenid 车位主openid
     * @param firstInTime 首次駛入的時間（僅供需要初始化首次駛入信息時，傳值）
     */
    @Transactional(rollbackFor=Exception.class)
    public void initInout(OrderInfo order, String goodsOpenid, Date firstInTime) {
        if(order.getOrderType()!=1 && order.getOrderType()!=2){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "订单类型不正确[" + order.getOrderType() + "]");
        }
        String[] fromDates = order.getOpenFromDates().split("-");
        int len = fromDates.length;
        String currIndex = "1";
        for(String obj : fromDates){
            int endDate = Integer.parseInt(obj);
            if(order.getOpenType()==3 || (2==order.getOpenType() && order.getOpenEndTime()<order.getOpenFromTime())){
                try {
                    endDate = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(endDate+"", "yyyyMMdd"), 1), "yyyyMMdd"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            OrderInout inout = new OrderInout();
            inout.setCommunityId(order.getCommunityId());
            inout.setMaxIndex(len);
            inout.setCardNo(order.getOutTradeNo() + JadyerUtil.leftPadUseZero(currIndex, 3));
            inout.setOrderNo(order.getOutTradeNo());
            inout.setCarNumber(order.getCarNumber());
            inout.setFromDate(Integer.parseInt(obj));
            inout.setEndDate(endDate);
            inout.setFromTime(order.getOpenFromTime());
            inout.setEndTime(order.getOpenEndTime());
            inout.setAllowLatestOutDate(DateUtils.addMinutes(hhtcHelper.convertToDate(inout.getEndDate(), inout.getEndTime()), 5));
            inout.setOpenid(order.getOpenid());
            inout.setGoodsOpenid(goodsOpenid);
            if(currIndex.equals("1") && firstInTime!=null){
                //后台調配訂單場景下：同步首次入場的停車卡信息（如果出問題的話，車主無法出場，那麼就多半是這裡新生成的inout中的cardNo不是車主入場時的cardNo）
                inout.setInTime(firstInTime);
            }
            orderInoutRepository.saveAndFlush(inout);
            currIndex = String.valueOf(Integer.parseInt(currIndex) + 1);
        }
    }






    @Transactional(rollbackFor=Exception.class)
    public void settle() {
        List<OrderInout> inoutList = orderInoutRepository.findByEndDateAndInTimeNotNullAndOutTimeNull(Integer.parseInt(DateUtil.getCurrentDate()));
        String currIndex = "1";
        int len = inoutList.size();
        if(len == 0){
            return;
        }
        LogUtil.getQuartzLogger().info("定时任务：订单超时结算-->查到记录[{}]条", len);
        for(OrderInout obj : inoutList){
            currIndex = JadyerUtil.leftPadUseZero(currIndex, String.valueOf(len).length());
            LogUtil.getQuartzLogger().info("定时任务：订单超时结算-->开始处理[{}-{}]条， inoutId={}", len, currIndex, obj.getId());
            Date currdate = new Date();
            Date edate = hhtcHelper.convertToDate(obj.getEndDate(), obj.getEndTime());
            //不是最后一个卡号（不是最后一天），并且是租的全天的，那就看看有没有连着的下一个卡号（天数连着的）
            if(!obj.getCardNo().endsWith(JadyerUtil.leftPadUseZero(obj.getMaxIndex()+"", 3)) && obj.getFromTime()==obj.getEndTime() && (obj.getFromTime()==this.timeDay || obj.getFromTime()==this.timeNight)){
                String nextCardNo = obj.getOrderNo() + JadyerUtil.leftPadUseZero((Integer.parseInt(obj.getCardNo().substring(obj.getCardNo().length()-3)) + 1)+"", 3);
                OrderInout oi = orderInoutRepository.findByCardNo(nextCardNo);
                if(null!=oi && null!=oi.getId() && oi.getId()>0 && oi.getFromDate()==obj.getEndDate()){
                    LogUtil.getQuartzLogger().info("定时任务：订单超时结算-->[{}-{}]条记录属于长租且存在下一个连续天，故不需超时提醒和处罚", len, currIndex);
                    //本天停车若超时，那就更新本天的出场时间和下一天的入场时间（多加了一层校验：若任务执行期间，车主出场，那就不用更新了）
                    if(currdate.compareTo(edate)>=0 && null==orderInoutRepository.findOne(obj.getId()).getOutTime()){
                        try {
                            Date dtime = DateUtils.parseDate("20000101000000", "yyyyMMddHHmmss");
                            obj.setOutTime(dtime);
                            oi.setInTime(dtime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        orderInoutRepository.saveAndFlush(obj);
                        orderInoutRepository.saveAndFlush(oi);
                    }
                    LogUtil.getQuartzLogger().info("定时任务：订单超时结算-->处理完毕[{}-{}]条", len, currIndex);
                    continue;
                }
            }
            //处理业务
            OrderInfo order = null;
            String phone = fansService.getByOpenid(obj.getOpenid()).getPhoneNo();
            //提前30分钟通知车主（提前30分钟到27分钟的3分钟以內）
            if(currdate.compareTo(DateUtils.addMinutes(edate, -30))>=0 && currdate.compareTo(DateUtils.addMinutes(edate, -27))<=0){
                //模版CODE: SMS_86630115（車主）
                //模版内容: 尊敬的手机尾号为${phone}的用户：您于${time}预约的${carpark}车位还有30分钟的停车时间，请您在到点前及时将爱车腾挪离场，方便下一位车主用户入场停车。
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("phone", phone.substring(7, 11));
                paramMap.put("time", DateFormatUtils.format(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                paramMap.put("carpark", order.getCarParkNumber());
                hhtcHelper.sendSms(phone, "SMS_86630115", paramMap);
            }
            //提前10分钟通知车主（提前10分钟到7分钟的3分钟以內）
            if(currdate.compareTo(DateUtils.addMinutes(edate, -10))>=0 && currdate.compareTo(DateUtils.addMinutes(edate, -7))<=0){
                //模版CODE: SMS_86730100
                //模版内容: 尊敬的手机尾号为${phone}的用户：您于${time}预约的${carpark}车位还有10分钟的停车时间，请您及时将爱车腾挪离场，否则车位到点后每半小时将处罚金10元。
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("phone", phone.substring(7, 11));
                paramMap.put("time", DateFormatUtils.format(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                paramMap.put("carpark", order.getCarParkNumber());
                hhtcHelper.sendSms(phone, "SMS_86730100", paramMap);
            }
            //正好到点时通知物业（到点及之后的3分钟以內）
            if(currdate.compareTo(edate)>=0 && currdate.compareTo(DateUtils.addMinutes(edate, 3))<=0){
                //模版CODE: SMS_86625124
                //模版内容: ${carpark}停车已超时，请联系${phone}用户，催促其将爱车腾挪离场。
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("carpark", order.getCarParkNumber());
                paramMap.put("phone", phone);
                hhtcHelper.sendSms(communityService.get(order.getCommunityId()).getLinkTel(), "SMS_86625124", paramMap);
            }
            UserFunds funds = userFundsService.get(obj.getOpenid());
            if(funds.getMoneyBase().compareTo(new BigDecimal(0)) > 0){
                //超时的第6分钟开始，罚一次钱（到点后10：00再余出5min给车主容错，但这5分钟是不提示，默默的容错，不扣费，10：05分前离场就放过，10：06开始给车主与车位主发消息告知车位还未离场）
                if(null==obj.getLastDeductTime() && currdate.compareTo(DateUtils.addMinutes(edate, 6))>=0){
                    //发短信通知车主与车位主
                    //模版CODE: SMS_89000047
                    //模版内容: 尊敬的手机尾号为${phone}的用户：您已超时停车，平台开始扣除您10元押金作为您超时停车的罚金若您的爱车一直占用车位，则系统每半小时都会扣除您10元押金作为您超时停车的罚金，系统工作人员也会同步处理您的超时停车的事宜。
                    Map<String, String> paramMap = new HashMap<>();
                    paramMap.put("phone", phone.substring(7, 11));
                    hhtcHelper.sendSms(phone, "SMS_89000047", paramMap);
                    //模版CODE: SMS_89040046
                    //模版内容: 尊敬的手机尾号为${phone}的用户：你的车位被占用，平台已催促车主尽快离场，并派出工作人员处理此事。
                    phone = fansService.getByOpenid(obj.getGoodsOpenid()).getPhoneNo();
                    paramMap = new HashMap<>();
                    paramMap.put("phone", phone.substring(7, 11));
                    hhtcHelper.sendSms(phone, "SMS_89040046", paramMap);
                    //罚钱并更新扣款时间
                    obj.setLastDeductTime(currdate);
                    obj.setNextDeductStartTime(DateUtils.addMinutes(edate, 60));
                    obj.setNextDeductEndTime(DateUtils.addMinutes(edate, 90));
                    obj.setAllowLatestOutDate(obj.getNextDeductStartTime());
                    orderInoutRepository.saveAndFlush(obj);
                }
                //超时的第61分钟开始，每半个小时，罚一次钱，直至押金罚完（10：06-11：00扣10块，接下来每半小时系统做判断车主是否离场，若仍未离场（11：01）每半小时扣10元，并通知两边，11：31分再扣钱（此时不需要通知），扣完押金为止）
                if(null!=obj.getLastDeductTime() && currdate.compareTo(obj.getNextDeductStartTime())>=0 && currdate.compareTo(obj.getNextDeductEndTime())<0){
                    //罚钱并更新扣款时间
                    //TODO  原工程的超时补款 可能要删除
//                    obj.setLastDeductMoney(this.deduct(funds, obj.getGoodsOpenid()));
                    obj.setLastDeductTime(currdate);
                    obj.setNextDeductStartTime(obj.getNextDeductEndTime());
                    obj.setNextDeductEndTime(DateUtils.addMinutes(obj.getNextDeductEndTime(), 30));
                    obj.setAllowLatestOutDate(obj.getNextDeductStartTime());
                    orderInoutRepository.saveAndFlush(obj);
                }
            }
            LogUtil.getQuartzLogger().info("定时任务：订单超时结算-->处理完毕[{}-{}]条", len, currIndex);
            currIndex = String.valueOf(Integer.parseInt(currIndex) + 1);
        }
    }
}