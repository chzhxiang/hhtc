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
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 15:51.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_order_infor")
public class OrderInfor extends BaseEntity<Long> {
    private static final long serialVersionUID = 8020680057027208315L;
    @Column(name="order_status")
    private int orderStatus;
    @Column(name="post_openid")
    private String postOpenid;
    @Column(name="post_phone_no")
    private String postPhoneNO;
    @Column(name="owners_phone_no")
    private String ownersPhoneNO;
    @Column(name="owners_openid")
    private String ownersOpenid;
    @Column(name="community_id")
    private long communityId;
    @Column(name="community_name")
    private String communityName;
    @Column(name="goods_id")
    private long goodsId;
    @Column(name="order_id")
    private String orderId;
    @Column(name="car_park_number")
    private String carParkNumber;
    @Column(name="car_park_img")
    private String carParkImg;
    @Column(name="car_number")
    private String carNumber;
    @Column(name="total_price")
    private BigDecimal totalPrice;
    @Column(name="time_start")
    private String timeStart;
    @Column(name="time_start_calculate")
    private long timeStartCalculate;
    @Column(name="time_end")
    private String timeEnd;
    @Column(name="time_end_calculate")
    private long timeEndCalculate;
    @Column(name="out_price")
    private BigDecimal outPrice;
    @Column(name="out_price_time")
    private long outPriceTime;


    public void setTimeStartCalculate(long timeStartCalculate) {
        this.timeStartCalculate = timeStartCalculate;
    }

    public void setTimeEndCalculate(long timeEndCalculate) {
        this.timeEndCalculate = timeEndCalculate;
    }

    public void setOutPriceTime(long outPriceTime) {
        this.outPriceTime = outPriceTime;
    }

    public String getPostPhoneNO() {
        return postPhoneNO;
    }

    public void setPostPhoneNO(String postPhoneNO) {
        this.postPhoneNO = postPhoneNO;
    }

    public String getOwnersPhoneNO() {
        return ownersPhoneNO;
    }

    public void setOwnersPhoneNO(String ownersPhoneNO) {
        this.ownersPhoneNO = ownersPhoneNO;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getTimeStartCalculate() {
        return timeStartCalculate;
    }

    public void setTimeEnd(String timeEnd) {
        try {
            this.timeEndCalculate= new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(timeEnd).getTime();
        } catch (ParseException e) {
            throw new HHTCException(CodeEnum.SYSTEM_NULL); }
        this.timeEnd = timeEnd;
    }

    public long getTimeEndCalculate() {
        return timeEndCalculate;
    }


    public BigDecimal getOutPrice() {
        return outPrice;
    }

    public void setOutPrice(BigDecimal outPrice) {
        this.outPrice = outPrice;
    }

    public long getOutPriceTime() {
        return outPriceTime;
    }

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPostOpenid() {
        return postOpenid;
    }

    public void setPostOpenid(String postOpenid) {
        this.postOpenid = postOpenid;
    }

    public String getOwnersOpenid() {
        return ownersOpenid;
    }

    public void setOwnersOpenid(String ownersOpenid) {
        this.ownersOpenid = ownersOpenid;
    }

    public String getCarParkNumber() {
        return carParkNumber;
    }

    public void setCarParkNumber(String carParkNumber) {
        this.carParkNumber = carParkNumber;
    }

    public String getCarParkImg() {
        return carParkImg;
    }

    public void setCarParkImg(String carParkImg) {
        this.carParkImg = carParkImg;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        try {
            this.timeStartCalculate= new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(timeStart).getTime();
            this.outPriceTime = this.timeStartCalculate;
        } catch (ParseException e) {
            throw new HHTCException(CodeEnum.SYSTEM_NULL); }
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

}