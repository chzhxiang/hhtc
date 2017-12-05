package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 15:51.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_temporary_out")
public class TemporaryOut extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    @Column(name="community_id")
    private long communityId;
    @Column(name="car_number")
    private String carNumber;

    @Column(name="time_end")
    private long timeEnd;

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }
}