package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.CommunityInfo;
import com.jadyer.seed.mpp.web.model.GoodsInfo;
import com.jadyer.seed.mpp.web.model.GoodsPublishOrder;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.model.UserFunds;
import com.jadyer.seed.mpp.web.repository.GoodsPublishOrderRepository;
import com.jadyer.seed.mpp.web.service.CommunityService;
import com.jadyer.seed.mpp.web.service.FansService;
import com.jadyer.seed.mpp.web.service.GoodsPublishService;
import com.jadyer.seed.mpp.web.service.GoodsService;
import com.jadyer.seed.mpp.web.service.UserFundsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/19 16:41.
 */
@RestController
@RequestMapping("/wx/goods/publish")
public class WxGoodsPublishController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private CommunityService communityService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private GoodsPublishService goodsPublishService;
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;

    /**
     * 发布的车位是否可合并
     */
    @RequestMapping("/add/canMerge")
    public CommonResult addCanMerge(long goodsId, int publishType, int publishFromTime, int publishEndTime, String publishFromDates, HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(goodsPublishService.addCanMerge(openid, goodsId, publishType, publishFromTime, publishEndTime, publishFromDates));
    }


    /**
     * 车位主发布车位
     * http://127.0.0.1/wx/goods/publish/add?goodsId=24&publishType=2&publishFromTime=1900&publishEndTime300&publishFromDates=20170820-20170821-20170823-20170824
     */
    @RequestMapping("/add")
    public CommonResult add(long goodsId, int publishType, int publishFromTime, int publishEndTime, String publishFromDates, HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        GoodsInfo goodsInfo = goodsPublishService.verifyBeforeAdd(openid, goodsId, publishType, publishFromTime, publishEndTime, publishFromDates);
        //校验押金是否足够（一个车位一份押金）
        Map<String, String> fundsMap = new HashMap<>();
        UserFunds funds = userFundsService.get(openid);
        CommunityInfo communityInfo = communityService.get(goodsInfo.getCommunityId());
        if(communityInfo.getMoneyBase().compareTo(funds.getMoneyBase()) > 0){
            fundsMap.put("moneyBase", (communityInfo.getMoneyBase().subtract(funds.getMoneyBase())).toString());
            fundsMap.put("moneyRent", new BigDecimal(0).toString());
            fundsMap.put("moneyFull", (new BigDecimal(fundsMap.get("moneyBase")).add(new BigDecimal(fundsMap.get("moneyRent")))).toString());
            return new CommonResult(CodeEnum.HHTC_NEED_NO_MONEY, fundsMap);
        }
        return new CommonResult(goodsPublishService.add(openid, publishType, publishFromTime, publishEndTime, publishFromDates, goodsInfo));
    }


    /**
     * 匹配列表
     * @param communityId     小区ID
     * @param publishType     发布类型
     * @param publishFromDate 发布日期（入场日期）
     * @param publishEndDate  发布日期（出场日期）
     * @param publishFromTime 发布时间（入场时间）
     * @param publishEndTime  发布时间（出场时间）
     */
    @GetMapping("/matchlist")
    public CommonResult matchlist(long communityId, int publishType, String publishFromDate, String publishEndDate, String publishFromTime, String publishEndTime){
        return new CommonResult(goodsPublishService.matchlist(communityId, publishType, publishFromDate, publishEndDate, publishFromTime, publishEndTime));
    }


    /**
     * 预约下单
     */
    @GetMapping("/order")
    public CommonResult order(String ids, String carNumber, HttpServletRequest request){
        String appid = hhtcHelper.getWxAppidFromSession(request.getSession());
        String openid = hhtcHelper.getWxOpenidFromSession(request.getSession());
        //校验是否注册车主
        MppFansInfor fansInfor = fansService.getByOpenid(openid);
        //TODO
//        if(2 != fansInfor.getCarOwnerStatus()){
//            throw new HHTCException(CodeEnum.HHTC_UNREG_CAR_OWNER);
//        }
        //校验是否已被锁定
        BigDecimal price = new BigDecimal(0);
        List<GoodsPublishOrder> orderList = new ArrayList<>();
        for(String id : ids.split("`")){
            GoodsPublishOrder order = goodsPublishOrderRepository.findOne(Long.parseLong(id));
            if(order.getStatus() != 0){
                throw new HHTCException(CodeEnum.HHTC_GOODS_ORDER_FAIL);
            }
            orderList.add(order);
            price = price.add(order.getPrice());
        }
        //校验押金和余额是否足够
        Map<String, String> fundsMap = new HashMap<>();
        fundsMap.put("moneyBase", new BigDecimal(0).toString());
        fundsMap.put("moneyRent", new BigDecimal(0).toString());
        UserFunds funds = userFundsService.get(openid);
        Map<String, String> dataMap = userFundsService.depositIsenough(openid, orderList.get(0).getCommunityId());
        if("0".equals(dataMap.get("isenough")) || price.compareTo(funds.getMoneyBalance())>0){
            fundsMap.put("moneyBase", dataMap.get("money"));
            if(price.compareTo(funds.getMoneyBalance()) > 0){
                fundsMap.put("moneyRent", (price.subtract(funds.getMoneyBalance())).toString());
            }
            fundsMap.put("moneyFull", (new BigDecimal(fundsMap.get("moneyBase")).add(new BigDecimal(fundsMap.get("moneyRent")))).toString());
            return new CommonResult(CodeEnum.HHTC_NEED_NO_MONEY, fundsMap);
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
        //计算publishFromDates
        List<String> publishFromDateList = new ArrayList<>();
        for(GoodsPublishOrder obj : orderList){
            for(String _fromDate : obj.getPublishFromDates().split("-")){
                if(!publishFromDateList.contains(_fromDate)){
                    publishFromDateList.add(_fromDate);
                }
            }
        }
        publishFromDateList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.parseInt(o1) - Integer.parseInt(o2);
            }
        });
        StringBuilder sb = new StringBuilder();
        for(String obj : publishFromDateList){
            sb.append("-").append(obj);
        }
        return new CommonResult(goodsPublishService.order(appid, openid, carNumber, price, ids, sb.toString().substring(1), orderList, fansInfor));
    }
}