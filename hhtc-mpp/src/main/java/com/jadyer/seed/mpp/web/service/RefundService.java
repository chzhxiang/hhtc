package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayRefundqueryReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayRefundqueryRespData;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.RefundInfo;
import com.jadyer.seed.mpp.web.repository.RefundRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 16:11.
 */
@Service
public class RefundService {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private RefundRepository refundRepository;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private RefundApplyService refundApplyService;
    @Resource
    private UserFundsFlowService userFundsFlowService;

    public List<RefundInfo> getByRefundApplyId(long refundApplyId){
        return refundRepository.findByRefundApplyId(refundApplyId);
    }


    /**
     * 获取状态为交易中的列表
     */
    public List<RefundInfo> getPayingList(){
        return refundRepository.findByRefundStatus(1);
    }


    /**
     * 向腾讯查询退款或红包的交易结果
     */
    @Transactional(rollbackFor=Exception.class)
    public void query(RefundInfo refundInfo) {
        WeixinPayRefundqueryReqData reqData = new WeixinPayRefundqueryReqData();
        reqData.setAppid(refundInfo.getAppid());
        reqData.setMch_id(WeixinTokenHolder.getWeixinMchid(refundInfo.getAppid()));
        reqData.setNonce_str(RandomStringUtils.randomNumeric(20));
        reqData.setSign_type("MD5");
        if(StringUtils.isNotBlank(refundInfo.getRefundId())){
            reqData.setRefund_id(refundInfo.getRefundId());
        }else{
            reqData.setOut_refund_no(refundInfo.getOutRefundNo());
        }
        WeixinPayRefundqueryRespData respData = WeixinHelper.payRefundquery(reqData);
        if(Integer.parseInt(respData.getTotal_fee()) != refundInfo.getTotalFee()){
            LogUtil.getLogger().error("错误：退款订单查询-->商户订单号[{}]-->返回的支付订单金额[{}]与当初请求时的金额[{}]不一致", refundInfo.getOutRefundNo(), respData.getTotal_fee(), refundInfo.getTotalFee());
            LogUtil.logToTask().error("错误：退款订单查询-->商户订单号[{}]-->返回的支付订单金额[{}]与当初请求时的金额[{}]不一致", refundInfo.getOutRefundNo(), respData.getTotal_fee(), refundInfo.getTotalFee());
        }
        for(WeixinPayRefundqueryRespData.RefundInfo wxrefund : respData.getRefundInfoList()){
            if(StringUtils.equals(refundInfo.getOutRefundNo(), wxrefund.getOut_refund_no())){
                if(Integer.parseInt(wxrefund.getRefund_fee()) != refundInfo.getRefundFee()){
                    LogUtil.getLogger().error("错误：退款订单查询-->商户订单号[{}]-->返回的退款金额[{}]与当初请求时的金额[{}]不一致", refundInfo.getOutRefundNo(), wxrefund.getRefund_fee(), refundInfo.getRefundFee());
                    LogUtil.logToTask().error("错误：退款订单查询-->商户订单号[{}]-->返回的退款金额[{}]与当初请求时的金额[{}]不一致", refundInfo.getOutRefundNo(), wxrefund.getRefund_fee(), refundInfo.getRefundFee());
                }
                switch(wxrefund.getRefund_status()){
                    case "PROCESSING"  : refundInfo.setRefundStatus(1); break;
                    case "SUCCESS"     : refundInfo.setRefundStatus(2); break;
                    case "REFUNDCLOSE" : refundInfo.setRefundStatus(3); refundApplyService.doFail(2, null, refundInfo); break;
                    case "CHANGE"      : refundInfo.setRefundStatus(4); refundApplyService.doFail(2, null, refundInfo); break;
                }
                refundInfo.setRefundAccout(wxrefund.getRefund_account());
                refundInfo.setRefundChannel(wxrefund.getRefund_channel());
                refundInfo.setRefundRecvAccout(wxrefund.getRefund_recv_accout());
                refundInfo.setRefundSuccessTime(wxrefund.getRefund_success_time());
                refundRepository.saveAndFlush(refundInfo);
            }
        }
    }
}