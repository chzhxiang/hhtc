package com.jadyer.seed.comm.jpa;

import org.springframework.data.domain.Persistable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> implements Persistable<ID> {
    private static final long serialVersionUID = 5563689039804450746L;
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private ID id;

    @Column(name="create_time", updatable=false)
    @Basic(fetch= FetchType.LAZY)
    private Date createTime = new Date();

    @Column(name="update_time", updatable=false, insertable=false)
    @Basic(fetch=FetchType.LAZY)
    private Date updateTime;

    @Override
    public boolean isNew() {
        return null == this.id;
    }

    @Override
    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}