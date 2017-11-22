package com.jadyer.seed.mpp.sdk.weixin.model.pay;

import java.util.ArrayList;
import java.util.List;

public class WeixinPayRefundqueryRespData extends WeixinPayRespData {
    private String transaction_id;
    private String out_trade_no;
    private String total_fee;
    private String settlement_total_fee;
    private String fee_type;
    private String cash_fee;
    private String refund_count;
    private List<RefundInfo> refundInfoList = new ArrayList<>();
    public static class RefundInfo{
        private String out_refund_no;
        private String refund_id;
        private String refund_channel;
        private String refund_fee;
        private String settlement_refund_fee;
        private String coupon_type;
        private String coupon_refund_fee;
        private String refund_status;
        private String refund_account;
        private String refund_recv_accout;
        private String refund_success_time;
        private String coupon_refund_count;
        private List<CouponRefundInfo> couponRefundInfoList = new ArrayList<>();
        public static class CouponRefundInfo{
            private String coupon_refund_id;
            private String coupon_refund_fee;

            public String getCoupon_refund_id() {
                return coupon_refund_id;
            }

            public void setCoupon_refund_id(String coupon_refund_id) {
                this.coupon_refund_id = coupon_refund_id;
            }

            public String getCoupon_refund_fee() {
                return coupon_refund_fee;
            }

            public void setCoupon_refund_fee(String coupon_refund_fee) {
                this.coupon_refund_fee = coupon_refund_fee;
            }
        }

        public String getOut_refund_no() {
            return out_refund_no;
        }

        public void setOut_refund_no(String out_refund_no) {
            this.out_refund_no = out_refund_no;
        }

        public String getRefund_id() {
            return refund_id;
        }

        public void setRefund_id(String refund_id) {
            this.refund_id = refund_id;
        }

        public String getRefund_channel() {
            return refund_channel;
        }

        public void setRefund_channel(String refund_channel) {
            this.refund_channel = refund_channel;
        }

        public String getRefund_fee() {
            return refund_fee;
        }

        public void setRefund_fee(String refund_fee) {
            this.refund_fee = refund_fee;
        }

        public String getSettlement_refund_fee() {
            return settlement_refund_fee;
        }

        public void setSettlement_refund_fee(String settlement_refund_fee) {
            this.settlement_refund_fee = settlement_refund_fee;
        }

        public String getCoupon_type() {
            return coupon_type;
        }

        public void setCoupon_type(String coupon_type) {
            this.coupon_type = coupon_type;
        }

        public String getCoupon_refund_fee() {
            return coupon_refund_fee;
        }

        public void setCoupon_refund_fee(String coupon_refund_fee) {
            this.coupon_refund_fee = coupon_refund_fee;
        }

        public String getRefund_status() {
            return refund_status;
        }

        public void setRefund_status(String refund_status) {
            this.refund_status = refund_status;
        }

        public String getRefund_account() {
            return refund_account;
        }

        public void setRefund_account(String refund_account) {
            this.refund_account = refund_account;
        }

        public String getRefund_recv_accout() {
            return refund_recv_accout;
        }

        public void setRefund_recv_accout(String refund_recv_accout) {
            this.refund_recv_accout = refund_recv_accout;
        }

        public String getRefund_success_time() {
            return refund_success_time;
        }

        public void setRefund_success_time(String refund_success_time) {
            this.refund_success_time = refund_success_time;
        }

        public String getCoupon_refund_count() {
            return coupon_refund_count;
        }

        public void setCoupon_refund_count(String coupon_refund_count) {
            this.coupon_refund_count = coupon_refund_count;
        }

        public List<CouponRefundInfo> getCouponRefundInfoList() {
            return couponRefundInfoList;
        }

        public void setCouponRefundInfoList(List<CouponRefundInfo> couponRefundInfoList) {
            this.couponRefundInfoList = couponRefundInfoList;
        }
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getSettlement_total_fee() {
        return settlement_total_fee;
    }

    public void setSettlement_total_fee(String settlement_total_fee) {
        this.settlement_total_fee = settlement_total_fee;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public String getCash_fee() {
        return cash_fee;
    }

    public void setCash_fee(String cash_fee) {
        this.cash_fee = cash_fee;
    }

    public String getRefund_count() {
        return refund_count;
    }

    public void setRefund_count(String refund_count) {
        this.refund_count = refund_count;
    }

    public List<RefundInfo> getRefundInfoList() {
        return refundInfoList;
    }

    public void setRefundInfoList(List<RefundInfo> refundInfoList) {
        this.refundInfoList = refundInfoList;
    }
}