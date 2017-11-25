package com.jadyer.seed.mpp.web.service.async;

import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinFansInfo;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.FansService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
@Async("mppExecutor")
public class FansSaveAsync {
    @Resource
    private FansService fansService;

    /**
     * 保存粉丝列表
     * **/
    public void save(MppUserInfo mppUserInfo, String openid){
        try{
            MppFansInfor MppFansInfor = fansService.getByUidAndOpenid(mppUserInfo.getId(), openid);
            if(null == MppFansInfor){
                MppFansInfor = new MppFansInfor();
            }
            /*
             * 向微信服务器或QQ服务器查询粉丝信息
             */
            //if(1 == mppUserInfo.getMptype()){
                WeixinFansInfo weixinFansInfo = WeixinHelper.getWeixinFansInfo(WeixinTokenHolder.getWeixinAccessToken(mppUserInfo.getAppid()), openid);
                MppFansInfor.setUid(mppUserInfo.getId());
                //TODO
               // MppFansInfor.setWxid(mppUserInfo.getMpid());
                MppFansInfor.setOpenid(openid);
                MppFansInfor.setSubscribe(String.valueOf(weixinFansInfo.getSubscribe()));
                MppFansInfor.setSubscribeTime(DateFormatUtils.format(new Date(Long.parseLong(weixinFansInfo.getSubscribe_time())*1000), "yyyy-MM-dd HH:mm:ss"));
                MppFansInfor.setNickname(JadyerUtil.escapeEmoji(weixinFansInfo.getNickname()));
                MppFansInfor.setSex(weixinFansInfo.getSex());
                MppFansInfor.setCity(weixinFansInfo.getCity());
                MppFansInfor.setCountry(weixinFansInfo.getCountry());
                MppFansInfor.setProvince(weixinFansInfo.getProvince());
                MppFansInfor.setLanguage(weixinFansInfo.getLanguage());
                MppFansInfor.setHeadimgurl(weixinFansInfo.getHeadimgurl());
                MppFansInfor.setUnionid(weixinFansInfo.getUnionid());
                MppFansInfor.setRemark(weixinFansInfo.getRemark());
                MppFansInfor.setGroupid(weixinFansInfo.getGroupid());
                fansService.upsert(MppFansInfor);
            //}else{
            //    QQFansInfo qqFansInfo = QQHelper.getQQFansInfo(QQTokenHolder.getQQAccessToken(mppUserInfo.getAppid()), openid);
            //    MppFansInfor.setUid(mppUserInfo.getId());
            //    MppFansInfor.setWxid(mppUserInfo.getMpid());
            //    MppFansInfor.setOpenid(openid);
            //    MppFansInfor.setSubscribe(String.valueOf(qqFansInfo.getSubscribe()));
            //    MppFansInfor.setSubscribeTime(DateFormatUtils.format(new Date(Long.parseLong(qqFansInfo.getSubscribe_time())*1000), "yyyy-MM-dd HH:mm:ss"));
            //    MppFansInfor.setNickname(JadyerUtil.escapeEmoji(qqFansInfo.getNickname()));
            //    MppFansInfor.setSex(qqFansInfo.getSex());
            //    MppFansInfor.setCity(qqFansInfo.getCity());
            //    MppFansInfor.setCountry(qqFansInfo.getCountry());
            //    MppFansInfor.setProvince(qqFansInfo.getProvince());
            //    MppFansInfor.setLanguage(qqFansInfo.getLanguage());
            //    MppFansInfor.setHeadimgurl(qqFansInfo.getHeadimgurl());
            //    MppFansInfor.setUnionid(qqFansInfo.getUnionid());
            //    MppFansInfor.setRemark(qqFansInfo.getRemark());
            //    MppFansInfor.setGroupid(qqFansInfo.getGroupid());
            //    fansService.upsert(MppFansInfor);
            //}
        }catch(Exception e){
            LogUtil.logToTask().error("致命异常：粉丝信息openid=["+openid+"]异步保存时发生异常，堆栈轨迹如下：", e);
        }
    }
}