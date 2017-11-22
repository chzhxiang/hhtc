package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Comment by 玄玉<http://jadyer.cn/> on 2017/9/2 11:41.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_community_device")
public class CommunityDevice extends BaseEntity<Long> {
    private static final long serialVersionUID = 2573378047302790886L;
    @Column(name="community_id")
    private long communityId;
    @Column(name="community_name")
    private String communityName;
    @Column(name="serialno_camera")
    private String serialnoCamera;
    @Column(name="serialno_relays")
    private String serialnoRelays;
    @Column(name="relays_doorid")
    private String relaysDoorid;
    private int type;
    private String remark;

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public String getSerialnoCamera() {
        return serialnoCamera;
    }

    public void setSerialnoCamera(String serialnoCamera) {
        this.serialnoCamera = serialnoCamera;
    }

    public String getSerialnoRelays() {
        return serialnoRelays;
    }

    public void setSerialnoRelays(String serialnoRelays) {
        this.serialnoRelays = serialnoRelays;
    }

    public String getRelaysDoorid() {
        return relaysDoorid;
    }

    public void setRelaysDoorid(String relaysDoorid) {
        this.relaysDoorid = relaysDoorid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
}