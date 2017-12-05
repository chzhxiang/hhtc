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
    private GoodsPublishRepository goodsPublishRepository;
    @Resource
    private GoodsPublishOrderService goodsPublishOrderService;
    @Resource
    private GoodsPublishHistoryRepository goodsPublishHistoryRepository;


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