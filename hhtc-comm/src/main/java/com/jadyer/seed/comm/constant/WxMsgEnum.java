package com.jadyer.seed.comm.constant;

public enum WxMsgEnum {
        /*
        尊敬的 XXX，您的爱车已经通过审核，
        车牌号码：京A00004
        生效时间：2017-03-01
        恭喜您成为萌马成员，您将从以上生效时间起开始享受萌马为爱车提供的全面保障和贴心服务。
        */
        /*lhvy1gpBCZsBbvOMf93qNWxpF2I4j4cnBe5rt2q3z6w*/
    AUDIT_CARNUMBERPASS("crGTxcHRTABjZu6hgUCmseLK-ra0YvmWs6lCAHVldY4","车牌号审核通过"),

    /**
     First：尊敬的用户，您提交的车位资料未通过物业审核。
     手机号：尾号3432
     审核结果：未通过  /通过
     Remark：拒绝的原因就是后台审核拒绝的原因。*/
    /*upsa1MpVfulcu69n_f7B6kF2s8uV9ODU47estmNWuK4*/
    AUDIT_COMMON("crGTxcHRTABjZu6hgUCmseLK-ra0YvmWs6lCAHVldY4","审核结果通用版"),

    /**
     您申请的提现业务未通过审核
     提现金额：30元
     失败原因：仍有未完成的订单
     请您做出相应调整后，再申请提现。
     * */
     /*ZhGiBnC7ugrDs-raCC0E1kJ2aaRl_i1by8bwAkBIGtA*/
    WITHDRAWAL_FAILED("crGTxcHRTABjZu6hgUCmseLK-ra0YvmWs6lCAHVldY4","提现失败"),



    WX_TEST("oS4jS3saxCoTTUQtGTv8EBD4ZNP6H_JYyTn4BFn0aKI","测试用户"),



    ;


    private final String ID;
    private final String note;

     WxMsgEnum(String ID, String note) {
        this.ID = ID;
        this.note = note;
    }

    public String getID() {
        return ID;
    }
}