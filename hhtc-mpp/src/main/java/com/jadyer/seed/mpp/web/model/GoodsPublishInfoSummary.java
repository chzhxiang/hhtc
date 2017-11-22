package com.jadyer.seed.mpp.web.model;

import java.math.BigDecimal;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/20 11:42.
 */
public interface GoodsPublishInfoSummary {
    long getGoodsId();
    String getCommunityName();
    String getCarParkNumber();
    String getCarParkImg();
    BigDecimal getPrice();
    int getPublishEndDate();
    int getPublishEndTime();
}