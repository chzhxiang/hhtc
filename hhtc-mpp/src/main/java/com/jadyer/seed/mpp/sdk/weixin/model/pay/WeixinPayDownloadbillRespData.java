package com.jadyer.seed.mpp.sdk.weixin.model.pay;

import java.util.ArrayList;
import java.util.List;

public class WeixinPayDownloadbillRespData {
    private String return_code;
    private String return_msg;
    private static class BillInfo{
        private String 总交易单数;
        private String 总交易额;
        private String 总退款金额;
        private String 总代金券或立减优惠退款金额;
        private String 手续费总金额;
        private List<BillDetailInfo> billDetailInfoList = new ArrayList<>();
        private static class BillDetailInfo{
            private String 交易时间;
            private String 公众账号ID;
            private String 商户号;
            private String 子商户号;
            private String 设备号;
            private String 微信订单号;
            private String 商户订单号;
            private String 用户标识;
            private String 交易类型;
            private String 交易状态;
            private String 付款银行;
            private String 货币种类;
            private String 总金额;
            private String 代金券或立减优惠金额;
            private String 退款申请时间;
            private String 退款成功时间;
            private String 微信退款单号;
            private String 商户退款单号;
            private String 退款金额;
            private String 代金券或立减优惠退款金额;
            private String 退款类型;
            private String 退款状态;
            private String 商品名称;
            private String 商户数据包;
            private String 手续费;
            private String 费率;
        }
    }
}