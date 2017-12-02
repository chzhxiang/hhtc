package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.mpp.web.model.FansInforAudit;
import com.jadyer.seed.mpp.web.model.OwnersInfor;
import com.jadyer.seed.mpp.web.repository.OwnersInforRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @描述
 * @创建人 TOKGO
 * @创建时间 2017/12/2
 * @修改人和其它信息
 */
@Service
public class OwnersInforServic {

    @Resource
    private OwnersInforRepository ownersInforRepository;

    /**
     * TOKGO 获取车牌数据
     * */
    public List<OwnersInfor> Get(String opendid){
        return ownersInforRepository.findByOpenid(opendid);
    }

    /**
     * TOKGO 删除车牌
     * **/
    public void Delete(long id){
        ownersInforRepository.delete(id);
    }

    /**
     * TOKGO 检查是否存在车牌号
     * */
    public boolean IsExist(String carNumber){
        return ownersInforRepository.findByCaNumber(carNumber)!=null;
    }

    /**
     * TOKGO  添加车牌
     * */
    public void AddCarNumber(FansInforAudit fansInforAudit,long uid){
        OwnersInfor ownersInfor = ownersInforRepository.findByCaNumber(fansInforAudit.getContent());
        if (ownersInfor==null){
            ownersInfor = new OwnersInfor();
        }
        ownersInfor.setOpenid(fansInforAudit.getOpenid());
        ownersInfor.setCaNumber(fansInforAudit.getContent());
        ownersInfor.setCommunityId(fansInforAudit.getCommunityId());
        ownersInfor.setCommunityName(fansInforAudit.getCommunityName());
        ownersInfor.setCarNumberImg(fansInforAudit.getImgurl1());
        ownersInfor.setCarAuditUid(uid);
        ownersInforRepository.save(ownersInfor);
    }

}
