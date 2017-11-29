package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.BeanUtil;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.MoneyUtil;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.GoodsPublishHistoryRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishOrderRepository;
import com.jadyer.seed.mpp.web.repository.GoodsPublishRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/19 18:32.
 */
@Service
public class GoodsPublishService {
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Value("${hhtc.wxtemplateUrl.needsucc}")
    private String templateUrlNeed;
    @Value("${hhtc.portalUrl.center}")
    private String portalCenterUrl;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderService orderService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private CommunityService communityService;
    @Resource
    private GoodsNeedService goodsNeedService;
    @Resource
    private OrderRentService orderRentService;
    @Resource
    private OrderInoutService orderInoutService;
    @Resource
    private GoodsPublishService goodsPublishService;
    @Resource
    private UserFundsFlowService userFundsFlowService;
    @Resource
    private GoodsPublishRepository goodsPublishRepository;
    @Resource
    private GoodsPublishOrderService goodsPublishOrderService;
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;
    @Resource
    private GoodsPublishHistoryRepository goodsPublishHistoryRepository;

    /**
     * 业务校验（车位主发布车位）
     */
    public GoodsInfor verifyBeforeAdd(String openid, long goodsId, int publishType, int publishFromTime, int publishEndTime, String publishFromDates){
        List<String> dateList = Arrays.asList(publishFromDates.split("-"));
        hhtcHelper.verifyOfTime(publishType, publishFromTime, publishEndTime, Integer.parseInt(dateList.get(0)), Integer.parseInt(dateList.get(dateList.size()-1)));
        //TODO
        //        //校验是否注册车位主
//        if(2 != fansService.getByOpenid(openid).getCarParkStatus()){
//            throw new HHTCException(CodeEnum.HHTC_UNREG_CAR_PARK);
//        }
        GoodsInfor goodsInfor = goodsService.get(goodsId);
        try {
            Date endDate = DateUtils.parseDate(dateList.get(dateList.size()-1), "yyyyMMdd");
            if(publishType==3 || (2==publishType && publishEndTime<publishFromTime)){
                endDate = DateUtils.addDays(endDate, 1);
            }
//            if(Integer.parseInt(DateFormatUtils.format(endDate, "yyyyMMdd")) > goodsInfo.getCarUsefulEndDate()){
//                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "发布截止日不能超过车位有效期");
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //时间段不可重叠
        hhtcHelper.verifyOfTimeCross(dateList, goodsId, publishFromTime, publishEndTime);
        return goodsInfor;
    }


    /**
     * 时间段连接：三合一
     * <p>
     *     注意：该方法返回的是t_goods_publish_info，而非t_goods_publish_order
     * </p>
     * @return 返回合并后的信息（如果存在三合一，则返回的信息中deleteId属性会记录需要删除的信息）
     */
    private GoodsPublishInfo threeToOne(long goodsId, int publishType, int publishFromTime, int publishEndTime, int publishFromDate, int publishEndDate){
        GoodsPublishInfo info = new GoodsPublishInfo();
        //标记是否已合并了头部和尾部
        boolean matchHeader = false;
        boolean matchFooter = false;
        //连接到数据库中的头部
        Condition<GoodsPublishInfo> spec01 = Condition.and();
        spec01.eq("goodsId", goodsId);
        spec01.eq("status", 0);
        spec01.eq("publishType", publishType);
        spec01.eq("publishFromDate", publishEndDate);
        spec01.eq("publishFromTime", publishEndTime);
        List<GoodsPublishInfo> list01 = goodsPublishRepository.findAll(spec01);
        if(!list01.isEmpty()){
            matchHeader = true;
            BeanUtil.copyProperties(list01.get(0), info);
            info.setId(list01.get(0).getId());
            info.setPublishFromDate(publishFromDate);
            info.setPublishFromTime(publishFromTime);
        }
        //连接到数据库中的尾部
        Condition<GoodsPublishInfo> spec02 = Condition.and();
        spec02.eq("goodsId", goodsId);
        spec02.eq("status", 0);
        spec02.eq("publishType", publishType);
        spec02.eq("publishEndDate", publishFromDate);
        spec02.eq("publishEndTime", publishFromTime);
        List<GoodsPublishInfo> list02 = goodsPublishRepository.findAll(spec02);
        if(!list02.isEmpty()){
            matchFooter = true;
            BeanUtil.copyProperties(list02.get(0), info);
            info.setId(list02.get(0).getId());
            if(matchHeader){
                info.setPublishEndDate(list01.get(0).getPublishEndDate());
                info.setPublishEndTime(list01.get(0).getPublishEndTime());
            }else{
                info.setPublishEndDate(publishEndDate);
                info.setPublishEndTime(publishEndTime);
            }
        }
        if(matchHeader && matchFooter){
            info.setDeleteId(list01.get(0).getId());
        }
        //最后判断info所属的order是否是大于24小时发布的那种
        if(null!=info.getId() && info.getId()>0){
//            GoodsPublishOrder order = goodsPublishOrderRepository.findByGoodsPublishIdsLike("%"+info.getId()+"%");
//            if(null!=order && order.getId()>0 && order.getPublishFromDates().contains("-")){
//                return new GoodsPublishInfo();
//            }
        }
        return info;
    }


    /**
     * 发布的车位是否可合并
     */
    public boolean addCanMerge(String openid, long goodsId, int publishType, int publishFromTime, int publishEndTime, String publishFromDates){
//        GoodsInfo goodsInfo = this.verifyBeforeAdd(openid, goodsId, publishType, publishFromTime, publishEndTime, publishFromDates);
        //全天或超过24小时的发布订单，都不合并
        if(publishFromDates.contains("-") || publishType==3){
            return false;
        }
        //计算起止日期（注意夜间跨天）
        int publishFromDate = Integer.parseInt(publishFromDates);
        int publishEndDate = Integer.parseInt(publishFromDates);
        if(2==publishType && publishEndTime<publishFromTime){
            try {
                publishEndDate = Integer.parseInt(DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(publishEndDate+"", "yyyyMMdd"), 1), "yyyyMMdd"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        GoodsPublishInfo info = this.threeToOne(goodsId, publishType, publishFromTime, publishEndTime, publishFromDate, publishEndDate);
        return null!=info.getId() && info.getId()>0;
    }




    public GoodsPublishInfo get(long id){
        return goodsPublishRepository.findOne(id);
    }


    @Transactional(rollbackFor=Exception.class)
    public GoodsPublishInfo upsert(GoodsPublishInfo publish) {
        return goodsPublishRepository.saveAndFlush(publish);
    }


    /**
     * 更新发布状态
     * @param goodsPublishId 对应OrderInfo#goodsPublishId属性
     */
    @Transactional(rollbackFor=Exception.class)
    public void updateStatus(String goodsPublishId, int status, int preStatus){
        String[] goodsPublishIds = goodsPublishId.split("`");
        List<Long> idList = new ArrayList<>();
        for(String obj : goodsPublishIds){
            idList.add(Long.parseLong(obj));
        }
        goodsPublishRepository.updateStatus(idList, status, preStatus);
    }






}