package com.jadyer.seed.mpp.web.controller.wx;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.repository.GoodsPublishOrderRepository;
import com.jadyer.seed.mpp.web.service.FansService;
import com.jadyer.seed.mpp.web.service.UserFundsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/28 17:51.
 */
@RestController
@RequestMapping("/wx/userfunds")
public class WxUserFundsController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private FansService fansService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private GoodsPublishOrderRepository goodsPublishOrderRepository;

    /**
     * 查询粉丝押金是否足够
     * <p>
     *     目前该接口仅供车主预约下单后，预览订单时使用
     * </p>
     */
    @GetMapping("/deposit/isenough")
    public CommonResult depositIsenough(long communityId, HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(userFundsService.depositIsenough(openid, communityId));
    }

    /**
     * 查询粉丝资金情况
     */
    @GetMapping("/get")
    public CommonResult get(HttpSession session){
        String openid = hhtcHelper.getWxOpenidFromSession(session);
        return new CommonResult(userFundsService.get(openid));
    }


    /**
     * 充值
     * @param type      充值类型：10--个人中心充值，11--车位主发布车位充值，12--车主预约下单充值，13--车主发布需求充值
     * @param goodsId   车位ID
     * @param moneyBase 押金，单位：元
     * @param moneyRent 租金，单位：元
     * @param money     实际充值金额，单位：元
     */
    @PostMapping("/recharge")
    public CommonResult recharge(int type, String goodsId, String moneyBase, String moneyRent, BigDecimal money, HttpServletRequest request){
        return new CommonResult(userFundsService.recharge(type, goodsId, moneyBase, moneyRent, money, request));
    }
}