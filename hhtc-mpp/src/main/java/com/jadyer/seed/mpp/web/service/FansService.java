package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinFansInfo;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/15 14:02.
 */
@Service
public class FansService {
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Value("${hhtc.portalUrl.center}")
    private String portalCenterUrl;
    @Value("${hhtc.wxtemplateUrl.regauditnotpass}")
    private String templateUrlRegAuditNotpass;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private SmsService smsService;
    @Resource
    private OrderService orderService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private CommunityService communityService;
    @Resource
    private AdviceRepository adviceRepository;
    @Resource
    private FansInfoRepository fansInfoRepository;
    @Resource
    private GoodsPublishRepository goodsPublishRepository;
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;
    @Resource
    private MppUserInfoRepository mppUserInfoRepository;
    @Resource
    private AuditService auditService;


    /**
     * 查询平台某用户的所有粉丝信息
     */
    public List<MppFansInfo> getByUid(long uid){
        return fansInfoRepository.findByUid(uid);
    }

    /**
     * 查询粉丝的信息状态
     * */
    public String GetInforState(long uid, String openid){
        return fansInfoRepository.findByUidAndOpenid(uid,openid).getInfor_state();
    }


    /**
     * 查询某个粉丝的信息
     */
    public MppFansInfo getByUidAndOpenid(long uid, String openid){
        return fansInfoRepository.findByUidAndOpenid(uid, openid);
    }

    /**
     * TOKGO 更新粉丝的信息状态
     * @param NO 0--是否授权，1--是否验证电话，2--是否验证住址，3--是否验证车位 4--是否验证车牌
     * @param Sate '0' 未做 '1 '完成  '2' 审核中
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean UpdatedataInforSate(int NO,char Sate,long uid, String openid){
        MppFansInfo mppFansInfo= fansInfoRepository.findByUidAndOpenid(uid, openid);
        if (mppFansInfo==null)
            return false;
        StringBuilder sb = new StringBuilder(mppFansInfo.getInfor_state());
        sb.setCharAt(NO,Sate);
        mppFansInfo.setInfor_state(sb.toString());
        fansInfoRepository.saveAndFlush(mppFansInfo);
        return true;
    }


    /**
     * 更新粉丝的关注状态
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean unSubscribe(long uid, String openid){
        return 1 == fansInfoRepository.updateSubscribe("0", uid, openid);
    }


    /**
     * saveOrUpdate粉丝信息
     */
    @Transactional(rollbackFor=Exception.class)
    public MppFansInfo upsert(MppFansInfo mppFansInfo){
        return fansInfoRepository.saveAndFlush(mppFansInfo);
    }


