package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.mpp.web.model.FansInforAudit;
import com.jadyer.seed.mpp.web.repository.FansAuditRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditService {
    @Resource
    private FansAuditRepository fansAuditRepository;

    /**
     * TOKGO 添加审核
     * */
    public boolean AddAudit(long uid, String openid,int type,String Content){
        return AddAudit(uid,openid,type,Content,"");
    }
    /**
     * TOKGO 添加审核
     * */
    public boolean AddAudit(long uid, String openid,int type,String Content,String imageurl1){
        return AddAudit(uid,openid,type,Content,"","");
    }
    /**
     * TOKGO 添加审核
     * */
    public boolean AddAudit(long uid, String openid,int type,String Content,String imageurl1,String imageurl2){
        FansInforAudit fansInforAudit = new FansInforAudit();
        fansInforAudit.setUid(uid);
        fansInforAudit.setOpenid(openid);
        fansInforAudit.setState(0);
        fansInforAudit.setType(type);
        fansInforAudit.setContent(Content);
        fansInforAudit.setImgurl1(imageurl1);
        fansInforAudit.setImgurl2(imageurl2);
        fansAuditRepository.saveAndFlush(fansInforAudit);
        return true;
    }



    /**
     * TOKGO 获取用户审核数据
     * */
    public List<FansInforAudit> GetAudit(long uid, String openid,int type){
       return fansAuditRepository.findByUidAndOpenidAndType(uid,openid,type);
    }




}
