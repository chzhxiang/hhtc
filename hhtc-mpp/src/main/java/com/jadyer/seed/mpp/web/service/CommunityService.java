package com.jadyer.seed.mpp.web.service;

import com.google.common.collect.Sets;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.mpp.web.model.CommunityInfo;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.repository.CommunityDeviceRepository;
import com.jadyer.seed.mpp.web.repository.CommunityRepository;
import com.jadyer.seed.mpp.web.repository.MppUserInfoRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/16 11:06.
 */
@Service
public class CommunityService {
    @Resource
    private MppUserService mppUserService;
    @Resource
    private CommunityRepository communityRepository;
    @Resource
    private MppUserInfoRepository mppUserInfoRepository;
    @Resource
    private CommunityDeviceRepository communityDeviceRepository;

    /**
     * 分页查询所有小区列表
     * @param pageNo zero-based page index
     */
    public Page<CommunityInfo> listViaPage(MppUserInfo userInfo, String pageNo,String name){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        Condition<CommunityInfo> spec = null;
        //物管登录则只显示其管理的小区，运营登录则显示所有小区
        if(userInfo.getType() == 2){
            spec = Condition.<CommunityInfo>and().eq("uid", userInfo.getId());
        }
        if(StringUtils.isNotBlank(name)){
            spec = Condition.<CommunityInfo>and().like("name", name);
        }
        return communityRepository.findAll(spec, pageable);
    }


    public Page<MppUserInfo> listMgrViaPage(MppUserInfo userInfo, String pageNo){
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以查看物管列表");
        }
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        Condition<MppUserInfo> spec = Condition.<MppUserInfo>and().eq("type", 2);
        return mppUserInfoRepository.findAll(spec, pageable);
    }


    public List<CommunityInfo> listAll(){
        return communityRepository.findAll();
    }


    /**
     * 获取省份列表
     */
    public Set<String> getProvinceSet(){
        List<CommunityInfo> communityInfoList = communityRepository.findAll();
        Set<String> provinceNameSet = Sets.newHashSet();
        for(CommunityInfo obj : communityInfoList){
            provinceNameSet.add(obj.getProvinceName());
        }
        return provinceNameSet;
    }


    /**
     * 获取城市列表
     */
    public Set<String> getCitySet(String provinceName){
        List<CommunityInfo> communityInfoList = communityRepository.findByProvinceName(provinceName);
        Set<String> cityNameSet = Sets.newHashSet();
        for(CommunityInfo obj : communityInfoList){
            cityNameSet.add(obj.getCityName());
        }
        return cityNameSet;
    }


    /**
     *  TOKGO 获取小区列表
     */
    public List<CommunityInfo> listByCityName(String cityName){
        return communityRepository.findByCityName(cityName);
    }


    /**
     * 查询小区详情
     */
    public CommunityInfo get(long id){
        return communityRepository.findOne(id);
    }


    /**
     * 查询某个物管所管理的小区列表
     */
    public List<CommunityInfo> getByUid(long uid){
        return communityRepository.findByUid(uid);
    }


    /**
     * save or update
     */
    @Transactional(rollbackFor=Exception.class)
    public CommunityInfo upsert(MppUserInfo userInfo, CommunityInfo communityInfo) {
        if(userInfo.getType() != 1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "只有平台运营才可以更新小区信息");
        }
        if(null!=communityInfo.getId() && 0!=communityInfo.getId()){
            CommunityInfo obj = communityRepository.findOne(communityInfo.getId());
            if(obj.getUid() != communityInfo.getUid()){
                throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "更新时不能修改物管账号");
            }
            //更新小区设备信息中冗余的小区名称
            if(!StringUtils.equals(obj.getName(), communityInfo.getName())){
                communityDeviceRepository.updateCommunityName(communityInfo.getId(), communityInfo.getName());
            }
        }else{
            if(0 == communityInfo.getUid()){
                //自动生成物管帐号
                String username = "hhtc" + RandomStringUtils.randomNumeric(4);
                if(mppUserInfoRepository.countByUsername(username) > 0){
                    throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "帐号已存在");
                }
                MppUserInfo user = new MppUserInfo();
                user.setPid(0);
                user.setUsername(username);
                user.setPassword(username);
                user.setType(2);
                communityInfo.setUid(mppUserInfoRepository.saveAndFlush(user).getId());
            }
        }
        communityInfo.setPointLat(communityInfo.getPointLng().substring(communityInfo.getPointLng().indexOf(",")+1));
        communityInfo.setPointLng(communityInfo.getPointLng().substring(0, communityInfo.getPointLng().indexOf(",")));
        return communityRepository.saveAndFlush(communityInfo);
    }


    /**
     * 平台运营重置物管密码
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean resetPassword(MppUserInfo userInfo, long uid, String newPassword) {
        if(userInfo.getId()==uid || userInfo.getType()!=1){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "权限不足，无法重置密码");
        }
        return 1 == mppUserInfoRepository.updatePassword(uid, mppUserService.buildEncryptPassword(newPassword));
    }
}