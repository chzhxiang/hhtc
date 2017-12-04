package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 11:04.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_user_funds_flow")
public class UserFundsFlow extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    @Column(name="order_id")
    private String orderId;
    private long uid;
    private String openid;
    private BigDecimal money;
    @Column(name="in_out_type")
    private int inOutType;
    @Column(name="in_out_desc")
    private String inOutDesc;



    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getInOutDesc() {
        return inOutDesc;
    }

    public void setInOutDesc(String inOutDesc) {
        this.inOutDesc = inOutDesc;
    }

    public int getInOutType() {
        return inOutType;
    }

    public void setInOutType(int inOutType) {
        this.inOutType = inOutType;
    }

}