    /**
     * TOKGO电话号码验证
     * */
    @Transactional(rollbackFor=Exception.class)
    public String PhoneNOCheck(String phoneNO ,String verifyCode, String openid){
        //检查是否用户授权
        if(GetInforState(2, openid).charAt(0) == '0')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_ACCREDIT);
        //短信验证
        if(!smsService.smsVerify(phoneNO, verifyCode, 1))
            throw new HHTCException(CodeEnum.HHTC_SMS_VERIFY_FAIL);
        //验证成功 进行电话写入，并修改状态
        MppFansInfo fansInfo = fansInfoRepository.findByOpenid(openid);
        fansInfo.setPhoneNo(phoneNO);
        StringBuilder sb = new StringBuilder(fansInfo.getInfor_state());
        sb.setCharAt(1,'1');
        fansInfo.setInfor_state(sb.toString());
        fansInfo = fansInfoRepository.saveAndFlush(fansInfo);
        //TODO  是否删除历史验证码
        //返回当前状态码
        return fansInfo.getInfor_state();
    }

    /**
     * TOKGO地址号审核
     * */
    @Transactional(rollbackFor=Exception.class)
    public String CommunityCheck(String FansCommunity ,String houseNumber, String openid){
        MppFansInfo mppFansInfo=fansInfoRepository.findByOpenid(openid);
        //检查是否用户验证电话
        if(GetInforState(mppFansInfo.getUid(), openid).charAt(1) == '0')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_PHOMENO);
        //检测用户是否已经提交房屋验证
        List<FansInforAudit> fansInforAudits = auditService.GetAudit(mppFansInfo.getUid(),openid,1);
        if (fansInforAudits.size() > 0){
            for (FansInforAudit fansInforAudit :fansInforAudits)
                if (fansInforAudit.getState()==0)
                    throw  new HHTCException(CodeEnum.HHTC_INFOR_COMMUNITY);
        }
        //重组信息
        String infor = FansCommunity+":"+houseNumber;
        //写入审核
        auditService.AddAudit(mppFansInfo.getUid(),openid,1,infor);
        UpdatedataInforSate(2,'2',mppFansInfo.getUid(),openid);
        //返回当前状态码
        return  fansInfoRepository.findByOpenid(openid).getInfor_state();
    }

    /**
     * TOKGO车牌号验证
     * *//*
    @Transactional(rollbackFor=Exception.class)
    public String PhoneNOCheck(String phoneNO ,String verifyCode, String openid){
        //检查是否用户授权
        if(GetInforState(2, openid).charAt(0) == '0')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_ACCREDIT);
        //短信验证
        if(!smsService.smsVerify(phoneNO, verifyCode, 1))
            throw new HHTCException(CodeEnum.HHTC_SMS_VERIFY_FAIL);
        //验证成功 进行电话写入，并修改状态
        MppFansInfo fansInfo = fansInfoRepository.findByOpenid(openid);
        fansInfo.setPhoneNo(phoneNO);
        StringBuilder sb = new StringBuilder(fansInfo.getInfor_state());
        sb.setCharAt(1,'1');
        fansInfo.setInfor_state(sb.toString());
        fansInfo = fansInfoRepository.saveAndFlush(fansInfo);
        //TODO  是否删除历史验证码
        //返回当前状态码
        return fansInfo.getInfor_state();
    }*/

    /**
     * TOKGO车位验证
     * *//*
    @Transactional(rollbackFor=Exception.class)
    public String PhoneNOCheck(String phoneNO ,String verifyCode, String openid){
        //检查是否用户授权
        if(GetInforState(2, openid).charAt(0) == '0')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_ACCREDIT);
        //短信验证
        if(!smsService.smsVerify(phoneNO, verifyCode, 1))
            throw new HHTCException(CodeEnum.HHTC_SMS_VERIFY_FAIL);
        //验证成功 进行电话写入，并修改状态
        MppFansInfo fansInfo = fansInfoRepository.findByOpenid(openid);
        fansInfo.setPhoneNo(phoneNO);
        StringBuilder sb = new StringBuilder(fansInfo.getInfor_state());
        sb.setCharAt(1,'1');
        fansInfo.setInfor_state(sb.toString());
        fansInfo = fansInfoRepository.saveAndFlush(fansInfo);
        //TODO  是否删除历史验证码
        //返回当前状态码
        return fansInfo.getInfor_state();
    }

*/



    /**
     * 分页查询待审核的粉丝信息（车主或车位主）
     * @param type：1--车主，2--车位主
     */
    public Page<MppFansInfo> listTaskViaPage(MppUserInfo userInfo, String pageNo, int type){
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        Condition<MppFansInfo> spec = null;
        //运营登录时，就正常的全部筛选
        if(userInfo.getType() == 1){
            if(1 == type){
                spec = Condition.<MppFansInfo>and().eq("uid", userInfo.getId()).eq("carOwnerAuditStatus", 1);
            }
            if(2 == type){
                spec = Condition.<MppFansInfo>and().eq("uid", userInfo.getId()).eq("carParkAuditStatus", 1);
            }
        }
        //物管登录时，只筛选他管理的小区的
        if(userInfo.getType() == 2){
            List<Long> idList = new ArrayList<>();
            for(CommunityInfo obj : communityService.getByUid(userInfo.getId())){
                idList.add(obj.getId());
            }
            if(1 == type){
                spec = Condition.<MppFansInfo>and().eq("carOwnerAuditStatus", 1).in("carOwnerCommunityId", idList);
            }
            if(2 == type){
                spec = Condition.<MppFansInfo>and().eq("carParkAuditStatus", 1).in("carParkCommunityId", idList);
            }
        }
        //执行
        return fansInfoRepository.findAll(spec, pageable);
    }


    /**
     * 分页查询粉丝信息
     */
    public Page<MppFansInfo> listViaPage(long uid, String pageNo){
        //排序
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        //分页
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        //条件
        Condition<MppFansInfo> spec = Condition.<MppFansInfo>and().eq("uid", uid);
        //执行
        return fansInfoRepository.findAll(spec, pageable);
    }


    public Page<AdviceInfo> listAdviceViaPage(String pageNo){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        Page<AdviceInfo> page = adviceRepository.findAll(pageable);
        List<AdviceInfo> list = page.getContent();
        for(AdviceInfo obj : list){
            MppFansInfo fans = this.getByOpenid(obj.getOpenid());
            obj.setNickname(fans.getNickname());
            obj.setHeadimgurl(fans.getHeadimgurl());
        }
        return page;
    }


    /**
     * 查询粉丝信息
     */
    @Transactional(rollbackFor=Exception.class)
    public MppFansInfo getByOpenid(String openid){
        MppFansInfo mppFansInfo = fansInfoRepository.findByOpenid(openid);
        //查询不到：先关注的，但应用是后上线的
        if(null == mppFansInfo){
            mppFansInfo = new MppFansInfo();
            MppUserInfo mppUserInfo = mppUserInfoRepository.findByMptypeAndBindStatus(1, 1);
            WeixinFansInfo weixinFansInfo = WeixinHelper.getWeixinFansInfo(WeixinTokenHolder.getWeixinAccessToken(mppUserInfo.getAppid()), openid);
            mppFansInfo.setUid(mppUserInfo.getId());
            //TODO
           //mppFansInfo.setWxid(mppUserInfo.getMpid());
            mppFansInfo.setOpenid(openid);
            mppFansInfo.setSubscribe(String.valueOf(weixinFansInfo.getSubscribe()));
            mppFansInfo.setNickname(JadyerUtil.escapeEmoji(weixinFansInfo.getNickname()));
            mppFansInfo.setSex(weixinFansInfo.getSex());
            mppFansInfo.setCity(weixinFansInfo.getCity());
            mppFansInfo.setCountry(weixinFansInfo.getCountry());
            mppFansInfo.setProvince(weixinFansInfo.getProvince());
            mppFansInfo.setLanguage(weixinFansInfo.getLanguage());
            mppFansInfo.setHeadimgurl(weixinFansInfo.getHeadimgurl());
            mppFansInfo.setUnionid(weixinFansInfo.getUnionid());
            mppFansInfo.setRemark(weixinFansInfo.getRemark());
            mppFansInfo.setGroupid(weixinFansInfo.getGroupid());
            mppFansInfo = fansInfoRepository.saveAndFlush(mppFansInfo);
        }
        return mppFansInfo;
    }


    /**
     * 注册前的短信校验
     * @param type：1--车主注册，2--车位主注册
     */
    private MppFansInfo verifyBeforeReg(String phoneNo, String verifyCode, int type, String openid){
        //短信验证
        if(!smsService.smsVerify(phoneNo, verifyCode, type)){
            throw new HHTCException(CodeEnum.HHTC_SMS_VERIFY_FAIL);
        }
        //SmsInfo smsInfo = smsRepository.findFirstByPhoneNoAndTypeOrderByIdDesc(phoneNo, type);
        //if(null==smsInfo || 1!=smsInfo.getUsedResult()){
        //    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "未验证短信");
        //}
        ////校验短信验证通过后，填写注册资料的时间间隔，不超过一个小时
        //if(new Date().getTime() - smsInfo.getUsedTime().getTime() > 1000*60*60){
        //    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "超时，请重新提交注册申请");
        //}
        //校验粉丝是否已关注
        MppFansInfo fansInfo = fansInfoRepository.findByOpenid(openid);
        if(null==fansInfo || "0".equals(fansInfo.getSubscribe())){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无此粉丝openid=[" + openid + "]或未关注公众号");
        }
        //校验车主或车位主注册状态
        if(1==type && fansInfo.getCarOwnerStatus()==2){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "已注册车主，请不要重复注册");
        }
        if(2==type && fansInfo.getCarParkStatus()==2){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "已注册车位主，请不要重复注册");
        }
        //校验手机号
        if(StringUtils.isBlank(fansInfo.getPhoneNo()) && fansInfoRepository.countByPhoneNo(phoneNo)>0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "手机号已被注册");
        }
        if(StringUtils.isNotBlank(fansInfo.getPhoneNo()) && !StringUtils.equals(fansInfo.getPhoneNo(), phoneNo)){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "一个人只能使用一个手机号");
        }
        return fansInfo;
    }


    /**
     * 车主注册
     * @return 是否可以继续注册车位主
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean regCarOwner(String openid, String phoneNo, String verifyCode, long carOwnerCommunityId, String carNumber, String houseNumber){
        if(StringUtils.isBlank(carNumber) || StringUtils.isBlank(houseNumber)){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "车牌号或门牌号不能为空");
        }
        //TODO -infor 查询小区id---住址是否在车位租赁的小区
        CommunityInfo communityInfo = communityService.get(carOwnerCommunityId);
        if(StringUtils.isBlank(communityInfo.getName())){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无此小区communityId=[" + carOwnerCommunityId + "]");
        }
        MppFansInfo fansInfo = this.verifyBeforeReg(phoneNo, verifyCode, 1, openid);
        fansInfo.setPhoneNo(phoneNo);
        fansInfo.setCarOwnerCommunityId(carOwnerCommunityId);
        fansInfo.setCarOwnerCommunityName(communityInfo.getName());
        fansInfo.setCarNumber(carNumber);
        fansInfo.setHouseNumber(houseNumber);
        fansInfo.setCarOwnerStatus(1);
        fansInfo.setCarOwnerAuditStatus(1);
        fansInfo.setCarOwnerRegTime(new Date());
        fansInfo = fansInfoRepository.saveAndFlush(fansInfo);
        return fansInfo.getCarParkStatus() == 0;
    }


    /**
     * 车位主注册
     * @return 是否可以继续注册车主
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean regCarPark(String appid, String openid, String phoneNo, String verifyCode, long carParkCommunityId, String carParkNumber, String carEquityImg,
                              Integer carUsefulFromDate, Integer carUsefulEndDate){
        if(StringUtils.isBlank(carParkNumber)){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "车位号不能为空");
        }
        if(carUsefulFromDate==null || carUsefulEndDate==null){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "车位有效期不能为空");
        }
        CommunityInfo communityInfo = communityService.get(carParkCommunityId);
        if(StringUtils.isBlank(communityInfo.getName())){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无此小区communityId=[" + carParkCommunityId + "]");
        }
        MppFansInfo fansInfo = this.verifyBeforeReg(phoneNo, verifyCode, 2, openid);
        fansInfo.setPhoneNo(phoneNo);
        fansInfo.setCarParkCommunityId(carParkCommunityId);
        fansInfo.setCarParkCommunityName(communityInfo.getName());
        fansInfo.setCarParkStatus(1);
        fansInfo.setCarParkAuditStatus(1);
        fansInfo.setCarParkRegTime(new Date());
        fansInfo = fansInfoRepository.saveAndFlush(fansInfo);
        //新增或更新（审核拒绝后再次注册）车位信息
        GoodsInfo goodsInfo = goodsRepository.findByOpenidAndCarParkNumber(openid, carParkNumber);
        if(null == goodsInfo){
            goodsInfo = new GoodsInfo();
        }
        goodsInfo.setCommunityId(carParkCommunityId);
        goodsInfo.setCommunityName(communityInfo.getName());
        goodsInfo.setAppid(appid);
        goodsInfo.setOpenid(openid);
        goodsInfo.setCarParkNumber(carParkNumber);
        goodsInfo.setCarEquityImg(carEquityImg);
        goodsInfo.setCarAuditStatus(1);
        goodsInfo.setIsUsed(0);
        goodsInfo.setCarUsefulFromDate(carUsefulFromDate);
        goodsInfo.setCarUsefulEndDate(carUsefulEndDate);
        goodsInfo.setIsRepetition(0);
        // 是否重复
        List<GoodsInfo> byCarParkNumber = goodsRepository.findByCarParkNumber(goodsInfo.getCarParkNumber());
        if(byCarParkNumber.size()>0){
            goodsInfo.setIsRepetition(1);
            // 更新所有车位为重复
            goodsRepository.updateStatus(goodsInfo.getCarParkNumber(),1);
        }
        goodsRepository.saveAndFlush(goodsInfo);
        return fansInfo.getCarOwnerStatus() == 0;
    }


    /**
     * 审核车主或车位主
     * @param userInfo 当前登录的用户信息（用于校验以及记录审核人）
     * @param id       粉丝ID
     * @param status   审核状态：2--审核通过，3--审核拒绝
     * @param type     类型：1--车主，2--车位主
     */
    @Transactional(rollbackFor=Exception.class)
    public void carAudit(MppUserInfo userInfo, long id, int status, int type, String auditRemark, String appid){
        MppFansInfo fansInfo = fansInfoRepository.findOne(id);
        //如果是物管，先校验其审核的是否为所管理小区的注册粉丝
        if(userInfo.getType() == 2){
            List<Long> idList = new ArrayList<>();
            for(CommunityInfo obj : communityService.getByUid(userInfo.getId())){
                idList.add(obj.getId());
            }
            if((1==type && !idList.contains(fansInfo.getCarOwnerCommunityId())) || (2==type && !idList.contains(fansInfo.getCarParkCommunityId()))){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只能审核自己管理小区的注册粉丝");
            }
        }
        //更新审核信息
        if(1 == type){
            fansInfo.setCarOwnerStatus(status==2 ? 2 : 0);
            fansInfo.setCarOwnerAuditStatus(status);
            fansInfo.setCarOwnerAuditTime(new Date());
            fansInfo.setCarOwnerAuditUid(userInfo.getId());
            fansInfo.setCarOwnerAuditRemark(auditRemark);
        }
        if(2 == type){
            fansInfo.setCarParkStatus(status==2 ? 2 : 0);
            fansInfo.setCarParkAuditStatus(status);
            fansInfo.setCarParkAuditTime(new Date());
            fansInfo.setCarParkAuditUid(userInfo.getId());
            fansInfo.setCarParkAuditRemark(auditRemark);
        }
        if(status == 2){
            if(type == 1 && Constants.ISSMS){
                //模版CODE: SMS_86570144（車主）
                //模版内容: 尊敬的手机尾号为${phone}的用户：您好！感谢您注册吼吼停车，您提交的车主资料已通过物业审核。请登录吼吼停车微信公众号开始抢车位吧！
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("phone", fansInfo.getPhoneNo().substring(7, 11));
                hhtcHelper.sendSms(fansInfo.getPhoneNo(), "SMS_86570144", paramMap);
                /*
                {{first.DATA}}
                车牌号码：{{keyword1.DATA}}
                生效时间：{{keyword2.DATA}}
                {{remark.DATA}}

                尊敬的 XXX，您的爱车已经通过审核，
                车牌号码：京A00004
                生效时间：2017-03-01
                恭喜您成为萌马成员，您将从以上生效时间起开始享受萌马为爱车提供的全面保障和贴心服务。
                */
                WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
                dataItem.put("first", new WeixinTemplateMsg.DItem("尊敬的车主，您的注册申请已经审核通过！"));
                dataItem.put("keyword1", new WeixinTemplateMsg.DItem(fansInfo.getCarNumber()));
                dataItem.put("keyword2", new WeixinTemplateMsg.DItem(DateFormatUtils.format(new Date(), "yyyy-MM-dd")));
                dataItem.put("remark", new WeixinTemplateMsg.DItem("我要抢车位，方便加一倍！点击“抢车位”，停车舒心更省心！"));
                String url = this.hhtcContextPath + "/portal/index.html#/park";
                url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+appid+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
                WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
                templateMsg.setTemplate_id("lhvy1gpBCZsBbvOMf93qNWxpF2I4j4cnBe5rt2q3z6w");
                templateMsg.setUrl(url);
                templateMsg.setTouser(fansInfo.getOpenid());
                templateMsg.setData(dataItem);
                WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(appid), templateMsg);
            }
            if(type == 2){
                //模版CODE: SMS_86725136（車位主）
                //模版内容: 尊敬的手机尾号为${phone}的用户：您好！感谢您注册吼吼停车，您提交的车位主资料已通过物业审核。请登录吼吼停车微信公众号发布您的车位吧！
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("phone", fansInfo.getPhoneNo().substring(7, 11));
                hhtcHelper.sendSms(fansInfo.getPhoneNo(), "SMS_86725136", paramMap);
                /*
                {{first.DATA}}
                手机号：{{keyword1.DATA}}
                审核结果：{{keyword2.DATA}}
                {{remark.DATA}}

                尊敬的用户，您的押金退回业务审核结果如下
                手机号：尾号3432
                审核结果：通过
                您交付平台的押金已退回您原支付账户，预计1~7个工作日到账，请注意查收。
                */
                WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
                dataItem.put("first", new WeixinTemplateMsg.DItem("尊敬的车位主，您的注册申请已经审核通过！"));
                dataItem.put("keyword1", new WeixinTemplateMsg.DItem("尾号" + fansInfo.getPhoneNo().substring(7,11)));
                dataItem.put("keyword2", new WeixinTemplateMsg.DItem("通过"));
                dataItem.put("remark", new WeixinTemplateMsg.DItem("恭喜您，您将从以上生效时间起开始享受吼吼停车提供的全面保障和贴心服务。"));
                String url = this.hhtcContextPath + this.portalCenterUrl;
                url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+appid+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
                WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
                templateMsg.setTemplate_id("upsa1MpVfulcu69n_f7B6kF2s8uV9ODU47estmNWuK4");
                templateMsg.setUrl(url);
                templateMsg.setTouser(fansInfo.getOpenid());
                templateMsg.setData(dataItem);
                WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(appid), templateMsg);
            }
        }
        //审核拒绝发送微信模板消息
        if(status == 3){
            if(type == 2){
                //删除车位
                for(GoodsInfo obj : goodsRepository.findByOpenid(fansInfo.getOpenid())){
                    if(2 == obj.getIsUsed()){
                        throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "车位已被下单");
                    }
                    if(orderService.countByGoodsIdAndOrderTypeInAndOrderStatusIn(obj.getId(), Arrays.asList(1, 2), Arrays.asList(2, 9)) > 0){
                        throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "车位已被下单或转租");
                    }
                    goodsPublishOrderRepository.deleteByGoodsId(obj.getId());
                    goodsPublishRepository.deleteByGoodsId(obj.getId());
                    goodsRepository.delete(obj.getId());
                    //更新车位重复状态
                    List<GoodsInfo> list = goodsRepository.findByCarParkNumber(obj.getCarParkNumber());
                    if(null!=list && list.size()==1){
                        goodsRepository.updateStatus(obj.getCarParkNumber(), 0);
                    }
                }
            }
            if (Constants.ISSMS) {
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
                WeixinTemplateMsg.DataItem dataItem = new WeixinTemplateMsg.DataItem();
                dataItem.put("first", new WeixinTemplateMsg.DItem("尊敬的" + (type == 1 ? "车主" : "车位主") + "，您的注册未审核通过！"));
                dataItem.put("keyword1", new WeixinTemplateMsg.DItem(fansInfo.getPhoneNo().substring(0, 3) + "***" + fansInfo.getPhoneNo().substring(8)));
                dataItem.put("keyword2", new WeixinTemplateMsg.DItem(auditRemark));
                dataItem.put("remark", new WeixinTemplateMsg.DItem("请填写正确的有效信息，重新申请，谢谢！"));
                WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
                templateMsg.setTemplate_id("337mC1vqm0l4bxf8WdEKfiNYO9BOjKCWlJus7hw2bPI");
                templateMsg.setUrl(this.hhtcContextPath + this.templateUrlRegAuditNotpass.replace("{userType}", type + ""));
                templateMsg.setTouser(fansInfo.getOpenid());
                templateMsg.setData(dataItem);
                WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(appid), templateMsg);
            }
        }
        fansInfoRepository.saveAndFlush(fansInfo);
    }


    /**
     * 添加车牌号
     */
    @Transactional(rollbackFor=Exception.class)
    public MppFansInfo carNumberAdd(String openid, String carNumber) {
        MppFansInfo fansInfo = fansInfoRepository.findByOpenid(openid);
        if(StringUtils.isNotBlank(fansInfo.getCarNumber()) && fansInfo.getCarNumber().contains(carNumber)){
            return fansInfo;
        }
        if(StringUtils.isBlank(fansInfo.getCarNumber())){
            fansInfo.setCarNumber(carNumber);
        }else{
            fansInfo.setCarNumber(fansInfo.getCarNumber() + "`" + carNumber);
        }
        return fansInfoRepository.saveAndFlush(fansInfo);
    }
}