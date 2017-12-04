package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.IPUtil;
import com.jadyer.seed.comm.util.MoneyUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayRefundReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayRefundRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.redpack.WeixinRedpackSendReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.redpack.WeixinRedpackSendRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.GoodsNeedRepository;
import com.jadyer.seed.mpp.web.repository.OrderRepository;
import com.jadyer.seed.mpp.web.repository.RedpackInfoRepository;
import com.jadyer.seed.mpp.web.repository.RefundApplyRepository;
import com.jadyer.seed.mpp.web.repository.RefundRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/5 16:47.
 */
@Service
public class RefundApplyService {
    @Value("${hhtc.p12savepath}")
    private String p12FilePath;
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Value("${hhtc.portalUrl.center}")
    private String portalCenterUrl;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private AuditService auditService;
    @Resource
    private RefundService refundService;
    @Resource
    private RedpackService redpackService;
    @Resource
    private OrderRepository orderRepository;
    @Resource
    private RefundRepository refundRepository;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private GoodsNeedRepository goodsNeedRepository;
    @Resource
    private UserFundsFlowService userFundsFlowService;
    @Resource
    private RefundApplyRepository refundApplyRepository;
    @Resource
    private RedpackInfoRepository redpackInfoRepository;

    /**
     * 匹配可供指定金额退款的订单
     * <p>
     *     这里不用判断匹配到的订单的合计可退款金额是否足够，不足时会发红包
     * </p>
     * @param money 退款金额
     * @return 返回可退款的订单ids（多个以`分隔）
     */
    private String match(String openid, BigDecimal money) {
        List<OrderInfo> orderIdlist = new ArrayList<>();
        BigDecimal fullMoney = new BigDecimal(0);
        int totalPages;
        int pageNo = 0;
        do {
            Pageable pageable = new PageRequest(pageNo, 5, new Sort(Sort.Direction.ASC, "id"));
            Condition<OrderInfo> spec = Condition.<OrderInfo>and().eq("openid", openid).gt("canRefundMoney", 0);
            Page<OrderInfo> page = orderRepository.findAll(spec, pageable);
            for (OrderInfo order : page.getContent()) {
                orderIdlist.add(order);
                fullMoney = fullMoney.add(new BigDecimal(MoneyUtil.fenToYuan(order.getCanRefundMoney() + "")));
                if (fullMoney.compareTo(money) >= 0) {
                    break;
                }
            }
            totalPages = page.getTotalPages();
            pageNo++;
        } while (fullMoney.compareTo(money)==-1 && totalPages > pageNo);
        String ids = "";
        for(OrderInfo order : orderIdlist){
            ids = ids + "`" + order.getId();
        }
        return StringUtils.isBlank(ids) ? "" : ids.substring(1);
    }


