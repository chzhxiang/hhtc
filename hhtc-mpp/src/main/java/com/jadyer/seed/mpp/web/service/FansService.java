package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.constant.WxMsgEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinFansInfo;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.*;
import com.jadyer.seed.mpp.web.service.async.WeixinTemplateMsgAsync;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

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
    private AuditService auditService;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private SmsService smsService;
    @Resource
    private OrderService orderService;
    @Resource
    private AdviceRepository adviceRepository;
    @Resource
    private CommunityService communityService;
    @Resource
    private FansInforRepository fansInforRepository;
    @Resource
    private GoodsInforRepository goodsInforRepository;
    @Resource
    private MppUserInfoRepository mppUserInfoRepository;
    @Resource
    private OwnersInforRepository ownersInforRepository;
    @Resource
    private WeixinTemplateMsgAsync weixinTemplateMsgAsync;


    /**
     * 查询平台某用户的所有粉丝信息
     */
    public List<MppFansInfor> getByUid(long uid){
        return fansInforRepository.findByUid(uid);
    }

    /**
     * 查询粉丝的信息状态
     * */
    public String GetInforState(long uid, String openid){
        return fansInforRepository.findByUidAndOpenid(uid,openid).getInfor_state();
    }

    /**
     * 查询粉丝的信息状态
     * */
    public char GetInforState(long uid, String openid,int inforbit){
        return fansInforRepository.findByUidAndOpenid(uid,openid).getInfor_state().charAt(inforbit);
    }

    /**
     * 查询某个粉丝的信息
     */
    public MppFansInfor getByUidAndOpenid(long uid, String openid){
        return fansInforRepository.findByUidAndOpenid(uid, openid);
    }

    /**
     * TOKGO 更新粉丝的信息状态并保存
     * @param InforState 0--是否授权，1--是否验证电话，2--是否验证住址，3--是否验证车位 4--是否验证车牌
     * @param Sate '0' 未做 '1 '完成  '2' 审核中
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean UpdatedataInforSate(int  InforState,char Sate,long uid, String openid){
        MppFansInfor MppFansInfor= fansInforRepository.findByUidAndOpenid(uid, openid);
        if (MppFansInfor==null)
            return false;
        StringBuilder sb = new StringBuilder(MppFansInfor.getInfor_state());
        sb.setCharAt(InforState,Sate);
        MppFansInfor.setInfor_state(sb.toString());
        fansInforRepository.saveAndFlush(MppFansInfor);
        return true;
    }

    /**
     * TOKGO 更新粉丝的信息状态并保存
     * @param InforState 0--是否授权，1--是否验证电话，2--是否验证住址，3--是否验证车位 4--是否验证车牌
     * @param Sate '0' 未做 '1 '完成  '2' 审核中
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean UpdatedataInforSate(int InforState,char Sate,MppFansInfor MppFansInfor){
        if (MppFansInfor==null)
            return false;
        StringBuilder sb = new StringBuilder(MppFansInfor.getInfor_state());
        sb.setCharAt(InforState,Sate);
        MppFansInfor.setInfor_state(sb.toString());
        fansInforRepository.saveAndFlush(MppFansInfor);
        return true;
    }



    /**
     * 更新粉丝的关注状态
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean unSubscribe(long uid, String openid){
        return 1 == fansInforRepository.updateSubscribe("0", uid, openid);
    }


    /**
     *  TOKGO saveOrUpdate粉丝信息
     */
    @Transactional(rollbackFor=Exception.class)
    public MppFansInfor upsert(MppFansInfor MppFansInfor){
        return fansInforRepository.saveAndFlush(MppFansInfor);
    }

    /**
     * TOKGO电话号码验证
     * */
    @Transactional(rollbackFor=Exception.class)
    public String PhoneNOCheck(String phoneNO ,String verifyCode, String openid){
        MppFansInfor fansInfor = fansInforRepository.findByOpenid(openid);
        //检查是否用户授权
        if(fansInfor.getInfor_state().charAt(INFOR_STATE_ACCREDIT_BIT) == '0')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_ACCREDIT);
        //短信验证
        if(!smsService.smsVerify(phoneNO, verifyCode, 1))
            throw new HHTCException(CodeEnum.HHTC_SMS_VERIFY_FAIL);
