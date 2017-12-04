package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.jadyer.seed.comm.constant.Constants.*;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/17 18:31.
 */
@Service
public class GoodsService {
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Value("${hhtc.portalUrl.center}")
    private String portalCenterUrl;
    @Resource
    private FansService fansService;
    @Resource
    private OrderService orderService;
    @Resource
    private AuditService auditService;
    @Resource
    private CommunityService communityService;
    @Resource
    private FansInforRepository fansInforRepository;
    @Resource
    private GoodsInforRepository goodsInforRepository;
    @Resource
    private GoodsPublishRepository goodsPublishRepository;


    /**
     * TOKGO 获取用户当前车位信息
     * */
    public List<HashMap> getFansCarParkInfor(String openid){
        List<HashMap> list = new ArrayList<>();
        HashMap hashMap;
        MppFansInfor mppFansInfor = fansInforRepository.findByOpenid(openid);
        if (mppFansInfor==null )
            throw new HHTCException(CodeEnum.SYSTEM_NULL);
        List<GoodsInfor> goodsInfors = goodsInforRepository.findByOpenid(openid);
        for (GoodsInfor goodsInfor:goodsInfors){
            hashMap = new HashMap();
            hashMap.put("state","work");
            hashMap.put("id",goodsInfor.getId());
            hashMap.put("communityName",goodsInfor.getCommunityName());
            hashMap.put("parkNumber",goodsInfor.getCarParkNumber());
            hashMap.put("endTime",goodsInfor.getCarUsefulEndDate());
            list.add(hashMap);
        }
        List<FansInforAudit> fansInforAudits = auditService.GetAudit(2,openid,2);
        //添加审核数据
        for (FansInforAudit fansInforAudit:fansInforAudits){
            hashMap = new HashMap();
            String[] T = fansInforAudit.getContent().split("@");
            hashMap.put("state","audit");
            hashMap.put("id",fansInforAudit.getId());
            hashMap.put("communityName",GetFansCommunityName(mppFansInfor));
            hashMap.put("parkNumber",T[0]);
            hashMap.put("endTime",T[1]);
            list.add(hashMap);
        }
        return list;
    }



