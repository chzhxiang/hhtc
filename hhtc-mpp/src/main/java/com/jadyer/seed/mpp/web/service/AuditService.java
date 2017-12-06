package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.mpp.web.model.FansInforAudit;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.repository.FansAuditRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.jadyer.seed.comm.constant.Constants.AUDTI_TEPY_CARNUMBER;
import static com.jadyer.seed.comm.constant.Constants.AUDTI_TEPY_CARPARK;
import static com.jadyer.seed.comm.constant.Constants.INFOR_STATE_COMMUNITY_BIT;

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
    public FansInforAudit AddAudit(MppFansInfor mppFansInfor,int type,long communityId,String communityName,String Content){
        return AddAudit(mppFansInfor,type,communityId,communityName,Content,"");
    }
    /**
     * TOKGO 添加审核
     * */
    public FansInforAudit AddAudit(MppFansInfor mppFansInfor,int type,long communityId,String communityName
            ,String Content,String imageurl1){
        return AddAudit(mppFansInfor,type,communityId,communityName,Content,imageurl1,"");
    }
    /**
     * TOKGO 添加审核
     * */
    public FansInforAudit AddAudit(MppFansInfor mppFansInfor, int type, long communityId, String communityName
            , String Content, String imageurl1, String imageurl2){
        FansInforAudit fansInforAudit = new FansInforAudit();
        fansInforAudit.setUid(mppFansInfor.getUid());
        fansInforAudit.setOpenid(mppFansInfor.getOpenid());
        fansInforAudit.setCommunityId(communityId);
        fansInforAudit.setCommunityName(communityName);
        fansInforAudit.setPhoneNo(mppFansInfor.getPhoneNo());
        fansInforAudit.setType(type);
        fansInforAudit.setContent(Content);
        fansInforAudit.setImgurl1(imageurl1);
        fansInforAudit.setImgurl2(imageurl2);
        if ((mppFansInfor.getInfor_state().charAt(INFOR_STATE_COMMUNITY_BIT)!='1')
                &&(type==AUDTI_TEPY_CARNUMBER || type == AUDTI_TEPY_CARPARK)){
            //如果地址没有审核通过 车牌和车位不给与显示
            fansInforAudit.setState(1);
        }
        return fansAuditRepository.save(fansInforAudit);
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
     * TOKGO 获取用户审核数据
     * */
    public List<FansInforAudit> GetAudit(String openid,int type){
        return fansAuditRepository.findByOpenidAndType(openid,type);
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

    /**
     * TOKGO 保存
     * */
    public void save(List<FansInforAudit> list){
        fansAuditRepository.save(list);
    }

}