//        //验证成功 进行电话写入，并修改状态
        fansInfor.setPhoneNo(phoneNO);
        UpdatedataInforSate(INFOR_STATE_PHOMENO_BIT,'1',fansInfor);
        //返回当前状态码
        return fansInfor.getInfor_state();
    }


    /**
     * TOKGO电话号码注销
     * */
    @Transactional(rollbackFor=Exception.class)
    public String PhoneNODelete(String phoneNO ,String verifyCode, String openid){
        MppFansInfor fansInfor = fansInforRepository.findByOpenid(openid);
        //检查是否用户授权
        if(fansInfor.getInfor_state().charAt(INFOR_STATE_PHOMENO_BIT) != '1')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_PHOMENO);
        //短信验证
        if(!smsService.smsVerify(phoneNO, verifyCode, 2))
            throw new HHTCException(CodeEnum.HHTC_SMS_VERIFY_FAIL);
        //验证成功 进行电话写入，并修改状态
        fansInfor.setPhoneNo(null);
        UpdatedataInforSate(INFOR_STATE_PHOMENO_BIT,'0',fansInfor);
        //返回当前状态码
        return fansInfor.getInfor_state();
    }


    /**
     * TOKGO地址号审核
     * */
    @Transactional(rollbackFor=Exception.class)
    public String CommunityCheck(long CommunityID ,String houseNumber, String openid){
        if(StringUtils.isBlank(houseNumber)){
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_NULL);
        }
        MppFansInfor mppFansInfor= fansInforRepository.findByOpenid(openid);
        //检查是否用户验证电话
        if(GetInforState(mppFansInfor.getUid(), openid,INFOR_STATE_PHOMENO_BIT)== '0')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_PHOMENO);
        //检测用户是否已经提交房屋验证
        List<FansInforAudit> fansInforAudits = auditService.GetAudit(mppFansInfor.getUid(),openid,AUDTI_TEPY_COMMUNITY);
        if (fansInforAudits.size() > 0){
            throw  new HHTCException(CodeEnum.HHTC_INFOR_COMMUNITY);
        }
        CommunityInfo communityInfo = communityService.get(CommunityID);
        //写入审核
        auditService.AddAudit(mppFansInfor,AUDTI_TEPY_COMMUNITY,CommunityID,communityInfo.getName(),houseNumber);
        UpdatedataInforSate(INFOR_STATE_COMMUNITY_BIT,'2',mppFansInfor);
        //返回当前状态码
        return  fansInforRepository.findByOpenid(openid).getInfor_state();
    }



    /**
     * TOKGO 分页查询待审核的车位列表
     * @param tuype 类型：1--住房地址，2--车位，3--车牌
     * @param pageNo zero-based page index
     */
    public Page<FansInforAudit> TaskViaPagelist(MppUserInfo userInfo, String pageNo,int tuype){
        //排序
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        //分页
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        //条件（物管只能查询自己小区的车位列表）
        Condition<FansInforAudit> spec = Condition.<FansInforAudit>and().eq("type", tuype).eq("state",0);
        if(userInfo.getType() == 2){
            List<Long> idList = new ArrayList<>();
            for(CommunityInfo obj : communityService.getByUid(userInfo.getId())){
                idList.add(obj.getId());
            }
            spec.in("communityId", idList);
        }
        //执行
        Page<FansInforAudit> page = auditService.getpage(spec, pageable);
        List<FansInforAudit> list = page.getContent();
        for(FansInforAudit obj : list){
            MppFansInfor fans = getByOpenid(obj.getOpenid());
            obj.setNickname(fans.getNickname());
            obj.setHeadimgurl(fans.getHeadimgurl());
            obj.setPhone(fans.getPhoneNo());
            if (tuype != 1)
                obj.setCommunity(fans.getCommunityName()+fans.getHouseNumber());
        }
        return page;
    }





    /**
     * 分页查询待审核的粉丝信息（车主或车位主）
     * @param type：1--车主，2--车位主
     */
    public Page<MppFansInfor> listTaskViaPage(MppUserInfo userInfo, String pageNo, int type){
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        Condition<MppFansInfor> spec = null;
        //运营登录时，就正常的全部筛选
        if(userInfo.getType() == 1){
            if(1 == type){
                spec = Condition.<MppFansInfor>and().eq("uid", userInfo.getId()).eq("carOwnerAuditStatus", 1);
            }
            if(2 == type){
                spec = Condition.<MppFansInfor>and().eq("uid", userInfo.getId()).eq("carParkAuditStatus", 1);
            }
            spec = Condition.<MppFansInfor>or().eq("uid", userInfo.getId()).eq("carParkAuditStatus", 1);

        }
        //物管登录时，只筛选他管理的小区的
        if(userInfo.getType() == 2){
            List<Long> idList = new ArrayList<>();
            for(CommunityInfo obj : communityService.getByUid(userInfo.getId())){
                idList.add(obj.getId());
            }
            if(1 == type){
                spec = Condition.<MppFansInfor>and().eq("carOwnerAuditStatus", 1).in("carOwnerCommunityId", idList);
            }
            if(2 == type){
                spec = Condition.<MppFansInfor>and().eq("carParkAuditStatus", 1).in("carParkCommunityId", idList);
            }
        }
        //执行
        return fansInforRepository.findAll(spec, pageable);
    }


    /**
     * 分页查询粉丝信息
     */
    public Page<MppFansInfor> listViaPage(long uid, String pageNo){
        //排序
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        //分页
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        //条件
        Condition<MppFansInfor> spec = Condition.<MppFansInfor>and().eq("uid", uid);
        //执行
        return fansInforRepository.findAll(spec, pageable);
    }


    public Page<AdviceInfor> listAdviceViaPage(String pageNo){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        Page<AdviceInfor> page = adviceRepository.findAll(pageable);
        List<AdviceInfor> list = page.getContent();
        for(AdviceInfor obj : list){
            MppFansInfor fans = this.getByOpenid(obj.getOpenid());
            obj.setNickname(fans.getNickname());
            obj.setHeadimgurl(fans.getHeadimgurl());
        }
        return page;
    }


    /**
     * 查询粉丝信息
     */
    @Transactional(rollbackFor=Exception.class)
    public MppFansInfor getByOpenid(String openid){
        MppFansInfor MppFansInfor = fansInforRepository.findByOpenid(openid);
        // TODO 查询不到：先关注的，但应用是后上线的
        if(null == MppFansInfor){
            MppFansInfor = new MppFansInfor();
            MppUserInfo mppUserInfo = mppUserInfoRepository.findByMptypeAndBindStatus(1, 1);
            WeixinFansInfo weixinFansInfo = WeixinHelper.getWeixinFansInfo(WeixinTokenHolder.getWeixinAccessToken(mppUserInfo.getAppid()), openid);
            MppFansInfor.setUid(mppUserInfo.getId());
            //TODO
           //MppFansInfor.setWxid(mppUserInfo.getMpid());
            MppFansInfor.setOpenid(openid);
            MppFansInfor.setSubscribe(String.valueOf(weixinFansInfo.getSubscribe()));
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
            MppFansInfor = fansInforRepository.saveAndFlush(MppFansInfor);
        }
        return MppFansInfor;
    }


    /**
     * TOKGO 获取用户当前车牌信息
     * */
    public List<HashMap> getFansCarNumberInfor(String openid){
        List<HashMap> list = new ArrayList<>();
        HashMap hashMap;
        MppFansInfor mppFansInfor = fansInforRepository.findByOpenid(openid);
        if (mppFansInfor==null )
            throw new HHTCException(CodeEnum.SYSTEM_NULL);
        //添加已有的数据
        for (OwnersInfor ownersInfor:ownersInforRepository.findByOpenid(openid)){
            hashMap = new HashMap();
            hashMap.put("state","work");
            hashMap.put("id",ownersInfor.getId());
            hashMap.put("infor",ownersInfor.getCaNumber());
            list.add(hashMap);
        }
        //添加审核数据
        for (FansInforAudit carnumber:auditService.GetAudit(2,openid,3)){
            hashMap = new HashMap();
            hashMap.put("state","audit");
            hashMap.put("id",carnumber.getId());
            hashMap.put("infor",carnumber.getContent());
            list.add(hashMap);
        }
        return list;
    }



    /**
     * 注册前的短信校验
     * @param type：1--车主注册，2--车位主注册
     */
    private MppFansInfor verifyBeforeReg(String phoneNo, String verifyCode, int type, String openid){
        //短信验证
        if(!smsService.smsVerify(phoneNo, verifyCode, type)){
            throw new HHTCException(CodeEnum.HHTC_SMS_VERIFY_FAIL);
        }
        //SmsInfor smsInfo = smsRepository.findFirstByPhoneNoAndTypeOrderByIdDesc(phoneNo, type);
        //if(null==smsInfo || 1!=smsInfo.getUsedResult()){
        //    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "未验证短信");
        //}
        ////校验短信验证通过后，填写注册资料的时间间隔，不超过一个小时
        //if(new Date().getTime() - smsInfo.getUsedTime().getTime() > 1000*60*60){
        //    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "超时，请重新提交注册申请");
        //}
        //校验粉丝是否已关注
        MppFansInfor fansInfo = fansInforRepository.findByOpenid(openid);
        if(null==fansInfo || "0".equals(fansInfo.getSubscribe())){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无此粉丝openid=[" + openid + "]或未关注公众号");
        }
        //TODO
//        //校验车主或车位主注册状态
//        if(1==type && fansInfo.getCarOwnerStatus()==2){
//            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "已注册车主，请不要重复注册");
//        }
//        if(2==type && fansInfo.getCarParkStatus()==2){
//            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "已注册车位主，请不要重复注册");
//        }
        //校验手机号
        if(StringUtils.isBlank(fansInfo.getPhoneNo()) && fansInforRepository.countByPhoneNo(phoneNo)>0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "手机号已被注册");
        }
        if(StringUtils.isNotBlank(fansInfo.getPhoneNo()) && !StringUtils.equals(fansInfo.getPhoneNo(), phoneNo)){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "一个人只能使用一个手机号");
        }
        return fansInfo;
    }

    /**
     * TOKGO 车牌注销
     */
    @Transactional(rollbackFor=Exception.class)
    public String CarNumberLogout(String openid,long id,String state){
        if(StringUtils.isBlank(state)){
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_NULL);
        }
        if ("audit".equals(state)){
            auditService.Delete(id);
        }
        else{
            ownersInforRepository.delete(id);
            if (ownersInforRepository.findByOpenid(openid).size()==0)
                UpdatedataInforSate(INFOR_STATE_CARPARK_BIT, '0', fansInforRepository.findByOpenid(openid));

        }
        return  fansInforRepository.findByOpenid(openid).getInfor_state();
    }



    /**
     * TOKGO 车牌审核请求
     */
    @Transactional(rollbackFor=Exception.class)
    public HashMap CarNumber(String openid,String CarNumber,String carNumberImg){
        FansInforAudit fansInforAudit;
        if(StringUtils.isBlank(CarNumber)){
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_NULL);
        }
        MppFansInfor mppFansInfor= fansInforRepository.findByOpenid(openid);
        //检查是否用户验证电话
        if(mppFansInfor.getInfor_state().charAt(INFOR_STATE_PHOMENO_BIT) == '0')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_PHOMENO);
        if(mppFansInfor.getInfor_state().charAt(INFOR_STATE_COMMUNITY_BIT) == '0')
            throw  new HHTCException(CodeEnum.HHTC_INFOR_COMMUNITY);
        //检测用户是否已经提交改车牌号 或者提交了两次车牌审核
        CheckCarnumber(mppFansInfor,CarNumber);
         if (mppFansInfor.getInfor_state().charAt(INFOR_STATE_PHOMENO_BIT) == '1'){
            //写入审核
            fansInforAudit= auditService.AddAudit(mppFansInfor,AUDTI_TEPY_CARNUMBER,mppFansInfor.getCommunityId()
                    ,mppFansInfor.getCommunityName(),CarNumber,carNumberImg);
        }else{
             List<FansInforAudit> audit = auditService.GetAudit(mppFansInfor.getUid(),openid,AUDTI_TEPY_CARNUMBER);
             if (audit.size()<1)
                 throw new HHTCException(CodeEnum.SYSTEM_PARAM_DATA_ERROR);
            //写入审核
             fansInforAudit= auditService.AddAudit(mppFansInfor,AUDTI_TEPY_CARNUMBER,audit.get(0).getCommunityId()
                     ,audit.get(0).getCommunityName(),CarNumber,carNumberImg);
        }
        if (mppFansInfor.getInfor_state().charAt(INFOR_STATE_CARNUMBE_BIT)!='1')
            UpdatedataInforSate(INFOR_STATE_CARNUMBE_BIT,'2',mppFansInfor);
        //返回当前车牌信息
        HashMap hashMap = new HashMap();
        hashMap.put("state","audit");
        hashMap.put("id",fansInforAudit.getId());
        hashMap.put("infor",CarNumber);
        return  hashMap;
    }

    /**
     * TOKGO 检测用户车牌请求是否符合要求
     * */
    private void CheckCarnumber(MppFansInfor mppFansInfor,String CarNumber){

        List<FansInforAudit> fansInforAudits = auditService.GetAudit(mppFansInfor.getUid()
                ,mppFansInfor.getOpenid(),AUDTI_TEPY_CARNUMBER);
        List<OwnersInfor> ownersInfors = ownersInforRepository.findByOpenid(mppFansInfor.getOpenid());
        if ((ownersInfors.size()+fansInforAudits.size())>Constants.S_CARNUMBERMAX) {
            //判断用户已经拥有了两块车牌
            throw new HHTCException(CodeEnum.HHTC_INFOR_CARNUMBERFULL);
        }
        //判断用户是否已经提交请求
        for (FansInforAudit fansInforAudit :fansInforAudits) {
            if (CarNumber.equals(fansInforAudit.getContent()))
                throw new HHTCException(CodeEnum.HHTC_INFOR_CARNUMBER_USED);
        }
    }


