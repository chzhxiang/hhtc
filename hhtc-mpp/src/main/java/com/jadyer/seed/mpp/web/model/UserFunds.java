package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 11:04.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_user_funds")
public class UserFunds extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    private long uid;
    private String openid;
    @Column(name="money_base")
    private BigDecimal moneyBase;
    @Column(name="money_balance")
    private BigDecimal moneyBalance;

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

    public BigDecimal getMoneyBase() {
        return moneyBase;
    }

    public void setMoneyBase(BigDecimal moneyBase) {
        this.moneyBase = moneyBase;
    }

    public BigDecimal getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(BigDecimal moneyBalance) {
        this.moneyBalance = moneyBalance;
    }
}