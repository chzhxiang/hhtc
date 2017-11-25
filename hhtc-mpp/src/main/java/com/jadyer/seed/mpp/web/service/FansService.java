package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinFansInfo;
import com.jadyer.seed.mpp.web.HHTCHelper;
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
import java.util.Date;
import java.util.List;

import static com.jadyer.seed.comm.constant.Constants.*;


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
     * TOKGO 更新粉丝的信息状态并保存
     * @param InforState 0--是否授权，1--是否验证电话，2--是否验证住址，3--是否验证车位 4--是否验证车牌
     * @param Sate '0' 未做 '1 '完成  '2' 审核中
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean UpdatedataInforSate(int  InforState,char Sate,long uid, String openid){
        MppFansInfo mppFansInfo= fansInfoRepository.findByUidAndOpenid(uid, openid);
        if (mppFansInfo==null)
            return false;
        StringBuilder sb = new StringBuilder(mppFansInfo.getInfor_state());
        sb.setCharAt(InforState,Sate);
        mppFansInfo.setInfor_state(sb.toString());
        fansInfoRepository.saveAndFlush(mppFansInfo);
        return true;
    }

    /**
     * TOKGO 更新粉丝的信息状态并保存
     * @param InforState 0--是否授权，1--是否验证电话，2--是否验证住址，3--是否验证车位 4--是否验证车牌
     * @param Sate '0' 未做 '1 '完成  '2' 审核中
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean UpdatedataInforSate(int InforState,char Sate,MppFansInfo mppFansInfo){
        if (mppFansInfo==null)
            return false;
        StringBuilder sb = new StringBuilder(mppFansInfo.getInfor_state());
        sb.setCharAt(InforState,Sate);
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
        MppFansInfo fansInfo = fansInfoRepository.findByOpenid(openid);
        //检查是否用户授权
        if(GetInforState(fansInfo.getUid(), openid).charAt(INFOR_STATE_ACCREDIT_BIT) == '0')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_ACCREDIT);
        //短信验证
        if(!smsService.smsVerify(phoneNO, verifyCode, 1))
            throw new HHTCException(CodeEnum.HHTC_SMS_VERIFY_FAIL);
        //验证成功 进行电话写入，并修改状态
        fansInfo.setPhoneNo(phoneNO);
        UpdatedataInforSate(INFOR_STATE_PHOMENO_BIT,'1',fansInfo);
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
        if(GetInforState(mppFansInfo.getUid(), openid).charAt(INFOR_STATE_PHOMENO_BIT) == '0')
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
        UpdatedataInforSate(INFOR_STATE_COMMUNITY_BIT,'2',mppFansInfo);
        //返回当前状态码
        return  fansInfoRepository.findByOpenid(openid).getInfor_state();
    }




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
    public String CarNumber(String openid,String CarNumber,int InforState){
        MppFansInfo mppFansInfo=fansInfoRepository.findByOpenid(openid);
        //检查是否用户验证电话
        if(GetInforState(mppFansInfo.getUid(), openid).charAt(INFOR_STATE_PHOMENO_BIT) == '0')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_PHOMENO);
        //检测用户是否已经提交房屋验证
        List<FansInforAudit> fansInforAudits = auditService.GetAudit(mppFansInfo.getUid(),openid,1);
        if (fansInforAudits.size() > 0){
            for (FansInforAudit fansInforAudit :fansInforAudits)
                if (fansInforAudit.getState()==0)
                    throw  new HHTCException(CodeEnum.HHTC_INFOR_COMMUNITY);
        }
        //重组信息
        String infor = CarNumber+":";
        //写入审核
        auditService.AddAudit(mppFansInfo.getUid(),openid,3,infor);
        UpdatedataInforSate(INFOR_STATE_CARNUMBE_BIT,'2',mppFansInfo.getUid(),openid);
        //返回当前状态码
        return  fansInfoRepository.findByOpenid(openid).getInfor_state();





//        if(StringUtils.isBlank(carNumber) || StringUtils.isBlank(houseNumber)){
//            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "车牌号或门牌号不能为空");
//        }
//        //TODO -infor 查询小区id---住址是否在车位租赁的小区
//        CommunityInfo communityInfo = communityService.get(carOwnerCommunityId);
//        if(StringUtils.isBlank(communityInfo.getName())){
//            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无此小区communityId=[" + carOwnerCommunityId + "]");
//        }
//        MppFansInfo fansInfo = this.verifyBeforeReg(phoneNo, verifyCode, 1, openid);
//        fansInfo.setPhoneNo(phoneNo);
//        fansInfo.setCarOwnerCommunityId(carOwnerCommunityId);
//        fansInfo.setCarOwnerCommunityName(communityInfo.getName());
//        fansInfo.setCarNumber(carNumber);
//        fansInfo.setHouseNumber(houseNumber);
//        fansInfo.setCarOwnerStatus(1);
//        fansInfo.setCarOwnerAuditStatus(1);
//        fansInfo.setCarOwnerRegTime(new Date());
//        fansInfo = fansInfoRepository.saveAndFlush(fansInfo);
//        return fansInfo.getCarParkStatus() == 0;
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


//    /**
//     * 审核车主或车位主
//     * @param userInfo 当前登录的用户信息（用于校验以及记录审核人）
//     * @param id       粉丝ID
//     * @param status   审核状态：1--审核通过，2--审核拒绝
//     * @param type     类型：1--车主，2--车位主
//     */
    /**
     * TOKGO审核粉丝地址、车牌和车位
     * @param userInfo 审核员对象
     * @param fansInforAudit 审核数据
     * @param status   审核状态：1--审核通过，2--审核拒绝
     * */
    @Transactional(rollbackFor=Exception.class)
    public void Audit(MppUserInfo userInfo,FansInforAudit fansInforAudit,int status, String auditRemark, String appid){
        MppFansInfo fansInfo = fansInfoRepository.findByOpenid(fansInforAudit.getOpenid());
        String FirstData = null, Key1Data = null, Key2Data, RemarkData ;
        //·TODO   这个验证现在还有问题
        //如果是物管，先校验其审核的是否为所管理小区的注册粉丝
//
//        if(userInfo.getType() == 2){
//            List<Long> idList = new ArrayList<>();
//            for(CommunityInfo obj : communityService.getByUid(userInfo.getId())){
//                idList.add(obj.getId());
//            }
//            if((1==type && !idList.contains(fansInfo.getCarOwnerCommunityId())) || (2==type && !idList.contains(fansInfo.getCarParkCommunityId()))){
//                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只能审核自己管理小区的注册粉丝");
//            }
//        }
        fansInforAudit.setState(status);
        fansInforAudit.setAudit_reason(auditRemark);
        Key2Data = auditRemark;
        RemarkData = "请填写正确的有效信息，重新申请，谢谢！";
        //住房审核
        if (fansInforAudit.getType() == Constants.AUDTI_TEPY_COMMUNITY) {
            Key1Data ="管理员";
            if (status == 1) {
                // TODO 这里解码有问题
                FirstData = "尊敬的用户，你的地址审核通过了";
                Key1Data ="管理员";
                fansInfo.setCarOwnerCommunityName(fansInforAudit.getContent());
                UpdatedataInforSate(INFOR_STATE_COMMUNITY_BIT, '1', fansInfo);
            }else {
                FirstData = "尊敬的用户，你的地址未审核通过";
                Key1Data ="管理员";
                UpdatedataInforSate(INFOR_STATE_COMMUNITY_BIT, '0', fansInfo);
            }
        }
        //车位审核
        else if (fansInforAudit.getType() == Constants.AUDTI_TEPY_CARLOCAL){
            if (status == 1) {
                //TODO  写审核通过的事件
                UpdatedataInforSate(INFOR_STATE_CARLOCAL_BIT, '1', fansInfo);
            }else
                UpdatedataInforSate(INFOR_STATE_CARLOCAL_BIT, '0', fansInfo);

        }
        //车牌审核
        else{
            if (status == 1) {
                //TODO  写审核通过的事件
                UpdatedataInforSate(INFOR_STATE_CARNUMBE_BIT, '1', fansInfo);
            }else
                UpdatedataInforSate(INFOR_STATE_CARNUMBE_BIT, '0',fansInfo);
        }
        //审核结果发送微信模板消息
        auditService.SendAuditResult(FirstData,Key1Data,Key2Data,RemarkData,fansInfo,appid,2);
        //


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