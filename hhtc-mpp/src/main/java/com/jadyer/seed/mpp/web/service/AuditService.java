package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import com.jadyer.seed.mpp.web.model.FansInforAudit;
import com.jadyer.seed.mpp.web.model.GoodsInfo;
import com.jadyer.seed.mpp.web.model.GoodsInfor;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.repository.FansAuditRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditService {
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Value("${hhtc.portalUrl.center}")
    private String portalCenterUrl;
    @Value("${hhtc.wxtemplateUrl.regauditnotpass}")
    private String templateUrlRegAuditNotpass;
    @Resource
    private FansAuditRepository fansAuditRepository;

    /**
     * TOKGO 添加审核
     * */
    public FansInforAudit AddAudit(long uid, String openid,int type,int State,String Content){
        return AddAudit(uid,openid,type,State,Content,"");
    }
    /**
     * TOKGO 添加审核
     * */
    public FansInforAudit AddAudit(long uid, String openid,int type,int State,String Content,String imageurl1){
        return AddAudit(uid,openid,type,State,Content,"","");
    }
    /**
     * TOKGO 添加审核
     * */
    public FansInforAudit AddAudit(long uid, String openid,int type,int State,String Content,String imageurl1,String imageurl2){
        FansInforAudit fansInforAudit = new FansInforAudit();
        fansInforAudit.setUid(uid);
        fansInforAudit.setOpenid(openid);
        fansInforAudit.setState(State);
        fansInforAudit.setType(type);
        fansInforAudit.setContent(Content);
        fansInforAudit.setImgurl1(imageurl1);
        fansInforAudit.setImgurl2(imageurl2);
        return fansAuditRepository.saveAndFlush(fansInforAudit);
    }

    /**
     * 删除一个记录
     *
     * */
    public void Delete(FansInforAudit fansInforAudit){
        fansAuditRepository.delete(fansInforAudit);
    }

    /**
     * 删除一个记录
     *
     * */
    public void Delete(long id){
        fansAuditRepository.delete(id);
    }

    /**
     * TOKGO 获取用户审核数据
     * */
    public List<FansInforAudit> GetAudit(long uid, String openid,int type){
       return fansAuditRepository.findByUidAndOpenidAndType(uid,openid,type);
    }

    /**
     * TOKGO 获取一个审核请求
     * */
    public FansInforAudit GetOne(long id){
        return fansAuditRepository.getOne(id);
    }

    /**
     * TOKGO 分页查询
     * **/
    public Page<FansInforAudit> getpage(Condition<FansInforAudit> spec, Pageable pageable){
        return fansAuditRepository.findAll(spec,pageable);
    }

}
