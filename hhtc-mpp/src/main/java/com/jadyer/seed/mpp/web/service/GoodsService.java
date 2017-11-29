package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
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
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;


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
        if(StringUtils.isBlank(carParkNumber)||carUsefulEndDate==null){
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_NULL);
        }
        MppFansInfor mppFansInfor= fansInforRepository.findByOpenid(openid);
        //检查是否用户验证电话
        if(mppFansInfor.getInfor_state().charAt(INFOR_STATE_PHOMENO_BIT) == '0')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_PHOMENO);
        List<FansInforAudit> fansInforAudits = auditService.GetAudit(mppFansInfor.getUid()
                ,mppFansInfor.getOpenid(),AUDTI_TEPY_CARPARK);
        //判断用户是否已经提交请求
        if (fansInforAudits.size() > 0){
            for (FansInforAudit fansInforAudit :fansInforAudits)
                if (fansInforAudit.getState()==0) {
                    if (carParkNumber.equals(fansInforAudit.getContent().split("@")[0]))
                        throw new HHTCException(CodeEnum.HHTC_INFOR_CARPARK);
                }
        }
        //写入审核
        FansInforAudit fansInforAudit = auditService.AddAudit(mppFansInfor.getUid(),openid
                ,2,0,carParkNumber+"@"+carUsefulEndDate,carEquityImg);
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



//    public Page<GoodsInfo> listViaPage(MppUserInfo userInfo, String pageNo){
//        Sort sort = new Sort(Sort.Direction.ASC, "id");
//        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
//        Condition<GoodsInfo> spec = null;
//        //物管只能查询自己小区的车位列表
//        if(userInfo.getType() == 2){
//            List<Long> idList = new ArrayList<>();
//            for(CommunityInfo obj : communityService.getByUid(userInfo.getId())){
//                idList.add(obj.getId());
//            }
//            spec = Condition.<GoodsInfo>and().in("communityId", idList);
//        }
//        Page<GoodsInfo> page = goodsRepository.findAll(spec, pageable);
//        List<GoodsInfo> list = page.getContent();
//        for(GoodsInfo obj : list){
//            MppFansInfor fans = fansService.getByOpenid(obj.getOpenid());
//            obj.setNickname(fans.getNickname());
//            obj.setHeadimgurl(fans.getHeadimgurl());
//        }
//        return null;
//    }


//    public List<GoodsInfo> listAllByOpenid(String openid){
//        Sort sort = new Sort(Sort.Direction.DESC, "id");
//        Condition<GoodsInfo> spec = Condition.<GoodsInfo>and().eq("openid", openid).ne("isUsed", 3);
//        return goodsRepository.findAll(spec, sort);
//    }
//
//
//    public long count(MppUserInfo userInfo){
//        if(userInfo.getType() == 2){
//            long count = 0;
//            List<Long> idList = new ArrayList<>();
//            for(CommunityInfo obj : communityService.getByUid(userInfo.getId())){
//                count = count + goodsRepository.countByCommunityId(obj.getId());
//            }
//            return count;
//        }else{
//            return goodsRepository.count();
//        }
//    }


    /**
     * 查询车位详情
     */
    public GoodsInfor get(long id){
        return goodsInforRepository.findOne(id);
    }


//    /**
//     * 审核通过或拒绝车位
//     */
//    @Transactional(rollbackFor=Exception.class)
//    public GoodsInfo audit(MppUserInfo userInfo, GoodsInfo goodsInfo){
//        if(goodsInfo.getCarAuditStatus() == 1){
//            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "审核时传输的状态无效");
//        }
//        //物管只能审核自己小区的车位
//        if(userInfo.getType() == 2){
//            List<Long> idList = new ArrayList<>();
//            for(CommunityInfo obj : communityService.getByUid(userInfo.getId())){
//                idList.add(obj.getId());
//            }
//            if(!idList.contains(goodsInfo.getCommunityId())){
//                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只能审核自己小区的车位");
//            }
//        }
        //TODO
