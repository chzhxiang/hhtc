package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.model.UserFunds;
import com.jadyer.seed.mpp.web.service.FansService;
import com.jadyer.seed.mpp.web.service.GoodsNeedService;
import com.jadyer.seed.mpp.web.service.UserFundsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/26 14:41.
 */
@RestController
@RequestMapping("/wx/goods/need")
public class WxGoodsNeedController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private GoodsNeedService goodsNeedService;

    //TODO
    String openid = "ojZ6h1U3w-d-ueEdPv-UfttvdBcU";


    @GetMapping("/get")
    public CommonResult get(long id){
        return new CommonResult(goodsNeedService.get(id));
    }


    @RequestMapping("/list")
    public CommonResult list(String pageNo, HttpSession session){
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(goodsNeedService.listViaPage(openid, pageNo));
    }


    /**
     * 发布需求
     */
    @PostMapping("/add")
    public CommonResult add(String communityId, String carNumber, int needType, int needFromTime, int needEndTime, int needFromDate, int needEndDate, HttpSession session){
        String appid = hhtcHelper.getWxAppidFromSession(session);
        if(Constants.ISWEIXIN) openid = hhtcHelper.getWxOpenidFromSession(session);
        //校验是否注册车主
        MppFansInfor fansInfor = fansService.getByOpenid(openid);
        //TODO
//        if(2 != fansInfor.getCarOwnerStatus()){
//            throw new HHTCException(CodeEnum.HHTC_UNREG_CAR_OWNER);
//        }
        //计算小区ID
        if(StringUtils.isBlank(communityId)){
            communityId = fansInfor.getCommunityId()+"";
        }
        //校验时间
        if(DateUtils.addHours(hhtcHelper.convertToDate(needFromDate, needFromTime), 24).getTime() < hhtcHelper.convertToDate(needEndDate, needEndTime).getTime()){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "需求时长不能超过24小时");
        }
        //车牌号更新
        if(StringUtils.isBlank(carNumber)){
            carNumber = fansInfor.getCarNumber().split("`")[0];
        }else{
            carNumber = carNumber.toUpperCase();
            if(!fansInfor.getCarNumber().contains(carNumber)){
                fansInfor.setCarNumber(fansInfor.getCarNumber() + "`" + carNumber);
                fansService.upsert(fansInfor);
            }
        }
        hhtcHelper.verifyOfTime(needType, needFromTime, needEndTime, needFromDate, needEndDate);
        hhtcHelper.verifyOfTimeCross(needFromDate, Long.parseLong(communityId), carNumber, needFromTime, needEndTime);
        //校验押金和余额是否足够
        BigDecimal price = hhtcHelper.calcPrice(Long.parseLong(communityId), needType);
        Map<String, String> fundsMap = new HashMap<>();
        fundsMap.put("moneyBase", new BigDecimal(0).toString());
        fundsMap.put("moneyRent", new BigDecimal(0).toString());
        UserFunds funds = userFundsService.get(openid);
        Map<String, String> dataMap = userFundsService.depositIsenough(openid, Long.parseLong(communityId));
        if("0".equals(dataMap.get("isenough")) || price.compareTo(funds.getMoneyBalance())>0){
            fundsMap.put("moneyBase", dataMap.get("money"));
            if(price.compareTo(funds.getMoneyBalance()) > 0){
                fundsMap.put("moneyRent", (price.subtract(funds.getMoneyBalance())).toString());
            }
            fundsMap.put("moneyFull", (new BigDecimal(fundsMap.get("moneyBase")).add(new BigDecimal(fundsMap.get("moneyRent")))).toString());
            return new CommonResult(CodeEnum.HHTC_NEED_NO_MONEY, fundsMap);
        }
        return new CommonResult(goodsNeedService.add(appid, openid, Long.parseLong(communityId), carNumber, price, needType, needFromTime, needEndTime, needFromDate, needEndDate));
    }
}