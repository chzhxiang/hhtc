package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.redpack.WeixinRedpackGethbinfoReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.redpack.WeixinRedpackGethbinfoRespData;
import com.jadyer.seed.mpp.web.model.RedpackInfo;
import com.jadyer.seed.mpp.web.repository.RedpackInfoRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/5 20:05.
 */
@Service
public class RedpackService {
    @Value("${hhtc.p12savepath}")
    private String p12FilePath;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private RefundApplyService refundApplyService;
    @Resource
    private UserFundsFlowService userFundsFlowService;
    @Resource
    private RedpackInfoRepository redpackInfoRepository;

    public List<RedpackInfo> getByRefundApplyId(long refundApplyId){
        return redpackInfoRepository.findByRefundApplyId(refundApplyId);
    }


    /**
     * 获取状态为交易中的列表
     */
    public List<RedpackInfo> getPayingList(){
        return redpackInfoRepository.findByStatus(1);
    }


    /**
     * 向腾讯查询退款或红包的交易结果
     */
    @Transactional(rollbackFor=Exception.class)
    public void query(RedpackInfo redpackInfo) {
        WeixinRedpackGethbinfoReqData reqData = new WeixinRedpackGethbinfoReqData();
        reqData.setAppid(redpackInfo.getAppid());
        reqData.setMch_id(WeixinTokenHolder.getWeixinMchid(redpackInfo.getAppid()));
        reqData.setNonce_str(RandomStringUtils.randomNumeric(20));
        reqData.setMch_billno(redpackInfo.getMchBillno());
        reqData.setBill_type("MCHT");
        WeixinRedpackGethbinfoRespData respData = WeixinHelper.redpackGethbinfo(reqData, this.p12FilePath);
        if(!StringUtils.equals(redpackInfo.getDetailId(), respData.getDetail_id())){
            LogUtil.getLogger().error("错误：红包订单查询-->商户订单号[{}]-->返回的红包单号[{}]与当初请求时返回的单号[{}]不一致", redpackInfo.getMchBillno(), respData.getDetail_id(), redpackInfo.getDetailId());
            LogUtil.logToTask().error("错误：红包订单查询-->商户订单号[{}]-->返回的红包单号[{}]与当初请求时返回的单号[{}]不一致", redpackInfo.getMchBillno(), respData.getDetail_id(), redpackInfo.getDetailId());
        }
        if(respData.getTotal_num() != 1){
            LogUtil.getLogger().error("错误：红包订单查询-->商户订单号[{}]-->返回的红包个数[{}]与当初请求时的个数[{}]不一致", redpackInfo.getMchBillno(), respData.getTotal_num(), 1);
            LogUtil.logToTask().error("错误：红包订单查询-->商户订单号[{}]-->返回的红包个数[{}]与当初请求时的个数[{}]不一致", redpackInfo.getMchBillno(), respData.getTotal_num(), 1);
        }
        if(respData.getTotal_amount() != redpackInfo.getTotalAmount()){
            LogUtil.getLogger().error("错误：红包订单查询-->商户订单号[{}]-->返回的红包总金额[{}]与当初请求时的金额[{}]不一致", redpackInfo.getMchBillno(), respData.getTotal_amount(), redpackInfo.getTotalAmount());
            LogUtil.logToTask().error("错误：红包订单查询-->商户订单号[{}]-->返回的红包总金额[{}]与当初请求时的金额[{}]不一致", redpackInfo.getMchBillno(), respData.getTotal_amount(), redpackInfo.getTotalAmount());
        }
        switch(respData.getStatus()){
            case "SENDING"   : redpackInfo.setStatus(1); break;
            case "SENT"      : redpackInfo.setStatus(2); break;
            case "FAILED"    : redpackInfo.setStatus(3); refundApplyService.doFail(1, redpackInfo, null); break;
            case "RECEIVED"  : redpackInfo.setStatus(4); break;
            case "RFUND_ING" : redpackInfo.setStatus(5); break;
            case "REFUND"    : redpackInfo.setStatus(6); refundApplyService.doFail(1, redpackInfo, null); break;
        }
        redpackInfo.setReason(respData.getReason());
        redpackInfo.setSendTime(respData.getSend_time());
        redpackInfo.setRefundTime(respData.getRefund_time());
        redpackInfo.setRefundAmount(respData.getRefund_amount());
        redpackInfoRepository.saveAndFlush(redpackInfo);
    }
}