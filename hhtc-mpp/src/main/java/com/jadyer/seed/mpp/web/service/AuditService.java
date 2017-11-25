package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import com.jadyer.seed.mpp.web.model.FansInforAudit;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.repository.FansAuditRepository;
import org.springframework.beans.factory.annotation.Value;
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
     * 删除一个记录
     *
     * */
    public void Delete(FansInforAudit fansInforAudit){
        fansAuditRepository.delete(fansInforAudit);
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

    public void SendAuditResult(String FirstData,String Key1Data,String Key2Data
            ,String RemarkData, MppFansInfor fansInfo,String appid,int type){
        /*
        {{first.DATA}}
        审核姓名：{{keyword1.DATA}}
        拒绝原因：{{keyword2.DATA}}
        {{remark.DATA}}

        尊敬的司导您好，您的专车服务未通过审核！
        审核姓名：张三 师傅
        拒绝原因：身份证照片模糊不清
        请填写正确的有效信息，重新申请。如有问题请点击查看司导填写教程
        */
        try {
            WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
            dataItem.put("first", new WeixinTemplateMsg.DItem(FirstData));
            dataItem.put("keyword1", new WeixinTemplateMsg.DItem(Key1Data));
            dataItem.put("keyword2", new WeixinTemplateMsg.DItem(Key2Data));
            dataItem.put("remark", new WeixinTemplateMsg.DItem(RemarkData));
            WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
            templateMsg.setTemplate_id("337mC1vqm0l4bxf8WdEKfiNYO9BOjKCWlJus7hw2bPI");
            templateMsg.setUrl(hhtcContextPath + this.templateUrlRegAuditNotpass.replace("{userType}", type + ""));
            templateMsg.setTouser(fansInfo.getOpenid());
            templateMsg.setData(dataItem);
            WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(appid), templateMsg);
        }catch (Exception ex){
            new HHTCException(CodeEnum.SYSTEM_ERROR.getCode(),"微信模板消息错误");
        }
    }




}
