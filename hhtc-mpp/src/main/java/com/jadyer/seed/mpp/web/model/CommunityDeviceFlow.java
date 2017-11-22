package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 小区设备扫描流水
 * Generated from seed-simcoder by 玄玉<http://jadyer.cn/> on 2017/09/24 10:47.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_community_device_flow")
public class CommunityDeviceFlow extends BaseEntity<Long> {
    private static final long serialVersionUID = 4794423120161554779L;
    /** 设备ID，对应t_community_device#id */
    @Column(name="device_id")
    private long deviceId;
    /** 小区ID，对应t_community_info#id */
    @Column(name="community_id")
    private long communityId;
    /** 小区名称，对应t_community_info#name */
    @Column(name="community_name")
    private String communityName;
    /** 车牌识别时间 */
    @Column(name="scan_time")
    private Date scanTime;
    /** 识别的车牌号 */
    @Column(name="scan_car_number")
    private String scanCarNumber;
    /** 识别结果：0--不开闸，1--开闸 */
    @Column(name="scan_allow_open")
    private int scanAllowOpen;
    /** 开闸结果：0--开闸失败，1--开闸成功 */
    @Column(name="open_result")
    private int openResult;
    /** 开闸时间 */
    @Column(name="open_time")
    private Date openTime;
    /** 开闸备注 */
    @Column(name="open_remark")
    private String openRemark;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
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

    public Date getScanTime() {
        return scanTime;
    }

    public void setScanTime(Date scanTime) {
        this.scanTime = scanTime;
    }

    public String getScanCarNumber() {
        return scanCarNumber;
    }

    public void setScanCarNumber(String scanCarNumber) {
        this.scanCarNumber = scanCarNumber;
    }

    public int getScanAllowOpen() {
        return scanAllowOpen;
    }

    public void setScanAllowOpen(int scanAllowOpen) {
        this.scanAllowOpen = scanAllowOpen;
    }

    public int getOpenResult() {
        return openResult;
    }

    public void setOpenResult(int openResult) {
        this.openResult = openResult;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public String getOpenRemark() {
        return openRemark;
    }

    public void setOpenRemark(String openRemark) {
        this.openRemark = openRemark;
    }
}