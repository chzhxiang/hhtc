package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.mpp.web.model.AdviceInfo;
import com.jadyer.seed.mpp.web.model.AdviceInfor;
import com.jadyer.seed.mpp.web.repository.AdviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/25 15:36.
 */
@Service
public class AdviceService {
    @Resource
    private AdviceRepository adviceRepository;

    /**
     * TOKGO 新增意见反馈
     */
    @Transactional(rollbackFor=Exception.class)
    public AdviceInfor add(String openid,String img, String content){
        AdviceInfor infor = new AdviceInfor();
        infor.setOpenid(openid);
        infor.setContent(content);
        infor.setHeadimgurl(img);
        return adviceRepository.saveAndFlush(infor);
    }

    /**
     * 查询投诉信息
     */
    @Transactional(rollbackFor=Exception.class)
    public List<HashMap> Get(String openid){
        List<HashMap> list = new ArrayList<>();
        //TODO 查询问题  未知错误
        List<AdviceInfor> adviceInfors = adviceRepository.findByOpenid(openid);
        HashMap hashMap;
        for (AdviceInfor adviceInfor:adviceInfors){
            hashMap = new HashMap();
            hashMap.put("content",adviceInfor.getContent());
            if (adviceInfor.getAuditUid()==0)
                hashMap.put("result","审核中");
            else
                hashMap.put("result",adviceInfor.getResult());
            hashMap.put("uptime",adviceInfor.getCreateTime().toString());
            list.add(hashMap);
        }
        return list;
    }


}