    /**
     * TOKGO 车位注销
     */
    @Transactional(rollbackFor=Exception.class)
    public String carParkLogout(String openid,long id,String state){
        if(StringUtils.isBlank(state)){
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_NULL);
        }
        if ("audit".equals(state)){
            auditService.Delete(id);
        }
        else{
            fansInforRepository.delete(id);
        }
        return  fansInforRepository.findByOpenid(openid).getInfor_state();
    }


    /**
     *TOKGO车位主注册
     */
    @Transactional(rollbackFor=Exception.class)
    public HashMap regCarPark(String openid, String carParkNumber, String carEquityImg, String carUsefulEndDate){
        FansInforAudit fansInforAudit;
        if(StringUtils.isBlank(carParkNumber)||carUsefulEndDate==null){
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_NULL);
        }
        MppFansInfor mppFansInfor= fansInforRepository.findByOpenid(openid);
        //检查是否用户验证电话
        if(mppFansInfor.getInfor_state().charAt(INFOR_STATE_PHOMENO_BIT) == '0')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_PHOMENO);
        if(mppFansInfor.getInfor_state().charAt(INFOR_STATE_PHOMENO_BIT) == '0')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_COMMUNITY);
        List<FansInforAudit> fansInforAudits = auditService.GetAudit(mppFansInfor.getUid()
                ,mppFansInfor.getOpenid(),AUDTI_TEPY_CARPARK);
        //判断用户是否已经提交请求
        if (fansInforAudits.size() > 0){
            for (FansInforAudit Audit :fansInforAudits)
                if (carParkNumber.equals(Audit.getContent().split("@")[0]))
                    throw new HHTCException(CodeEnum.HHTC_INFOR_CARPARK);
        }
        //TODO  记录这个车位主 是租赁的还是自己拥有的
        if (mppFansInfor.getInfor_state().charAt(INFOR_STATE_PHOMENO_BIT) == '1'){
            //写入审核
            fansInforAudit= auditService.AddAudit(mppFansInfor,AUDTI_TEPY_CARPARK,mppFansInfor.getCommunityId()
                    ,mppFansInfor.getCommunityName(),carParkNumber+"@"+carUsefulEndDate,carEquityImg);
        }else{
            List<FansInforAudit> audit = auditService.GetAudit(mppFansInfor.getUid(),openid,AUDTI_TEPY_CARNUMBER);
            if (audit.size()<1)
                throw new HHTCException(CodeEnum.SYSTEM_PARAM_DATA_ERROR);
            //写入审核
            fansInforAudit= auditService.AddAudit(mppFansInfor,AUDTI_TEPY_CARPARK,audit.get(0).getCommunityId()
                    ,audit.get(0).getCommunityName(),carParkNumber+"@"+carUsefulEndDate,carEquityImg);
        }
        if (mppFansInfor.getInfor_state().charAt(INFOR_STATE_CARPARK_BIT)!='1')
            fansService.UpdatedataInforSate(INFOR_STATE_CARPARK_BIT,'2',mppFansInfor);
        //返回当前车位信息
        HashMap hashMap = new HashMap();
        hashMap.put("state","audit");
        hashMap.put("id",fansInforAudit.getId());
        hashMap.put("communityName",GetFansCommunityName(mppFansInfor));
        hashMap.put("parkNumber",carParkNumber);
        hashMap.put("endTime",carUsefulEndDate);
        return  hashMap;
    }

    /**
     * TOKGO 获取当前用户小区名
     * */
    private String GetFansCommunityName(MppFansInfor mppFansInfor){
        if (mppFansInfor ==null)
            return "";
        if (mppFansInfor.getInfor_state().charAt(INFOR_STATE_CARPARK_BIT)=='1')
            return mppFansInfor.getCommunityName();
        List<FansInforAudit>fansInforAudits = auditService.GetAudit(2,mppFansInfor.getOpenid(),1);
        if (fansInforAudits.size()<1)
            return "";
        if (StringUtils.isBlank(fansInforAudits.get(0).getContent()))
            return "";
        return fansInforAudits.get(0).getContent().split(SPLITFLAG)[0];
    }


    /**
     * 查询车位详情
     */
    public GoodsInfor get(long id){
        return goodsInforRepository.findOne(id);
    }

    /**
     * 查询车位详情
     */
    public List<GoodsInfor> get(String  openid){
        return goodsInforRepository.findByOpenid(openid);
    }



    /**
     * 查询车位详情
     */
    public GoodsInfor get(long communityId,String carParkNumber){
        return goodsInforRepository.findByCommunityIdAndCarParkNumber(communityId,carParkNumber);
    }
    /**
     * 查询车位详情
     */
    public GoodsInfor get(String openid,long goodsid){
        return goodsInforRepository.findByOpenidAndId(openid,goodsid);
    }

    /***
     * TOKGO 检测车位是否冲突
     * */
    public boolean IsExist(long communityId,String carParkNumber){
        return goodsInforRepository.findByCommunityIdAndCarParkNumber(communityId,carParkNumber)!=null;
    }


    /**
     * TOKGO 添加车位
     * */
    public void AddGoods(FansInforAudit fansInforAudit, String appid,long uid){
        //如果车位存在 直接改变openid
        GoodsInfor goodsInfor = goodsInforRepository.findByCommunityIdAndCarParkNumber(
                fansInforAudit.getCommunityId(),fansInforAudit.getCommunityName());
        if (goodsInfor ==null)
            goodsInfor = new GoodsInfor();
        goodsInfor.setCommunityId(fansInforAudit.getCommunityId());
        goodsInfor.setCommunityName(fansInforAudit.getCommunityName());
        goodsInfor.setAppid(appid);
        goodsInfor.setOpenid(fansInforAudit.getOpenid());
        String[] ifor = fansInforAudit.getContent().split(Constants.SPLITFLAG);
        if (ifor.length<2)
            new HHTCException(CodeEnum.SYSTEM_NULL);
        goodsInfor.setCarParkNumber(ifor[0]);
        goodsInfor.setCarUsefulEndDate(ifor[1]);
        goodsInfor.setCarEquityImg(fansInforAudit.getImgurl1());
        goodsInfor.setCarAuditUid(uid);
        goodsInforRepository.save(goodsInfor);
    }

    /**
     * TOKGO 删除车位
     * */
    public void Delete(GoodsInfor goodsInfor){
        goodsInforRepository.delete(goodsInfor);
    }


    /**
     * TOKGO 获取用户车位
     * */
    @Transactional(rollbackFor=Exception.class)
    public List<GoodsInfor> GetPublishCarpark(String openid){
        return goodsInforRepository.findByOpenid(openid);
    }


    /**
     * TOKGO 获取小区的车位总数
     * */
    public int GetGoodsCount( MppUserInfo userInfo){
        if (userInfo.getType() == 2)
            return goodsInforRepository.findAll().size();
        List<CommunityInfo> communityInfos = communityService.getByUid(userInfo.getId());
        int cout = 0;
        for (CommunityInfo communityInfo:communityInfos){
            cout += goodsInforRepository.findByCommunityId(communityInfo.getId()).size();
        }
        return cout;
    }

}