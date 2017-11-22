package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import com.jadyer.seed.mpp.web.model.CommunityInfo;
import com.jadyer.seed.mpp.web.model.GoodsInfo;
import com.jadyer.seed.mpp.web.model.MppFansInfo;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.repository.GoodsPublishOrderRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishRepository;
import com.jadyer.seed.mpp.web.repository.GoodsRepository;
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
import java.util.Date;
import java.util.List;

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
    private GoodsRepository goodsRepository;
    @Resource
    private CommunityService communityService;
    @Resource
    private GoodsPublishRepository goodsPublishRepository;
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;

    /**
     * 分页查询待审核的车位列表
     * @param pageNo zero-based page index
     */
    public Page<GoodsInfo> listTaskViaPage(MppUserInfo userInfo, String pageNo){
        //排序
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        //分页
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        //条件（物管只能查询自己小区的车位列表）
        Condition<GoodsInfo> spec = Condition.<GoodsInfo>and().eq("carAuditStatus", 1);
        if(userInfo.getType() == 2){
            List<Long> idList = new ArrayList<>();
            for(CommunityInfo obj : communityService.getByUid(userInfo.getId())){
                idList.add(obj.getId());
            }
            spec.in("communityId", idList);
        }
        //执行
        Page<GoodsInfo> page = goodsRepository.findAll(spec, pageable);
        List<GoodsInfo> list = page.getContent();
        for(GoodsInfo obj : list){
            MppFansInfo fans = fansService.getByOpenid(obj.getOpenid());
            obj.setNickname(fans.getNickname());
            obj.setHeadimgurl(fans.getHeadimgurl());
        }
        return page;
    }


    public Page<GoodsInfo> listViaPage(MppUserInfo userInfo, String pageNo){
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        Condition<GoodsInfo> spec = null;
        //物管只能查询自己小区的车位列表
        if(userInfo.getType() == 2){
            List<Long> idList = new ArrayList<>();
            for(CommunityInfo obj : communityService.getByUid(userInfo.getId())){
                idList.add(obj.getId());
            }
            spec = Condition.<GoodsInfo>and().in("communityId", idList);
        }
        Page<GoodsInfo> page = goodsRepository.findAll(spec, pageable);
        List<GoodsInfo> list = page.getContent();
        for(GoodsInfo obj : list){
            MppFansInfo fans = fansService.getByOpenid(obj.getOpenid());
            obj.setNickname(fans.getNickname());
            obj.setHeadimgurl(fans.getHeadimgurl());
        }
        return page;
    }


    public List<GoodsInfo> listAllByOpenid(String openid){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Condition<GoodsInfo> spec = Condition.<GoodsInfo>and().eq("openid", openid).ne("isUsed", 3);
        return goodsRepository.findAll(spec, sort);
    }


    public long count(MppUserInfo userInfo){
        if(userInfo.getType() == 2){
            long count = 0;
            List<Long> idList = new ArrayList<>();
            for(CommunityInfo obj : communityService.getByUid(userInfo.getId())){
                count = count + goodsRepository.countByCommunityId(obj.getId());
            }
            return count;
        }else{
            return goodsRepository.count();
        }
    }


    /**
     * 查询车位详情
     */
    public GoodsInfo get(long id){
        return goodsRepository.findOne(id);
    }


    /**
     * 审核通过或拒绝车位
     */
    @Transactional(rollbackFor=Exception.class)
    public GoodsInfo audit(MppUserInfo userInfo, GoodsInfo goodsInfo){
        if(goodsInfo.getCarAuditStatus() == 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "审核时传输的状态无效");
        }
        //物管只能审核自己小区的车位
        if(userInfo.getType() == 2){
            List<Long> idList = new ArrayList<>();
            for(CommunityInfo obj : communityService.getByUid(userInfo.getId())){
                idList.add(obj.getId());
            }
            if(!idList.contains(goodsInfo.getCommunityId())){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只能审核自己小区的车位");
            }
        }
        //校验是否注册车位主
        MppFansInfo fansInfo = fansService.getByOpenid(goodsInfo.getOpenid());
        if(2 != fansInfo.getCarParkStatus()){
            throw new HHTCException(CodeEnum.HHTC_UNREG_CAR_PARK);
        }
        goodsInfo.setCarAuditTime(new Date());
        goodsInfo.setCarAuditUid(userInfo.getId());
        goodsInfo = goodsRepository.saveAndFlush(goodsInfo);

        if(goodsInfo.getCarAuditStatus() == 2 && Constants.ISSMS){
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
            dataItem.put("first", new WeixinTemplateMsg.DItem("尊敬的车位主，您的车位申请已经审核通过！"));
            dataItem.put("keyword1", new WeixinTemplateMsg.DItem("尾号" + fansInfo.getPhoneNo().substring(7,11)));
            dataItem.put("keyword2", new WeixinTemplateMsg.DItem("车位号：" + goodsInfo.getCarParkNumber() + "审核通过"));
            dataItem.put("remark", new WeixinTemplateMsg.DItem("我要抢车位，方便加一倍！点击“抢车位”，停车舒心更省心！"));
            String url = this.hhtcContextPath + "/portal/index.html#/publish";
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+goodsInfo.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+goodsInfo.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
            WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
            templateMsg.setTemplate_id("upsa1MpVfulcu69n_f7B6kF2s8uV9ODU47estmNWuK4");
            templateMsg.setUrl(url);
            templateMsg.setTouser(fansInfo.getOpenid());
            templateMsg.setData(dataItem);
            WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(goodsInfo.getAppid()), templateMsg);
        }
        if(goodsInfo.getCarAuditStatus() == 3 && Constants.ISSMS){
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
            dataItem.put("first", new WeixinTemplateMsg.DItem("尊敬的车位主，您的车位未审核通过！"));
            dataItem.put("keyword1", new WeixinTemplateMsg.DItem(goodsInfo.getCarParkNumber()));
            dataItem.put("keyword2", new WeixinTemplateMsg.DItem(goodsInfo.getCarAuditRemark()));
            dataItem.put("remark", new WeixinTemplateMsg.DItem("请填写正确的有效信息，重新申请，谢谢！"));
            String url = this.hhtcContextPath + "/portal/index.html#/publish";
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+goodsInfo.getAppid()+"&redirect_uri="+this.hhtcContextPath+"/weixin/helper/oauth/"+goodsInfo.getAppid()+"&response_type=code&scope=snsapi_base&state="+url+"#wechat_redirect";
            WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
            templateMsg.setTemplate_id("337mC1vqm0l4bxf8WdEKfiNYO9BOjKCWlJus7hw2bPI");
            templateMsg.setUrl(url);
            templateMsg.setTouser(goodsInfo.getOpenid());
            templateMsg.setData(dataItem);
            WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(goodsInfo.getAppid()), templateMsg);
        }
        return goodsInfo;
    }


    /**
     * 添加车位
     */
    @Transactional(rollbackFor=Exception.class)
    public GoodsInfo add(GoodsInfo goodsInfo){
        //校验唯一性
        GoodsInfo byOpenidAndCarParkNumber = goodsRepository.findByOpenidAndCarParkNumber(goodsInfo.getOpenid(), goodsInfo.getCarParkNumber());
        if(null!=byOpenidAndCarParkNumber){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "添加失败：您发布的车位已存在");
        }
        //校验小区是否存在
        CommunityInfo communityInfo = communityService.get(goodsInfo.getCommunityId());
        if(StringUtils.isBlank(communityInfo.getName())){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "无此小区communityId=[" + goodsInfo.getCommunityId() + "]");
        }
        //校验是否注册车位主
        if(2 != fansService.getByOpenid(goodsInfo.getOpenid()).getCarParkStatus()){
            throw new HHTCException(CodeEnum.HHTC_UNREG_CAR_PARK);
        }
        goodsInfo.setCommunityName(communityInfo.getName());
        goodsInfo.setIsUsed(0);
        goodsInfo.setCarAuditStatus(1);
        goodsInfo.setCarUsefulFromDate(goodsInfo.getCarUsefulFromDate());
        goodsInfo.setCarUsefulEndDate(goodsInfo.getCarUsefulEndDate());
        goodsInfo.setId(null);
        goodsInfo.setIsRepetition(0);
        // 是否重复
        List<GoodsInfo> byCarParkNumber = goodsRepository.findByCarParkNumber(goodsInfo.getCarParkNumber());
        if(byCarParkNumber.size()>0){
            goodsInfo.setIsRepetition(1);
            // 更新所有车位为重复
            goodsRepository.updateStatus(goodsInfo.getCarParkNumber(),1);
        }
        return goodsRepository.saveAndFlush(goodsInfo);
    }


    /**
     * 更新状态
     * @param preIsUsed 本次欲更新的状态的前置状态
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean updateStatus(long id, int isUsed, int preIsUsed){
        return 1 == goodsRepository.updateStatus(id, isUsed, preIsUsed);
    }


    /**
     * 修改车位
     */
    @Transactional(rollbackFor=Exception.class)
    public GoodsInfo update(long id, long communityId, String carParkNumber, String carEquityImg) {
        GoodsInfo info = goodsRepository.findOne(id);
        if(info.getCarAuditStatus() == 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "不能修改审核中的车位");
        }
        if(info.getCarAuditStatus() == 2){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "不能修改已审核通过的车位");
        }
        if(communityId != info.getCommunityId()){
            info.setCommunityId(communityId);
            info.setCommunityName(communityService.get(communityId).getName());
        }
        GoodsInfo gi = goodsRepository.findByOpenidAndCarParkNumber(info.getOpenid(), carParkNumber);
        if(null!=gi && null!=gi.getId() && gi.getId()!=id){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "您发布的车位已存在");
        }
        info.setCarParkNumber(carParkNumber);
        info.setCarEquityImg(carEquityImg);
        info.setCarAuditStatus(1);
        return goodsRepository.saveAndFlush(info);
    }


    /**
     * 删除车位（其会将该车位发布信息一并删除）
     */
    @Transactional(rollbackFor=Exception.class)
    public void del(MppUserInfo userInfo, long id) {
        if(null!=userInfo && userInfo.getType()!=1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "仅平台运营有删除权限");
        }
        //List<GoodsInfo> goodsInfoList = goodsRepository.findByOpenid(openid);
        //if(goodsInfoList.isEmpty() || goodsInfoList.size()==1){
        //    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "请至少保留一个车位");
        //}
        GoodsInfo goods = goodsRepository.findOne(id);
        if(goods.getCarAuditStatus() == 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "审核中的车位不能删除");
        }
        if(2 == goods.getIsUsed()){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "车位已被下单");
        }
        if(orderService.countByGoodsIdAndOrderTypeInAndOrderStatusIn(id, Arrays.asList(1, 2), Arrays.asList(2, 9)) > 0){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "车位已被下单或转租");
        }
        goodsPublishOrderRepository.deleteByGoodsId(id);
        goodsPublishRepository.deleteByGoodsId(id);
        goodsRepository.delete(id);
        //更新车位重复状态
        List<GoodsInfo> list = goodsRepository.findByCarParkNumber(goods.getCarParkNumber());
        if(null!=list && list.size()==1){
            goodsRepository.updateStatus(goods.getCarParkNumber(), 0);
        }
    }
}