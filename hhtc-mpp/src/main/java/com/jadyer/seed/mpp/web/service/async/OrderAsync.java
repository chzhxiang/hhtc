package com.jadyer.seed.mpp.web.service.async;

import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.mpp.web.model.CommunityInfo;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.model.OrderHistory;
import com.jadyer.seed.mpp.web.model.OrderInfor;
import com.jadyer.seed.mpp.web.repository.FansInforRepository;
import com.jadyer.seed.mpp.web.repository.OrderHistoryRepository;
import com.jadyer.seed.mpp.web.repository.OrderInforRepository;
import com.jadyer.seed.mpp.web.service.CommunityService;
import com.jadyer.seed.mpp.web.service.UserFundsService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @描述
 * @创建人 TOKGO
 * @创建时间 2017/12/2
 * @修改人和其它信息
 */
@Async
@Component
public class OrderAsync {
    @Resource
    private CommunityService communityService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private FansInforRepository fansInforRepository;
    @Resource
    private OrderInforRepository orderInforRepository;

    /**
     * TOKGO  分账
     * */
    public double Penny(double total,OrderInfor orderInfor){
        MppFansInfor mppFansInfor = fansInforRepository.findByOpenid(orderInfor.getPostOpenid());
        CommunityInfo communityInfo = communityService.get(mppFansInfor.getCommunityId());
        //给卖家 打钱
        double carparkrent = (total*communityInfo.getRentRatioCarparker())/100.0;
        userFundsService.addMoneyBalanceForFans(mppFansInfor.getOpenid(),new BigDecimal(carparkrent)
                ,orderInfor.getOrderId(),"订单收款，车主分润");
        //分钱给平台
        double platformerent = (total*communityInfo.getRentRatioPlatform())/100.0;
        userFundsService.addMoneyBalanceForPlatform(new BigDecimal(platformerent),orderInfor.getOrderId()
                ,"订单收款，平台分润");
        return carparkrent;
    }

    /**
     * TOKGO  超时补款分账
     * */
    public double Penny(double total,OrderHistory orderHistory){
        MppFansInfor mppFansInfor = fansInforRepository.findByOpenid(orderHistory.getPostOpenid());
        CommunityInfo communityInfo = communityService.get(mppFansInfor.getCommunityId());
        //给卖家 打钱
        double carparkrent = (total*communityInfo.getRentRatioCarparker())/100.0;
        userFundsService.addMoneyBalanceForFans(mppFansInfor.getOpenid(),new BigDecimal(carparkrent)
                ,orderHistory.getOrderId(),"订单收款，车主分润");
        //分钱给平台
        double platformerent = (total*communityInfo.getRentRatioPlatform())/100.0;
        userFundsService.addMoneyBalanceForPlatform(new BigDecimal(platformerent),orderHistory.getOrderId()
                ,"订单收款，平台分润");
        return carparkrent;
    }


    /**
     * TOKGO 计算月结
     * */
    public void CalculateMonth(OrderInfor orderInfor, long timenow){
        MppFansInfor mppFansInfor = fansInforRepository.findByOpenid(orderInfor.getPostOpenid());
        CommunityInfo communityInfo = communityService.get(mppFansInfor.getCommunityId());
        double total = orderInfor.getOutPrice().doubleValue();
        total += communityInfo.getMoneyRentFull().doubleValue()* Constants.S_CALCULATEdAY;
        orderInfor.setOutPrice(new BigDecimal(total));
        orderInfor.setOutPriceTime(timenow);
        orderInforRepository.save(orderInfor);
    }
}
