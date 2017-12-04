package com.jadyer.seed.comm.constant;

public enum WxMsgEnum {
        /*
        尊敬的司导您好，您的专车服务未通过审核！
        审核姓名：张三 师傅
        拒绝原因：身份证照片模糊不清
        请填写正确的有效信息，重新申请。如有问题请点击查看司导填写教程
        */
    AUDIT_NOTPASS("337mC1vqm0l4bxf8WdEKfiNYO9BOjKCWlJus7hw2bPI","审核被拒通知"),

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