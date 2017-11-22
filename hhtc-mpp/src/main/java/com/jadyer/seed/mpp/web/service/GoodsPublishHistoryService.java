package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.mpp.web.model.GoodsPublishHistory;
import com.jadyer.seed.mpp.web.repository.GoodsPublishHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/22 19:03.
 */
@Service
public class GoodsPublishHistoryService {
    @Resource
    private GoodsPublishHistoryRepository goodsPublishHistoryRepository;

    public GoodsPublishHistory get(long id) {
        return goodsPublishHistoryRepository.findOne(id);
    }


    public GoodsPublishHistory getByGoodsPublishId(long goodsPublishId) {
        return goodsPublishHistoryRepository.findByGoodsPublishId(goodsPublishId);
    }


    @Transactional(rollbackFor=Exception.class)
    public GoodsPublishHistory add(GoodsPublishHistory history) {
        history.setId(null);
        return goodsPublishHistoryRepository.saveAndFlush(history);
    }
}