//    /**
//     * 审核车主或车位主
//     * @param userInfo 当前登录的用户信息（用于校验以及记录审核人）
//     * @param id       粉丝ID
//     * @param status   审核状态：1--审核通过，2--审核拒绝
//     * @param type     类型：1--车主，2--车位主
//     */
    /**
     * TOKGO 审核粉丝地址、车牌和车位
     * @param userInfo 审核员对象
     * @param fansInforAudit 审核数据
     * @param status   审核状态：1--审核通过，2--审核拒绝
     * */
    @Transactional(rollbackFor=Exception.class)
    public void Audit(MppUserInfo userInfo,FansInforAudit fansInforAudit,int status, String auditRemark, String appid){
        MppFansInfor fansInfor = fansInforRepository.findByOpenid(fansInforAudit.getOpenid());
        String FirstData = null, Key1Data = null, Key2Data, RemarkData ;
        //如果是物管，先校验其审核的是否为所管理小区的注册粉丝
        if(userInfo.getType() == 2){
            List<Long> idList = new ArrayList<>();
            for(CommunityInfo obj : communityService.getByUid(userInfo.getId()))
                idList.add(obj.getId());
            if(!idList.contains(fansInfor.getCommunityId()))
                throw new HHTCException(CodeEnum.SYSTEM_PERMISSIONS);
        }
        Key2Data = auditRemark;
        RemarkData = "请填写正确的有效信息，重新申请，谢谢！";
        Key1Data ="管理员";
        //住房审核
        if (fansInforAudit.getType() == Constants.AUDTI_TEPY_COMMUNITY) {
            if (status == 1) {
                FirstData = "尊敬的用户，你的地址审核通过了";
                fansInfor.setCommunityId(fansInforAudit.getCommunityId());
                fansInfor.setCommunityName(fansInforAudit.getCommunityName());
                //地址改变 改变用户的相关请求 跟新状态并保存
                ChangeAdrres(fansInfor,fansInforAudit);
            }else {
                FirstData = "尊敬的用户，你的地址审核未通过";
                //如果小区审核没有通过 但是其历史绑定过，则使用原来的小区
                if (!StringUtils.isBlank(fansInfor.getCommunityName()))
                    UpdatedataInforSate(INFOR_STATE_COMMUNITY_BIT, '1', fansInfor);
                else {
                    UpdatedataInforSate(INFOR_STATE_COMMUNITY_BIT, '0', fansInfor);
                }
            }
        }
        //车位审核
        else if (fansInforAudit.getType() == Constants.AUDTI_TEPY_CARPARK){
            if (status == 1) {
                FirstData = "尊敬的用户，你的车位："+fansInforAudit.getContent()+",审核通过了";
                AddGoods(fansInfor,fansInforAudit,appid);
                UpdatedataInforSate(INFOR_STATE_CARPARK_BIT, '1', fansInfor);
            }else {
                FirstData = "尊敬的用户，你的车牌："+fansInforAudit.getContent()+",审核未通过";
                if (goodsInforRepository.findByOpenid(fansInfor.getOpenid()).size()==0)
                    UpdatedataInforSate(INFOR_STATE_CARPARK_BIT, '0', fansInfor);
            }
        }
        //车牌审核
        else{
            if (status == 1) {
                FirstData = "尊敬的用户，你的车位："+fansInforAudit.getContent()+",审核通过了";
                //TODO  uid 没有获取
                AddCarNumber(fansInfor,fansInforAudit.getContent(),fansInforAudit.getImgurl1(),0);
                UpdatedataInforSate(INFOR_STATE_CARNUMBE_BIT, '1', fansInfor);
            }else {
                FirstData = "尊敬的用户，你的车牌："+fansInforAudit.getContent()+",审核未通过";
                if (ownersInforRepository.findByOpenid(fansInfor.getOpenid()).size()==0)
                    UpdatedataInforSate(INFOR_STATE_CARNUMBE_BIT, '0', fansInfor);
            }
        }
        //审核结果发送微信模板消息
        if (status == 1)
            weixinTemplateMsgAsync.Send(FirstData,Key1Data,Key2Data,RemarkData,appid
                    ,fansInfor.getOpenid(), WxMsgEnum.AUDIT_NOTPASS);
        else
            weixinTemplateMsgAsync.Send(FirstData,Key1Data,Key2Data,RemarkData,appid
                    ,fansInfor.getOpenid(), WxMsgEnum.AUDIT_NOTPASS);
        //删除审核记录
        auditService.Delete(fansInforAudit);
    }

    /**
     * TOKGO  地址改变 删除以前的绑定数据和审查 并保存用户状态
     * */
    private void ChangeAdrres(MppFansInfor fansInfor,FansInforAudit fansInforAudit){
        int ownerfalg =0,carparkflag=0;
        //检测车牌
        for (OwnersInfor ownersInfor: ownersInforRepository.findByOpenid(fansInfor.getOpenid())){
            if (ownersInfor.getCommunityId() != fansInforAudit.getCommunityId())
                ownersInforRepository.delete(ownersInfor);
            else
                ownerfalg =1;
        }
        //检测车位
        for (GoodsInfor goodsInfor: goodsInforRepository.findByOpenid(fansInfor.getOpenid())){
            if (goodsInfor.getCommunityId() != fansInforAudit.getCommunityId())
                goodsInforRepository.delete(goodsInfor);
            else
                carparkflag=1;
        }
        //检测审核的车牌和车位
        for (FansInforAudit audit:auditService.GetAudit(fansInfor.getUid(),fansInfor.getOpenid()
                ,AUDTI_TEPY_CARPARK)){
            if (audit.getCommunityId() != fansInforAudit.getCommunityId())
                auditService.Delete(audit);
            else {
                auditService.UpdataState(audit.getId());
                if (ownerfalg == 0)
                    ownerfalg = 2;
            }
        }
        for (FansInforAudit audit:auditService.GetAudit(fansInfor.getUid(),fansInfor.getOpenid()
                ,AUDTI_TEPY_CARNUMBER)){
            if (audit.getCommunityId() != fansInforAudit.getCommunityId())
                auditService.Delete(audit);
            else {
                auditService.UpdataState(audit.getId());
                if (carparkflag == 0)
                    carparkflag = 2;
            }
        }
        String string = fansInfor.getInfor_state().substring(0,2);
        fansInfor.setInfor_state(string+"1"+carparkflag+ownerfalg);
        fansInforRepository.saveAndFlush(fansInfor);
    }



    /**
     * TOKGO  添加车牌
     * */
    private void AddCarNumber( MppFansInfor fansInfor,String Carnumber,String img,long uid){
        //TODO 车牌重复检测
        OwnersInfor ownersInfor = new OwnersInfor();
        ownersInfor.setOpenid(fansInfor.getOpenid());
        ownersInfor.setCommunityId(fansInfor.getCommunityId());
        ownersInfor.setCommunityName(fansInfor.getCommunityName());
        ownersInfor.setCaNumber(Carnumber);
        ownersInfor.setCarNumberImg(img);
        ownersInfor.setCarAuditUid(uid);
        ownersInforRepository.save(ownersInfor);
    }

    /**
     * TOKGO 添加车位
     * */
    private void AddGoods(MppFansInfor fansInfor,FansInforAudit fansInforAudit, String appid){

        // TODO 新增或更新（审核拒绝后再次注册）车位信息
//        GoodsInfo goodsInfo = goodsRepository.findByOpenidAndCarParkNumber(openid, carParkNumber);
//        if(null == goodsInfo){
//            goodsInfo = new GoodsInfo();
//        }
        GoodsInfor goodsInfor = new GoodsInfor();
        goodsInfor.setCommunityId(fansInfor.getCommunityId());
        goodsInfor.setCommunityName(fansInfor.getCommunityName());
        goodsInfor.setAppid(appid);
        goodsInfor.setOpenid(fansInfor.getOpenid());
        goodsInfor.setCarParkNumber(fansInforAudit.getContent().split("@")[0]);
        goodsInfor.setCarEquityImg(fansInforAudit.getImgurl1());
        //TODO 这里缺少uid
//        goodsInfor.setCarAuditUid(userInfo.getUuid());
        goodsInfor.setCarUsefulEndDate(fansInforAudit.getContent().split("@")[1]);
        goodsInforRepository.saveAndFlush(goodsInfor);
//        return fansInfo.getCarOwnerStatus() == 0;

    }

    /**
     * TOKGO 检测用户用户车牌使用资格 返回相应的状态吗
     *
     * */
    public void CheckOwners(MppFansInfor fansInfor){
        //检测用户是否有电话号码
        if(fansInfor.getInfor_state().charAt(Constants.INFOR_STATE_PHOMENO_BIT)!='1'){
            throw new HHTCException(CodeEnum.HHTC_INFOR_PHOMENO);
        }
        //检测用户是否验证小区
        if(fansInfor.getInfor_state().charAt(Constants.INFOR_STATE_COMMUNITY_BIT)!='1'){
            throw new HHTCException(CodeEnum.HHTC_INFOR_COMMUNITY_NO);
        }
        //检测用户是否是车主
        if(fansInfor.getInfor_state().charAt(Constants.INFOR_STATE_CARNUMBE_BIT)!='1'){
            throw new HHTCException(CodeEnum.HHTC_INFOR_CARNUMBER_NO);
        }
    }

    /**
     * TOKGO 检测用户用户车位使用资格 返回相应的状态吗
     *
     * */
    public void CheckCarpark(MppFansInfor fansInfor){
        //检测用户是否有电话号码
        if(fansInfor.getInfor_state().charAt(Constants.INFOR_STATE_PHOMENO_BIT)!='1'){
            throw new HHTCException(CodeEnum.HHTC_INFOR_PHOMENO);
        }
        //检测用户是否验证小区
        if(fansInfor.getInfor_state().charAt(Constants.INFOR_STATE_COMMUNITY_BIT)!='1'){
            throw new HHTCException(CodeEnum.HHTC_INFOR_COMMUNITY_NO);
        }
        //检测用户是否是车主
        if(fansInfor.getInfor_state().charAt(Constants.INFOR_STATE_CARPARK_BIT)!='1'){
            throw new HHTCException(CodeEnum.HHTC_INFOR_CARPARK_NO);
        }
    }

}