//        //校验是否注册车位主
//        MppFansInfor fansInfo = fansService.getByOpenid(goodsInfo.getOpenid());
//        if(2 != fansInfo.getCarParkStatus()){
//            throw new HHTCException(CodeEnum.HHTC_UNREG_CAR_PARK);
//        }
//        goodsInfo.setCarAuditTime(new Date());
//        goodsInfo.setCarAuditUid(userInfo.getId());
//        goodsInfo = goodsRepository.saveAndFlush(goodsInfo);
//
//        if(goodsInfo.getCarAuditStatus() == 2 && Constants.ISSMS){
//            /*
//            {{first.DATA}}
//            手机号：{{keyword1.DATA}}
//            审核结果：{{keyword2.DATA}}
//            {{remark.DATA}}
//
//            尊敬的用户，您的押金退回业务审核结果如下
//            手机号：尾号3432
//            审核结果：通过
//            您交付平台的押金已退回您原支付账户，预计1~7个工作日到账，请注意查收。
//            */
//            WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
//            dataItem.put("first", new WeixinTemplateMsg.DItem("尊敬的车位主，您的车位申请已经审核通过！"));
//            dataItem.put("keyword1", new WeixinTemplateMsg.DItem("尾号" + fansInfor.getPhoneNo().substring(7,11)));
//            dataItem.put("keyword2", new WeixinTemplateMsg.DItem("车位号：" + goodsInfo.getCarParkNumber() + "审核通过"));
//            dataItem.put("remark", new WeixinTemplateMsg.DItem("我要抢车位，方便加一倍！点击“抢车位”，停车舒心更省心！"));
//            String url = this.hhtcContextPath + "/portal/index.html#/publish";
//            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+goodsInfo.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+goodsInfo.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
//            WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
//            templateMsg.setTemplate_id("upsa1MpVfulcu69n_f7B6kF2s8uV9ODU47estmNWuK4");
//            templateMsg.setUrl(url);
//            templateMsg.setTouser(fansInfo.getOpenid());
//            templateMsg.setData(dataItem);
//            WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(goodsInfo.getAppid()), templateMsg);
//        }
//        if(goodsInfo.getCarAuditStatus() == 3 && Constants.ISSMS){
//            /*
//            {{first.DATA}}
//            审核姓名：{{keyword1.DATA}}
//            拒绝原因：{{keyword2.DATA}}
//            {{remark.DATA}}
//
//            尊敬的司导您好，您的专车服务未通过审核！
//            审核姓名：张三 师傅
//            拒绝原因：身份证照片模糊不清
//            请填写正确的有效信息，重新申请。如有问题请点击查看司导填写教程
//            */
//            WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
//            dataItem.put("first", new WeixinTemplateMsg.DItem("尊敬的车位主，您的车位未审核通过！"));
//            dataItem.put("keyword1", new WeixinTemplateMsg.DItem(goodsInfo.getCarParkNumber()));
//            dataItem.put("keyword2", new WeixinTemplateMsg.DItem(goodsInfo.getCarAuditRemark()));
//            dataItem.put("remark", new WeixinTemplateMsg.DItem("请填写正确的有效信息，重新申请，谢谢！"));
//            String url = this.hhtcContextPath + "/portal/index.html#/publish";
//            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+goodsInfo.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+goodsInfo.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
//            WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
//            templateMsg.setTemplate_id("337mC1vqm0l4bxf8WdEKfiNYO9BOjKCWlJus7hw2bPI");
//            templateMsg.setUrl(url);
//            templateMsg.setTouser(goodsInfo.getOpenid());
//            templateMsg.setData(dataItem);
//            WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(goodsInfo.getAppid()), templateMsg);
//        }
//        return goodsInfo;
//    }



    /**
     * TOKGO 更新状态
     * @param preIsUsed 本次欲更新的状态的前置状态
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean updateStatus(long id, int isUsed, int preIsUsed){
        return 1 == goodsInforRepository.updateStatus(id, isUsed, preIsUsed);
    }

}