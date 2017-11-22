package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.mpp.web.model.AdviceInfo;
import com.jadyer.seed.mpp.web.repository.AdviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/25 15:36.
 */
@Service
public class AdviceService {
    @Resource
    private AdviceRepository adviceRepository;

    /**
     * 新增意见反馈
     */
    @Transactional(rollbackFor=Exception.class)
    public AdviceInfo add(String openid, String content){
        AdviceInfo info = new AdviceInfo();
        info.setOpenid(openid);
        info.setContent(content);
        return adviceRepository.saveAndFlush(info);
    }
}