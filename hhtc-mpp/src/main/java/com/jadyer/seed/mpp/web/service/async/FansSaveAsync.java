package com.jadyer.seed.mpp.web.service.async;

import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinFansInfo;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.model.UserFunds;
import com.jadyer.seed.mpp.web.service.FansService;
import com.jadyer.seed.mpp.web.service.UserFundsService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Component
@Async("mppExecutor")
public class FansSaveAsync {
    @Resource
    private FansService fansService;
    @Resource
    private UserFundsService userFundsService;

    /**
     * TOKGO 保存粉丝列表
     * **/
    public void save(MppUserInfo mppUserInfo, String openid){
        try{
            //首先查询改用户是否存在
            MppFansInfor mppFansInfor = fansService.getByUidAndOpenid(mppUserInfo.getId(), openid);
            if(null == mppFansInfor)
                mppFansInfor = new MppFansInfor();
            loaddata(mppFansInfor,mppUserInfo,openid);
            //创建钱包系统
            UserFunds userFunds = userFundsService.get(openid);
            if(null == userFunds){
                userFunds = new UserFunds();
                userFunds.setOpenid(openid);
                userFunds.setMoneyBase(new BigDecimal(0));
                userFunds.setMoneyBalance(new BigDecimal(0));
                userFundsService.save(userFunds);
            }
        }catch(Exception e){
            LogUtil.logToTask().error("致命异常：粉丝信息openid=["+openid+"]异步保存时发生异常，堆栈轨迹如下：", e);
        }
    }


    /**
     * TOKGO 装填数据
     * */

    private void loaddata(MppFansInfor mppFansInfor,MppUserInfo mppUserInfo, String openid){
         /*
             * 向微信服务器查询粉丝信息  (QQ服务器还可以QQ 他的原来工程中有)
             */
        WeixinFansInfo weixinFansInfo = WeixinHelper.getWeixinFansInfo(WeixinTokenHolder.getWeixinAccessToken(mppUserInfo.getAppid()), openid);
        mppFansInfor.setUid(mppUserInfo.getId());
        mppFansInfor.setOpenid(openid);
        mppFansInfor.setSubscribe(String.valueOf(weixinFansInfo.getSubscribe()));
        mppFansInfor.setSubscribeTime(DateFormatUtils.format(new Date(Long.parseLong(weixinFansInfo.getSubscribe_time())*1000), "yyyy-MM-dd HH:mm:ss"));
        mppFansInfor.setNickname(JadyerUtil.escapeEmoji(weixinFansInfo.getNickname()));
        mppFansInfor.setSex(weixinFansInfo.getSex());
        mppFansInfor.setCity(weixinFansInfo.getCity());
        mppFansInfor.setCountry(weixinFansInfo.getCountry());
        mppFansInfor.setProvince(weixinFansInfo.getProvince());
        mppFansInfor.setLanguage(weixinFansInfo.getLanguage());
        mppFansInfor.setHeadimgurl(weixinFansInfo.getHeadimgurl());
        mppFansInfor.setUnionid(weixinFansInfo.getUnionid());
        mppFansInfor.setRemark(weixinFansInfo.getRemark());
        mppFansInfor.setGroupid(weixinFansInfo.getGroupid());
        fansService.upsert(mppFansInfor);
    }

}