    /**
     * 判断是否允许发起退款（押金）申请
     */
    private void allowApply(String openid){
        if(goodsNeedRepository.countByOpenidAndStatus(openid, 1) > 0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "退款失败：存在匹配中的需求");
        }
//        if(goodsRepository.countByOpenidAndIsUsedIn(openid, Arrays.asList(1, 2)) > 0){
//            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "退款失败：存在发布中或已使用的车位");
//        }
        if(orderRepository.countByOpenidAndOrderTypeInAndOrderStatusIn(openid, Arrays.asList(1, 2), Arrays.asList(0, 1, 2)) > 0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "退款失败：存在未完成订单");
        }
        if(orderRepository.countByOpenidAndOrderTypeInAndOrderStatusIn(openid, Arrays.asList(1, 2), Collections.singletonList(9)) > 0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "退款失败：存在已转租订单");
        }
    }


    /**
     * 退款或提现申请
     * @param applyType 1--退款（押金），2--提现（余额）
     * @param money     提现金额（仅applyType=2时有值）
     */
    @Transactional(rollbackFor=Exception.class)
    public void apply(int applyType, BigDecimal money, HttpServletRequest request) {
        String appid = hhtcHelper.getWxAppidFromSession(request.getSession());
        String openid = hhtcHelper.getWxOpenidFromSession(request.getSession());
        List<RefundApply> applyList = refundApplyRepository.findByOpenidAndPayStatusIn(openid, Arrays.asList(0, 1));
        for(RefundApply obj : applyList){
            if(obj.getAuditStatus() != 3){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "最多同时提交一笔");
            }
        }
        if(applyType == 1){
            this.allowApply(openid);
            UserFunds funds = userFundsService.get(openid);
            if(null==funds || funds.getMoneyBase().compareTo(new BigDecimal(0))<1){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "没有可退款押金");
            }
            money = funds.getMoneyBase();
            if(money.compareTo(new BigDecimal(1)) < 0){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "退款最少一元");
            }
        }
        if(applyType == 2){
            if(money.compareTo(new BigDecimal(1)) < 0){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "提现最低一元");
            }
            UserFunds funds = userFundsService.get(openid);
            if(null==funds || money.compareTo(funds.getMoneyBalance())>0){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "可提现余额不足");
            }
        }
        RefundApply apply = new RefundApply();
        apply.setAppid(appid);
        apply.setClientIp(IPUtil.getClientIP(request));
        apply.setOpenid(openid);
        apply.setOrderIds(this.match(openid, money));
        apply.setRefundFee(Long.parseLong(MoneyUtil.yuanToFen(money.toString())));
        apply.setApplyType(applyType);
        apply.setPayStatus(0);
        apply.setAuditStatus(1);
        refundApplyRepository.saveAndFlush(apply);
        if(applyType == 1){
            //模版CODE: SMS_86680145
            //模版内容: 尊敬的手机尾号为${phone}的用户，您于${time}申请的押金退回业务已受理成功，平台审核通过后即可将押金退回您原支付账户。
            String phone = fansService.getByOpenid(openid).getPhoneNo();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("phone", phone.substring(7, 11));
            paramMap.put("time", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            hhtcHelper.sendSms(phone, "SMS_86680145", paramMap);
            /*
            {{first.DATA}}
            申请人：{{keyword1.DATA}}
            申请时间：{{keyword2.DATA}}
            押金退还金额：{{keyword3.DATA}}
            {{remark.DATA}}

            您好:
            申请人：张三
            申请时间：20170801 22:00
            押金退还金额：199元
            您的退还押金申请已提交，请耐心等待平台审核。
            */
            WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
            dataItem.put("first", new WeixinTemplateMsg.DItem("尊敬的用户，您好："));
            dataItem.put("keyword1", new WeixinTemplateMsg.DItem("手机尾号为" + phone.substring(7, 11)));
            dataItem.put("keyword2", new WeixinTemplateMsg.DItem(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")));
            dataItem.put("keyword3", new WeixinTemplateMsg.DItem(money.toString() + "元"));
            dataItem.put("remark", new WeixinTemplateMsg.DItem("您的退还押金申请已提交，请耐心等待平台审核。"));
            String url = this.hhtcContextPath + this.portalCenterUrl;
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+apply.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+apply.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
            WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
            templateMsg.setTemplate_id("icvejpeS8si4HTT8mivfGgg_xn62EjH4bRobSTPDx2U");
            templateMsg.setUrl(url);
            templateMsg.setTouser(apply.getOpenid());
            templateMsg.setData(dataItem);
            WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(apply.getAppid()), templateMsg);
        }
        if(applyType == 2){
            //模版CODE: SMS_86680144
            //模版内容: 尊敬的手机尾号为${phone}的用户，您于${time}申请的${money}元的提现业务已受理成功，平台审核通过后即可向您微信账户转账汇款。
            String phone = fansService.getByOpenid(openid).getPhoneNo();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("phone", phone.substring(7, 11));
            paramMap.put("time", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            paramMap.put("money", money.toString());
            hhtcHelper.sendSms(phone, "SMS_86680144", paramMap);
            /*
            {{first.DATA}}
            申请人：{{keyword1.DATA}}
            申请时间：{{keyword2.DATA}}
            提现金额：{{keyword3.DATA}}
            {{remark.DATA}}

            您好:
            申请人：张三
            申请时间：20170801 22:00
            提现金额：50元
            您的提现申请已提交，请耐心等待平台审核，预计2个工作日内到账
            */
            WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
            dataItem.put("first", new WeixinTemplateMsg.DItem("尊敬的用户，您好："));
            dataItem.put("keyword1", new WeixinTemplateMsg.DItem("手机尾号为" + phone.substring(7, 11)));
            dataItem.put("keyword2", new WeixinTemplateMsg.DItem(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")));
            dataItem.put("keyword3", new WeixinTemplateMsg.DItem(money.toString() + "元"));
            dataItem.put("remark", new WeixinTemplateMsg.DItem("您的提现申请已提交，平台审核通过后即可向您微信账户转账汇款。"));
            String url = this.hhtcContextPath + this.portalCenterUrl;
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+apply.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+apply.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
            WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
            templateMsg.setTemplate_id("cxPmkCSXAs2rXH4_xork3lbIugMN87C2mnyavJdOLu8");
            templateMsg.setUrl(url);
            templateMsg.setTouser(apply.getOpenid());
            templateMsg.setData(dataItem);
            WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(apply.getAppid()), templateMsg);
        }
    }


    /**
     * 退款列表
     */
    public Page<RefundApply> listViaPage(MppUserInfo userInfo, String pageNo){
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以查看退款列表");
        }
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        return refundApplyRepository.findAll(pageable);
    }


    /**
     * 退款审核列表
     */
    public Page<FansInforAudit> listTaskViaPage(MppUserInfo userInfo, String pageNo){
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以查看退款审核列表");
        }
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        Condition<FansInforAudit> spec = Condition.<FansInforAudit>or().eq("type", Constants.AUDTI_TEPY_BALANCE)
                .eq("type", Constants.AUDTI_TEPY_DEPOSIT);
        //执行
        Page<FansInforAudit> page = auditService.getpage(spec, pageable);
        List<FansInforAudit> list = page.getContent();
        for(FansInforAudit obj : list){
            MppFansInfor fans = fansService.getByOpenid(obj.getOpenid());
            //检测住址是否验证 如果住址未审核通过 不给与显示
            if (fans.getInfor_state().charAt(Constants.INFOR_STATE_COMMUNITY_BIT)!='1'){
                list.remove(obj);
                continue;
            }
            obj.setNickname(fans.getNickname());
            obj.setHeadimgurl(fans.getHeadimgurl());
            obj.setPhone(fans.getPhoneNo());
            obj.setCommunity(fans.getCommunityName()+fans.getHouseNumber());
        }
        return page;
    }


    /**
     * 退款或提现审核
     */
    @Transactional(rollbackFor=Exception.class)
    public void audit(long id, int auditStatus) {
        RefundApply apply = refundApplyRepository.findOne(id);
        apply.setAuditTime(new Date());
        apply.setAuditStatus(auditStatus);
        refundApplyRepository.saveAndFlush(apply);
        if(auditStatus == 1){
            if(apply.getApplyType() == 1){
                //模版CODE: SMS_86520128
                //模版内容: 尊敬的手机尾号为${phone}的用户，您申请押金退回已通过平台审核，您交付平台的押金已退回您原支付账户，预计1~7个工作日到账，请注意查收。
                String phone = fansService.getByOpenid(apply.getOpenid()).getPhoneNo();
//                Map<String, String> paramMap = new HashMap<>();
//                paramMap.put("phone", phone.substring(7, 11));
//                hhtcHelper.sendSms(phone, "SMS_86520128", paramMap);
            }
        }
        if(auditStatus == 2){
            /*
            {{first.DATA}}
            提现金额：{{keyword1.DATA}}
            失败原因：{{keyword2.DATA}}
            {{remark.DATA}}

            您申请的提现业务未通过审核
            提现金额：30元
            失败原因：仍有未完成的订单
            请您做出相应调整后，再申请提现。
            */
//            WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
//            dataItem.put("first", new WeixinTemplateMsg.DItem("您申请的" + (apply.getApplyType()==1?"退款":"提现") + "业务未通过审核"));
//            dataItem.put("keyword1", new WeixinTemplateMsg.DItem(MoneyUtil.fenToYuan(apply.getRefundFee()+"") + "元"));
//            dataItem.put("keyword2", new WeixinTemplateMsg.DItem("仍有未完成的订单或系统交易忙"));
//            dataItem.put("remark", new WeixinTemplateMsg.DItem("请您做出相应调整后，再申请提现。"));
//            String url = this.hhtcContextPath + this.portalCenterUrl;
//            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+apply.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+apply.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
//            WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
//            templateMsg.setTemplate_id("ZhGiBnC7ugrDs-raCC0E1kJ2aaRl_i1by8bwAkBIGtA");
//            templateMsg.setUrl(url);
//            templateMsg.setTouser(apply.getOpenid());
//            templateMsg.setData(dataItem);
//            WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(apply.getAppid()), templateMsg);
        }
    }


    /**
     * 更新支付状态
     * @param payStatus 支付状态：0--未支付，1--支付中，2--支付成功，3--支付部分失败，4--支付全部失败
     */
    @Transactional(rollbackFor=Exception.class)
    public void updatePayStatus(long id, int payStatus) {
        refundApplyRepository.updatePayStatus(id, payStatus);
    }


    /**
     * 退款申请详情
     */
    public RefundApply get(long id) {
        return refundApplyRepository.findOne(id);
    }


    /**
     * 定时任务：获取需要处理的申请列表
     * @param payStatus 0--需要发起退款或提现，1--需要更新支付状态
     */
    public List<RefundApply> getTaskList(int payStatus) {
        return refundApplyRepository.findByAuditStatusAndPayStatus(2, payStatus);
    }


    /**
     * 微信退款或提现
     */
    @Transactional(rollbackFor=Exception.class)
    public void refund(RefundApply obj) {
        /*
         * 退款或提现金额转入冻结金额
         */
        //需要退款的总金额
        BigDecimal money = new BigDecimal(MoneyUtil.fenToYuan(obj.getRefundFee()+""));
        //1--退款（押金）
        if(obj.getApplyType() == 1){
            UserFunds funds = userFundsService.subtractMoneyBaseForFans(obj.getOpenid(), money);
            UserFundsFlow fundsFlow = new UserFundsFlow();
            fundsFlow.setOpenid(obj.getOpenid());
            fundsFlow.setMoney(money);
            fundsFlow.setInOutDesc("退款时扣减租金");
            fundsFlow.setInOutType(3);
            userFundsFlowService.upsert(fundsFlow);
        }
        //2--提现（余额）
        if(obj.getApplyType() == 2){
            UserFunds funds = userFundsService.subtractMoneyBalanceForFans(obj.getOpenid(), money,"","");
            UserFundsFlow fundsFlow = new UserFundsFlow();
            fundsFlow.setOpenid(obj.getOpenid());
            fundsFlow.setMoney(money);
            fundsFlow.setInOutDesc("提现时扣减余额");
            fundsFlow.setInOutType(4);
            userFundsFlowService.upsert(fundsFlow);
        }
        /*
        * TODO 退款支付 好好研究
         * 开始退款或提现（向腾讯发起支付交易）
         */
        BigDecimal hasRefundMoney = new BigDecimal(0);
        if(StringUtils.isNotBlank(obj.getOrderIds())){
            for(String id : obj.getOrderIds().split("`")){
                //校验已处理的退款总金额小于需要退款的总金额
                if(hasRefundMoney.compareTo(money) == -1){
                    OrderInfo order = orderRepository.findOne(Long.parseLong(id));
                    RefundInfo refundInfo = new RefundInfo();
                    //剩余需要退款的总金额
                    BigDecimal shengMoney = money.subtract(hasRefundMoney);
                    //计算退款金额以及订单剩余可退款金额
                    if(shengMoney.compareTo(order.getCanRefundMoney()) == -1){
                        refundInfo.setRefundFee(Long.parseLong(MoneyUtil.yuanToFen(shengMoney.toString())));
                        order.setCanRefundMoney(order.getCanRefundMoney().subtract(shengMoney));
                    }else{
                        refundInfo.setRefundFee(Long.parseLong(MoneyUtil.yuanToFen(order.getCanRefundMoney()+"")));
                        order.setCanRefundMoney(new BigDecimal(0));
                    }
                    refundInfo.setRefundApplyId(obj.getId());
                    refundInfo.setRefundApplyType(obj.getApplyType());
                    refundInfo.setOpenid(obj.getOpenid());
                    refundInfo.setOrderId(order.getId());
                    refundInfo.setAppid(obj.getAppid());
                    refundInfo.setOutRefundNo(hhtcHelper.buildOrderNo());
                    refundInfo.setTotalFee(order.getTotalFee());
                    refundInfo.setRefundDesc("吼吼共享车位 - 退款");
                    refundInfo.setRefundStatus(0);
                    //refundInfo.setRefundAccout("REFUND_SOURCE_UNSETTLED_FUNDS");
                    refundInfo = refundRepository.saveAndFlush(refundInfo);
                    WeixinPayRefundReqData reqData = new WeixinPayRefundReqData();
                    reqData.setAppid(refundInfo.getAppid());
                    reqData.setMch_id(WeixinTokenHolder.getWeixinMchid(refundInfo.getAppid()));
                    reqData.setNonce_str(RandomStringUtils.randomNumeric(20));
                    reqData.setSign_type("MD5");
                    reqData.setTransaction_id(order.getTransactionId());
                    reqData.setOut_refund_no(refundInfo.getOutRefundNo());
                    reqData.setTotal_fee(refundInfo.getTotalFee()+"");
                    reqData.setRefund_fee(refundInfo.getRefundFee()+"");
                    reqData.setRefund_desc(refundInfo.getRefundDesc());
                    //reqData.setRefund_account(refundInfo.getRefundAccout());
                    WeixinPayRefundRespData respData = WeixinHelper.payRefund(reqData, this.p12FilePath);
                    refundInfo.setRefundId(respData.getRefund_id());
                    refundInfo.setRefundStatus(1);
                    refundRepository.saveAndFlush(refundInfo);
                    orderRepository.saveAndFlush(order);
                    hasRefundMoney = hasRefundMoney.add(new BigDecimal(MoneyUtil.fenToYuan(refundInfo.getRefundFee()+"")));
                }
            }
        }
        //订单上的钱不够，那就发红包
        if(hasRefundMoney.compareTo(money) == -1){
            RedpackInfo redpack = new RedpackInfo();
            redpack.setRefundApplyId(obj.getId());
            redpack.setRefundApplyType(obj.getApplyType());
            redpack.setAppid(obj.getAppid());
            redpack.setMchBillno(hhtcHelper.buildOrderNo());
            redpack.setReOpenid(obj.getOpenid());
            redpack.setTotalAmount(Long.parseLong(MoneyUtil.yuanToFen(money.subtract(hasRefundMoney).toString())));
            redpack.setStatus(0);
            redpack = redpackInfoRepository.saveAndFlush(redpack);
            WeixinRedpackSendReqData reqData = new WeixinRedpackSendReqData();
            reqData.setMch_id(WeixinTokenHolder.getWeixinMchid(redpack.getAppid()));
            reqData.setNonce_str(RandomStringUtils.randomNumeric(20));
            reqData.setWxappid(redpack.getAppid());
            reqData.setMch_billno(redpack.getMchBillno());
            reqData.setSend_name("吼吼共享车位");
            reqData.setRe_openid(redpack.getReOpenid());
            reqData.setTotal_amount(Integer.parseInt(redpack.getTotalAmount()+""));
            reqData.setTotal_num(1);
            reqData.setWishing("祝您天天开心！");
            reqData.setClient_ip(obj.getClientIp());
            reqData.setAct_name("fans-withdraw");
            reqData.setRemark("fans-withdraw-remark");
            if(reqData.getTotal_amount() > 20000){
                reqData.setScene_id("PRODUCT_5");
            }
            WeixinRedpackSendRespData respData = WeixinHelper.redpackSend(reqData, this.p12FilePath);
            redpack.setDetailId(respData.getSend_listid());
            redpack.setStatus(1);
            redpackInfoRepository.saveAndFlush(redpack);
        }
        //更新退款申请状态
        obj.setPayStatus(1);
        refundApplyRepository.saveAndFlush(obj);
    }


    /**
     * 微信退款或提现
     */
    @Transactional(rollbackFor=Exception.class)
    public void refundUpdatePayStatus(RefundApply obj) {
        if(obj.getPayStatus() == 0){
            return;
        }
        List<RefundInfo> refundList = refundService.getByRefundApplyId(obj.getId());
        List<RedpackInfo> redpackList = redpackService.getByRefundApplyId(obj.getId());
        //标记退款或发红包的交易状态，是否为腾讯处理中
        boolean processing = false;
        //总记录数
        int countAll = 0;
        //失败的数量
        int countFail = 0;
        //成功的数量
        int countSuccess = 0;
        for(RefundInfo refund : refundList){
            if(refund.getRefundStatus()==0 || refund.getRefundStatus()==1){
                processing = true;
                break;
            }
            if(refund.getRefundStatus() == 2){
                countSuccess++;
            }
            if(refund.getRefundStatus()==3 || refund.getRefundStatus()==4){
                countFail++;
            }
            countAll++;
        }
        if(!processing){
            for(RedpackInfo redpack : redpackList){
                if(redpack.getStatus()==0 || redpack.getStatus()==1 || redpack.getStatus()==2){
                    processing = true;
                    break;
                }
                if(redpack.getStatus() == 4){
                    countSuccess++;
                }
                if(redpack.getStatus()==3 || redpack.getStatus()==5 || redpack.getStatus()==6){
                    countFail++;
                }
                countAll++;
            }
        }
        if(processing){
            obj.setPayStatus(1);
        }else{
            if(countAll == countSuccess){
                obj.setPayStatus(2);
                if(obj.getApplyType() == 2){
                    //模版CODE: SMS_88430020
                    //模版内容: 尊敬的手机尾号为${phone}的用户：您好！您于${time}申请的提现${money}元，资金已经原路退回您的账户中，微信平台可能存在延时，具体到账时间以微信到账通知为准。有任何疑问请咨询电话${phoneNo}
                    String phone = fansService.getByOpenid(obj.getOpenid()).getPhoneNo();
                    Map<String, String> paramMap = new HashMap<>();
                    paramMap.put("phone", phone.substring(7, 11));
                    paramMap.put("time", DateFormatUtils.format(obj.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                    paramMap.put("money", MoneyUtil.fenToYuan(obj.getRefundFee()+""));
                    paramMap.put("phoneNo", "15023194640");
                    hhtcHelper.sendSms(phone, "SMS_88430020", paramMap);
                    /*
                    {{first.DATA}}
                    金额：{{keyword1.DATA}}
                    到账时间：{{keyword2.DATA}}
                    {{remark.DATA}}

                    您在嗨拼车的提现已经到账
                    金额：100元
                    到账时间：2016年11月20日 18:36
                    感谢您使用嗨拼车。
                    */
                    WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
                    dataItem.put("first", new WeixinTemplateMsg.DItem("您在吼吼共享停车的提现已经到账"));
                    dataItem.put("keyword1", new WeixinTemplateMsg.DItem(MoneyUtil.fenToYuan(obj.getRefundFee()+"") + "元"));
                    dataItem.put("keyword2", new WeixinTemplateMsg.DItem(DateFormatUtils.format(obj.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
                    dataItem.put("remark", new WeixinTemplateMsg.DItem("感谢您使用吼吼共享停车。"));
                    String url = this.hhtcContextPath + this.portalCenterUrl;
                    url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+obj.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+obj.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
                    WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
                    templateMsg.setTemplate_id("tqKMtSvWd3FXPh30Hzdy2Tp0uJ7dhjtVY1JRRULAivU");
                    templateMsg.setUrl(url);
                    templateMsg.setTouser(obj.getOpenid());
                    templateMsg.setData(dataItem);
                    WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(obj.getAppid()), templateMsg);
                }
            }else if(countAll == countFail){
                obj.setPayStatus(4);
            }else{
                obj.setPayStatus(3);
            }
        }
        refundApplyRepository.saveAndFlush(obj);
    }


    /**
     * 退款失败的逻辑处理
     * <ul>
     *     <li>包含红包接口：“发放失败”和“已退款”</li>
     *     <li>包含退款接口：“退款关闭”和“退款异常”</li>
     * </ul>
     * @param type 1--红包，2--退款
     */
    public void doFail(int type, RedpackInfo redpack, RefundInfo refund){
        String openid = "";
        int refundApplyType = 0;
        BigDecimal money = new BigDecimal(0);
        if(type == 1){
            openid = redpack.getReOpenid();
            refundApplyType = redpack.getRefundApplyType();
            money = money.add(new BigDecimal(MoneyUtil.fenToYuan(redpack.getRefundAmount()+"")));
        }
        if(type == 2){
            openid = refund.getOpenid();
            refundApplyType = refund.getRefundApplyType();
            money = money.add(new BigDecimal(MoneyUtil.fenToYuan(refund.getRefundFee()+"")));
        }
        //1--退款（押金），2--提现（余额）
        if(refundApplyType == 1){
            UserFunds funds = userFundsService.addMoneyBaseForFans(openid, money);
            UserFundsFlow fundsFlow = new UserFundsFlow();
            fundsFlow.setOpenid(openid);
            fundsFlow.setMoney(money);
            fundsFlow.setInOutDesc("退款失败时返还押金");
            fundsFlow.setInOutType(7);
            userFundsFlowService.upsert(fundsFlow);
            OrderInfo order = orderRepository.findOne(refund.getOrderId());
            order.setCanRefundMoney(order.getCanRefundMoney().add(money));
            orderRepository.saveAndFlush(order);
        }
        if(refundApplyType == 2){
            UserFunds funds = userFundsService.addMoneyBalanceForFans(openid, money,"","");
            UserFundsFlow fundsFlow = new UserFundsFlow();
            fundsFlow.setOpenid(openid);
            fundsFlow.setMoney(money);
            fundsFlow.setInOutDesc("提现失败时返还余额");
            fundsFlow.setInOutType(6);
            userFundsFlowService.upsert(fundsFlow);
        }
    }
}