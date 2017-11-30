package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/8/13 17:09.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_goods_publish_order")
public class GoodsPublishOrder extends BaseEntity<Long> {
    private static final long serialVersionUID = 5452996244293033218L;
    @Column(name="order_id")
    private String orderID;
    private String openid;
    @Column(name="phone_no")
    private String phoneNO;
    @Column(name="community_id")
    private long communityId;
    @Column(name="community_name")
    private String communityName;
    @Column(name="goods_id")
    private long goodsId;
    @Column(name="car_park_number")
    private String carParkNumber;
    @Column(name="car_park_img")
    private String carParkImg;

    private BigDecimal price;
    @Column(name="from_dates_calculate")
    private long fromdateCalculate;
    @Column(name="publish_from_time")
    private String publishFromTime;
    @Column(name="publish_end_time")
    private String publishEndTime;


    public String getPhoneNO() {
        return phoneNO;
    }

    public void setPhoneNO(String phoneNO) {
        this.phoneNO = phoneNO;
    }

    public void setFromdateCalculate(long fromdateCalculate) {
        this.fromdateCalculate = fromdateCalculate;
    }

    public long getFromdateCalculate() {
        return fromdateCalculate;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getPublishFromTime() {
        return publishFromTime;
    }

    public void setPublishFromTime(String publishFromTime) {
        try {
            this.fromdateCalculate= new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(publishFromTime).getTime();
        } catch (ParseException e) {
            throw new HHTCException(CodeEnum.SYSTEM_NULL); }
        this.publishFromTime = publishFromTime;
    }


    public String getPublishEndTime() {
        return publishEndTime;
    }

    public void setPublishEndTime(String publishEndTime) {
        this.publishEndTime = publishEndTime;
    }

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getCarParkImg() {
        return carParkImg;
    }

    public void setCarParkImg(String carParkImg) {
        this.carParkImg = carParkImg;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCarParkNumber() {
        return carParkNumber;
    }

    public void setCarParkNumber(String carParkNumber) {
        this.carParkNumber = carParkNumber;
    }

}