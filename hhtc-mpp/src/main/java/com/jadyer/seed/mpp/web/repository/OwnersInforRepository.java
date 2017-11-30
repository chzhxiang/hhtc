package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.GoodsInfor;
import com.jadyer.seed.mpp.web.model.OwnersInfor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 15:01.
 */
public interface OwnersInforRepository extends BaseRepository<OwnersInfor, Long> {
    List<OwnersInfor> findByOpenid(String openid);


}