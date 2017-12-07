package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 10:58.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_community_info")
public class CommunityInfo extends BaseEntity<Long> {
    private static final long serialVersionUID = 2573378047302790886L;
    private long uid;
    private String name;
    @Column(name="city_name")
    private String cityName;
    @Column(name="province_name")
    private String provinceName;
    @Column(name="point_lng")
    private String pointLng;
    @Column(name="point_lat")
    private String pointLat;
    private String address;
    @Column(name="link_man")
    private String linkMan;
    @Column(name="link_tel")
    private String linkTel;
    @Column(name="money_base")
    private BigDecimal moneyBase;
    @Column(name="money_rent_min")
    private BigDecimal moneyRentMin;
    @Column(name="money_rent_day")
    private BigDecimal moneyRentDay;
    @Column(name="money_rent_night")
    private BigDecimal moneyRentNight;
    @Column(name="money_rent_full")
    private BigDecimal moneyRentFull;
    @Column(name="rent_ratio_platform")
    private int rentRatioPlatform;
    @Column(name="rent_ratio_carparker")
    private int rentRatioCarparker;
    @Column(name="car_park_number_prefix")
    private String carParkNumberPrefix;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getPointLng() {
        return pointLng;
    }

    public void setPointLng(String pointLng) {
        this.pointLng = pointLng;
    }

    public String getPointLat() {
        return pointLat;
    }

    public void setPointLat(String pointLat) {
        this.pointLat = pointLat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLinkMan() {
        return linkMan;
    }

    public void setLinkMan(String linkMan) {
        this.linkMan = linkMan;
    }

    public String getLinkTel() {
        return linkTel;
    }

    public void setLinkTel(String linkTel) {
        this.linkTel = linkTel;
    }

    public BigDecimal getMoneyRentMin() {
        return moneyRentMin;
    }

    public void setMoneyRentMin(BigDecimal moneyRentMin) {
        this.moneyRentMin = moneyRentMin;
    }

    public BigDecimal getMoneyBase() {
        return moneyBase;
    }

    public void setMoneyBase(BigDecimal moneyBase) {
        this.moneyBase = moneyBase;
    }

    public BigDecimal getMoneyRentDay() {
        return moneyRentDay;
    }

    public void setMoneyRentDay(BigDecimal moneyRentDay) {
        this.moneyRentDay = moneyRentDay;
    }

    public BigDecimal getMoneyRentNight() {
        return moneyRentNight;
    }

    public void setMoneyRentNight(BigDecimal moneyRentNight) {
        this.moneyRentNight = moneyRentNight;
    }

    public BigDecimal getMoneyRentFull() {
        return moneyRentFull;
    }

    public void setMoneyRentFull(BigDecimal moneyRentFull) {
        this.moneyRentFull = moneyRentFull;
    }

    public int getRentRatioPlatform() {
        return rentRatioPlatform;
    }

    public void setRentRatioPlatform(int rentRatioPlatform) {
        this.rentRatioPlatform = rentRatioPlatform;
    }

    public int getRentRatioCarparker() {
        return rentRatioCarparker;
    }

    public void setRentRatioCarparker(int rentRatioCarparker) {
        this.rentRatioCarparker = rentRatioCarparker;
    }

    public String getCarParkNumberPrefix() {
        return carParkNumberPrefix;
    }

    public void setCarParkNumberPrefix(String carParkNumberPrefix) {
        this.carParkNumberPrefix = carParkNumberPrefix;
    }
}